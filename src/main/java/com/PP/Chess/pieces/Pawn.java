package com.PP.Chess.pieces;

import com.PP.Chess.logic.Position;

public class Pawn extends Piece {
	public Pawn(PieceColor color, Position position){
		super(color,position);
	}

	@Override
	public boolean isValidMove(Position newPosition, Piece[][] board){
		if (newPosition.equals(this.position)) {
			return false; // No puede moverse a su propia posición
		}
		//Si es blanco la posición disminuye en 1, si es negro aumenta
		int forwardDirection = color == PieceColor.WHITE ? -1 : 1;
		int rowDiff = (newPosition.getRow()-position.getRow())*forwardDirection;
		int colDiff = newPosition.getColumn()-position.getColumn();

		//Verifica movimiento de avance
		if(colDiff==0 && rowDiff==1 && board[newPosition.getRow()][newPosition.getColumn()]==null){
			return true;
		}
		//Movimiento inicial de dos espacios
		boolean isStartingPosition = (color==PieceColor.WHITE && position.getRow()==6) ||
				(color==PieceColor.BLACK && position.getRow()==1);
		if(colDiff==0 && rowDiff==2 && isStartingPosition && board[newPosition.getRow()][newPosition.getColumn()]==null){
			//Revisar que el casillero intermedio este vacío
			int middleRow = position.getRow()+forwardDirection;
			if(board[middleRow][position.getColumn()]==null){
				return true;
			}
		}
		//Comer en diagonal
		if(Math.abs(colDiff)==1 && rowDiff == 1 && board[newPosition.getRow()][newPosition.getColumn()]!=null
				&& board[newPosition.getRow()][newPosition.getColumn()].getColor() !=this.color){
			return true;

		}
		return false;
	}
}
