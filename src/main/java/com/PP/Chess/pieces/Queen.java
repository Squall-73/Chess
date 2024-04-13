package com.PP.Chess.pieces;

import com.PP.Chess.logic.Position;

public class Queen extends	Piece{
	public Queen (PieceColor color, Position position){super(color, position);}

	@Override
	public boolean isValidMove(Position newPosition, Piece[][] board){
		if (newPosition.equals(this.position)) {
			return false; // No puede moverse a su propia posición
		}
		int rowDiff=Math.abs(this.position.getRow()-newPosition.getRow());
		int colDiff=Math.abs(this.position.getColumn()-newPosition.getColumn());
		//Si la diferencia en filas es igual a la diferencia en columnas el movimiento es diagonal
		boolean diagonal = rowDiff == colDiff;
		//Si la columna o la fila es constante el movimiento es en linea recta
		boolean straightLine = this.position.getRow() == newPosition.getRow()
				|| this.position.getColumn()== newPosition.getColumn();
		if(!diagonal && !straightLine){
			return false; //Movimiento inválido
		}
		//Dirección de movimiento
		int rowDirection = Integer.compare(newPosition.getRow(), this.position.getRow());
		int colDirection = Integer.compare(newPosition.getColumn(),this.position.getColumn());
		//Chequea piezas en el camino
		int currentRow = this.position.getRow() + rowDirection;
		int currentCol = this.position.getColumn() + colDirection;
		while (currentRow != newPosition.getRow() || currentCol != newPosition.getColumn()) {
			if (board[currentRow][currentCol] != null) {
				return false; // Hay piezas en el camino
			}
			currentRow += rowDirection;
			currentCol += colDirection;
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
