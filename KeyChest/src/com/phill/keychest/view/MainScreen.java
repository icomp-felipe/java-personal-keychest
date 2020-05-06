package com.phill.keychest.view;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.text.DefaultFormatter;

import com.phill.keychest.bd.Database;
import com.phill.keychest.controller.OwnerDAO;
import com.phill.keychest.model.Owner;
import com.phill.libs.*;

public class MainScreen extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JTextField textServico;
	private JButton botaoUsuarioAtualiza, botaoUsuarioDeleta;
	private JComboBox<String> comboBox;
	
	private final JTable tableResultado;
    private final DefaultTableModel modelo;
    private final String[] colunas = new String [] {"Serviço","Usuário","Login","Senha"};
	
	private ArrayList<Owner> ownerList;

	public static void main(String[] args) throws SQLException {
		
		Database.LOCAL.connect("x","y");
		
		new MainScreen();
		
	}

	public MainScreen() {
		super("KeyChest - build 20200504");
		
		GraphicsHelper instance = GraphicsHelper.getInstance();
		
		Font  fonte = instance.getFont ();
		Color color = instance.getColor();
		
		Icon clearIcon    = ResourceManager.getResizedIcon("icon/clear.png",20,20);
		
		Icon addIcon      = ResourceManager.getResizedIcon("icon/add.png",20,20);
		Icon deleteIcon   = ResourceManager.getResizedIcon("icon/delete.png",20,20);
		Icon updateIcon   = ResourceManager.getResizedIcon("icon/reload.png",20,20);
		
		setSize(960,560);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JPanel painelParametros = new JPanel();
		painelParametros.setBorder(instance.getTitledBorder("Parâmetros"));
		painelParametros.setBounds(12, 12, 936, 110);
		getContentPane().add(painelParametros);
		painelParametros.setLayout(null);
		
		JPanel painelServico = new JPanel();
		painelServico.setBorder(instance.getTitledBorder("Serviço"));
		painelServico.setBounds(12, 25, 450, 70);
		painelParametros.add(painelServico);
		painelServico.setLayout(null);
		
		textServico = new JTextField();
		textServico.addActionListener((event) -> listener_query());
		textServico.setFont(fonte);
		textServico.setForeground(color);
		textServico.setBounds(12, 30, 385, 25);
		painelServico.add(textServico);
		textServico.setColumns(10);
		
		JButton botaoServicoLimpa = new JButton(clearIcon);
		botaoServicoLimpa.addActionListener((event) -> {
			textServico.setText(null);
			textServico.requestFocus();
		});
		botaoServicoLimpa.setToolTipText("Limpa este campo de busca");
		botaoServicoLimpa.setBounds(408, 29, 30, 25);
		painelServico.add(botaoServicoLimpa);
		
		JPanel painelUsuario = new JPanel();
		painelUsuario.setLayout(null);
		painelUsuario.setBorder(instance.getTitledBorder("Usuário"));
		painelUsuario.setBounds(474, 25, 450, 70);
		painelParametros.add(painelUsuario);
		
		comboBox = new JComboBox<String>();
		comboBox.setFont(fonte);
		comboBox.setForeground(color);
		comboBox.addActionListener((event) -> listener_combo());
		comboBox.setBounds(12, 30, 300, 25);
		painelUsuario.add(comboBox);
		
		JButton botaoUsuarioCria = new JButton(addIcon);
		botaoUsuarioCria.addActionListener((event) -> action_create_user());
		botaoUsuarioCria.setToolTipText("Cria um novo usuário");
		botaoUsuarioCria.setBounds(324, 30, 30, 25);
		painelUsuario.add(botaoUsuarioCria);
		
		botaoUsuarioAtualiza = new JButton(updateIcon);
		botaoUsuarioAtualiza.addActionListener((event) -> action_update_user());
		botaoUsuarioAtualiza.setToolTipText("Atualiza o usuário selecionado");
		botaoUsuarioAtualiza.setBounds(366, 30, 30, 25);
		painelUsuario.add(botaoUsuarioAtualiza);
		
		botaoUsuarioDeleta = new JButton(deleteIcon);
		botaoUsuarioDeleta.addActionListener((event) -> action_delete_user());
		botaoUsuarioDeleta.setToolTipText("Remove o usuário selecionado");
		botaoUsuarioDeleta.setBounds(408, 30, 30, 25);
		painelUsuario.add(botaoUsuarioDeleta);
		
		JButton botaoCredencialAdd = new JButton(addIcon);
		botaoCredencialAdd.setToolTipText("Adiciona novas credenciais ao sistema");
		botaoCredencialAdd.addActionListener((event) -> action_credential_new());
		botaoCredencialAdd.setBounds(95, 129, 30, 25);
		getContentPane().add(botaoCredencialAdd);
		
		JPanel painelListagem = new JPanel();
		painelListagem.setBorder(instance.getTitledBorder("Listagem            "));
		painelListagem.setBounds(12, 134, 936, 350);
		getContentPane().add(painelListagem);
		painelListagem.setLayout(null);
		
		modelo  = new LockedTableModel(colunas);
		
		tableResultado = new JTable(modelo);
		tableResultado.setOpaque(false);
		tableResultado.addMouseListener(new TableMouseListener(tableResultado));
		tableResultado.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		/*final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		TableColumnModel columnModel = tableResultado.getColumnModel();
		
		columnModel.getColumn(0).setCellRenderer(centerRenderer);
		columnModel.getColumn(2).setCellRenderer(centerRenderer);
		columnModel.getColumn(3).setCellRenderer(centerRenderer);
		
		columnModel.getColumn(0).setPreferredWidth(5);
		columnModel.getColumn(1).setPreferredWidth(250);*/
		
		JScrollPane scrollPane = new JScrollPane(tableResultado);
		scrollPane.setBounds(12, 35, 912, 303);
		painelListagem.add(scrollPane);
		
		JButton botaoSair = new JButton((Icon) null);
		botaoSair.addActionListener((event) -> dispose());
		botaoSair.setToolTipText("Sai do sistema");
		botaoSair.setBounds(918, 498, 30, 25);
		getContentPane().add(botaoSair);
		
		action_fill_combo();
		
		setVisible(true);
		
	}
	
	private void action_credential_new() {
		
		CredentialsAdd screen = new CredentialsAdd();
		
		int option = JOptionPane.showConfirmDialog(this,
				screen,
				"Nova Credencial",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		
		
	}

	private void listener_query() {
		
		final String text = textServico.getText();
		
		
		
	}
	
	@Override
	public void dispose() {
		
		try {
			Database.LOCAL.disconnect();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.dispose();
		
	}
	
	private void action_fill_combo() {
		
		comboBox.removeAllItems();
		comboBox.addItem("Todos");
		
		this.ownerList = OwnerDAO.list();
		
		for (Owner owner: this.ownerList)
			comboBox.addItem(owner.getName());
		
	}
	
	private void listener_combo() {
		
		boolean buttonVisibility = comboBox.getSelectedIndex() > 0;

		botaoUsuarioAtualiza.setEnabled(buttonVisibility);
		botaoUsuarioDeleta  .setEnabled(buttonVisibility);
		
	}
	
	private void action_create_user() {
		
		String user = JOptionPane.showInputDialog("Digite um nome de usuário");
		
		if ((user != null) && (!user.isEmpty())) {
			
			boolean succeeded = OwnerDAO.insert(user);
			
			if (succeeded)
				AlertDialog.informativo("Novo usuário registrado!");
			else
				AlertDialog.erro("Falha ao registrar usuário!\nProvavelmente o mesmo já existe!");
			
		}
		
		action_fill_combo();
		
	}
	
	private void action_delete_user() {
		
		final int      index = comboBox .getSelectedIndex() - 1;
		final Owner selected = ownerList.get(index);

		String message = ResourceManager.getText(this,"user-deletion-confirm.txt",selected.getName());
		int choice = AlertDialog.dialog(message);
		
		if (choice == AlertDialog.OK_OPTION) {
			
			boolean succeeded = OwnerDAO.delete(selected);
			
			if (succeeded)
				AlertDialog.informativo("Usuário removido com sucesso!");
			else
				AlertDialog.erro("Falha ao remover usuário!\nTalvez ainda haja alguma credencial vinculada a ele no sistema.");
			
		}
		
		action_fill_combo();
		
	}
	
	private void action_update_user() {
		
		String user = JOptionPane.showInputDialog("Digite um novo nome de usuário");
		
		if ((user != null) && (!user.isEmpty())) {
		
			final int      index = comboBox .getSelectedIndex() - 1;
			final Owner selected = ownerList.get(index);

			String message = ResourceManager.getText(this,"user-update-confirm.txt",selected.getName());
			int choice = AlertDialog.dialog(message);
		
			if (choice == AlertDialog.OK_OPTION) {
			
				boolean succeeded = OwnerDAO.update(selected,user);
			
				if (succeeded)
					AlertDialog.informativo("Usuário atualizado com sucesso!");
				else
					AlertDialog.erro("Falha ao atualizar usuário!\nTalvez você esteja escolhendo um nome que já existe no sistema.");
			
			}
		
			action_fill_combo();
			
		}
		
	}
}
