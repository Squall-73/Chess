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

	public boolean castling(PieceColor color, int kingDestCol, int rookDestCol, int rookStartCol) {
		int row = (color == PieceColor.WHITE) ? 7 : 0;
		int startCol = 4;
		// Verificar si el rey está en posición original sin haberse movido y no en jaque
		Position kingPosition = new Position(row, startCol);
		King king = (King) board.getPiece(kingPosition.getRow(), kingPosition.getColumn());
		if (king == null || king.getMoved() || isInCheck(color) ||
				!king.isValidMove(new Position(row, kingDestCol), board.getBoard())) {
			return false; // No se puede hacer el enroque
		}
		// Verificar posición original de la torre
		Position rookPosition = new Position(row, rookStartCol);
		Rook rook = (Rook) board.getPiece(rookPosition.getRow(), rookPosition.getColumn());
		if (rook == null || rook.getMoved()) {
			return false; // No se puede hacer el enroque
		}
		// Verificar que las casillas entre el rey y la torre estén vacías
		int step = (rookStartCol < startCol) ? -1 : 1;
		for (int col = startCol + step; col != rookStartCol; col += step) {
			if (board.getPiece(row, col) != null) {
				return false; // Hay una pieza, no se puede hacer el enroque
			}
		}
		// Realizar el enroque
		board.movePiece(kingPosition, new Position(row, kingDestCol)); // Mover el rey
		board.movePiece(rookPosition, new Position(row, rookDestCol)); // Mover la torre
		return true; // Enroque realizado con éxito
	}
	public boolean shortCastling(PieceColor color) {
		return castling(color, 6, 5, 7); // Columnas de destino y de inicio de la torre para el enroque corto
	}
	public boolean longCastling(PieceColor color) {
		return castling(color, 2, 3, 0); // Columnas de destino y de inicio de la torre para el enroque largo
	}
}
