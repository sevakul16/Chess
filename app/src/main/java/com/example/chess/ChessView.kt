package com.example.chess

import android.view.View
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import kotlin.math.min

class ChessView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val scaleFactor = .9f
    private var cellSide = 130f
    private var originX = 20f
    private var originY = 200f
    private val paint = Paint()
    private val imageResIDs = setOf(
            R.drawable.b_bishop,
            R.drawable.b_king,
            R.drawable.b_knight,
            R.drawable.b_pawn,
            R.drawable.b_queen,
            R.drawable.b_rook,
            R.drawable.w_bishop,
            R.drawable.w_king,
            R.drawable.w_knight,
            R.drawable.w_pawn,
            R.drawable.w_queen,
            R.drawable.w_rook
    )

    private val bitmaps = mutableMapOf<Int,Bitmap>()
    private var fromCol:Int = -1
    private var fromRow:Int = -1
    private var movingPieceX = -1f
    private var movingPieceY = -1f
    private var movingPieceBitmap: Bitmap? = null
    private var movingPiece: ChessPiece? = null

    var chessDelegate: ChessDelegate? = null

    init {
        loadBitmaps()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val smaller = min(widthMeasureSpec,heightMeasureSpec)
        setMeasuredDimension(smaller,smaller)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?:return
            val chessBoardSide = min(width, height) * scaleFactor
            cellSide = chessBoardSide / 8f
            originX = (width - chessBoardSide) / 2f
            originY = (height - chessBoardSide) /2f
        drawChessboard(canvas)
        drawPieces(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?: return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                fromCol = ((event.x - originX) / cellSide).toInt()
                fromRow = 7 -((event.y - originY) / cellSide).toInt()

               // movingPiece = chessDelegate?.pieceAt(fromCol,fromRow)
               // movingPieceBitmap = bitmaps[movingPiece!!.resID]

                chessDelegate?.pieceAt(Square(fromCol, fromRow))?.let {
                    movingPiece = it
                    movingPieceBitmap = bitmaps[it.resID]

                }
               // Log.d(TAG, "down at ($col, $row) ")
            }
            MotionEvent.ACTION_MOVE -> {
                movingPieceX = event.x
                movingPieceY = event.y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                val col = ((event.x - originX) / cellSide).toInt()
                val row = 7 -((event.y - originY) / cellSide).toInt()
                if (fromCol != col || fromRow != row) {
                    Log.d(TAG, "from ($fromCol, $fromRow) to ($col, $row) ")
                    chessDelegate?.movePiece(Square(fromCol, fromRow), Square(col, row))
                }
                movingPiece = null
                movingPieceBitmap = null
                invalidate()
            }
        }
        return  true
    }

    private fun drawPieces(canvas: Canvas) {
        for (row in 0 until 8){
            for (col in 0 until 8){
                chessDelegate?.pieceAt(Square(col, row))?.let {piece ->
                    if (piece !=movingPiece) {
                        drawPieceAt(canvas, col, row, piece.resID)
                    }
                }
            }
        }

        movingPieceBitmap?.let {
            canvas.drawBitmap(it, null, RectF(movingPieceX - cellSide/2,movingPieceY-cellSide/2,movingPieceX+cellSide/2,movingPieceY + cellSide/2),paint)

        }
    }

    private fun drawPieceAt(canvas: Canvas, col: Int, row: Int, resID: Int) =
        canvas.drawBitmap(bitmaps[resID]!!, null, RectF(originX + col * cellSide,originY + (7 - row) * cellSide,originX + (col + 1) * cellSide,originY + ((7 - row) + 1) * cellSide),paint)



    private fun loadBitmaps() =
        imageResIDs.forEach {imageResIDs ->
            bitmaps[imageResIDs] = BitmapFactory.decodeResource(resources,imageResIDs)
        }


    private fun drawChessboard(canvas: Canvas) {
        for (row in 0 until 8) {
            for (col in 0 until 8) {

                drawSquareAt(canvas, row, col,(row+col) % 2 ==1 )
            }
        }
    }
    private fun drawSquareAt (canvas: Canvas, col: Int, row:Int, isDark:Boolean) {
        paint.color = if (isDark) Color.DKGRAY else Color.LTGRAY
        canvas.drawRect(originX + col * cellSide, originY+row*cellSide, originX + (col + 1) * cellSide, originY +(row+1)* cellSide, paint)

    }
}
