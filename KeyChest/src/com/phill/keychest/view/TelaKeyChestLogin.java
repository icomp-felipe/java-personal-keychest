package com.phill.keychest.view;

import java.sql.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import com.phill.libs.*;
import com.phill.keychest.bd.*;

/** Classe TelaKeychestLogin - cria um ambiente gráfico para o usuário fazer login no sistema
 *  @author Felipe André - fass@icomp.ufam.edu.br
 *  @version 1.0, 08/05/2020 */
public class TelaKeyChestLogin extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private final JTextField textLogin;
	private final JPasswordField textSenha;
	private final JRadioButton radioProducao;
	private final JLabel labelConexaoStatus;
	private final ImageIcon loading = new ImageIcon(ResourceManager.getResource("img/loading.gif"));
	
	/** Construtor da classe TelaLogin - Cria a janela */
	public static void main(String[] args) {
		new TelaKeyChestLogin();
	}
	
	/** Construtor da classe TelaLogin - constrói a janela gráfica */
	public TelaKeyChestLogin() {
		super("KeyChest - Login");
		
		// Inicializando atributos gráficos
		GraphicsHelper helper = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"img/logo.png");
		
		Dimension dimension = new Dimension(550,300);
		JPanel    mainPanel = new JPaintedPanel("img/background.png",dimension);
		
		mainPanel.setLayout(null);
		setContentPane(mainPanel);
		
		// Recuperando fontes e cores
		Font dialog = helper.getFont (  );
		Font  fonte = helper.getFont (18);
		Font radios = new Font("Dialog", Font.PLAIN, 12);
		Color color = helper.getColor(  );
		
		// Recuperando ícones
		ImageIcon loginIcon = new ImageIcon(ResourceManager.getResource("img/login.png"));
		
		// Declaração da janela gráfica
		JLabel labelImagem = new JLabel(loginIcon);
		labelImagem.setBounds(12, 12, 232, 235);
		mainPanel.add(labelImagem);
		
		JPanel painelCredenciais = new JPanel();
		painelCredenciais.setOpaque(false);
		painelCredenciais.setBorder(helper.getTitledBorder("Credenciais"));
		painelCredenciais.setBounds(250, 25, 285, 110);
		painelCredenciais.setLayout(null);
		mainPanel.add(painelCredenciais);
		
		JLabel labelLogin = new JLabel("Login:");
		labelLogin.setBounds(12, 28, 75, 25);
		labelLogin.setHorizontalAlignment(JLabel.RIGHT);
		labelLogin.setFont(fonte);
		painelCredenciais.add(labelLogin);
		
		textLogin = new JTextField();
		textLogin.setBounds(95, 30, 180, 25);
		textLogin.setFont(dialog);
		textLogin.setForeground(color);
		textLogin.setToolTipText("Digite aqui seu login");
		painelCredenciais.add(textLogin);
		
		JLabel labelSenha = new JLabel("Senha:");
		labelSenha.setBounds(12, 68, 75, 25);
		labelSenha.setHorizontalAlignment(JLabel.RIGHT);
		labelSenha.setFont(fonte);
		painelCredenciais.add(labelSenha);
		
		textSenha = new JPasswordField();
		textSenha.setBounds(95, 70, 180, 25);
		textSenha.setFont(dialog);
		textSenha.setForeground(color);
		textSenha.setToolTipText("Digite aqui sua senha");
		painelCredenciais.add(textSenha);
		
		JPanel painelOpcoes = new JPanel();
		painelOpcoes.setOpaque(false);
		painelOpcoes.setBorder(helper.getTitledBorder("Opções"));
		painelOpcoes.setBounds(250, 145, 285, 60);
		painelOpcoes.setLayout(null);
		mainPanel.add(painelOpcoes);
		
		JLabel labelConexao = new JLabel("Conexão:");
		labelConexao.setBounds(12, 30, 66, 15);
		painelOpcoes.add(labelConexao);
		
		radioProducao = new JRadioButton("Produção");
		radioProducao.setOpaque(false);
		radioProducao.setSelected(true);
		radioProducao.setFont(radios);
		radioProducao.setBounds(90, 25, 102, 23);
		painelOpcoes.add(radioProducao);
		
		JRadioButton radioLocal = new JRadioButton("Local");
		radioLocal.setOpaque(false);
		radioLocal.setFont(radios);
		radioLocal.setBounds(190, 25, 70, 23);
		painelOpcoes.add(radioLocal);
		
		ButtonGroup group = new ButtonGroup();
		group.add(radioLocal);
		group.add(radioProducao);
		
		JButton botaoSair = new JButton("Sair");
		botaoSair.addActionListener((event) -> dispose());
		botaoSair.setBounds(250, 228, 85, 25);
		mainPanel.add(botaoSair);
		
		JButton botaoLimpar = new JButton("Limpar");
		botaoLimpar.addActionListener((event) -> limpaCampos());
		botaoLimpar.setBounds(350, 228, 85, 25);
		mainPanel.add(botaoLimpar);
		
		JButton botaoEntrar = new JButton("Entrar");
		botaoEntrar.addActionListener((event) -> tryLogin());
		botaoEntrar.setBounds(450, 228, 85, 25);
		mainPanel.add(botaoEntrar);
		
		KeyListener listener = (KeyboardAdapter) (event) -> { if (event.getKeyCode() == KeyEvent.VK_ENTER) botaoEntrar.doClick(); };
		textSenha.addKeyListener(listener);
		
		labelConexaoStatus = new JLabel();
		labelConexaoStatus.setHorizontalAlignment(JLabel.LEFT);
		labelConexaoStatus.setBounds(12, 245, 220, 20);
		labelConexaoStatus.setVisible(false);
		mainPanel.add(labelConexaoStatus);

	    setSize(dimension);
		setResizable(false);
	    setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setVisible(true);
		
	}
	
	/** Implementa a tentativa de login no sistema */
	private void tryLogin() {
		
		new Thread(() -> {
		
			// Recuperando login e senha da view
			final String login = textLogin.getText();
			final String senha = new String(textSenha.getPassword());
			
			label(false);
		
			// Aqui tento conectar ao banco e validar o usuário
			try {
				
				// Estabelece a conexão ao banco de acordo com a seleção na área de opções
				Database.LOCAL.connect(login,senha);
				
				new TelaKeyChestMain();
				dispose();
				
			}
			catch (SQLException exception) {
				label(true);
			}
			
		
		}).start();
		
	}
	
	/** Controla o label de conexão */
	private void label(boolean falha) {
		
		SwingUtilities.invokeLater(() -> {
			
			labelConexaoStatus.setVisible(true);
			
			if (falha) {
				labelConexaoStatus.setText("Falha na conexão ao banco");
				labelConexaoStatus.setIcon(null);
				labelConexaoStatus.setForeground(Color.RED);
			}
			else {
				labelConexaoStatus.setText("Validando credenciais...");
				labelConexaoStatus.setIcon(loading);
				labelConexaoStatus.setForeground(Color.BLACK);
			}
			
		});
		
	}
	
	/** Método para limpar os campos de texto da janela */
	private void limpaCampos() {
		textLogin.setText(null);
		textSenha.setText(null);
	}
	
}