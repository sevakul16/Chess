package com.example.chess

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.io.PrintWriter
import java.util.concurrent.Executors

const val TAG = "MainActivity"


class MainActivity : AppCompatActivity(), ChessDelegate {
    private val PORT:Int = 50000
    private lateinit var chessView: ChessView
    private var printWriter: PrintWriter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        chessView = findViewById<ChessView>(R.id.chess_view)
        chessView.chessDelegate = this

        findViewById<Button>(R.id.reset_button).setOnClickListener {
            ChessGame.reset()
            chessView.invalidate()
        }
    }


    override fun pieceAt(square: Square): ChessPiece? = ChessGame.pieceAt(square)


    override fun movePiece(from: Square, to:Square) {
        findViewById<TextView>(R.id.textView3).setVisibility(View.INVISIBLE)
        ChessGame.movePiece(from, to)
        chessView.invalidate()
        printWriter?.let {
            val moveStr = "${from.col}, ${from.row}, ${to.col}, ${to.row}"
            Executors.newSingleThreadExecutor().execute() {
                it.println(moveStr)
            }
        }

    }
}