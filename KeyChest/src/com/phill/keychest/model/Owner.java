package com.phill.keychest.model;

/** Modelagem da entidade "Owner". Contém o nome de usuário e seu ID.
 *  @author Felipe André - felipeandre.eng@gmail.com
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
	
	/** Prepara os dados para salvamento na base de dados.
	 *  @return Um array de objetos na ordem dos scrips de insert / update desta entidade na base de dados. */
	public Object[] getCommitData() {
		return new Object[] { this.name, this.id };
	}
	
	/** @return 'true' if this instance is a new database record (id = 0);<br>'false' otherwise. */
	public boolean isNewRecord() {
		return this.id == 0;
	}
	
	/** Retorna verdadeiro se 'object' for um {@link Owner} e estes tiverem o mesmo id. */
	@Override
	public boolean equals(Object object) {
		
		if (object instanceof Owner)
			return ((Owner) object).getID() == getID();
		
		return false;
	}

}
