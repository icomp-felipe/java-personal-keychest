package com.phill.keychest.view;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import com.phill.libs.*;
import com.phill.libs.ui.*;
import com.phill.libs.i18n.*;
import com.phill.libs.mfvapi.*;

import com.phill.keychest.bd.*;

/** Classe TelaKeychestLogin - cria um ambiente gráfico para o usuário fazer login no sistema
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 2.1, 22/FEV/2025 */
public class TelaKeyChestLogin extends JFrame {

	// Serial
	private static final long serialVersionUID = 541326736568738526L;
	
	// Declaração de atributos gráficos
	private final JTextField textLogin, textEndereco;
	private final JPasswordField textSenha;
	private final JRadioButton radioLocal, radioRemota;
	private final JButton buttonSair, buttonClear, buttonLogin;
	private final JLabel labelConexaoStatus;
	private final ImageIcon loading;
	
	// MFV API
	private final MandatoryFieldsManager fieldValidator;
	private final MandatoryFieldsLogger  fieldLogger;
	
	// Carregando bundle de idiomas
	private final static PropertyBundle bundle = new PropertyBundle("i18n/tela-keychest-login", null);
	
	public static void main(String[] args) {
		new TelaKeyChestLogin();
	}
	
	/** Construtor da classe TelaLogin - constrói a janela gráfica */
	public TelaKeyChestLogin() {
		super("KeyChest - Login");
		
		// Inicializando atributos gráficos
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"img/logo.png");
		ESCDispose.register(this);
		getContentPane().setLayout(null);
		
		// Recuperando ícones
		ImageIcon key = new ImageIcon(ResourceManager.getResource("img/login.png"));
		
		Icon exitIcon  = ResourceManager.getIcon("icon/on-off.png", 20, 20);
		Icon clearIcon = ResourceManager.getIcon("icon/brush.png" , 20, 20);
		Icon loginIcon = ResourceManager.getIcon("icon/user.png"  , 20, 20);
		
		this.loading = new ImageIcon(ResourceManager.getResource("icon/loading.gif"));
		
		// Recuperando fontes e cores
		Font  dialg = instance.getFont ();
		Color color = instance.getColor();
		Font fonte = instance.getFont(18);

		// Imagem da chave
		JLabel labelImagem = new JLabel(key);
		labelImagem.setBounds(12, 12, 232, 235);
		getContentPane().add(labelImagem);
		
		// Painel 'Credenciais'
		JPanel painelCredenciais = new JPanel();
		painelCredenciais.setBorder(instance.getTitledBorder("Credenciais"));
		painelCredenciais.setBounds(250, 10, 285, 110);
		painelCredenciais.setLayout(null);
		getContentPane().add(painelCredenciais);
		
		JLabel labelLogin = new JLabel("Login:");
		labelLogin.setBounds(12, 28, 75, 25);
		labelLogin.setHorizontalAlignment(JLabel.RIGHT);
		labelLogin.setFont(fonte);
		painelCredenciais.add(labelLogin);
		
		textLogin = new JTextField();
		textLogin.setBounds(95, 30, 180, 25);
		textLogin.setFont(dialg);
		textLogin.setForeground(color);
		textLogin.setToolTipText(bundle.getString("hint-text-login"));
		painelCredenciais.add(textLogin);
		
		JLabel labelSenha = new JLabel("Senha:");
		labelSenha.setBounds(12, 68, 75, 25);
		labelSenha.setHorizontalAlignment(JLabel.RIGHT);
		labelSenha.setFont(fonte);
		painelCredenciais.add(labelSenha);
		
		textSenha = new JPasswordField();
		textSenha.setEchoChar('*');
		textSenha.setBounds(95, 70, 180, 25);
		textSenha.setFont(dialg);
		textSenha.setForeground(color);
		textSenha.setToolTipText(bundle.getString("hint-text-senha"));
		painelCredenciais.add(textSenha);
		
		// Painel 'Opções'
		JPanel painelOpcoes = new JPanel();
		painelOpcoes.setBorder(instance.getTitledBorder("Opções"));
		painelOpcoes.setBounds(250, 120, 285, 100);
		painelOpcoes.setLayout(null);
		getContentPane().add(painelOpcoes);
		
		JLabel labelConexao = new JLabel("Conexão:");
		labelConexao.setHorizontalAlignment(JLabel.RIGHT);
		labelConexao.setFont(dialg);
		labelConexao.setBounds(12, 30, 75, 20);
		painelOpcoes.add(labelConexao);
		
		radioLocal = new JRadioButton("Local");
		radioLocal.setToolTipText(bundle.getString("hint-radio-local"));
		radioLocal.addActionListener((_) -> utilAddressLabel());
		radioLocal.setSelected(true);
		radioLocal.setFont(dialg);
		radioLocal.setBounds(105, 30, 70, 20);
		painelOpcoes.add(radioLocal);
		
		radioRemota = new JRadioButton("Remota");
		radioRemota.setToolTipText(bundle.getString("hint-radio-remota"));
		radioRemota.addActionListener((_) -> utilAddressLabel());
		radioRemota.setFont(dialg);
		radioRemota.setBounds(175, 30, 100, 20);
		painelOpcoes.add(radioRemota);
		
		ButtonGroup group = new ButtonGroup();
		group.add(radioRemota);
		group.add(radioLocal);
		
		JLabel labelEndereco = new JLabel("Endereço:");
		labelEndereco.setHorizontalAlignment(JLabel.RIGHT);
		labelEndereco.setFont(dialg);
		labelEndereco.setBounds(12, 60, 75, 20);
		painelOpcoes.add(labelEndereco);
		
		textEndereco = new JTextField("localhost");
		textEndereco.setToolTipText(bundle.getString("hint-text-endereco"));
		textEndereco.setEditable(false);
		textEndereco.setFont(dialg);
		textEndereco.setForeground(color);
		textEndereco.setBounds(95, 60, 180, 25);
		painelOpcoes.add(textEndereco);
		
		// Botões
		buttonSair = new JButton(exitIcon);
		buttonSair.addActionListener((_) -> dispose());
		buttonSair.setToolTipText(bundle.getString("hint-button-exit"));
		buttonSair.setBounds(335, 228, 30, 25);
		getContentPane().add(buttonSair);
		
		buttonClear = new JButton(clearIcon);
		buttonClear.addActionListener((_) -> actionClear());
		buttonClear.setToolTipText(bundle.getString("hint-button-clear"));
		buttonClear.setBounds(377, 228, 30, 25);
		getContentPane().add(buttonClear);
		
		buttonLogin = new JButton(loginIcon);
		buttonLogin.addActionListener((_) -> actionLogin());
		buttonLogin.setToolTipText(bundle.getString("hint-button-login"));
		buttonLogin.setBounds(419, 228, 30, 25);
		getContentPane().add(buttonLogin);
		
		labelConexaoStatus = new JLabel();
		labelConexaoStatus.setHorizontalAlignment(JLabel.LEFT);
		labelConexaoStatus.setBounds(12, 245, 220, 20);
		labelConexaoStatus.setVisible(false);
		getContentPane().add(labelConexaoStatus);
		
		// Definindo listener do botão 'Enter'
		KeyListener listener = (KeyReleasedListener) (event) -> { if (event.getKeyCode() == KeyEvent.VK_ENTER) buttonLogin.doClick(); };
		
		textLogin   .addKeyListener(listener);
		textSenha   .addKeyListener(listener);
		radioRemota .addKeyListener(listener);
		radioLocal  .addKeyListener(listener);
		textEndereco.addKeyListener(listener);
		
		// Cadastrando validação de campos
		this.fieldValidator = new MandatoryFieldsManager();
		this.fieldLogger    = new MandatoryFieldsLogger ();

		// Validação de Dados da GUI
		fieldValidator.addPermanent(labelLogin   , () -> !textLogin.getText().isBlank()                , bundle.getString("klogin-mfv-login"), false);
		fieldValidator.addPermanent(labelSenha   , () -> !new String(textSenha.getPassword()).isBlank(), bundle.getString("klogin-mfv-senha"), false);
		fieldValidator.addPermanent(labelEndereco, () -> !textEndereco.getText().isBlank()             , bundle.getString("klogin-mfv-addr" ), false);
		
	    setSize(550,300);
		setResizable(false);
	    setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
	}
	
	/******************** Bloco de Tratamento de Eventos de Botões *************************/
	
	/** Limpa os campos de entrada de dados. */
	private void actionClear() {
		
		textLogin.setText(null);
		textSenha.setText(null);
		
		textLogin.requestFocus();
		
	}
	
	/** Realiza o login no sistema. */
	private void actionLogin() {
		
		// Realizando validação dos campos antes de prosseguir
		fieldValidator.validate(fieldLogger);
		
		// Só prossigo se todas os campos foram devidamente preenchidos
		if (fieldLogger.hasErrors()) {
							
			final String errors = bundle.getFormattedString("klogin-save-errors", fieldLogger.getErrorString());
							
			AlertDialog.error(this, getTitle(), errors);
			fieldLogger.clear(); return;
											
		}
		
		new Thread(() -> {
		
			// Recuperando login e senha da view
			final String login  = textLogin.getText();
			final String senha  = new String(textSenha.getPassword());
			final String server = textEndereco.getText().trim();
			
			// Exibe o label 'carregando...'
			utilShowLabel(false); utilLockFields(true);
		
			// Aqui tento conectar ao banco e validar o usuário
			try {
				
				// Estabelece a conexão ao banco de acordo com a seleção na área de opções
				Database.INSTANCE.connect(server, login, senha);
				
				new TelaKeyChestMain(server);
				dispose();
				
			}
			catch (Exception exception) {
				
				// Exibe o label 'falha'
				utilShowLabel(true);
				
			}
			finally {
				
				utilLockFields(false);
				
			}
			
		}).start();
		
	}
	
	/************************ Bloco de Métodos Utilitários *********************************/
	
	/** Controla o comportamento do input de endereços */
	private void utilAddressLabel() {

		if (radioLocal.isSelected())
			textEndereco.setText("localhost");
		else {
			textEndereco.setText(null);
			textEndereco.requestFocus();
		}
		
		textEndereco.setEditable(!radioLocal.isSelected());
		
	}
	
	/** Controla o bloqueio dos campos de entrada da tela.
	 *  @param lockFields - bloqueia ou desbloqueia os campos de entrada de dados */
	private void utilLockFields(final boolean lockFields) {
		
		final boolean lock = !lockFields;
		
		SwingUtilities.invokeLater(() -> {
		
			textLogin   .setEditable(lock);
			textSenha   .setEditable(lock);
			textEndereco.setEditable(!radioLocal.isSelected());
			
			radioLocal .setEnabled(lock);
			radioRemota.setEnabled(lock);
			
			buttonSair .setEnabled(lock);
			buttonClear.setEnabled(lock);
			buttonLogin.setEnabled(lock);
		
		});
		
	}
	
	/** Controla o label de conexão */
	private void utilShowLabel(final boolean falha) {
		
		SwingUtilities.invokeLater(() -> {
			
			labelConexaoStatus.setVisible(true);
			
			if (falha) {
				labelConexaoStatus.setText(bundle.getString("klogin-label-fail"));
				labelConexaoStatus.setIcon(null);
				labelConexaoStatus.setForeground(Color.RED);
			}
			else {
				
				labelConexaoStatus.setText(bundle.getString("klogin-label-running"));
				labelConexaoStatus.setIcon(loading);
				labelConexaoStatus.setForeground(Color.BLACK);
				
			}
			
		});
		
	}
	
}