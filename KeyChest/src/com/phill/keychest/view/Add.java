package com.phill.keychest.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;

public class Add extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;
	private JPasswordField passwordField;
	private JLabel lblUsurio;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Add frame = new Add();
					
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setBounds(100, 100, 450, 300);
					
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Add() {
		
		JPanel contentPane = new JPanel();
		contentPane.setPreferredSize(new Dimension(450,300));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNome = new JLabel("Serviço:");
		lblNome.setBounds(12, 12, 78, 15);
		contentPane.add(lblNome);
		
		textField = new JTextField();
		textField.setBounds(108, 10, 201, 19);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblLogin = new JLabel("Login:");
		lblLogin.setBounds(20, 56, 70, 15);
		contentPane.add(lblLogin);
		
		textField_1 = new JTextField();
		textField_1.setBounds(108, 54, 201, 19);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblSenha = new JLabel("Senha:");
		lblSenha.setBounds(12, 108, 70, 15);
		contentPane.add(lblSenha);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(108, 106, 201, 19);
		contentPane.add(passwordField);
		
		lblUsurio = new JLabel("Usuário:");
		lblUsurio.setBounds(12, 150, 70, 15);
		contentPane.add(lblUsurio);
		
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setBounds(108, 145, 201, 24);
		contentPane.add(comboBox);
	}
	
}
