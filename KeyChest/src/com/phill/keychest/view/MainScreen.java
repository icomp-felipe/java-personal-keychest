package com.phill.keychest.view;

import java.awt.*;
import javax.swing.*;

public class MainScreen extends JFrame {
	
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new MainScreen();
	}

	public MainScreen() {
		super("KeyChest - build 20200504");
		
		setSize(640,480);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

}
