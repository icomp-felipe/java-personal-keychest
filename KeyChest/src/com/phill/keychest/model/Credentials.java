package com.phill.keychest.model;

public class Credentials {
	
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
	
	public Owner getOwner() {
		return this.owner;
	}

}
