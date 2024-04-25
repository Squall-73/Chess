package com.PP.Chess.GUI;

import javax.swing.*;
import java.awt.*;

public class ChessSquareComponent extends JButton {
	private int row;
	private int col;

	public ChessSquareComponent(int row, int col){
		this.col = col;
		this.row=row;
		initButton();
	}

	private void initButton(){
		//Setea tamaño preferencial de boton para uniformidad
		setPreferredSize(new Dimension(64,64));
		//Setea el color del fondo, basado en fila y columna para simular tablero de ajedrez
		if(row+col%2==0){
			setBackground(Color.LIGHT_GRAY);
		} else {
			setBackground(new Color(205, 133, 63));
		}
		//Asegura que el símbolo de la pieza esta centrado en el cuadrado
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);

		//Setea la fuente para que se vea mejor
		setFont(new Font("Serif", Font.BOLD,36));
	}
	public void setPieceSymbol(String symbol, Color color){
		this.setText(symbol);
		this.setForeground(color);
	}
	public void clearPieceSymbol() {
		this.setText("");
	}


}
