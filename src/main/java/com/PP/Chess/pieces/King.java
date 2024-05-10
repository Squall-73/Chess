package com.PP.Chess.pieces;
import com.PP.Chess.logic.*;

public class King extends Piece {
	public King (PieceColor color, Position position){super(color,position);}

	@Override
	public boolean isValidMove(Position newPosition, Piece[][] board){

		if (newPosition.equals(this.position)) {
			return false; // No puede moverse a su propia posición
		}
		int rowDiff=Math.abs(this.position.getRow()- newPosition.getRow());
		int colDiff=Math.abs(this.position.getColumn()- newPosition.getColumn());
		if((rowDiff > 1||colDiff > 1) && (rowDiff != 0 || colDiff != 2)){
			return false;
			//Verifica que el movimiento sea de un solo cuadro, no importa la dirección o de dos en horizontal(enroque)
		}
		//Movimiento válido, resta chequear posición final
		Piece destinationPiece = board[newPosition.getRow()][newPosition.getColumn()];
		if (destinationPiece==null || destinationPiece.getColor()!=this.color){
			//Si la posición destino está vacío o tiene una pieza de otro color
			return true;
		}



		return false;

	}
}
