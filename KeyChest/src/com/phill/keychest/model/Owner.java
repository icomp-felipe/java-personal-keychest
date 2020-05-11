package com.phill.keychest.model;

/** Modelagem da entidade "Owner". Contém o nome de usuário e seu ID.
 *  @author Felipe André - fass@icomp.ufam.edu.br
 *  @version 1.1, 11/05/2020 */
public class Owner {
	
	private int id;
	private String name;
	
	/******************************* Bloco de Setters *************************************/
	
	/** Setter do ID.
	 *  @param id - ID do usuário no banco de dados */
	public void setID(final int id) {
		this.id = id;
	}
	
	/** Setter do nome de usuário.
	 *  @param name - nome de usuário */
	public void setName(final String name) {
		this.name = name;
	}
	
	/******************************* Bloco de Getters *************************************/
	
	/** Getter do ID.
	 *  @return ID do usuário no banco de dados. */
	public int getID() {
		return this.id;
	}
	
	/** Getter do nome de usuário.
	 *  @return Nome de usuário. */
	public String getName() {
		return this.name;
	}
	
	/** Retorna verdadeiro se 'object' for um {@link Owner} e estes tiverem o mesmo id. */
	@Override
	public boolean equals(Object object) {
		
		if (object instanceof Owner)
			return ((Owner) object).getID() == getID();
		
		return false;
	}

}
