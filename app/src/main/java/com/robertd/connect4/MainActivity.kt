package com.robertd.connect4

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val board: Array<Array<Button?>> = Array(6) { arrayOfNulls(7) }
    private var player1Turn: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createBoard()

    }

    private fun createBoard() {
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)

        for (row in 0 until 6) {
            for (col in 0 until 7) {
                val button = Button(this)
                button.text = " "
                button.setBackgroundResource(R.drawable.empty_cell)
                button.setOnClickListener {
                    dropPiece(it)
                }

                val layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 220
                    columnSpec = GridLayout.spec(col, 1f)
                    rowSpec = GridLayout.spec(row, 1f)
                    setMargins(2, 2, 2, 2)
                }

                gridLayout.addView(button, layoutParams)
                board[row][col] = button
            }
        }

        setPlayerTurnText()
    }

    private fun setPlayerTurnText() {
        val playersTurn: TextView = findViewById(R.id.players_turn)
        if (player1Turn) {
            playersTurn.text = "Player 1's turn (Red Cross)"
        } else {
            playersTurn.text = "Player 2's turn (Blue Circle)"
        }
    }


    fun dropPiece(view: View) {
        val pressedButton = view as Button
        var col = -1
        var lowestUnoccupiedRow = -1

        outerLoop@ for (column in 0 until 7) {
            for (row in 5 downTo 0) {
                val buttonBeingChecked = board[row][column]
                if (pressedButton === buttonBeingChecked) {
                    col = column
                    lowestUnoccupiedRow = findLowestUnoccupiedRow(col)
                    break@outerLoop
                }
            }
        }

        if (col == -1 || lowestUnoccupiedRow == -1) {
            return  // Invalid button
        }

        if (board[lowestUnoccupiedRow][col]?.text == " ") {
            board[lowestUnoccupiedRow][col]?.text = if (player1Turn) "X" else "O"
            board[lowestUnoccupiedRow][col]?.setTextColor(if (player1Turn) Color.RED else Color.BLUE)
            val textSizeDP = resources.getDimensionPixelSize(R.dimen.button_text_size)
            board[lowestUnoccupiedRow][col]?.setTextSize(
                TypedValue.COMPLEX_UNIT_DIP,
                textSizeDP.toFloat()
            )

            // Check for a win
            if (checkWin(lowestUnoccupiedRow, col)) {
                val winner = if (player1Turn) "Player 1" else "Player 2"
                showToast("Congratulations, $winner wins!")
                disableBoard()
            }

            // Check for a draw
            if (checkDraw()) {
                showToast("It's a draw!")
                disableBoard()
            }

            player1Turn = !player1Turn
            setPlayerTurnText()
        }
    }


    private fun checkWin(row: Int, col: Int): Boolean {
        // Check horizontally
        var count = 0
        for (c in col - 3..col + 3) {
            if (c in 0..6 && board[row][c]?.text == board[row][col]?.text) {
                count++
                if (count >= 4) return true
            } else {
                count = 0
            }
        }

        // Check vertically
        count = 0
        for (r in row - 3..row + 3) {
            if (r in 0..5 && board[r][col]?.text == board[row][col]?.text) {
                count++
                if (count >= 4) return true
            } else {
                count = 0
            }
        }

        // Check diagonally (top-left to bottom-right)
        count = 0
        for (i in -3..3) {
            val r = row + i
            val c = col + i
            if (r in 0..5 && c in 0..6 && board[r][c]?.text == board[row][col]?.text) {
                count++
                if (count >= 4) return true
            } else {
                count = 0
            }
        }

        // Check diagonally (top-right to bottom-left)
        count = 0
        for (i in -3..3) {
            val r = row - i
            val c = col + i
            if (r in 0..5 && c in 0..6 && board[r][c]?.text == board[row][col]?.text) {
                count++
                if (count >= 4) return true
            } else {
                count = 0
            }
        }

        return false
    }

    private fun findLowestUnoccupiedRow(col: Int): Int {
        for (row in 5 downTo 0) {
            if (board[row][col]?.text == " ") {
                return row
            }
        }
        return -1
    }

    private fun checkDraw(): Boolean {
        for (row in 0 until 6) {
            for (col in 0 until 7) {
                if (board[row][col]?.text == " ") {
                    return false
                }
            }
        }
        return true
    }

    private fun disableBoard() {
        for (row in 0 until 6) {
            for (col in 0 until 7) {
                board[row][col]?.isEnabled = false
            }
        }
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    @Suppress("UNUSED_PARAMETER")
    // view is not used but the method expects it as a signature
    fun resetGame(view: View) {
        for (row in 0 until 6) {
            for (col in 0 until 7) {
                board[row][col]?.text = " "
                board[row][col]?.isEnabled = true
                board[row][col]?.setTextColor(Color.BLACK)
            }
        }
        player1Turn = true
    }
}
