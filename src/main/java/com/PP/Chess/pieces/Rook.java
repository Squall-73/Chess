package com.PP.Chess.pieces;

import com.PP.Chess.Piece;
import com.PP.Chess.PieceColor;
import com.PP.Chess.Position;

public class Rook extends Piece {
	public Rook(PieceColor color, Position position){
		super(color,position);
	}

	@Override
	public boolean isValidMove(Position newPosition, Piece[][] board){
		//Movimiento vertical u horizontal, sin saltar piezas
		if (newPosition.equals(this.position)) {
			return false; // No puede moverse a su propia posición
		}
		if(position.getRow() == newPosition.getRow()){
			//Verifica en el movimiento horizontal que no haya piezas en el camino
			int columnStart = Math.min(position.getColumn(), newPosition.getColumn())+1;
			int columnEnd = Math.max(position.getColumn(), newPosition.getColumn());
			for(int column = columnStart; column < columnEnd; column++){
				if(board[position.getRow()][column] !=null){
					return false;
				}
			}
		}else if(position.getColumn()==newPosition.getColumn()){
			//Verifica en el movimiento vertical que no haya piezas en el camino
			int rowStart = Math.min(position.getRow(), newPosition.getRow())+1;
			int rowEnd = Math.max(position.getRow(), newPosition.getRow());
			for(int row = rowStart; row < rowEnd; row++){
				if(board[row][position.getColumn()] !=null){
					return false;
				}
			}
		}else{//movimiento no es válido
			return false;
		}
		Piece destinationPiece = board[newPosition.getRow()][newPosition.getColumn()];
		if (destinationPiece==null || destinationPiece.getColor()!=this.color){
			//Si la posición destino está vacío o tiene una pieza de otro color
			return true;
		}
		return false;
	}
}
