package com.example.a2048.data.models.gamesfield

import com.example.a2048.data.models.cell.Cell

class Field: IField {
    private var cellsList: List<Cell>
    private var movedList = ArrayList<Int>(16)

    var isMoved = false

    constructor() {
        cellsList = initList()
    }

    constructor(cells: List<Cell>) {
        cellsList = cells
    }

    private fun initList(): List<Cell> {
        val list = ArrayList<Cell>(16)
            for (i in 0..15) list.add(Cell(i))
        return list
    }

    override fun getCell(x: Int, y: Int) = cellsList[4*y + x]

    override fun getCellsStringList(): List<String>{
        val list = ArrayList<String>()
        for(cell in cellsList){
            with(cell) {
                list.add(if (value != 0) value.toString() else "")
            }
        }
        return list
    }

    override fun getMovedList() = movedList

    override fun addNewFolder(){
        //создает новое значение на поле, по пути сохраняет все суммы и чистит isSum в Cell
        movedList.clear()
        val emptyCells = ArrayList<Cell>()
        cellsList.forEach{cell ->
            if(cell.value == 0) emptyCells.add(cell)
            if(cell.isSum){
                cell.isSum = false
                movedList.add(cell.position)
            }

        }
        if(emptyCells.size!=0) {
            val randomPoint = ((Math.random() * 100).toInt() % emptyCells.size)
            emptyCells[randomPoint].value = ((Math.random() * 100).toInt() % 2 + 1)*2
        }
    }

    fun swipeOrMoveCells(firstCell: Cell, secondCell: Cell){
        if(firstCell.value == 0 && secondCell.value != 0){
            firstCell.value = secondCell.value
            secondCell.value = 0
            if(secondCell.isSum){
                firstCell.isSum = true
                secondCell.isSum = false
            }
            isMoved = true
        } else if(firstCell.value != 0 && firstCell.value == secondCell.value && !secondCell.isSum && !firstCell.isSum){
            firstCell.isSum = true
            firstCell.value *= 2
            secondCell.value = 0
            isMoved = true
        }
    }
}