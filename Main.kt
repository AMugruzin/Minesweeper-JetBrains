import java.awt.Point
import java.lang.Integer.max
import kotlin.math.min
import kotlin.random.Random

enum class MINESOPERATING() {;  // used to handle main menu and choose default values
    companion object {
        val greetMes = "How many mines do you want on the field? "
        val wrongInput = "Wrong Input, try again."
        var input = listOf("")
        var mine = "X"
        var safe = "/"
        var height = 9  // won't scale if more than 9
        var width = 9   // won't scale if more than 9
        var minesNumber: Int = 0
        var unrevealedCell = "."
        var markedCell = "*"
        var status = "running"
        var gameType = 0 // "gg" for type 1, 0 by default
        var bigVisual = false

        fun askForMineNum() {
            print(greetMes)
            input = readLine()!!.split(" ")
            when {
                isN(input[0]) -> { minesNumber = if (input[0].toInt() < height * width - 9) input[0].toInt() else height * width - 9 }
                else -> {
                    println(wrongInput)
                    askForMineNum() }
            }
        }
        fun startGame() {
            val nf = PlayField(mine, safe, width, height, minesNumber, unrevealedCell, markedCell, bigVisual)
            nf.start()
        }
        fun isN(string: String): Boolean {//  check if it is a Number
            return string.matches("-?\\d+(\\.\\d+)?".toRegex())
        }
        fun gg() {
            fun askForMineSymb() {
                print("What symbol do u want to use as Mine Symbol: ")
                input = readLine()!!.split(" ")
                when {
                    input[0].length == 1 -> {mine = input[0]}
                    else -> {
                        println(wrongInput)
                        askForMineSymb()}
                }
            }
            fun askForSafeCell() {
                print("What symbol do u want to use as SafeCell Symbol: ")
                input = readLine()!!.split(" ")
                when {
                    input[0].length == 1 -> { safe = input[0]}
                    else -> {
                        println(wrongInput)
                        askForSafeCell() }
                }
            }
            fun askForUnrevealedCell() {
                print("What symbol do u want to use as UnrevealedCell Symbol: ")
                input = readLine()!!.split(" ")
                when {
                    input[0].length == 1 -> { unrevealedCell = input[0]}
                    else -> {
                        println(wrongInput)
                        askForUnrevealedCell() }
                }
            }
            fun askForMarkedCell() {
                print("What symbol do u want to use as MarkedCell Symbol: ")
                input = readLine()!!.split(" ")
                when {
                    input[0].length == 1 -> { markedCell = input[0]}
                    else -> {
                        println(wrongInput)
                        askForMarkedCell() }
                }
            }
            fun askForWidth() {
                print("Width > How wide playfield do you want : ")
                input = readLine()!!.split(" ")
                when {
                    isN(input[0]) -> { width = input[0].toInt() }
                    else -> {
                        println(wrongInput)
                        askForWidth() }
                }
            }
            fun askForHeight() {
                print("Height > How tall playfield do you want : ")
                input = readLine()!!.split(" ")
                when {
                    isN(input[0]) -> { height = input[0].toInt() }
                    else -> {
                        println(wrongInput)
                        askForHeight() }
                }
            }
            fun askForBiggerVisual() {
                print(  "╬═════╬═════╬\n" +
                        "║     ║  M  ║\n" +
                        "╬═════╬═════╬\n" +
                        "║  ?  ║  X  ║\n" +
                        "╬═════╬═════╬\n" +
                        "Do you want to have bigger visual like that? Y / N : ")
                input = readLine()!!.split(" ")
                when {
                    input[0].toLowerCase() == "y" -> {
                        bigVisual = true
                    }
                    input[0].toLowerCase() == "n" -> {
                        bigVisual = false
                    }
                    else -> {
                        println(wrongInput)
                        askForBiggerVisual() }
                }
            }
            askForBiggerVisual()
            askForMineSymb()
            askForSafeCell()
            askForUnrevealedCell()
            askForMarkedCell()
            askForWidth()
            askForHeight()
            askForMineNum()
            startGame()
        }

        fun run() {
            print(greetMes)
            input = readLine()!!.split(" ")
            when {
                input[0].toLowerCase() == "exit" -> { status = "break" ; return}
                input[0].toLowerCase() == "gg" -> {
                    gameType = 1
                    gg()
                }
                isN(input[0]) && gameType == 0 -> {
                    minesNumber = if ( input[0].toInt() < height * width - 9 ) input[0].toInt() else height * width - 9
                    startGame() }
                else -> {
                    println(wrongInput)
                    run() }
            }
        }
    }
}

class Cell(var visual: String, var isMine: Boolean, var isVisible: Boolean, var isMarked: Boolean, var isAvailabe: Boolean)

class PlayField(val mineCell : String, val safeCell : String, val width: Int, val height: Int,
                val minesNumber : Int, val unrevealedCell : String, val markedCell : String,
                val bigVisual : Boolean) {
    val playfieldSize = width * height
    var leftoverCells = playfieldSize
    var playField = Array(height) { Array(width) { Cell(safeCell, false, false, false, true)} }
    var markedMines = 0
    val leftBorder = if (bigVisual) { "╬═════" } else " │"
    val midBorder = "║"
    val rightBorder =if (bigVisual) { "╬" } else "│"
    val delim = if (bigVisual) { "═════" } else "—"
    val inputMistake = "Wrong command. You can use 'mine' and 'free' commands to set mine or free cell.\n" +
            "X_axis_number Y_axis_number command\n" +
            "Example : 1 1 free\n" +
            "Example : 2 3 mine"

    private fun visibleAround(i: Int, j: Int) {
        fun visibleAroundChecker(y: Int, x: Int) {
            if (!playField[y][x].isMine) {
                playField[y][x].isVisible = true
                playField[y][x].isMarked = false
            }
        }
        if (i - 1 >= 0 && j - 1 >= 0) visibleAroundChecker(i - 1, j - 1)
        if (i - 1 >= 0) visibleAroundChecker(i - 1, j)
        if (i - 1 >= 0 && j + 1 < width) visibleAroundChecker(i - 1, j + 1)
        if (j - 1 >= 0) visibleAroundChecker(i, j - 1)
        if (j + 1 < width) visibleAroundChecker(i, j + 1)
        if (i + 1 < height && j - 1 >= 0) visibleAroundChecker(i + 1, j - 1)
        if (i + 1 < height) visibleAroundChecker(i + 1, j)
        if (i + 1 < height && j + 1 < width) visibleAroundChecker(i + 1, j + 1)
    }
    fun checkEverything() {
        for (y in playField.indices) {
            for (x in playField[y].indices) {
                if (playField[y][x].isVisible && playField[y][x].visual == safeCell) {
                    visibleAround(y, x)
                }
            }
        }
    }
    fun countLeftOverCells() {
        var counter = 0
        for (y in playField.indices) {
            for (x in playField[y].indices) {
                if (playField[y][x].isVisible) {
                    counter++
                }
            }
        }
        leftoverCells = playfieldSize - counter
    }
    fun countMarkedMines() {
        var counter = 0
        for (y in playField.indices) {
            for (x in playField[y].indices) {
                if (playField[y][x].isMarked) {
                    counter++
                }
            }
        }
        markedMines = counter
    }
    fun commands(xWidth: Int, yHeight: Int, command: String) {
        val y = yHeight - 1
        val x = xWidth - 1
        val alreadyDiscovered = "It's already discovered!"
        if( xWidth > width || xWidth <= 0 || yHeight > height || yHeight <= 0) {
            println("Input numbers  should be equal or less than WIDTH and Height of gamefield and more than 0")
            operate()
        }
        when (command) {
            "free" -> { if (playField[y][x].isVisible == false) {
                if (playField[y][x].visual == mineCell) {  // fail condition, if u explore mined field = gameover
                    playField[y][x].isVisible = true
                    playField[y][x].isMarked = false
                    printField()
                    println("You stepped on a mine and failed!")
                    return
                }
                playField[y][x].isVisible = true
                playField[y][x].isMarked = false
                if (leftoverCells == playfieldSize) {
                    randomizeBoard(y, x)
                    setMinesNumbersOnTheField()
                }
                repeat(width){ checkEverything() }
                countLeftOverCells()
            } else if (playField[y][x].isVisible == true) {
                println(alreadyDiscovered)
                operate() }
            }
            "mine" -> { if (!playField[y][x].isVisible) {
                playField[y][x].isMarked = !playField[y][x].isMarked
                countMarkedMines()
                repeat(width){ checkEverything() }
                countLeftOverCells()
            } else {
                println(alreadyDiscovered)
                operate()
            }

            }
            "exit" -> {
                MINESOPERATING.status = "break"
                return }
            else -> {
                println(inputMistake)
                operate()
            }
        }
        winCondition(0)
    }
    fun winCondition(cheat: Int) {
        if (markedMines == minesNumber || leftoverCells == minesNumber) {
            printField()
            println("Congratulations! You found all the mines!")
            MINESOPERATING.status = "break"
        } else if (cheat == 1) {
            if (width >= 9 && height >= 9)printRose()
            printField()
            println("Sladkii zayc ti! Bulik :D.")
            MINESOPERATING.status = "break"
        } else { printField() ; operate() }
    }
    fun randomizeBoard(i: Int, j: Int) {
        if (i - 1 >= 0 && j - 1 >= 0)       playField[i - 1][j - 1].isAvailabe = false
        if (i - 1 >= 0)                     playField[i - 1][j].isAvailabe = false
        if (i - 1 >= 0 && j + 1 < width)    playField[i - 1][j + 1].isAvailabe = false
        if (j - 1 >= 0)                     playField[i][j - 1].isAvailabe = false
        if (j + 1 < width)                  playField[i][j + 1].isAvailabe = false
        if (i + 1 < height && j - 1 >= 0)   playField[i + 1][j - 1].isAvailabe = false
        if (i + 1 < height)                 playField[i + 1][j].isAvailabe = false
        if (i + 1 < height && j + 1 < width) playField[i + 1][j + 1].isAvailabe = false
        var minesCounter = 0
        while (minesCounter < minesNumber) {
            val rndmY = Random.nextInt(height)
            val rndmX = Random.nextInt(width)
            if (!playField[rndmY][rndmX].isMine && !playField[rndmY][rndmX].isVisible && playField[rndmY][rndmX].isAvailabe) {
                playField[rndmY][rndmX].isMine = true
                //playField[rndm][rndm].isAvailabe = false
                playField[rndmY][rndmX].visual = mineCell
                minesCounter++
            }

        } }
    fun setMinesNumbersOnTheField () {
        fun getNumberOfMines(pos: Point): Int {
            var mines = 0
            val yRage = max(0, pos.y - 1)..min(pos.y + 1, playField.lastIndex)
            val xRage = max(0, pos.x - 1)..min(pos.x + 1, playField[pos.y].lastIndex)
            for (y in yRage) {
                for (x in xRage) {
                    if (playField[y][x].isMine) {
                        mines++
                    }
                }
            }
            if (playField[pos.y][pos.x].isMine) {
                mines--
            }
            return mines
        }
        for (y in playField.indices) {
            for (x in playField[y].indices) {
                val mines = getNumberOfMines(Point(x, y))
                (if (!playField[y][x].isMine && mines > 0 && !playField[y][x].isVisible) {
                    playField[y][x].visual = mines.toString()
                } else playField[y][x])
            }
        }
    }

    fun printField() {
        val bigVisTopLine = " " + delim + (rightBorder + delim).repeat(width) + rightBorder
        val bigVisBorderLine = "      " + midBorder + ("     $midBorder").repeat(width)
        fun firstLine() {
            if (bigVisual) {
                print(bigVisBorderLine)
                print("\n      ")
            } else print(leftBorder)
            for (i in 1..width) {
                if (bigVisual) {
                    print(if (i in 10..99) {"$midBorder  $i "} else {"$midBorder  $i  "})
                } else { print("$i")}
            }
            if (bigVisual) {
                print(midBorder)
                print("\n")
                print(bigVisTopLine)
                print("\n")
            } else {
                print(rightBorder)
                println()
            }
        }
        fun borderLine() {
            print(leftBorder)
            for (i in 1..width) {
                print(delim)
            }
            println(rightBorder)
        }
        println()
        firstLine()
        if (!bigVisual) borderLine()
        for (y in playField.indices) {
            if (bigVisual) print(if (y + 1 in 10..99) { "   ${y + 1} $midBorder" } else "   ${y + 1}  $midBorder" ) else print("${y + 1}│")  // col numbers
            for (x in playField[y].indices) {
                if (bigVisual) {
                    if(playField[y][x].isVisible) print("  ${playField[y][x].visual}  $midBorder")
                    if (playField[y][x].isMarked) print("  $markedCell  $midBorder")
                    if (!playField[y][x].isVisible && !playField[y][x].isMarked) print("  $unrevealedCell  $midBorder")
                } else {
                    if(playField[y][x].isVisible) print(playField[y][x].visual)
                    if (playField[y][x].isMarked) print(markedCell)
                    if (!playField[y][x].isVisible && !playField[y][x].isMarked) print(unrevealedCell)
                }
            }
            if (bigVisual) {
                print("\n")
                print(bigVisTopLine)
                print("\n")
            } else println(rightBorder)
        }
        if (!bigVisual) borderLine()
    }

    fun operate() {
        print("Set/unset mines marks or claim a cell as free: ")
        val line = readLine()!!.split(" ").toList()
        if (line[0].toLowerCase() == "easter_egg") {
            println("Hello, Bula!")
            easter()
        } else if (line[0].toLowerCase() == "exit") {
            MINESOPERATING.status = "break"
            return
        } else if (line[0].matches("-?\\d+(\\.\\d+)?".toRegex()) && line[1].matches("-?\\d+(\\.\\d+)?".toRegex())/* && (line[2].toLowerCase() == "free" || line[2].toLowerCase() == "mine")*/) {
            commands(line[0].toInt(), line[1].toInt(), line[2])
        } else {
            println(inputMistake)
            printField()
            operate()}
    }

    fun start(){
        printField()
        operate()
    }
/*
* Easter is unfinished function for my GF and also its debug tool/cheatmenu
* PrintRose prints simple flower instead of minefield and finish the game via cheatmenu
*/
    fun easter() {
        val greeting = "You can try this commands: 'compliment' , 'surprise' , 'motherlode' , 'howBbigIsLove' or 'back' to previous menu!\n" +
                "Enter your command : "
        print(greeting)
        val easterinput= readLine().toString().toLowerCase().trim()
        when (easterinput) {
            "compliment" -> {}
            "surprise" -> {}
            "motherlode" -> {winCondition(1)}
            "howbigislove" -> { }
            "exit" -> {
                MINESOPERATING.status = "break"
                return }
            "back" -> { printField()
                operate() }
            else -> { print("Wrong command, but...\n")
                easter()
            }
        }
    }
    fun printRose() { // easter_egg motherlode
        for (y in playField.indices) {
            for (x in playField[y].indices) {
                playField[y][x].isVisible = true
            }
        }
        for (y in playField.indices) {
            for (x in playField[y].indices) {
                playField[y][x].visual = " "
            }
        }
        playField[1][3].visual = "_"    ; playField[1][3].isVisible = true
        playField[1][4].visual = " "    ; playField[1][4].isVisible = true
        playField[1][5].visual = "_"    ; playField[1][5].isVisible = true
        playField[2][2].visual = "("    ; playField[2][2].isVisible = true
        playField[2][3].visual = "_"    ; playField[2][3].isVisible = true
        playField[2][4].visual = "\\"   ; playField[2][4].isVisible = true
        playField[2][5].visual = "_"    ; playField[2][5].isVisible = true
        playField[2][6].visual = ")"    ; playField[2][6].isVisible = true
        playField[3][1].visual = "("    ; playField[3][1].isVisible = true
        playField[3][2].visual = "_"    ; playField[3][2].isVisible = true
        playField[3][3].visual = "_"    ; playField[3][3].isVisible = true
        playField[3][4].visual = "<"    ; playField[3][4].isVisible = true
        playField[3][5].visual = "_"    ; playField[3][5].isVisible = true
        playField[3][6].visual = "{"    ; playField[3][6].isVisible = true
        playField[3][7].visual = "}"    ; playField[3][7].isVisible = true
        playField[4][2].visual = "("    ; playField[4][2].isVisible = true
        playField[4][3].visual = "_"    ; playField[4][3].isVisible = true
        playField[4][4].visual = "/"    ; playField[4][4].isVisible = true
        playField[4][5].visual = "_"    ; playField[4][5].isVisible = true
        playField[4][6].visual = ")"    ; playField[4][6].isVisible = true
        playField[5][1].visual = "|"    ; playField[5][1].isVisible = true
        playField[5][2].visual = "\\"   ; playField[5][2].isVisible = true
        playField[5][4].visual = "|"    ; playField[5][4].isVisible = true
        playField[6][2].visual = "\\"   ; playField[6][2].isVisible = true
        playField[6][3].visual = "\\"   ; playField[6][3].isVisible = true
        playField[6][4].visual = "|"    ; playField[6][4].isVisible = true
        playField[6][6].visual = "/"    ; playField[6][6].isVisible = true
        playField[6][7].visual = "|"    ; playField[6][7].isVisible = true
        playField[7][3].visual = "\\"   ; playField[7][3].isVisible = true
        playField[7][4].visual = "|"    ; playField[7][4].isVisible = true
        playField[7][5].visual = "/"    ; playField[7][5].isVisible = true
        playField[7][6].visual = "|"    ; playField[7][6].isVisible = true
        playField[8][4].visual = "|"    ; playField[8][4].isVisible = true
        playField[8][5].visual = "/"    ; playField[8][5].isVisible = true
    }
}

fun main(args : Array<String>) {
    //val greetMes = "How many mines do you want on the field? "
    //print(greetMes)
    while (MINESOPERATING.status == "running") {
        MINESOPERATING.run()
    }
}
