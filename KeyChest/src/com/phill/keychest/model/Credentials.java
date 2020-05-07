package com.phill.keychest.model;

import com.phill.libs.Resumeable;

public class Credentials implements Resumeable {
	
	private int id;
	private String service, login, password;
	private Owner owner;
	
	public void setID(final int id) {
		this.id = id;
	}
	
	public void setService(final String service) {
		this.service = service;
	}
	
	public void setLogin(final String login) {
		this.login = login;
	}
	
	public void setPassword(final String password) {
		this.password = password;
	}
	
	public void setOwner(final Owner owner) {
		this.owner = owner;
	}
	
	public int getID() {
		return this.id;
	}
	
	public String getService() {
		return this.service;
	}
	
	public String getLogin() {
		return this.login;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public int getPasswordLength() {
		return (this.password == null) ? 0 : this.password.length();
	}
	
	public Owner getOwner() {
		return this.owner;
	}

	public Object[] getInsertFields() {
		return new Object[]{ getService(), getLogin(), getPassword(), getOwner().getID() };
	}

	public Object[] getUpdateFields() {
		return new Object[] { getService(), getLogin(), getPassword(), getOwner().getID(), getID() };
	}
	
	@Override
	public Object[] getResume() {
		return new Object[] { getService(), getOwner().getName(), getLogin(), getPassword(), getPasswordLength() };
	}

}
