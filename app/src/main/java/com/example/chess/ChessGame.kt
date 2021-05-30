 package com.example.chess

 import kotlin.math.abs



 object ChessGame {
    private var piecesBox = mutableSetOf<ChessPiece>()
     var flag = true
    init {
        reset()
    }


    fun clear() {
        piecesBox.clear()
    }

    private fun addPiece(piece: ChessPiece){
        piecesBox.add(piece)
    }

    private fun canMove(from: Square, to: Square):Boolean {

        if (from.col ==to.col && from.row ==to.row) {
            return false
        }

        val movingPiece = pieceAt(from) ?: return false
        when (movingPiece.player) {
            PLayer.WHITE -> if (!flag) return false
            PLayer.BLACK -> if (flag) return false
        }
        when (movingPiece.chessman) {
            Chessman.KNIGHT -> {
                if (canKnightMove(from, to)) flag = !flag
                return canKnightMove(from, to)
            }
            Chessman.ROOK -> {
                if (canRookMove(from, to)) flag = !flag
                return canRookMove(from, to)
            }
            Chessman.BISHOP -> {
                if (canBishopMove(from, to)) flag = !flag
                return canBishopMove(from, to)
            }
            Chessman.QUEEN -> {
                if (canQueenMove(from, to)) flag = !flag
                return canQueenMove(from, to)
            }
            Chessman.KING -> {
                if (canKingMove(from, to)) flag = !flag
                return canKingMove(from, to)
            }
            Chessman.PAWN -> {
                if (canBlackPawnMove(from, to) || canWhitePawnMove(from, to)) flag = !flag
                return when (movingPiece.player) {
                    PLayer.BLACK -> canBlackPawnMove(from, to)
                    PLayer.WHITE -> canWhitePawnMove(from, to)
                }
            }
        }
        return true
    }


     private fun canWhitePawnMove(from: Square, to: Square): Boolean {
         if (from.col == to.col) {
             if (pieceAt(to) == null) {
                 if (from.row == 1) {
                     return to.row == 2 || to.row == 3
                 } else {
                     return to.row == from.row + 1
                 }
             }
         }
         val hitPeace = pieceAt(to) ?: return false
         when (hitPeace.player) {
             PLayer.BLACK -> {
                 if ((to.col == from.col - 1 || to.col == from.col + 1) && to.row == from.row + 1) return true
             }
         }
         return false
     }

     private fun canBlackPawnMove(from: Square, to: Square): Boolean {
         if (from.col == to.col) {
             if (pieceAt(to) == null) {
                 if (from.row == 6) {
                     return to.row == 5 || to.row == 4
                 } else {
                     return to.row == from.row - 1
                 }
            }
         }
         val hitPeace = pieceAt(to) ?: return false
         when (hitPeace.player) {
             PLayer.WHITE -> {
                 if ((to.col == from.col - 1 || to.col == from.col + 1) && to.row == from.row - 1) return true
             }
         }
         return false
     }

     private fun canKingMove(from: Square, to: Square):Boolean {
         if (canQueenMove(from, to)){
             return abs(from.row - to.row) ==1 &&
                     abs(from.col - to.col) == 0|| abs(from.col - to.col) == 1 ||
                     abs(from.col - to.col) ==1 &&
                     abs(from.row - to.row) == 0 || abs(from.row - to.row) ==  1
         }
         return false
     }

    private fun canKnightMove(from: Square, to: Square):Boolean {
        return (abs(from.col - to.col) ==2 && abs(from.row - to.row) ==1 ||
                abs(from.col - to.col) ==1 && abs(from.row - to.row) ==2)
    }

     private fun canRookMove(from: Square, to: Square):Boolean {
        if (from.col == to.col && isClearVerticallyBetween(from, to) ||
                from.row == to.row && isClearHorizontallyBetween(from, to)) {
            return true
        }
         return false
     }

     private fun isClearHorizontallyBetween(from: Square, to: Square):Boolean{
         if (from.row != to.row) return false
         val gap = (abs(from.col - to.col) - 1)
         if (gap==0) return true
         for (i in 1..gap) {
             val nexCol = if (to.col > from.col) from.col + i else from.col -i
             if (pieceAt(Square(nexCol, from.row)) != null){
                 return false
             }
         }
         return true
     }

     private fun isClearVerticallyBetween(from: Square, to: Square):Boolean{
         if (from.col != to.col) return false
         val gap = (abs(from.row - to.row) - 1)
         if (gap==0) return true
         for (i in 1..gap) {
             val nexRow = if (to.row > from.row) from.row + i else from.row -i
             if (pieceAt(Square(from.col, nexRow)) != null){
                 return false
             }
         }
         return true
     }

     private  fun canBishopMove(from: Square, to: Square):Boolean{
         if (abs(from.col - to.col) == abs(from.row - to.row)){
             return isClearDiagonally(from, to)
         }
         return false
     }

     private fun canQueenMove(from: Square, to: Square):Boolean {
         return  canRookMove(from, to) || canBishopMove(from, to)
     }

     private fun isClearDiagonally(from: Square, to: Square): Boolean {
         if (abs(from.col - to.col) != abs(from.row - to.row)) return false
         val gap = abs(from.col - to.col) - 1
         for (i in 1..gap) {
             val nextCol = if (to.col > from.col) from.col + i else from.col - i
             val nextRow = if (to.row > from.row) from.row + i else from.row - i
             if (pieceAt(nextCol, nextRow) != null) {
                 return false
             }
         }
         return true
     }

    fun movePiece(from: Square, to: Square){
        if (canMove(from, to)) {
            movePiece(from.col, from.row, to.col, to.row)
        }
    }

    fun movePiece(fromCol: Int, frowRow: Int, toCol: Int, toRow: Int) {

        if (fromCol == toCol && frowRow == toRow) return

        val movingPiece = pieceAt(fromCol, frowRow) ?:return

        pieceAt(toCol, toRow)?.let {
            if (it.player == movingPiece.player) {
                return
            }
            piecesBox.remove(it)
        }
        piecesBox.remove(movingPiece)
        addPiece(movingPiece.copy(col = toCol, row = toRow))
    }

    fun reset() {
        clear()
        //rooks
        addPiece(ChessPiece(0, 0, PLayer.WHITE, Chessman.ROOK, R.drawable.w_rook))
        addPiece(ChessPiece(0, 7, PLayer.BLACK, Chessman.ROOK, R.drawable.b_rook))
        addPiece(ChessPiece(7, 7, PLayer.BLACK, Chessman.ROOK, R.drawable.b_rook))
        addPiece(ChessPiece(7, 0, PLayer.WHITE, Chessman.ROOK, R.drawable.w_rook))
        //pawns w
        addPiece(ChessPiece(0, 1, PLayer.WHITE, Chessman.PAWN, R.drawable.w_pawn))
        addPiece(ChessPiece(1, 1, PLayer.WHITE, Chessman.PAWN, R.drawable.w_pawn))
        addPiece(ChessPiece(2, 1, PLayer.WHITE, Chessman.PAWN, R.drawable.w_pawn))
        addPiece(ChessPiece(3, 1, PLayer.WHITE, Chessman.PAWN, R.drawable.w_pawn))
        addPiece(ChessPiece(4, 1, PLayer.WHITE, Chessman.PAWN, R.drawable.w_pawn))
        addPiece(ChessPiece(5, 1, PLayer.WHITE, Chessman.PAWN, R.drawable.w_pawn))
        addPiece(ChessPiece(6, 1, PLayer.WHITE, Chessman.PAWN, R.drawable.w_pawn))
        addPiece(ChessPiece(7, 1, PLayer.WHITE, Chessman.PAWN, R.drawable.w_pawn))
        //pawns b
        addPiece(ChessPiece(0, 6, PLayer.BLACK, Chessman.PAWN, R.drawable.b_pawn))
        addPiece(ChessPiece(1, 6, PLayer.BLACK, Chessman.PAWN, R.drawable.b_pawn))
        addPiece(ChessPiece(2, 6, PLayer.BLACK, Chessman.PAWN, R.drawable.b_pawn))
        addPiece(ChessPiece(3, 6, PLayer.BLACK, Chessman.PAWN, R.drawable.b_pawn))
        addPiece(ChessPiece(4, 6, PLayer.BLACK, Chessman.PAWN, R.drawable.b_pawn))
        addPiece(ChessPiece(5, 6, PLayer.BLACK, Chessman.PAWN, R.drawable.b_pawn))
        addPiece(ChessPiece(6, 6, PLayer.BLACK, Chessman.PAWN, R.drawable.b_pawn))
        addPiece(ChessPiece(7, 6, PLayer.BLACK, Chessman.PAWN, R.drawable.b_pawn))
        //knghts
        addPiece(ChessPiece(1, 0, PLayer.WHITE, Chessman.KNIGHT, R.drawable.w_knight))
        addPiece(ChessPiece(6, 0, PLayer.WHITE, Chessman.KNIGHT, R.drawable.w_knight))
        addPiece(ChessPiece(1, 7, PLayer.BLACK, Chessman.KNIGHT, R.drawable.b_knight))
        addPiece(ChessPiece(6, 7, PLayer.BLACK, Chessman.KNIGHT, R.drawable.b_knight))
        //bishops
        addPiece(ChessPiece(2, 0, PLayer.WHITE, Chessman.BISHOP, R.drawable.w_bishop))
        addPiece(ChessPiece(5, 0, PLayer.WHITE, Chessman.BISHOP, R.drawable.w_bishop))
        addPiece(ChessPiece(2, 7, PLayer.BLACK, Chessman.BISHOP, R.drawable.b_bishop))
        addPiece(ChessPiece(5, 7, PLayer.BLACK, Chessman.BISHOP, R.drawable.b_bishop))
        //queen
        addPiece(ChessPiece(3, 0, PLayer.WHITE, Chessman.QUEEN, R.drawable.w_queen))
        addPiece(ChessPiece(3, 7, PLayer.BLACK, Chessman.QUEEN, R.drawable.b_queen))
        //king
        addPiece(ChessPiece(4, 0, PLayer.WHITE, Chessman.KING, R.drawable.w_king))
        addPiece(ChessPiece(4, 7, PLayer.BLACK, Chessman.KING, R.drawable.b_king))

    }

    fun pieceAt(square: Square): ChessPiece?{
        return pieceAt(square.col, square.row)
    }

    private fun pieceAt(col: Int, row: Int) : ChessPiece? {
        for (piece in piecesBox) {
            if (col == piece.col && row == piece.row){
                return piece
            }
        }
        return null
    }

    override fun toString(): String {
        var desc = " \n"
        for (row in 7 downTo 0) {
            desc += "$row"
            for (col in 0 until 8) {
                desc+= " "
                desc += pieceAt(col, row)?.let {
                    val white = it.player == PLayer.WHITE
                    when (it.chessman) {
                        Chessman.KING -> if (white) "k" else "K"
                        Chessman.QUEEN -> if (white) "q" else "q"
                        Chessman.BISHOP -> if (white) "b" else "B"
                        Chessman.ROOK -> if (white) "r" else "R"
                        Chessman.KNIGHT -> if (white) "n" else "N"
                        Chessman.PAWN -> if (white) "p" else "P"
                    }
                }?: "."
            }
            desc += " \n"
        }
        desc += " 0 1 2 3 4 5 6 7"
        return desc
    }

}