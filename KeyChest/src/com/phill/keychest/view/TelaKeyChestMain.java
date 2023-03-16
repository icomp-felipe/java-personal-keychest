package com.phill.keychest.view;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import com.phill.keychest.bd.*;
import com.phill.keychest.model.*;
import com.phill.keychest.controller.*;
import com.phill.libs.*;
import com.phill.libs.i18n.PropertyBundle;
import com.phill.libs.sys.ClipboardUtils;
import com.phill.libs.ui.*;
import com.phill.libs.table.JTableMouseListener;
import com.phill.libs.table.LockedTableModel;
import com.phill.libs.table.PWDTableCellRenderer;
import com.phill.libs.table.TableUtils;

/** Tela principal do sistema de gerenciamento de credenciais.
 *  @author Felipe André - fass@icomp.ufam.edu.br
 *  @version 2.1, 16/MAR/2023 */
public class TelaKeyChestMain extends JFrame {
	
	// Serial da JFrame
	private static final long serialVersionUID = 1066745826416399579L;
	
	// Atributos gráficos
	private final JTextField textServico;
	private final JButton buttonUserUpdate, buttonUserDelete;
	private final JComboBox<String> comboUsuarios;
	private final JLabel labelInfo, labelQTD;
	private final JToggleButton buttonUserDefault;
	
	// Atributos gráficos (Tabela)
	private final JTable tableResultado;
    private final DefaultTableModel modelo;

    // Atributos dinâmicos
    private Thread waitThread;
	private ArrayList<Owner> ownerList;
	private ArrayList<Credentials> credentialsList;
	
	// Carregando bundle de idiomas
	private final static PropertyBundle bundle = new PropertyBundle("i18n/tela-keychest-main", null);

	/** Constrói a interface gráfica e inicializa as variáveis de controle */
	public TelaKeyChestMain(final String serverURL) {
		super("KeyChest - build 20230316 [" + serverURL + "]");
		
		// Inicializando atributos gráficos
		GraphicsHelper instance = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"img/logo.png");
		ESCDispose.register(this);
		
		// Recuperando ícones
		Icon clearIcon   = ResourceManager.getIcon("icon/clear.png",20,20);
		Icon addIcon     = ResourceManager.getIcon("icon/add.png",20,20);
		Icon deleteIcon  = ResourceManager.getIcon("icon/delete.png",20,20);
		Icon updateIcon  = ResourceManager.getIcon("icon/edit.png",20,20);
		Icon exitIcon    = ResourceManager.getIcon("icon/shutdown.png",20,20);
		Icon defaultIcon = ResourceManager.getIcon("icon/default.png",20,20);
		
		// Recuperando fontes e cores
		Font  fonte  = instance.getFont ();
		Font  ubuntu = instance.getUbuntuFont();
		Color color  = instance.getColor();
		
		// Painel 'Parâmetros'
		JPanel painelParametros = new JPanel();
		painelParametros.setBorder(instance.getTitledBorder("Parâmetros"));
		painelParametros.setBounds(10, 10, 940, 100);
		getContentPane().add(painelParametros);
		painelParametros.setLayout(null);
		
		// Subpainel 'Serviço'
		JPanel painelServico = new JPanel();
		painelServico.setBorder(instance.getTitledBorder("Serviço"));
		painelServico.setBounds(10, 20, 450, 70);
		painelParametros.add(painelServico);
		painelServico.setLayout(null);
		
		textServico = new JTextField();
		textServico.addKeyListener((KeyReleasedListener) (event) -> listenerSearch());
		textServico.setToolTipText(bundle.getString("hint-text-servico"));
		textServico.setFont(fonte);
		textServico.setBounds(10, 30, 390, 25);
		painelServico.add(textServico);
		
		JButton buttonServiceClear = new JButton(clearIcon);
		buttonServiceClear.addActionListener((event) -> actionClear());
		buttonServiceClear.setToolTipText(bundle.getString("hint-button-clear"));
		buttonServiceClear.setBounds(410, 30, 30, 25);
		painelServico.add(buttonServiceClear);
		
		// Subpainel 'Usuário'
		JPanel panelUsuario = new JPanel();
		panelUsuario.setLayout(null);
		panelUsuario.setBorder(instance.getTitledBorder("Usuário"));
		panelUsuario.setBounds(465, 20, 465, 70);
		painelParametros.add(panelUsuario);
		
		comboUsuarios = new JComboBox<String>();
		comboUsuarios.setFont(fonte);
		comboUsuarios.addActionListener((event) -> listenerCombo());
		comboUsuarios.addActionListener((event) -> listenerSearch ());
		comboUsuarios.setToolTipText(bundle.getString("hint-combo-usuario"));
		comboUsuarios.setBounds(10, 30, 258, 25);
		panelUsuario.add(comboUsuarios);
		
		JButton buttonUserCreate = new JButton(addIcon);
		buttonUserCreate.addActionListener((event) -> actionUserCreate());
		buttonUserCreate.setToolTipText(bundle.getString("hint-button-create"));
		buttonUserCreate.setBounds(285, 30, 30, 25);
		panelUsuario.add(buttonUserCreate);
		
		buttonUserUpdate = new JButton(updateIcon);
		buttonUserUpdate.addActionListener((event) -> actionUserUpdate());
		buttonUserUpdate.setToolTipText(bundle.getString("hint-button-update"));
		buttonUserUpdate.setBounds(330, 30, 30, 25);
		panelUsuario.add(buttonUserUpdate);
		
		buttonUserDelete = new JButton(deleteIcon);
		buttonUserDelete.addActionListener((event) -> actionUserDelete());
		buttonUserDelete.setToolTipText(bundle.getString("hint-button-delete"));
		buttonUserDelete.setBounds(375, 30, 30, 25);
		panelUsuario.add(buttonUserDelete);
		
		buttonUserDefault = new JToggleButton(defaultIcon);
		buttonUserDefault.addActionListener((event) -> actionToggleDefault());
		buttonUserDefault.setToolTipText(bundle.getString("hint-button-toggle"));
		buttonUserDefault.setBounds(420, 30, 30, 25);
		panelUsuario.add(buttonUserDefault);
		
		// Painel 'Listagem'
		JPanel panelListagem = new JPanel();
		panelListagem.setBorder(instance.getTitledBorder("Listagem            "));
		panelListagem.setBounds(10, 110, 940, 375);
		panelListagem.setOpaque(false);
		panelListagem.setLayout(null);
		getContentPane().add(panelListagem);
		
		JButton buttonKeyCreate = new JButton(addIcon);
		buttonKeyCreate.addActionListener((event) -> actionEntryCreate());
		buttonKeyCreate.setToolTipText(bundle.getString("hint-button-newkey"));
		buttonKeyCreate.setBounds(95, 110, 30, 25);
		getContentPane().add(buttonKeyCreate);
		
		modelo = new LockedTableModel(new String [] {"Serviço","Usuário","Login","Senha", "Tamanho", "Última Atualização"});
		
		tableResultado = new JTable(modelo);
		tableResultado.setRowHeight(20);
		tableResultado.getTableHeader().setFont(fonte);
		tableResultado.setFont(ubuntu);
		tableResultado.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableResultado.addMouseListener(new JTableMouseListener(tableResultado));

		tableResultado.getColumnModel().getColumn(3).setCellRenderer(new PWDTableCellRenderer());
		tableResultado.getColumnModel().getColumn(3).setCellEditor  (new DefaultCellEditor(new JPasswordField()));
		
		final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		TableColumnModel columnModel = tableResultado.getColumnModel();
		
		columnModel.getColumn(1).setCellRenderer(centerRenderer);
		columnModel.getColumn(4).setCellRenderer(centerRenderer);
		columnModel.getColumn(5).setCellRenderer(centerRenderer);
		
		columnModel.getColumn(0).setPreferredWidth(127);
		columnModel.getColumn(1).setPreferredWidth( 43);
		columnModel.getColumn(2).setPreferredWidth(180);
		columnModel.getColumn(3).setPreferredWidth( 82);
		columnModel.getColumn(4).setPreferredWidth( 15);
		columnModel.getColumn(5).setPreferredWidth( 96);
		
		JScrollPane scrollListagem = new JScrollPane(tableResultado);
		scrollListagem.setBounds(10, 35, 915, 305);
		panelListagem.add(scrollListagem);
		
		labelQTD = new JLabel();
		labelQTD.setFont(fonte);
		labelQTD.setForeground(color);
		labelQTD.setBounds(10, 345, 920, 20);
		panelListagem.add(labelQTD);
		
		labelInfo = new JLabel();
		labelInfo.setFont(fonte);
		labelInfo.setForeground(color);
		labelInfo.setBounds(10, 500, 900, 25);
		getContentPane().add(labelInfo);
		
		JButton buttonExit = new JButton(exitIcon);
		buttonExit.addActionListener((event) -> dispose());
		buttonExit.setToolTipText(bundle.getString("hint-button-exit"));
		buttonExit.setBounds(915, 500, 30, 25);
		getContentPane().add(buttonExit);
		
		// Criando menu de popup da tabela
		onCreateOptionsPopupMenu();
		
		// Ações padrão de inicialização
		actionFillCombos();
		actionSelectDefaultUser();
		
		setSize(960,560);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		setVisible(true);
		
	}
	
	/** Cria as opções do menu de popup da tabela. */
	private void onCreateOptionsPopupMenu() {
		
		JPopupMenu popupMenu = new JPopupMenu();
		
		// Definindo aceleradores
		KeyStroke editar  = KeyStroke.getKeyStroke(KeyEvent.VK_F2    , 0);
		KeyStroke deletar = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
		KeyStroke cpLogin = KeyStroke.getKeyStroke(KeyEvent.VK_L     , InputEvent.CTRL_DOWN_MASK);
		KeyStroke cpSenha = KeyStroke.getKeyStroke(KeyEvent.VK_S     , InputEvent.CTRL_DOWN_MASK);
		
		// Definindo ações dos itens de menu
		Action actionEditar  = new ShortcutAction("Editar"       , KeyEvent.VK_E, editar , (event) -> actionEntryEdit  ());
		Action actionDeletar = new ShortcutAction("Excluir"      , null         , deletar, (event) -> actionEntryDelete());
		Action actionCpLogin = new ShortcutAction("Copiar Login" , KeyEvent.VK_L, cpLogin, (event) -> actionLoginCopy  ());
		Action actionCpSenha = new ShortcutAction("Copiar Senha" , KeyEvent.VK_S, cpSenha, (event) -> actionPassCopy   ());
		
		// Declarando os itens de menu
		JMenuItem itemEditar  = new JMenuItem(actionEditar)  ; popupMenu.add(itemEditar);
		JMenuItem itemDeletar  = new JMenuItem(actionDeletar); popupMenu.add(itemDeletar);
		
		popupMenu.addSeparator();
		
		JMenuItem itemCopiaLogin = new JMenuItem(actionCpLogin); popupMenu.add(itemCopiaLogin);
		JMenuItem itemCopiaPwd = new JMenuItem(actionCpSenha)  ; popupMenu.add(itemCopiaPwd);
		
		// Definindo atalhos de teclado
		final InputMap  imap = tableResultado.getInputMap (JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		final ActionMap amap = tableResultado.getActionMap();
		
		imap.put(editar , "actionEditar" );
		imap.put(deletar, "actionDeletar");
		imap.put(cpLogin, "actionCpLogin");
		imap.put(cpSenha, "actionCpSenha");
		
		amap.put("actionEditar" , actionEditar );
		amap.put("actionDeletar", actionDeletar);
		amap.put("actionCpLogin", actionCpLogin);
		amap.put("actionCpSenha", actionCpSenha);
		
		// Atribuindo menu à tabela
		tableResultado.setComponentPopupMenu(popupMenu);
		
	}
	
	/************************** Implementação dos Listeners *******************************/
	
	/** Ajusta a visibilidade dos botões de edição de usuário. Estes só estão disponíveis
	 *  quando algum usuário está selecionado. Também controla o estado do botão de usuário
	 *  padrão, de acordo com o usuário selecionado e o padrão configurado no banco de dados. */
	private void listenerCombo() {
		
		boolean buttonVisibility = comboUsuarios.getSelectedIndex() > 0;

		buttonUserUpdate .setEnabled(buttonVisibility);
		buttonUserDelete .setEnabled(buttonVisibility);
		buttonUserDefault.setEnabled(buttonVisibility);
		
		// Se algum usuário foi selecionado
		if (buttonVisibility) {
			
			// Recupero este usuário + o padrão do banco de dados
			final Owner ownerDefault  = ConfigDAO.getDefaultUser();
			final Owner ownerSelected = ownerList.get(comboUsuarios.getSelectedIndex()-1);
			
			// Dependendo desta condição, sinalizo no botão
			if (ownerSelected.equals(ownerDefault))
				buttonUserDefault.setSelected(true);
			else
				buttonUserDefault.setSelected(false);
			
		}
		
		// Se nenhum usuário foi selecionado, removo qualquer seleção que tenha no botão.
		else
			buttonUserDefault.setSelected(false);
		
	}
	
	/** Realiza as buscas de credenciais do sistema de acordo com os parâmetros de entrada (serviço e usuário). */
	private synchronized void listenerSearch() {
		
		// Recuperando o serviço e o usuário da tela
		final String service = textServico.getText();
		final Owner  owner   = comboUsuarios.getSelectedIndex() > 0 ? ownerList.get(comboUsuarios.getSelectedIndex()-1) : null;
		
		// Realizando a busca e atualizando a lista interna
		this.credentialsList = CredentialsDAO.list(service,owner);
		
		// Atualizando a tabela com os novos dados
		TableUtils.clear(modelo);
		
		for (Credentials credentials: this.credentialsList)
			TableUtils.add(modelo,credentials);
		
		utilSizeUpdate();
		
	}
	
	/********************** Tratamento de Eventos de Usuários *****************************/
	
	/** Limpa o campo de busca de serviço e atualiza a tabela */
	private void actionClear() {
		
		textServico.setText(null);
		textServico.requestFocus();
		
		listenerSearch();
		
	}
	
	/** Exibe a tela de criação de um novo usuário */
	private void actionUserCreate() {
		
		// Exibindo o diálogo
		String title = bundle.getString("kchest-user-create-title");
		String user  = AlertDialog.input(this, title, bundle.getString("kchest-user-create-dialog"));
		
		// Se entrei com um nome válido...
		if ((user != null) && (!user.isEmpty())) {
			
			try {
				
				// ...preparo um novo objeto, ...
				final Owner owner = new Owner();
				owner.setName(user);
				
				// ...o insiro no banco de dados e, ...
				OwnerDAO.commit(owner);
				
				AlertDialog.info(this, title, bundle.getString("kchest-user-create-success"));
				
				// ...por fim, atualizo o combo de usuários e seleciono o novo usuário criado
				actionFillCombos();
				comboUsuarios.setSelectedItem(user);
				
			}
			catch (SQLIntegrityConstraintViolationException exception) {
				
				AlertDialog.error(this, title, bundle.getString("kchest-user-create-duplica"));
				
			}
			catch (Exception exception) {
				
				exception.printStackTrace();
				AlertDialog.error(this, title, bundle.getString("kchest-user-create-exception"));
				
			}
			
		}
		
	}
	
	/** Exibe a tela de remoção de um usuário selecionado */
	private void actionUserDelete() {
		
		// Aqui recupero o objeto usuário selecionado do meu ArrayList interno, ...
		final int      index = comboUsuarios.getSelectedIndex() - 1;
		final Owner selected = ownerList.get(index);

		// exibo um diálogo de confirmação de exclusão...
		String title  = bundle.getString("kchest-user-delete-title");
		String dialog = bundle.getFormattedString("kchest-user-delete-confirm", selected.getName());
		
		// e, se desejo mesmo excluir...
		if (AlertDialog.dialog(this, title, dialog) == AlertDialog.OK_OPTION) {
			
			try {
				
				OwnerDAO.delete(selected);
				
				AlertDialog.info(this, title, bundle.getString("kchest-user-delete-success"));
				
				// Por fim, atualizo o combo de usuários 
				actionFillCombos();
				
			}
			catch (SQLIntegrityConstraintViolationException exception) {
				
				AlertDialog.error(this, title, bundle.getString("kchest-user-delete-constraint"));
				
			}
			catch (Exception exception) {
				
				exception.printStackTrace();
				AlertDialog.error(this, title, bundle.getString("kchest-user-delete-exception"));
				
			}
			
		}
		
	}
	
	/** Exibe a tela de edição de um usuário selecionado */
	private void actionUserUpdate() {
		
		// Exibindo o diálogo
		String title = bundle.getString("kchest-user-update-title");
		String user  = AlertDialog.input(this, title, bundle.getString("kchest-user-update-dialog"));
		
		// Se entrei com um nome válido...
		if ((user != null) && (!user.isEmpty()) ) {
		
			// Recupero o objeto usuário selecionado do meu ArrayList interno, ...
			final int      index = comboUsuarios.getSelectedIndex() - 1;
			final Owner selected = ownerList.get(index);
			
			// Quebra a execução caso o nome de destino seja igual ao de origem
			if (selected.getName().equals(user)) return;

			// exibo um diálogo de confirmação de atualização...
			String dialog = bundle.getFormattedString("kchest-user-update-confirm", selected.getName(), user);
		
			// e, se desejo mesmo atualizar...
			if (AlertDialog.dialog(this, title, dialog) == AlertDialog.OK_OPTION) {
			
				try {
					
					// ...atualizo os dados no banco
					selected.setName(user);
					OwnerDAO.commit(selected);
					
					AlertDialog.info(this, title, bundle.getString("kchest-user-update-success"));
					
					// Por fim, atualizo o combo de usuários 
					actionFillCombos();
					
				}
				catch (SQLIntegrityConstraintViolationException exception) {
					
					AlertDialog.error(this, title, bundle.getString("kchest-user-update-duplica"));
					
				}
				catch (Exception exception) {
					
					exception.printStackTrace();
					AlertDialog.error(this, title, bundle.getString("kchest-user-update-exception"));
					
				}
				
			}
		
		}
		
	}
	
	/** Configura o usuário padrão da apĺicação no banco de dados. */
	private void actionToggleDefault() {
		
		// Aqui recupero o objeto usuário selecionado do meu ArrayList interno, ...
		final int      index = comboUsuarios.getSelectedIndex() - 1;
		final Owner selected = ownerList.get(index);
		
		try {
			
			// Sempre limpo a config ao clicar no botão
			ConfigDAO.deleteDefaultUser();
			
			// Caso tenha sido selecionado, configuro um novo usuário padrão
			if (buttonUserDefault.isSelected())
				ConfigDAO.insertDefaultUser(selected);
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			AlertDialog.error(this, bundle.getString("kchest-user-toggle-title"), bundle.getString("kchest-user-toggle-exception"));
			
		}
		
	}
	
	/********************** Tratamento de Eventos de Credenciais **************************/
	
	/** Exibe a tela de criação de uma nova credencial. */
	private void actionEntryCreate() {
		
		// Por conveniência, se um usuário foi selecionado no combo, o seleciono na próxima tela
		final Owner owner = comboUsuarios.getSelectedIndex() > 0 ? ownerList.get(comboUsuarios.getSelectedIndex()-1) : null;
		
		// Construindo a janela...
		PanelCredentials screen = new PanelCredentials(ownerList);
		screen.setOwner(owner);
		
		JOptionPane pane = new JOptionPane(screen, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
			
			private static final long serialVersionUID = -8833981707282178268L;

			@Override
			public void selectInitialValue() {
				screen.getFocusField().requestFocusInWindow();
			}
			
		};
		
		// Building dialog
		pane.createDialog("KeyChest v.2.0 - Nova Entrada").setVisible(true);
		
		try {
			
			int option = Integer.parseInt(pane.getValue().toString());
			
			// se a opção, "OK" foi selecionada...
			
			if (option == JOptionPane.OK_OPTION) {
				
				// salvo os dados no banco e atualizo a tabela
				screen.commit();
				listenerSearch();
				
			}
			
		}
		catch (NumberFormatException exception) {
			
		}
		
	}
	
	/******************** Tratamento de Eventos de Menu da Tabela *************************/
	
	/** Edita os dados de uma credencial selecionada da tabela. */
	private void actionEntryEdit() {
		
		// Recuperando a credencial associada na ArrayList
		Credentials selected = tableResultado.getSelectedRow() >= 0 ? credentialsList.get(tableResultado.getSelectedRow()) : null;
		
		// Se algo foi selecionado...
		if (selected != null) {
			
			// Construo a tela de edição...
			PanelCredentials screen = new PanelCredentials(ownerList,selected);
			screen.setOpaque(false);
			
			JOptionPane pane = new JOptionPane(screen, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void selectInitialValue() {
					screen.getFocusField().requestFocusInWindow();
				}
				
			};
			
			// Building dialog
			pane.createDialog("KeyChest v.2.0 - Editar Credencial").setVisible(true);
			
			try {
				
				int option = Integer.parseInt(pane.getValue().toString());
				
				// se a opção, "OK" foi selecionada...
				
				if (option == JOptionPane.OK_OPTION) {
					
					// salvo os dados no banco e atualizo a tabela
					screen.commit();
					listenerSearch();
					
				}
				
			}
			catch (NumberFormatException exception) {
				
			}
			
		}
		
	}
	
	/** Remove do banco de dados uma entrada selecionada na tabela. */
	private void actionEntryDelete() {
		
		// Recuperando a credencial associada na ArrayList
		Credentials selected = tableResultado.getSelectedRow() >= 0 ? credentialsList.get(tableResultado.getSelectedRow()) : null;
		
		// Se algo foi selecionado...
		if (selected != null) {
			
			// Construo a mensagem de aviso...
			String title  = bundle.getString("kchest-entry-delete-title");
			String dialog = bundle.getString("kchest-entry-delete-dialog");
			
			// e se a opção "OK" foi escolhida...
			if (AlertDialog.dialog(this, title, dialog) == AlertDialog.OK_OPTION) {
				
				try {
					
					// removo a credencial da base de dados e exibo uma mensagem
					CredentialsDAO.delete(selected);
					
				}
				catch (Exception exception) {
					
					exception.printStackTrace();
					AlertDialog.error(this, title, bundle.getString("kchest-entry-delete-exception"));
					
				}
				finally {
					
					// Atualiza a tabela
					listenerSearch();
					
				}
				
			}
			
		}
		
	}
	
	/** Copia o login de uma linha selecionada para a área de transferência */
	private void actionLoginCopy() {
		
		try {
			
			final String login = (String) modelo.getValueAt(tableResultado.getSelectedRow(),2);
			
			ClipboardUtils.paste(login);
			
			showInfo("Login copiado para a área de transferência");
			
		}
		catch (Exception exception) { }
		
	}
	
	/** Copia a senha de uma linha selecionada para a área de transferência */
	private void actionPassCopy() {
		
		try {
			
			final String pwd = (String) modelo.getValueAt(tableResultado.getSelectedRow(),3);
			
			ClipboardUtils.paste(pwd);
			
			showInfo("Senha copiada para a área de transferência");
			
		}
		catch (Exception exception) { }
		
	}
	
	/** Controla a exibição de mensagens na área inferior esquerda do programa.
	 *  A cada execução, este método exibe por 5 segundos a mensagem na tela.
	 *  Se uma mensagem já estava sendo exibida, ela é sobrescrita e o tempo, reiniciado.
	 *  @param message - Mensagem a ser exibida no label de mensagens */
	private void showInfo(String message) {
		
		// Exibindo a mensagem
		Runnable job = () -> {
			labelInfo.setText(message);
			labelInfo.setVisible(true);
		};
		
		SwingUtilities.invokeLater(job);
		
		// Aqui controlo a thread de tempo: se uma execução já estava ativa, interrompo-a
		if ((waitThread != null) && waitThread.isAlive())
			 waitThread.interrupt();
		
		// Declarando o trabalho: dormir por 5 segundos e depois sumir com a mensagem
		Runnable sleepJob = () -> {
			try {
				Thread.sleep(5000);
				SwingUtilities.invokeLater(() -> labelInfo.setVisible(false));
			} catch (InterruptedException e) {}
			
		};
		
		// Executando o serviço propriamente dito
		waitThread = new Thread(sleepJob);
		waitThread.start();
		
	}

	/************************** Implementação dos Utilitários *****************************/
	
	/** Inicializa o comboBox com os dados de usuário recuperados do banco */
	private void actionFillCombos() {
		
		// Limpando o combo
		comboUsuarios.removeAllItems();
		comboUsuarios.addItem("Todos");
		
		// Atualizando a lista de usuários
		this.ownerList = OwnerDAO.list();
		
		for (Owner owner: this.ownerList)
			comboUsuarios.addItem(owner.getName());
		
	}
	
	// Seleciona no combo o usuário padrão, apenas na instanciação desta tela
	private void actionSelectDefaultUser() {
		
		final Owner retrieved = ConfigDAO.getDefaultUser();
		
		if (retrieved != null)
			comboUsuarios.setSelectedItem(retrieved.getName());
		
	}
	
	/** Desconecta a aplicação do banco de dados e encerra o programa */
	@Override
	public void dispose() {
		
		try { Database.INSTANCE.disconnect(); }
		catch (SQLException exception) { exception.printStackTrace(); }
		finally { super.dispose(); }
		
	}
	
	/** Atualiza a quantidade de credenciais encontradas no fim da tabela. */
	private void utilSizeUpdate() {
		
		final int size = this.credentialsList.size();
		
		if (size == 1)
			labelQTD.setText("1 credencial encontrada");
		else
			labelQTD.setText(size + " credenciais encontradas");
		
	}
}
