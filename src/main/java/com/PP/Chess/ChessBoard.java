package com.PP.Chess;

import com.PP.Chess.pieces.*;

public class ChessBoard {
	private Piece[][] board;

	public ChessBoard(){
		this.board = new Piece[8][8]; // Tablero 8x8
		setupPieces();
	}

	private void setBoard(){
		//método para inicializar el tablero

		//Colocación de torres
		board[0][0] = new Rook(PieceColor.BLACK, new Position(0,0));
		board[0][7] = new Rook(PieceColor.BLACK, new Position(0,7));
		board[7][0] = new Rook(PieceColor.WHITE, new Position(7,0));
		board[7][7] = new Rook(PieceColor.WHITE, new Position(7,7));

		//Colocación de caballos
		board[0][1] = new Knight(PieceColor.BLACK, new Position(0,1));
		board[0][6] = new Knight(PieceColor.BLACK, new Position(0,6));
		board[7][1] = new Knight(PieceColor.WHITE, new Position(7,1));
		board[7][6] = new Knight(PieceColor.WHITE, new Position(7,6));

		//Colocación de alfiles
		board[0][2] = new Bishop(PieceColor.BLACK, new Position(0,2));
		board[0][5] = new Bishop(PieceColor.BLACK, new Position(0,5));
		board[7][2] = new Bishop(PieceColor.WHITE, new Position(7,2));
		board[7][5] = new Bishop(PieceColor.WHITE, new Position(7,5));

		//Colocación de reinas
		board[0][3] = new Queen(PieceColor.BLACK, new Position(0,3));
		board[7][3] = new Queen(PieceColor.WHITE, new Position(7,3));

		//Colocación de Reyes
		board[0][4] = new King(PieceColor.BLACK, new Position(0,4));
		board[7][4] = new King(PieceColor.WHITE, new Position(7,4));

		//Colocación de Peones

		for(int i=0; i<8; i++){
			board[1][i] = new Pawn(PieceColor.BLACK, new Position(1,i));
			board[6][i] = new Pawn(PieceColor.WHITE, new Position(6,i));
		}
	}

	public void movePiece(Position start, Position end){
		//Chequea si la posición inicial tiene una pieza y si el movimiento es válido
		if(board[start.getRow()][start.getColumn()] != null && board[start.getRow()][start.getColumn()].isValidMove(end, board)) {
			//Realizar el movimiento
			board[end.getRow()][end.getColumn()] = board[start.getRow()][start.getColumn()];
			//Actualizar posicion de pieza
			board[end.getRow()][end.getColumn()].setPosition(end);
			//Limpiar posición inicial
			board[start.getRow()][start.getColumn()]=null;
		}
	}
}
