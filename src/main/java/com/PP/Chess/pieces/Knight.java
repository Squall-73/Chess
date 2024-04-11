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
	}
}
