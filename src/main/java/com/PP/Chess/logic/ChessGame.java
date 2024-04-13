package com.PP.Chess.logic;

import com.PP.Chess.board.ChessBoard;
import com.PP.Chess.pieces.King;
import com.PP.Chess.pieces.Piece;
import com.PP.Chess.pieces.PieceColor;
import com.PP.Chess.pieces.Rook;

public class ChessGame {
	private ChessBoard board;
	private boolean whiteTurn = true;

	public ChessGame(){
		this.board= new ChessBoard();
	}
	public boolean makeMove(Position start, Position end){
		Piece movingPiece = board.getPiece(start.getRow(), start.getColumn());
		if(movingPiece == null || movingPiece.getColor() != (whiteTurn ? PieceColor.WHITE :PieceColor.BLACK)){
			return false; //No hay pieza en la posición o no es el turno correspondiente
		}
		if(movingPiece.isValidMove(end,board.getBoard())){
			board.movePiece(start,end);
			whiteTurn= !whiteTurn; //Cambio de turno
			board.getPiece(end.getRow(), end.getColumn()).setMoved();
			return true;
		}
		return false;
	}
	//Busca el rey de un color, si no lo encuentra tira error
	private Position findKingPosition(PieceColor color){
		for(int row=0; row<board.getBoard().length;row++) {
			for (int col = 0; col < board.getBoard()[row].length; col++) {
				Piece piece = board.getPiece(row,col);
				if(piece instanceof King && piece.getColor()==color){
					return new Position(row,col);
				}
			}
		}
		throw new RuntimeException("King not found, which should never happens");
	}


	//Chequeo que el movimiento vaya a ser dentro del tablero
	private boolean isPositionOnBoard(Position position){
		return position.getRow() >= 0 && position.getRow()< board.getBoard().length &&
				position.getColumn()>=0 &&  position.getColumn()<board.getBoard()[0].length;
	}
	//Simulo movimiento para ver que el rey no quede en jaque
	private boolean wouldBeInCheckAfterMove(PieceColor kingColor, Position from, Position to){
		//Simula movimiento temporal
		//Si hay pieza en posición destino la guardo temporalmente
		Piece temp = board.getPiece(to.getRow(), to.getColumn());
		//Pongo la pieza en posición destino
		board.setPiece(to.getRow(), to.getColumn(),board.getPiece(from.getRow(), from.getColumn()));
		//Elimino la pieza original
		board.setPiece(from.getRow(), from.getColumn(), null);
		//Reviso si hay jaque
		boolean inCheck = isInCheck(kingColor);
		//revierto el movimiento
		board.setPiece(from.getRow(), from.getColumn(),board.getPiece(to.getRow(), to.getColumn()));
		board.setPiece(to.getRow(), to.getColumn(), temp);
		return inCheck;
	}
	/*busco la posición del rey, reviso en cada celda que haya una pieza si esta pieza es del color contrario
	y si tiene movimiento válido para atacar al rey*/
	public boolean isInCheck(PieceColor kingColor){
		Position kingPosition=findKingPosition(kingColor);
		for(int row=0; row<board.getBoard().length;row++){
			for(int col=0; col<board.getBoard()[row].length;col++){
				Piece piece = board.getPiece(row,col);
				if(piece != null && piece.getColor() != kingColor){
					if(piece.isValidMove(kingPosition, board.getBoard())){
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean isCheckmate(PieceColor kingColor){
		if (!isInCheck(kingColor)) {
			return false; //Si no está en jaque no puede ser jaque mate
		}
		Position kingPosition=findKingPosition(kingColor);
		King king = (King) board.getPiece(kingPosition.getRow(), kingPosition.getColumn());

		//Buscamos movimiento que pueda sacar al rey de jaque
		for(int rowOffset =-1; rowOffset<=1; rowOffset++){
			for(int colOffset=-1; colOffset<=1; colOffset++){
				if(rowOffset==0 && colOffset==0){
					continue;//Salta la posición actual del rey
				}
				Position newPosition = new Position(kingPosition.getRow()+rowOffset,
						kingPosition.getColumn()+colOffset);
				/*Reviso que la posición nueva este dentro del tablero, sea movimiento válido
				y no este en jaque después del movimiento*/
				if(isPositionOnBoard(newPosition) && king.isValidMove(newPosition, board.getBoard())&&
						!wouldBeInCheckAfterMove(kingColor,kingPosition,newPosition)){
					return false; //si esto se cumple, encontre posición que no estará en jaque así que no es mate
				}
			}
		}
		return true;//No hay posición a salvo - PERDISTE
	}
	public boolean shortCastling(PieceColor color){
		int row;
		if(color==PieceColor.WHITE){
			row = 7;
		}else{
			row =0;
		}
		int startCol=4;
		//Reviso que el rey esté en posición original sin haberse movido y no en jaque
		Position kingPosition = new Position (row,startCol);
		King king = (King) board.getPiece(kingPosition.getRow(), kingPosition.getColumn());
		if(king == null|| king.getMoved()||isInCheck(color)||
				!king.isValidMove(new Position(row, startCol +2), board.getBoard())){
			/*Si no hay rey en posición original, o ya se movió, o está en jaque,
			o no puede moverse a la posición destino no se puede hacer enroque corto*/
			return false;
		}
		//Verifico posición original de la torre
		Position rookPosition = new Position(row, startCol+3);
		Rook rook = (Rook) board.getPiece(rookPosition.getRow(), rookPosition.getColumn());
		if(rook == null || rook.getMoved()){
			//Si no hay torre en la posición o ya se movió no puedo enrocar
			return false;
		}
		//Verifico que las casillas entre el rey y la torre estén vacías
		for(int col = startCol +1; col < startCol +3; col++){
			if(board.getPiece(row,col)!= null){
				//Hay una pieza, no se puede enrocar
				return false;
			}
		}
		//Realizo el enroque corto
		board.movePiece(kingPosition, new Position(row, startCol + 2)); //Muevo el rey
		board.movePiece(rookPosition, new Position(row, startCol + 1)); //Muevo la torre
		return true; // Enroque corto realizado con éxito
	}
}
