package com.phill.keychest.view;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import com.phill.keychest.model.*;
import com.phill.keychest.controller.*;
import com.phill.libs.*;

public class CredentialsAdd extends JPanel {

	private static final long serialVersionUID = 1L;
	private final JTextField textServico, textLogin;
	private final JPasswordField textSenha;
	private final JComboBox<String> comboUsuarios;
	
	private final ArrayList<Owner> ownerList;
	private final Credentials credentials;
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		
		frame.setContentPane(new CredentialsAdd(new ArrayList<Owner>()));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 200);
		
		frame.setVisible(true);
		
	}
	
	public CredentialsAdd(final ArrayList<Owner> ownerList) {
		this(ownerList,null);
	}
	
	public CredentialsAdd(final ArrayList<Owner> ownerList, Credentials credentials) {
		
		GraphicsHelper instance = GraphicsHelper.getInstance();
		
		Font  fonte = instance.getFont ();
		Color color = instance.getColor();
		
		this.ownerList   = ownerList;
		this.credentials = credentials;
		
		setPreferredSize(new Dimension(450,165));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		
		JLabel labelServico = new JLabel("Serviço:");
		labelServico.setHorizontalAlignment(JLabel.RIGHT);
		labelServico.setFont(fonte);
		labelServico.setBounds(10, 10, 60, 25);
		add(labelServico);
		
		textServico = new JTextField();
		textServico.setFont(fonte);
		textServico.setForeground(color);
		textServico.setBounds(80, 10, 350, 25);
		add(textServico);
		
		JLabel labelLogin = new JLabel("Login:");
		labelLogin.setHorizontalAlignment(JLabel.RIGHT);
		labelLogin.setFont(fonte);
		labelLogin.setBounds(10, 50, 60, 25);
		add(labelLogin);
		
		textLogin = new JTextField();
		textLogin.setFont(fonte);
		textLogin.setForeground(color);
		textLogin.setBounds(80, 50, 350, 25);
		add(textLogin);
		
		JLabel labelSenha = new JLabel("Senha:");
		labelSenha.setHorizontalAlignment(JLabel.RIGHT);
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
		labelUsuario.setHorizontalAlignment(JLabel.RIGHT);
		labelUsuario.setFont(fonte);
		labelUsuario.setBounds(10, 130, 60, 25);
		add(labelUsuario);
		
		comboUsuarios = new JComboBox<String>();
		comboUsuarios.setFont(fonte);
		comboUsuarios.setForeground(color);
		comboUsuarios.setBounds(80, 130, 350, 25);
		add(comboUsuarios);
		
		comboUsuarios.addItem("Selecione abaixo");
		
		for (Owner owner: ownerList)
			comboUsuarios.addItem(owner.getName());
		
		if (credentials != null)
			load();
		
	}
	
	private void load() {
		
		textServico.setText(this.credentials.getService ());
		textLogin  .setText(this.credentials.getLogin   ());
		textSenha  .setText(this.credentials.getPassword());
		
		comboUsuarios.setSelectedItem(this.credentials.getOwner().getName());
		
	}
	
	public void commit() {
		
		final String service = textServico.getText().trim();
		final String login   = textLogin  .getText().trim();
		final String passwd  = new String(textSenha.getPassword());
		final Owner  owner   = comboUsuarios.getSelectedIndex() > 0 ? ownerList.get(comboUsuarios.getSelectedIndex()-1) : null;
		
		if (service.isEmpty()) {
			
			AlertDialog.erro("O nome de serviço não foi especificado");
			return;
			
		}
		
		if (owner == null) {
			
			AlertDialog.erro("Nenhum usuário foi selecionado");
			return;
			
		}
		
		Credentials credentials = new Credentials();
		
		if (this.credentials != null)
			credentials.setID(this.credentials.getID());
		
		credentials.setService (service);
		credentials.setLogin   (login  );
		credentials.setPassword(passwd );
		credentials.setOwner   (owner  );
		
		boolean status = (this.credentials == null) ? CredentialsDAO.insert(credentials) : CredentialsDAO.update(credentials);
		
		if (status)
			if (this.credentials == null)
				AlertDialog.informativo("Credencial cadastrada com sucesso!");
			else
				AlertDialog.informativo("Credencial atualizada com sucesso!");
		else
			if (this.credentials == null)
				AlertDialog.erro("Falha ao cadastrar credencial.\nTalvez este serviço já tenha sido cadastrado para o usuário selecionado.");
			else
				AlertDialog.erro("Falha ao atualizar credencial.\nTalvez este serviço já tenha sido cadastrado para o usuário selecionado.");
		
	}

}
