package com.phill.keychest.view;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import com.phill.keychest.model.*;
import com.phill.keychest.controller.*;
import com.phill.libs.*;

/** Implementa a tela de cadastro e edição de credenciais.
 *  @author Felipe André - fass@icomp.ufam.edu.br
 *  @version 1.0, 05/05/2020 */
public class CredentialsAdd extends JPanel {

	// Serial da JFrame
	private static final long serialVersionUID = 1L;
	
	// Atributos gráficos
	private final JTextField textServico, textLogin;
	private final JPasswordField textSenha;
	private final JComboBox<String> comboUsuarios;
	
	// Atributos dinâmicos
	private final ArrayList<Owner> ownerList;
	private final Credentials credentials;
	
	/** Construtor utilizado para a criação de uma nova credencial.
	 *  Este construtor chama o principal com uma credencial nula, indicando que é um cadastro.
	 *  @param ownerList - lista de usuários cadastrados */
	public CredentialsAdd(final ArrayList<Owner> ownerList) {
		this(ownerList,null);
	}
	
	/** Construtor utilizado para a edição de uma credencial já existente.
	 *  Também é o construtor principal desta classe.
	 *  @param ownerList - lista de usuários cadastrados
	 *  @param credentials - credencial a ser editada. Quando nula, significa que é um cadastro! */
	public CredentialsAdd(final ArrayList<Owner> ownerList, Credentials credentials) {
		
		// Alimentando atributos locais
		this.ownerList   = ownerList;
		this.credentials = credentials;
		
		// Recuperando fontes e cores
		GraphicsHelper instance = GraphicsHelper.getInstance();
		
		Font  fonte = instance.getFont ();
		Color color = instance.getColor();
		
		// Recuperando ícones
		Icon viewIcon = ResourceManager.getResizedIcon("icon/eye.png",20,20);
		
		// Inicializando atributos gráficos
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
		
		JToggleButton mostraSenha = new JToggleButton(viewIcon);
		mostraSenha.setToolTipText("Exibe ou oculta a senha");
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
		
		// Preenchendo o combo com usuários
		comboUsuarios.addItem("Selecione abaixo");
		
		for (Owner owner: ownerList)
			comboUsuarios.addItem(owner.getName());
		
		// Se estou no modo edição, carrego os dados da credencial na tela
		if (credentials != null)
			load();
		
	}
	
	/** Carrega os dados da credencial informada na tela. */
	private void load() {
		
		textServico.setText(this.credentials.getService ());
		textLogin  .setText(this.credentials.getLogin   ());
		textSenha  .setText(this.credentials.getPassword());
		
		comboUsuarios.setSelectedItem(this.credentials.getOwner().getName());
		
	}
	
	/** Salva as alterações de credencial no banco de dados. De acordo com
	 *  o modo, este método sabe decidir se deve inserir ou atualizar. */
	public void commit() {
		
		// Recuperando dados da tela
		final String service = textServico.getText().trim();
		final String login   = textLogin  .getText().trim();
		final String passwd  = new String(textSenha.getPassword());
		final Owner  owner   = comboUsuarios.getSelectedIndex() > 0 ? ownerList.get(comboUsuarios.getSelectedIndex()-1) : null;
		
		// Validação de nome de serviço: este não pode ser vazio
		if (service.isEmpty()) {
			
			AlertDialog.erro("O nome de serviço não foi especificado");
			return;
			
		}
		
		// Validação de usuário: algum deve ser selecionado
		if (owner == null) {
			
			AlertDialog.erro("Nenhum usuário foi selecionado");
			return;
			
		}
		
		// Montando objeto de credencial
		Credentials credentials = new Credentials();
		
		if (this.credentials != null)
			credentials.setID(this.credentials.getID());
		
		credentials.setService (service);
		credentials.setLogin   (login  );
		credentials.setPassword(passwd );
		credentials.setOwner   (owner  );
		
		// Salvando alterações no banco de dados
		boolean status = (this.credentials == null) ? CredentialsDAO.insert(credentials) : CredentialsDAO.update(credentials);
		
		// Imprimindo mensagem de status
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

	/** Método apenas para fins de teste. Não possui ligação alguma com o BD. */
	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		
		frame.setContentPane(new CredentialsAdd(new ArrayList<Owner>()));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 200);
		frame.setLocationRelativeTo(null);
		frame.setTitle("KeyChest - Test Zone");
		
		frame.setVisible(true);
		
	}
	
}
