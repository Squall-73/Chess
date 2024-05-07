package com.PP.Chess.logic;

import com.PP.Chess.board.ChessBoard;
import com.PP.Chess.pieces.King;
import com.PP.Chess.pieces.Piece;
import com.PP.Chess.pieces.PieceColor;
import com.PP.Chess.pieces.Rook;

import java.util.ArrayList;
import java.util.List;

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
	public Position findKingPosition(PieceColor color){
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
	public boolean wouldBeInCheckAfterMove(PieceColor kingColor, Position from, Position to){
		//Simula movimiento temporal
		//Si hay pieza en posición destino la guardo temporalmente
		Piece temp = board.getPiece(to.getRow(), to.getColumn());
		// Realiza el movimiento temporal
		board.movePiece(from, to);
		Position kingPosition=findKingPosition(kingColor);
		//Reviso si hay jaque
		boolean inCheck = isInCheck(kingColor,kingPosition);
		// Revierte el movimiento
		board.movePiece(to, from);
		board.setPiece(to.getRow(), to.getColumn(), temp);

		return inCheck;
	}
	/*busco la posición del rey, reviso en cada celda que haya una pieza si esta pieza es del color contrario
	y si tiene movimiento válido para atacar al rey*/
	public boolean isInCheck(PieceColor kingColor, Position kingPosition){

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
		Position kingPosition=findKingPosition(kingColor);
		if (!isInCheck(kingColor,kingPosition)) {
			return false; //Si no está en jaque no puede ser jaque mate
		}

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
		if (king == null || king.getMoved() || isInCheck(color,kingPosition) ||
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
		Position actPos= (PieceColor.WHITE==color)? new Position(7,4):new Position(0,4);
		Position destPos= (PieceColor.WHITE==color)? new Position(7,6):new Position(0,6);
		if(wouldBeInCheckAfterMove(color,actPos,destPos)){
		return castling(color, 6, 5, 7); // Columnas de destino y de inicio de la torre para el enroque corto
	}else{
			return false;
		}
	}
	public boolean longCastling(PieceColor color) {
		Position actPos= (PieceColor.WHITE==color)? new Position(7,4):new Position(0,4);
		Position destPos= (PieceColor.WHITE==color)? new Position(7,2):new Position(0,2);
		if(wouldBeInCheckAfterMove(color,actPos,destPos)){
			return castling(color, 2, 53, 0); // Columnas de destino y de inicio de la torre para el enroque corto
		}else{
			return false;
		}}
	public ChessBoard getBoard(){
		return this.board;
	}
	public void resetGame(){
		this.board = new ChessBoard(); //Reinicia el tablero
		this.whiteTurn=true; // Setea turno en blancas
	}
	public PieceColor getCurrentPlayerColor(){
		return whiteTurn ? PieceColor.WHITE : PieceColor.BLACK;
	}
	private Position selectedPosition; //Lleva registro de la posición seleccionada

	public boolean isPieceSelected(){
		return selectedPosition != null;
	}
	public boolean handleSquareSelection(int row,int col){
		if(selectedPosition==null){
			//Intenta seleccionar una pieza
			Piece selectedPiece = board.getPiece(row,col);
			if (selectedPiece !=null && selectedPiece.getColor() ==(whiteTurn ? PieceColor.WHITE : PieceColor.BLACK)){
				selectedPosition = new Position(row,col);
				return false;//Pieza seleccionada pero no movida
			}
		}else{
			//Intenta mover la pieza seleccionada
			boolean moveMade = makeMove(selectedPosition, new Position(row,col));
			selectedPosition = null;//Resetea la posición seleccionada
			return moveMade;//Devuelve verdadero si el movimiento fue exitoso
		}
		return false; //Devuelve falso si la pieza no se movio o no fue seleccionada
	}
	public List<Position> getLegalMovesForPieceAt(Position position){
		Piece selectedPiece = board.getPiece(position.getRow(),position.getColumn());
		if (selectedPiece==null) return new ArrayList<>();
		List<Position> legalMoves = new ArrayList<>();
		switch (selectedPiece.getClass().getSimpleName()){
			case "Pawn":
				addPawnMoves(position,selectedPiece.getColor(),legalMoves);
				break;
			case "Rook":
				addLineMoves(position, new int[][] {{1,0},{-1,0},{0,1},{0,-1}}, legalMoves);
				break;
			case "Knight":
				addSingleMoves(position, new int[][] {{-2,1},{-2,-1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}},legalMoves);
				break;
			case "Bishop":
				addLineMoves(position, new int[][] {{1,1},{-1,1},{-1,1},{-1,-1}}, legalMoves);
				break;
			case "Queen":
				addLineMoves(position, new int[][] {{1,0},{-1,0},{0,1},{0,-1},{1,1},{-1,1},{-1,1},{-1,-1}}, legalMoves);
				break;
			case "King":
				addKingMoves(position,selectedPiece.getColor(),legalMoves);
		}
		return legalMoves;
	}
	private void addLineMoves(Position position,int[][] directions, List<Position> legalMoves){
		for(int[]d:directions){
			Position newPos = new Position(position.getRow()+d[0],position.getColumn()+d[1]);
			while(isPositionOnBoard(newPos)){
				if(board.getPiece(newPos.getRow(), newPos.getColumn())==null){
					legalMoves.add(new Position(newPos.getRow(), newPos.getColumn()));
					newPos = new Position(newPos.getRow()+d[0], newPos.getColumn()+d[1]);
				}else{
					if(board.getPiece(newPos.getRow(), newPos.getColumn()).getColor()!=
							board.getPiece(position.getRow(),position.getColumn()).getColor()){
						legalMoves.add(newPos);
					}
					break;
				}
			}
		}
	}
	private void addSingleMoves(Position position,int[][] moves, List<Position> legalMoves){
		for(int[] move:moves){
			Position newPos =new Position(position.getRow()+move[0], position.getColumn()+move[1]);
			if(isPositionOnBoard(newPos)&&(board.getPiece(newPos.getRow(), newPos.getColumn())==null||
			board.getPiece(newPos.getRow(), newPos.getColumn()).getColor()!=
					board.getPiece(position.getRow(), position.getColumn()).getColor())){
				legalMoves.add(newPos);
			}
		}
	}
	private void addPawnMoves(Position position,PieceColor color ,List<Position> legalMoves){
		int direction = color == PieceColor.WHITE ?-1:1;
		Position newPos = new Position(position.getRow()+direction, position.getColumn());
		//Movimiento Standard
		if(isPositionOnBoard(newPos)&&board.getPiece(newPos.getRow(), newPos.getColumn())==null){
			legalMoves.add(newPos);
		}
		//Movimiento doble inicial
		if((color==PieceColor.WHITE && position.getRow()==6)||(color==PieceColor.BLACK&& position.getRow()==1)){
			newPos = new Position(position.getRow()+2*direction, position.getColumn());
			Position intermediatePos = new Position(position.getRow() + direction, position.getColumn());
			if(isPositionOnBoard(newPos)&& board.getPiece(newPos.getRow(), newPos.getColumn())==null &&
				board.getPiece(intermediatePos.getRow(), intermediatePos.getColumn())==null){
				legalMoves.add(newPos);
			}
		}
		//Captura
		int[] captureCols = {position.getColumn()-1, position.getColumn()+1};
		for (int col:captureCols){
			newPos = new Position(position.getRow()+direction,col);
			if(isPositionOnBoard(newPos)&&board.getPiece(newPos.getRow(), newPos.getColumn())!=null&&
				board.getPiece(newPos.getRow(), newPos.getColumn()).getColor()!=color){
				legalMoves.add(newPos);
			}
		}
	}
	private void addKingMoves(Position position,PieceColor color ,List<Position> legalMoves){

		// Agregar movimientos de enroque si son posibles
		if (shortCastling(color)) {
			legalMoves.add(new Position((color == PieceColor.WHITE) ? 7 : 0, 6)); // Agregar posición de enroque corto
		}
		if (longCastling(color)) {
			legalMoves.add(new Position((color == PieceColor.WHITE) ? 7 : 0, 2)); // Agregar posición de enroque largo
		}
		addSingleMoves(position, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}}, legalMoves);
	}
}
