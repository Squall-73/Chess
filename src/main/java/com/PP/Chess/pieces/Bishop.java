package com.PP.Chess.pieces;

import com.PP.Chess.logic.Position;


public class Bishop extends Piece {
	public Bishop(PieceColor color, Position position){
		super (color,position);
	}

	@Override
	public boolean isValidMove(Position newPosition, Piece[][] board){
		if (newPosition.equals(this.position)) {
			return false; // No puede moverse a su propia posición
		}
		int rowDiff=Math.abs(this.position.getRow()-newPosition.getRow());
		int colDiff=Math.abs(this.position.getColumn()-newPosition.getColumn());
		//Si el movimiento es diagonal se mueve la misma cantidad de filas y columnas
		if(rowDiff != colDiff){
			return false;
		}
		//Cuento cantidad de cuadros recorridos
		int rowStep= newPosition.getRow()> position.getRow() ? 1 : -1;
		int colStep= newPosition.getColumn()> position.getColumn() ? 1 : -1;
		int steps = rowDiff-1;
		//Chequeo piezas en el camino
		for (int i=1; i<=steps; i++){
			if(board[position.getRow()+i*rowStep][position.getColumn()+i*colStep]!=null){
				return false; //Hay una pieza en el camino
			}
		}
		//Verifico que la posición final este vacía o sea una pieza contraria
		Piece targetPiece = board[newPosition.getRow()][newPosition.getColumn()];
		if(targetPiece==null){
			return true;
		}else{
			return targetPiece.getColor()!=this.getColor();
		}
	}
}
