package com.phill.keychest.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.phill.libs.*;

public class CredentialsAdd extends JPanel {

	private static final long serialVersionUID = 1L;
	private final JTextField textServico, textLogin;
	private final JPasswordField textSenha;
	private final JComboBox<String> comboUsuarios;
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		
		frame.setContentPane(new CredentialsAdd());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 200);
		
		frame.setVisible(true);
		
	}
	
	public CredentialsAdd() {
		
		GraphicsHelper instance = GraphicsHelper.getInstance();
		
		Font  fonte = instance.getFont ();
		Color color = instance.getColor();
		
		setPreferredSize(new Dimension(450,165));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		
		JLabel labelServico = new JLabel("Serviço:");
		labelServico.setHorizontalAlignment(SwingConstants.RIGHT);
		labelServico.setFont(fonte);
		labelServico.setBounds(10, 10, 60, 25);
		add(labelServico);
		
		textServico = new JTextField();
		textServico.setFont(fonte);
		textServico.setForeground(color);
		textServico.setBounds(80, 10, 350, 25);
		add(textServico);
		
		JLabel labelLogin = new JLabel("Login:");
		labelLogin.setHorizontalAlignment(SwingConstants.RIGHT);
		labelLogin.setFont(fonte);
		labelLogin.setBounds(10, 50, 60, 25);
		add(labelLogin);
		
		textLogin = new JTextField();
		textLogin.setFont(fonte);
		textLogin.setForeground(color);
		textLogin.setBounds(80, 50, 350, 25);
		add(textLogin);
		
		JLabel labelSenha = new JLabel("Senha:");
		labelSenha.setHorizontalAlignment(SwingConstants.RIGHT);
		labelSenha.setFont(fonte);
		labelSenha.setBounds(10, 90, 60, 25);
		add(labelSenha);
		
		textSenha = new JPasswordField();
		textSenha.setEchoChar('*');
		textSenha.setFont(fonte);
		textSenha.setForeground(color);
		textSenha.setBounds(80, 90, 310, 25);
		add(textSenha);
		
		JToggleButton mostraSenha = new JToggleButton();
		mostraSenha.setBounds(405, 90, 25, 25);
		mostraSenha.addActionListener((event) -> { if (mostraSenha.isSelected()) textSenha.setEchoChar((char)0); else textSenha.setEchoChar('*'); } );
		add(mostraSenha);
		
		JLabel labelUsuario = new JLabel("Usuário:");
		labelUsuario.setHorizontalAlignment(SwingConstants.RIGHT);
		labelUsuario.setFont(fonte);
		labelUsuario.setBounds(10, 130, 60, 25);
		add(labelUsuario);
		
		comboUsuarios = new JComboBox<String>();
		comboUsuarios.setFont(fonte);
		comboUsuarios.setForeground(color);
		comboUsuarios.setBounds(80, 130, 350, 25);
		add(comboUsuarios);

	}

}
