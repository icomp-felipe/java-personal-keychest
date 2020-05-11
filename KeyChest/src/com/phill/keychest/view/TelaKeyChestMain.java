package com.phill.keychest.view;

import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import com.phill.keychest.bd.*;
import com.phill.keychest.model.*;
import com.phill.keychest.controller.*;
import com.phill.libs.*;

/** Tela principal do sistema de gerenciamento de credenciais.
 *  @author Felipe André - fass@icomp.ufam.edu.br
 *  @version 1.3, 11/05/2020 */
public class TelaKeyChestMain extends JFrame {
	
	// Serial da JFrame
	private static final long serialVersionUID = 1L;
	
	// Atributos gráficos
	private final JTextField textServico;
	private final JButton botaoUsuarioAtualiza, botaoUsuarioDeleta;
	private final JComboBox<String> comboUsuarios;
	private final JLabel labelInfo, labelQTD;
	private final JToggleButton botaoPadrao;
	
	// Atributos gráficos (Tabela)
	private final JTable tableResultado;
    private final DefaultTableModel modelo;
    private final String[] colunas = new String [] {"Serviço","Usuário","Login","Senha", "Tamanho"};

    // Atributos dinâmicos
    private Thread waitThread;
	private ArrayList<Owner> ownerList;
	private ArrayList<Credentials> credentialsList;

	/** Constrói a interface gráfica e inicializa as variáveis de controle */
	public TelaKeyChestMain(final String serverURL) {
		super("KeyChest - build 20200508 [" + serverURL + "]");
		
		// Inicializando atributos gráficos
		GraphicsHelper helper = GraphicsHelper.getInstance();
		GraphicsHelper.setFrameIcon(this,"img/logo.png");
		
		Dimension dimension = new Dimension(960,560);
		JPanel mainFrame = new JPaintedPanel("img/background.png",dimension);
		
		mainFrame.setLayout(null);
		setContentPane(mainFrame);
		
		// Recuperando fontes e cores
		Font  fonte = helper.getFont ();
		Color color = helper.getColor();
		
		// Recuperando ícones
		Icon clearIcon   = ResourceManager.getResizedIcon("icon/clear.png",20,20);
		Icon addIcon     = ResourceManager.getResizedIcon("icon/add.png",20,20);
		Icon deleteIcon  = ResourceManager.getResizedIcon("icon/delete.png",20,20);
		Icon updateIcon  = ResourceManager.getResizedIcon("icon/edit.png",20,20);
		Icon exitIcon    = ResourceManager.getResizedIcon("icon/shutdown.png",20,20);
		Icon defaultIcon = ResourceManager.getResizedIcon("icon/default.png",20,20);
		
		setSize(dimension);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);		// Tira a função do botão de fechar, pra usar apenas o dispose()
		mainFrame.setLayout(null);
		
		JPanel painelParametros = new JPanel();
		painelParametros.setOpaque(false);
		painelParametros.setBorder(helper.getTitledBorder("Parâmetros"));
		painelParametros.setBounds(12, 12, 936, 110);
		mainFrame.add(painelParametros);
		painelParametros.setLayout(null);
		
		JPanel painelServico = new JPanel();
		painelServico.setOpaque(false);
		painelServico.setBorder(helper.getTitledBorder("Serviço"));
		painelServico.setBounds(12, 25, 450, 70);
		painelParametros.add(painelServico);
		painelServico.setLayout(null);
		
		textServico = new JTextField();
		textServico.addKeyListener((KeyboardAdapter) (event) -> listener_query());
		textServico.setFont(fonte);
		textServico.setForeground(color);
		textServico.setBounds(12, 30, 385, 25);
		painelServico.add(textServico);
		textServico.setColumns(10);
		
		JButton botaoServicoLimpa = new JButton(clearIcon);
		botaoServicoLimpa.addActionListener((event) -> action_clear());
		botaoServicoLimpa.setToolTipText("Limpa este campo de busca");
		botaoServicoLimpa.setBounds(408, 29, 30, 25);
		painelServico.add(botaoServicoLimpa);
		
		JPanel painelUsuario = new JPanel();
		painelUsuario.setOpaque(false);
		painelUsuario.setLayout(null);
		painelUsuario.setBorder(helper.getTitledBorder("Usuário"));
		painelUsuario.setBounds(474, 25, 450, 70);
		painelParametros.add(painelUsuario);
		
		comboUsuarios = new JComboBox<String>();
		comboUsuarios.setFont(fonte);
		comboUsuarios.setForeground(color);
		comboUsuarios.addActionListener((event) -> listener_combo());
		comboUsuarios.addActionListener((event) -> listener_query());
		comboUsuarios.setBounds(12, 30, 258, 25);
		painelUsuario.add(comboUsuarios);
		
		JButton botaoUsuarioCria = new JButton(addIcon);
		botaoUsuarioCria.addActionListener((event) -> action_create_user());
		botaoUsuarioCria.setToolTipText("Cria um novo usuário");
		botaoUsuarioCria.setBounds(282, 30, 30, 25);
		painelUsuario.add(botaoUsuarioCria);
		
		botaoUsuarioAtualiza = new JButton(updateIcon);
		botaoUsuarioAtualiza.addActionListener((event) -> action_update_user());
		botaoUsuarioAtualiza.setToolTipText("Atualiza o usuário selecionado");
		botaoUsuarioAtualiza.setBounds(324, 30, 30, 25);
		painelUsuario.add(botaoUsuarioAtualiza);
		
		botaoUsuarioDeleta = new JButton(deleteIcon);
		botaoUsuarioDeleta.addActionListener((event) -> action_delete_user());
		botaoUsuarioDeleta.setToolTipText("Remove o usuário selecionado");
		botaoUsuarioDeleta.setBounds(366, 30, 30, 25);
		painelUsuario.add(botaoUsuarioDeleta);
		
		botaoPadrao = new JToggleButton(defaultIcon);
		botaoPadrao.setToolTipText("Usuário padrão");
		botaoPadrao.addActionListener((event) -> action_toggle_default());
		botaoPadrao.setBounds(408, 30, 30, 25);
		painelUsuario.add(botaoPadrao);
		
		JButton botaoCredencialAdd = new JButton(addIcon);
		botaoCredencialAdd.setToolTipText("Adiciona novas credenciais ao sistema");
		botaoCredencialAdd.addActionListener((event) -> action_credential_new());
		botaoCredencialAdd.setBounds(95, 129, 30, 25);
		mainFrame.add(botaoCredencialAdd);
		
		JPanel painelListagem = new JPanel();
		painelListagem.setOpaque(false);
		painelListagem.setBorder(helper.getTitledBorder("Listagem            "));
		painelListagem.setBounds(12, 134, 936, 350);
		painelListagem.setLayout(null);
		mainFrame.add(painelListagem);
		
		modelo  = new LockedTableModel(colunas);
		
		tableResultado = new JTable(modelo);
		tableResultado.setOpaque(false);
		tableResultado.addMouseListener(new TableMouseListener(tableResultado));
		tableResultado.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		tableResultado.getColumnModel().getColumn(3).setCellRenderer(new PasswordFieldTableCellRenderer());
		tableResultado.getColumnModel().getColumn(3).setCellEditor  (new DefaultCellEditor(new JPasswordField()));
		
		final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		TableColumnModel columnModel = tableResultado.getColumnModel();
		
		columnModel.getColumn(4).setCellRenderer(centerRenderer);
		
		columnModel.getColumn(0).setPreferredWidth(165);
		columnModel.getColumn(1).setPreferredWidth(195);
		columnModel.getColumn(2).setPreferredWidth(195);
		columnModel.getColumn(3).setPreferredWidth(95);
		columnModel.getColumn(4).setPreferredWidth(25);
		
		JScrollPane scrollListagem = new JScrollPane(tableResultado);
		scrollListagem.setOpaque(false);
		scrollListagem.getViewport().setOpaque(false);
		scrollListagem.setBounds(12, 35, 912, 280);
		painelListagem.add(scrollListagem);
		
		labelQTD = new JLabel();
		labelQTD.setFont(fonte);
		labelQTD.setForeground(color);
		labelQTD.setBounds(12, 320, 912, 20);
		painelListagem.add(labelQTD);
		
		JButton botaoSair = new JButton(exitIcon);
		botaoSair.addActionListener((event) -> dispose());
		botaoSair.setToolTipText("Sai do sistema");
		botaoSair.setBounds(918, 498, 30, 25);
		mainFrame.add(botaoSair);
		
		labelInfo = new JLabel();
		labelInfo.setFont(fonte);
		labelInfo.setForeground(color);
		labelInfo.setBounds(12, 496, 888, 25);
		mainFrame.add(labelInfo);
		
		action_fill_combo();
		action_select_default();
		onCreateOptionsPopupMenu();
		
		setVisible(true);
		
	}
	
	/** Cria as opções do menu de popup da tabela */
	private void onCreateOptionsPopupMenu() {
		
		JPopupMenu popupMenu = new JPopupMenu();
		
		JMenuItem itemEditar = new JMenuItem("Editar");
		itemEditar.addActionListener((event) -> action_table_edit());
		popupMenu.add(itemEditar);
		
		JMenuItem itemExcluir = new JMenuItem("Excluir");
		itemExcluir.addActionListener((event) -> action_table_delete());
		popupMenu.add(itemExcluir);
		
		popupMenu.addSeparator();
		
		JMenuItem itemCopiaLogin = new JMenuItem("Copiar Login");
		itemCopiaLogin.addActionListener((event) -> action_table_copy_login());
		popupMenu.add(itemCopiaLogin);
		
		JMenuItem itemCopiaPwd = new JMenuItem("Copiar Senha");
		itemCopiaPwd.addActionListener((event) -> action_table_copy_pwd());
		popupMenu.add(itemCopiaPwd);
		
		tableResultado.setComponentPopupMenu(popupMenu);
		
	}
	
	/************************** Implementação dos Listeners *******************************/
	
	/** Ajusta a visibilidade dos botões de edição de usuário. Estes só estão disponíveis
	 *  quando algum usuário está selecionado. Também controla o estado do botão de usuário
	 *  padrão, de acordo com o usuário selecionado e o padrão configurado no banco de dados. */
	private void listener_combo() {
		
		boolean buttonVisibility = comboUsuarios.getSelectedIndex() > 0;

		botaoUsuarioAtualiza.setEnabled(buttonVisibility);
		botaoUsuarioDeleta  .setEnabled(buttonVisibility);
		botaoPadrao         .setEnabled(buttonVisibility);
		
		// Se algum usuário foi selecionado
		if (buttonVisibility) {
			
			// Recupero este usuário + o padrão do banco de dados
			final Owner ownerDefault  = ConfigDAO.getDefaultUser();
			final Owner ownerSelected = ownerList.get(comboUsuarios.getSelectedIndex()-1);
			
			// Dependendo desta condição, sinalizo no botão
			if (ownerSelected.equals(ownerDefault))
				botaoPadrao.setSelected(true);
			else
				botaoPadrao.setSelected(false);
			
		}
		
		// Se nenhum usuário foi selecionado, removo qualquer seleção que tenha no botão.
		else
			botaoPadrao.setSelected(false);
		
	}
	
	/** Realiza as buscas de credenciais do sistema de acordo com os parâmetros de entrada (serviço e usuário). */
	private synchronized void listener_query() {
		
		// Recuperando o serviço e o usuário da tela
		final String service = textServico.getText();
		final Owner  owner   = comboUsuarios.getSelectedIndex() > 0 ? ownerList.get(comboUsuarios.getSelectedIndex()-1) : null;
		
		// Realizando a busca e atualizando a lista interna
		this.credentialsList = CredentialsDAO.list(service,owner);
		
		// Atualizando a tabela com os novos dados
		TableUtils.clear(modelo);
		
		for (Credentials credentials: this.credentialsList)
			TableUtils.add(modelo,credentials);
		
		util_update_size();
		
	}
	
	/********************** Tratamento de Eventos de Usuários *****************************/
	
	/** Limpa o campo de busca de serviço e atualiza a tabela */
	private void action_clear() {
		
		textServico.setText(null);
		textServico.requestFocus();
		
		listener_query();
		
	}
	
	/** Exibe a tela de criação de um novo usuário */
	private void action_create_user() {
		
		// Exibindo o diálogo
		String user = JOptionPane.showInputDialog("Digite um nome de usuário");
		
		// Se entrei com um nome válido...
		if ((user != null) && (!user.isEmpty())) {
			
			// o insiro no banco de dados e...
			boolean succeeded = OwnerDAO.insert(user);
			
			// exibo a mensagem de status.
			if (succeeded)
				AlertDialog.informativo("Novo usuário registrado!");
			else
				AlertDialog.erro("Falha ao registrar usuário!\nProvavelmente o mesmo já existe!");
			
			// Por fim, atualizo o combo de usuários e seleciono o novo usuário criado
			action_fill_combo();
			comboUsuarios.setSelectedItem(user);
			
		}
		
	}
	
	/** Exibe a tela de edição de um usuário selecionado */
	private void action_update_user() {
		
		// Exibindo o diálogo
		String user = JOptionPane.showInputDialog("Digite um novo nome de usuário");
		
		// Se entrei com um nome válido...
		if ((user != null) && (!user.isEmpty())) {
		
			// Recupero o objeto usuário selecionado do meu ArrayList interno, ...
			final int      index = comboUsuarios.getSelectedIndex() - 1;
			final Owner selected = ownerList.get(index);

			// exibo um diálogo de confirmação de atualização...
			String message = ResourceManager.getText(this,"user-update-confirm.txt",selected.getName());
			int choice = AlertDialog.dialog(message);
		
			// e, se desejo mesmo atualizar...
			if (choice == AlertDialog.OK_OPTION) {
			
				// atualizo os dados no banco e...
				boolean succeeded = OwnerDAO.update(selected,user);
			
				// exibo uma mensagem de status
				if (succeeded)
					AlertDialog.informativo("Usuário atualizado com sucesso!");
				else
					AlertDialog.erro("Falha ao atualizar usuário!\nTalvez você esteja escolhendo um nome que já existe no sistema.");
			
				// Por fim, atualizo o combo de usuários 
				action_fill_combo();
				
			}
		
		}
		
	}
	
	/** Exibe a tela de remoção de um usuário selecionado */
	private void action_delete_user() {
		
		// Aqui recupero o objeto usuário selecionado do meu ArrayList interno, ...
		final int      index = comboUsuarios.getSelectedIndex() - 1;
		final Owner selected = ownerList.get(index);

		// exibo um diálogo de confirmação de exclusão...
		String message = ResourceManager.getText(this,"user-deletion-confirm.txt",selected.getName());
		int choice = AlertDialog.dialog(message);
		
		// e, se desejo mesmo excluir...
		if (choice == AlertDialog.OK_OPTION) {
			
			// removo o usuário do banco e...
			boolean succeeded = OwnerDAO.delete(selected);
			
			// exibo uma mensagem de status
			if (succeeded)
				AlertDialog.informativo("Usuário removido com sucesso!");
			else
				AlertDialog.erro("Falha ao remover usuário!\nTalvez ainda haja alguma credencial vinculada a ele no sistema.");
			
			// Por fim, atualizo o combo de usuários 
			action_fill_combo();
			
		}
		
	}
	
	/** Configura o usuário padrão da apĺicação no banco de dados */
	private void action_toggle_default() {
		
		// Aqui recupero o objeto usuário selecionado do meu ArrayList interno, ...
		final int      index = comboUsuarios.getSelectedIndex() - 1;
		final Owner selected = ownerList.get(index);
		
		// Sempre limpo a config ao clicar no botão
		ConfigDAO.deleteDefaultUser();
		
		// Caso tenha sido selecionado, configuro um novo usuário padrão
		if (botaoPadrao.isSelected())
			ConfigDAO.insertDefaultUser(selected);
		
	}
	
	/********************** Tratamento de Eventos de Credenciais **************************/
	
	/** Exibe a tela de criação de uma nova credencial */
	private void action_credential_new() {
		
		// Por conveniência, se um usuário foi selecionado no combo, o seleciono na próxima tela
		final Owner owner = comboUsuarios.getSelectedIndex() > 0 ? ownerList.get(comboUsuarios.getSelectedIndex()-1) : null;
		
		// Construindo a janela...
		PanelCredentials screen = new PanelCredentials(ownerList);
		screen.setOwner(owner);
		
		int option = JOptionPane.showConfirmDialog(this,
				screen,
				"Nova Credencial",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		
		// se a opção, "OK" foi selecionada...
		if (option == JOptionPane.OK_OPTION) {
			
			// salvo os dados no banco e atualizo a tabela
			screen.commit();
			listener_query();
			
		}
		
	}
	
	/******************** Tratamento de Eventos de Menu da Tabela *************************/
	
	/** Edita os dados de uma credencial selecionada da tabela */
	private void action_table_edit() {
		
		// Recuperando a credencial associada na ArrayList
		Credentials selected = tableResultado.getSelectedRow() >= 0 ? credentialsList.get(tableResultado.getSelectedRow()) : null;
		
		// Se algo foi selecionado...
		if (selected != null) {
			
			// Construo a tela de edição...
			PanelCredentials screen = new PanelCredentials(ownerList,selected);
			
			int option = JOptionPane.showConfirmDialog(this,
					screen,
					"Editar Credencial",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			
			// e se a opção "OK" foi escolhida...
			if (option == JOptionPane.OK_OPTION) {
				
				// salvo os novos dados no banco e atualizo a tabela
				screen.commit();
				listener_query();
				
			}
			
		}
		
	}
	
	/** Remove do banco de dados uma credencial selecionada na tabela */
	private void action_table_delete() {
		
		// Recuperando a credencial associada na ArrayList
		Credentials selected = tableResultado.getSelectedRow() >= 0 ? credentialsList.get(tableResultado.getSelectedRow()) : null;
		
		// Se algo foi selecionado...
		if (selected != null) {
			
			// Construo a mensagem de aviso...
			String message = ResourceManager.getText(this,"cred-deletion-confirm.txt",0);
			int choice = AlertDialog.dialog(message);
			
			// e se a opção "OK" foi escolhida...
			if (choice == AlertDialog.OK_OPTION) {
				
				// removo a credencial da base de dados e exibo uma mensagem
				if (CredentialsDAO.delete(selected))
					AlertDialog.informativo("Credencial removida com sucesso!");
				else
					AlertDialog.erro("Falha ao remover credencial.\nFavor verificar o console do sistema.");
				
			}
			
			// Atualiza a tabela
			listener_query();
			
		}
		
	}
	
	/** Copia o login de uma linha selecionada para a área de transferência */
	private void action_table_copy_login() {
		
		try {
			
			final String login = (String) modelo.getValueAt(tableResultado.getSelectedRow(),2);
			
			AlertDialog.pasteToClibpoard(login);
			
			showInfo("Login copiado para a área de transferência");
			
		}
		catch (Exception exception) { }
		
	}
	
	/** Copia a senha de uma linha selecionada para a área de transferência */
	private void action_table_copy_pwd() {
		
		try {
			
			final String pwd = (String) modelo.getValueAt(tableResultado.getSelectedRow(),3);
			
			AlertDialog.pasteToClibpoard(pwd);
			
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
	private void action_fill_combo() {
		
		// Limpando o combo
		comboUsuarios.removeAllItems();
		comboUsuarios.addItem("Todos");
		
		// Atualizando a lista de usuários
		this.ownerList = OwnerDAO.list();
		
		for (Owner owner: this.ownerList)
			comboUsuarios.addItem(owner.getName());
		
	}
	
	// Seleciona no combo o usuário padrão, apenas na instanciação desta tela
	private void action_select_default() {
		
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
	private void util_update_size() {
		
		final int size = this.credentialsList.size();
		
		if (size == 1)
			labelQTD.setText("1 credencial encontrada");
		else
			labelQTD.setText(size + " credenciais encontradas");
		
	}
}
