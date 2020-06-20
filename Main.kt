import java.awt.Point
import java.lang.Integer.max
import kotlin.math.min
import kotlin.random.Random


class Cell(var visual: String, var isMine: Boolean, var isVisible: Boolean, var isMarked: Boolean, var isAvailabe: Boolean)
class PlayField(val mineCell : String, val safeCell : String, val width: Int, val height: Int, val minesNumber : Int) {
    val unrevealedCell = "."
    val playfieldSize = width * height
    var leftoverCells = playfieldSize
    val markedCell = "*"
    var playField = Array(height) { Array(width) { Cell(safeCell, false, false, false, true)} }
    var markedMines = 0
    val leftBorder = " │"
    val rightBorder = "│"
    val delim = "—"
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
            "exit" -> { return }
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
            return
        } else if (cheat == 1) {
            printRose()
            printField()
            println("Sladkii zayc ti! Bulik.")
            return
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
            val rndmY = Random.nextInt(width)
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
        fun firstLine() {
            print(leftBorder)
            for (i in 1..width) {
                print("$i")
            }
            println(rightBorder)
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
        borderLine()
        for (y in playField.indices) {
            print("${y + 1}│")
            for (x in playField[y].indices) {
                if (playField[y][x].isVisible) print(playField[y][x].visual)
                if (playField[y][x].isMarked) print(markedCell)
                if (!playField[y][x].isVisible && !playField[y][x].isMarked) print(unrevealedCell)
            }
            println(rightBorder)
        }
        borderLine()
    }

    fun operate() {
        print("Set/unset mines marks or claim a cell as free: ")
        val line = readLine()!!.split(" ").toList()
        if (line[0].toLowerCase() == "easter_egg") {
            println("Hello, Bula!")
            easter()
        } else if (line[0].toLowerCase() == "exit") {
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
            "exit" -> { return }
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
    val greetMes = "How many mines do you want on the field? "
    print(greetMes)
    var input = readLine()!!
    val mine = "X"
    val safe = "-"
    val height = 9  // won't scale if more than 9
    val width = 9   // won't scale if more than 9
    while (!input.matches("-?\\d+(\\.\\d+)?".toRegex())) {
        print("Try again!\n"); print(greetMes)
        input = readLine()!!
    }
    if (input.matches("-?\\d+(\\.\\d+)?".toRegex())) {
        val minesNumber = if ( input!!.toInt() < height * width ) input!!.toInt() else height * width - 1
        val nf = PlayField(mine, safe, width, height, minesNumber)
        nf.start()
    }
}
