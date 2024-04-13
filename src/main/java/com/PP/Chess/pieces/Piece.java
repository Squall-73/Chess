package com.PP.Chess.pieces;

import com.PP.Chess.logic.Position;

public abstract class Piece {
	protected Position position;
	protected PieceColor color;
	protected boolean hasMoved = false;
	public Piece(PieceColor color, Position position){
		this.color=color;
		this.position=position;
	}
	public PieceColor getColor(){
		return color;
	}
	public Position getPosition(){
		return position;
	}
	public void setPosition(Position position){
		this.position =position;
	}
	public void setMoved(){
		this.hasMoved=true;
	}
	public boolean getMoved(){return hasMoved;}
	public abstract boolean isValidMove(Position newPosition, Piece[][] board);
	//Método que chequea que el movimiento sea válido, depende del tipo de pieza
}
