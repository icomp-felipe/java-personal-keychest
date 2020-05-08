package com.phill.keychest.model;

import com.phill.libs.Resumeable;

/** Modelagem da entidade "Credentials". Contém os dados de login, senha,
 *  serviço e usuário.
 *  @author Felipe André - fass@icomp.ufam.edu.br
 *  @version 1.0, 04/05/2020
 *  @see Resumeable */
public class Credentials implements Resumeable {
	
	private int id;
	private String service, login, password;
	private Owner owner;
	
	/******************************* Bloco de Setters *************************************/
	
	/** Setter do ID.
	 *  @param id - ID da credencial no banco de dados */
	public void setID(final int id) {
		this.id = id;
	}
	
	/** Setter do nome de serviço.
	 *  @param service - nome de serviço */
	public void setService(final String service) {
		this.service = service;
	}
	
	/** Setter do login.
	 *  @param login - login */
	public void setLogin(final String login) {
		this.login = login;
	}
	
	/** Setter da senha vinculada ao login.
	 *  @param password - senha */
	public void setPassword(final String password) {
		this.password = password;
	}
	
	/** Setter do usuário (objeto).
	 *  @param owner - usuário dono da credencial */
	public void setOwner(final Owner owner) {
		this.owner = owner;
	}
	
	/******************************* Bloco de Getters *************************************/
	
	/** Getter do ID.
	 *  @return ID da credencial no banco de dados. */
	public int getID() {
		return this.id;
	}
	
	/** Getter do nome de serviço.
	 *  @return Nome de serviço. */
	public String getService() {
		return this.service;
	}
	
	/** Getter do login.
	 *  @return Login. */
	public String getLogin() {
		return this.login;
	}
	
	/** Getter da senha vinculada ao login.
	 *  @return Senha. */
	public String getPassword() {
		return this.password;
	}
	
	/** Getter do usuário (objeto).
	 *  @return Usuário dono da credencial */
	public Owner getOwner() {
		return this.owner;
	}
	
	/*************************** Bloco de Getters Especiais *******************************/
	
	/** Recupera o tamanho da senha. Esta é zero quando o campo é nulo ou realmente vazio.
	 *  @return Tamanho da senha. */
	public int getPasswordLength() {
		return (this.password == null) ? 0 : this.password.length();
	}

	/** Monta um array com os dados de inserção no BD.
	 *  @return Array de {@link Object} com os dados prontos pro insert. */
	public Object[] getInsertFields() {
		return new Object[]{ getService(), getLogin(), getPassword(), getOwner().getID() };
	}

	/** Monta um array com os dados de atualização no BD.
	 *  @return Array de {@link Object} com os dados prontos pro update. */
	public Object[] getUpdateFields() {
		return new Object[] { getService(), getLogin(), getPassword(), getOwner().getID(), getID() };
	}
	
	/** Monta um array com os dados dispostos nas colunas da JTable.
	 *  @return Array de {@link Object} com os dados prontos para exibição na tabela. */
	@Override
	public Object[] getResume() {
		return new Object[] { getService(), getOwner().getName(), getLogin(), getPassword(), getPasswordLength() };
	}

}
