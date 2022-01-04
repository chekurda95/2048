package com.chekurda.design.custom_view_tools

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.text.Layout
import android.text.Layout.Alignment
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils.TruncateAt
import android.view.View
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.core.graphics.withTranslation
import org.apache.commons.lang3.StringUtils
import com.chekurda.design.custom_view_tools.TextLayout.Companion.createTextLayoutByStyle
import com.chekurda.design.custom_view_tools.TextLayout.TextLayoutParams
import com.chekurda.design.custom_view_tools.styles.CanvasStylesProvider
import com.chekurda.design.custom_view_tools.styles.StyleParams.StyleKey
import com.chekurda.design.custom_view_tools.styles.StyleParams.TextStyle
import com.chekurda.design.custom_view_tools.styles.StyleParamsProvider
import com.chekurda.design.custom_view_tools.utils.StaticLayoutConfigurator
import com.chekurda.design.custom_view_tools.utils.TextHighlights
import com.chekurda.design.custom_view_tools.utils.getTextWidth

/**
 * Разметка для отображения текста.
 *
 * Является оберткой над [Layout] для отображения текста,
 * который лениво создается по набору параметров модели [params].
 * Также содержит параметры и api ускоряющие и облегчающие работу с кастомной текстовой разметкой.
 *
 * Параметры разметки настраиваются с помощью конфига [TextLayoutConfig] в конструкторе,
 * или с помощью методов [configure] и [buildLayout].
 * Статичный метод [createTextLayoutByStyle] позволяет создавать разметку по xml стилю.
 *
 * Имеет возможность включения отладочных границ разметки при помощи [isInspectMode].
 *
 * @param config настройка параметров текстовой разметки.
 * @see TextLayoutParams
 */
class TextLayout(config: TextLayoutConfig? = null) {

    companion object {

        /**
         * Создать текстовую разметку [TextLayout] по параметрам ресурса стиля [styleRes].
         *
         * @param styleProvider поставщик стилей [TextStyle].
         * @param obtainPadding true, если текстовая разметка должна получить отступы из стиля.
         * @param postConfig конфиг параметров текстовой разметки
         * для дополнительной настройки после инициализии из ресурса стиля.
         */
        fun createTextLayoutByStyle(
            context: Context,
            @StyleRes styleRes: Int,
            styleProvider: StyleParamsProvider<TextStyle>? = null,
            obtainPadding: Boolean = true,
            postConfig: TextLayoutConfig? = null
        ): TextLayout =
            createTextLayoutByStyle(context, StyleKey(styleRes), styleProvider, obtainPadding, postConfig)

        /**
         * Создать текстовую разметку [TextLayout] по ключу стиля [styleKey].
         * @see StyleKey
         *
         * Использовать для сценариев, когда значения атрибутов стиля [StyleKey.styleRes] могут зависеть от разных тем,
         * поэтому для правильного кэширования помимо ресурса стиля необходим дополнительный [StyleKey.tag].
         *
         * @param styleProvider поставщик стилей [TextStyle].
         * @param obtainPadding true, если текстовая разметка должна получить отступы из стиля.
         * @param postConfig конфиг параметров текстовой разметки
         * для дополнительной настройки после инициализии из ресурса стиля.
         */
        fun createTextLayoutByStyle(
            context: Context,
            styleKey: StyleKey,
            styleProvider: StyleParamsProvider<TextStyle>? = null,
            obtainPadding: Boolean = true,
            postConfig: TextLayoutConfig? = null
        ): TextLayout =
            if (styleKey.styleRes != 0) {
                val style = styleProvider?.getStyleParams(context, styleKey)
                    ?: CanvasStylesProvider.obtainTextStyle(context, styleKey)
                TextLayout {
                    paint = TextPaint(ANTI_ALIAS_FLAG).also {
                        it.textSize = style.textSize ?: it.textSize
                        it.color = style.textColor ?: it.color
                    }
                    text = style.text ?: text
                    layoutWidth = style.layoutWidth.takeIf { it != 0 } ?: layoutWidth
                    alignment = style.alignment ?: alignment
                    ellipsize = style.ellipsize ?: ellipsize
                    includeFontPad = style.includeFontPad ?: includeFontPad
                    maxLines = style.maxLines ?: maxLines
                    isVisible = style.isVisible ?: isVisible
                    if (obtainPadding) {
                        style.paddingStyle?.also { paddingStyle ->
                            padding = TextLayoutPadding(
                                paddingStyle.paddingStart,
                                paddingStyle.paddingTop,
                                paddingStyle.paddingEnd,
                                paddingStyle.paddingBottom
                            )
                        }
                    }
                    postConfig?.invoke(this)
                }
            } else TextLayout(postConfig)
    }

    /**
     * Параметры для создания текстовой разметки [layout].
     */
    private val params = TextLayoutParams()

    /**
     * Вспомогательный класс для отладки текстовой разметки.
     * Для включения отладочного мода необходимо переключить [isInspectMode] в true.
     * Может оказаться крайне полезным на этапе интеграции [TextLayout].
     */
    private val inspectHelper = if (isInspectMode) InspectHelper() else null

    /**
     * Получить снимок состояния [TextLayout].
     */
    internal val state: TextLayoutState
        get() = TextLayoutState(
            params.copy(),
            cachedLayout,
            isLayoutChanged,
            textPos
        )

    init {
        config?.invoke(params)
    }

    /**
     * Получить текстовую разметку.
     * Имеет ленивую инициализацию.
     */
    private val layout: Layout
        get() = cachedLayout
            ?.takeIf { !isLayoutChanged }
            ?: updateStaticLayout()

    /**
     * Текущая текстовая разметка.
     * Лениво инициализируется при первом обращении к [layout].
     */
    private var cachedLayout: Layout? = null

    /**
     * Признак необходимости в построении layout при следующем обращении
     * по причине изменившихся данных.
     */
    private var isLayoutChanged: Boolean = true

    /**
     * Позиция текста для рисования с учетом внутренних отступов (координата левого верхнего угла).
     */
    private var textPos = params.padding.start.toFloat() to params.padding.top.toFloat()

    /**
     * Координаты границ [TextLayout], полученные в [layout].
     */
    private var rect = Rect()

    /**
     * Текст разметки.
     */
    val text: CharSequence
        get() = params.text

    /**
     * Краска текста разметки.
     */
    val textPaint: TextPaint
        get() = params.paint

    /**
     * Видимость разметки.
     */
    val isVisible: Boolean
        get() = params.isVisible.let {
            if (!params.isVisibleWhenBlank) it && params.text.isNotBlank()
            else it
        }

    /**
     * Максимальное количество строк.
     */
    val maxLines: Int
        get() = params.maxLines

    /**
     * Левая позиция разметки, установленная в [layout].
     */
    @get:Px
    val left: Int
        get() = rect.left

    /**
     * Верхняя позиция разметки, установленная в [layout].
     */
    @get:Px
    val top: Int
        get() = rect.top

    /**
     * Правая позиция разметки с учетом внутренних паддингов [left] + [width].
     */
    @get:Px
    val right: Int
        get() = rect.right

    /**
     * Нижняя позиция разметки с учетом внутренний паддингов [top] + [height].
     */
    @get:Px
    val bottom: Int
        get() = rect.bottom

    /**
     * Левый внутренний оступ разметки.
     */
    @get:Px
    val paddingStart: Int
        get() = params.padding.start

    /**
     * Верхний внутренний оступ разметки.
     */
    @get:Px
    val paddingTop: Int
        get() = params.padding.top

    /**
     * Првый внутренний оступ разметки.
     */
    @get:Px
    val paddingEnd: Int
        get() = params.padding.end

    /**
     * Нижний внутренний оступ разметки.
     */
    @get:Px
    val paddingBottom: Int
        get() = params.padding.bottom

    /**
     * Ширина всей разметки.
     */
    @get:Px
    val width: Int
        get() = if (isVisible) {
            maxOf(paddingStart + layout.width + paddingEnd, params.minWidth)
        } else 0

    /**
     * Высота всей разметки.
     */
    @get:Px
    val height: Int
        get() = if (isVisible && width != 0) {
            maxOf(paddingTop + layout.height + paddingBottom, params.minHeight)
        } else 0

    /**
     * Базовая линия текстовой разметки.
     */
    @get:Px
    val baseline: Int
        get() = paddingTop + layout.getLineBaseline(0)

    /**
     * Получить ожидаемую ширину разметки для текста [text].
     */
    @Px
    fun getDesiredWidth(text: CharSequence): Int =
        paddingStart + params.paint.getTextWidth(text) + paddingEnd

    /**
     * Настроить разметку.
     * Если параметры изменятся - разметка будет построена при следующем обращении.
     *
     * Использовать для изменения закэшированных параметров [params],
     * созданных при инициализации или переданных ранее,
     * кэш статичной разметки при этом будет обновлен по новым параметрам при следующем обращении.
     *
     * @param config настройка параметров текстовой разметки.
     * @return true, если параметры изменились.
     */
    fun configure(
        config: TextLayoutConfig
    ): Boolean {
        val oldTextSize = params.paint.textSize
        val oldParams = params.copy()

        config.invoke(params)

        val isTextSizeChanged = oldTextSize != params.paint.textSize
        return (oldParams != params || isTextSizeChanged).also { isChanged ->
            if (isChanged) isLayoutChanged = true
        }
    }

    /**
     * Построить разметку.
     *
     * Использовать для принудительного построения разметки на базе параметров [params],
     * при этом настройка [config] будет применена перед построением новой разметки.
     *
     * @param config настройка параметров текстовой разметки.
     * @return true, если разметка изменилась.
     */
    fun buildLayout(
        config: TextLayoutConfig? = null
    ): Boolean =
        config?.let { configure(it) }
            .also { if (isVisible) layout }
            ?: false

    /**
     * Обновить внутренние отступы.
     *
     * @return true, если отступы изменились.
     */
    fun updatePadding(
        start: Int = paddingStart,
        top: Int = paddingTop,
        end: Int = paddingEnd,
        bottom: Int = paddingBottom
    ): Boolean = with(params) {
        val oldPadding = padding
        padding = TextLayoutPadding(start, top, end, bottom)
        isLayoutChanged = oldPadding != padding || isLayoutChanged
        oldPadding != padding
    }

    /**
     * Разместить разметку на позициях [left] [top].
     */
    fun layout(@Px left: Int, @Px top: Int) {
        rect.set(
            left,
            top,
            left + width,
            top + height
        )
        textPos = left + paddingStart.toFloat() to top + paddingTop.toFloat()
        inspectHelper?.updatePositions()
    }

    /**
     * Нарисовать разметку.
     *
     * Рисуется именно кэш текстовой разметки [cachedLayout],
     * чтобы не допускать построения layout на [View.onDraw].
     */
    fun draw(canvas: Canvas) {
        cachedLayout?.let { layout ->
            if (!isVisible || params.text.isEmpty()) return
            inspectHelper?.draw(canvas)
            canvas.withTranslation(textPos.first, textPos.second) {
                layout.draw(this)
            }
        }
    }

    /**
     * Обновить разметку по набору параметров [params].
     * Если ширина в [params] не задана, то будет использована ширина текста.
     * Созданная разметка помещается в кэш [cachedLayout].
     */
    private fun updateStaticLayout(): Layout =
        StaticLayoutConfigurator.createStaticLayout(params.text, params.paint) {
            width = params.textWidth
            alignment = params.alignment
            ellipsize = params.ellipsize
            includeFontPad = params.includeFontPad
            maxLines = params.maxLines
            maxHeight = params.textMaxHeight
            highlights = params.highlights
            canContainUrl = params.canContainUrl
        }.also {
            isLayoutChanged = false
            cachedLayout = it
        }

    /**
     * Параметры для создания текстовой разметки [Layout] в [TextLayout].
     *
     * @property text текста разметки.
     * @property paint краска текста.
     * @property layoutWidth ширина разметки. Null -> WRAP_CONTENT.
     * @property alignment мод выравнивания текста.
     * @property ellipsize мод сокращения текста.
     * @property includeFontPad включить стандартные отступы шрифта.
     * @property maxLines максимальное количество строк.
     * @property isVisible состояние видимости разметки.
     * @property padding внутренние отступы разметки.
     * @property highlights модель для выделения текста.
     * @property minWidth минимальная ширина разметки.
     * @property minHeight минимальная высота разметки.
     * @property maxWidth максимальная ширина разметки.
     * @property maxHeight максимальная высота разметки с учетом [padding]. Необходима для автоматического подсчета [maxLines].
     * @property isVisibleWhenBlank мод скрытия разметки при пустом тексте, включая [padding].
     * @property canContainUrl true, если строка может содержать url. Влияет на точность сокращения текста
     * и скорость создания [StaticLayout]. (Использовать только для [maxLines] > 1, когда текст может содержать ссылки)
     */
    data class TextLayoutParams(
        var text: CharSequence = StringUtils.EMPTY,
        var paint: TextPaint = TextPaint(ANTI_ALIAS_FLAG),
        @Px var layoutWidth: Int? = null,
        var alignment: Alignment = Alignment.ALIGN_NORMAL,
        var ellipsize: TruncateAt = TruncateAt.END,
        var includeFontPad: Boolean = true,
        var maxLines: Int = 1,
        var isVisible: Boolean = true,
        var padding: TextLayoutPadding = TextLayoutPadding(),
        var highlights: TextHighlights? = null,
        @Px var minWidth: Int = 0,
        @Px var minHeight: Int = 0,
        @Px var maxWidth: Int? = null,
        @Px var maxHeight: Int? = null,
        var isVisibleWhenBlank: Boolean = true,
        var canContainUrl: Boolean = false
    ) {

        /**
         * Ширина текста.
         */
        @get:Px
        internal val textWidth: Int
            get() {
                val maxWidth = maxWidth
                val layoutWidth = layoutWidth
                val horizontalPadding = padding.start + padding.end
                return when {
                    maxWidth == null -> {
                        layoutWidth?.let { maxOf(it - horizontalPadding, 0) }
                            ?: paint.getTextWidth(text)
                    }
                    maxWidth > 0 -> {
                        val textWidth = paint.getTextWidth(text)
                        val availableTextWidth = maxOf(maxWidth - horizontalPadding, 0)
                        textWidth.takeIf { textWidth < availableTextWidth }
                            ?: availableTextWidth
                    }
                    else -> 0
                }
            }

        /**
         * Максимальная высота текста.
         */
        @get:Px
        internal val textMaxHeight: Int?
            get() = maxHeight?.let { it - padding.top - padding.bottom }
    }

    /**
     * Параметры отступов текстовой разметки [Layout] в [TextLayout].
     */
    data class TextLayoutPadding(
        @Px val start: Int = 0,
        @Px val top: Int = 0,
        @Px val end: Int = 0,
        @Px val bottom: Int = 0
    )

    /**
     * Вспомогательный класс для отладки текстовой разметки.
     * Позволяет отображать границы [TextLayout], а также внутренние отступы.
     * Может оказаться крайне полезным на этапе интеграции [TextLayout].
     */
    private inner class InspectHelper {

        /**
         * Краска линии границы по периметру [TextLayout].
         */
        val borderPaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            style = Paint.Style.STROKE
        }

        /**
         * Краска внутренних отступов [TextLayout].
         */
        val paddingPaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = Color.YELLOW
            style = Paint.Style.FILL
        }
        val borderPath = Path()
        val borderRectF = RectF()
        val paddingPath = Path()
        val textBackgroundPath = Path()

        /**
         * Обновить закэшированные позиции границ разметки.
         */
        fun updatePositions() {
            borderPath.reset()
            textBackgroundPath.reset()
            paddingPath.reset()

            borderRectF.set(
                left.toFloat() + ONE_PX,
                top.toFloat() + ONE_PX,
                right.toFloat() - ONE_PX,
                bottom.toFloat() - ONE_PX,
            )
            borderPath.addRect(borderRectF, Path.Direction.CW)

            textBackgroundPath.addRect(
                textPos.first,
                textPos.second,
                textPos.first + layout.width,
                textPos.second + layout.height,
                Path.Direction.CW
            )
            paddingPath.addRect(borderRectF, Path.Direction.CW)
            paddingPath.op(textBackgroundPath, Path.Op.DIFFERENCE)
        }

        /**
         * Нарисовать отладочные границы разметки.
         */
        fun draw(canvas: Canvas) {
            if (isVisible) {
                canvas.drawPath(paddingPath, paddingPaint)
                canvas.drawPath(borderPath, borderPaint)
            }
        }
    }

    /**
     * Модель внутреннего состояния [TextLayout].
     * @see TextLayout.params
     * @see TextLayout.cachedLayout
     * @see TextLayout.isLayoutChanged
     * @see TextLayout.textPos
     */
    internal data class TextLayoutState(
        val params: TextLayoutParams,
        val cachedLayout: Layout?,
        val isLayoutChanged: Boolean,
        val textPos: Pair<Float, Float>
    )
}

/**
 * Настройка для параметров [TextLayout.TextLayoutParams].
 */
typealias TextLayoutConfig = TextLayoutParams.() -> Unit

/**
 * Мод активации отладочных границ [TextLayout].
 * При включении дополнительно будут нарисованы границы вокруг [TextLayout], а также внутренние отступы.
 */
private const val isInspectMode = false
private const val ONE_PX = 1