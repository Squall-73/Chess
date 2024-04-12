package com.PP.Chess.pieces;

import com.PP.Chess.Piece;
import com.PP.Chess.PieceColor;
import com.PP.Chess.Position;

public class Knight extends Piece {
	public Knight(PieceColor color, Position position){
		super(color,position);
	}

	@Override
	public boolean isValidMove(Position newPosition, Piece[][] board){
		if (newPosition.equals(this.position)) {
			return false; // No puede moverse a su propia posición
		}
		int rowDiff=Math.abs(this.position.getRow()-newPosition.getRow());
		int colDiff=Math.abs(this.position.getColumn()-newPosition.getColumn());

		//chequeo si el movimiento es en "L" 2 casillas en un sentido y 1 en el otro
		boolean isValidMove = (rowDiff==2 && colDiff==1) || (rowDiff==1 && colDiff==2);
		if(!isValidMove){
			return false;
		}
		//Verifico que la posición final este vacía o sea una pieza contraria
		Piece targetPiece = board[newPosition.getRow()][newPosition.getColumn()];
		if(targetPiece==null){
			this.setMoved();
			return true;
		}else{
			return targetPiece.getColor()!=this.getColor();
		}

	}
}
