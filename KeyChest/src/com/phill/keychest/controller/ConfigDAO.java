package com.phill.keychest.controller;

import java.sql.*;
import com.phill.libs.*;
import com.phill.keychest.bd.*;
import com.phill.keychest.model.*;

/** Realiza a interface de controle de usuário padrão entre o BD e a aplicação.
 *  @author Felipe André - fass@icomp.ufam.edu.br
 *  @version 1.0, 11/05/2020 */
public class ConfigDAO {
	
	/** Configura o usuário padrão no banco de dados.
	 *  @param owner - usuário selecionado
	 *  @return 'true' caso a operação tenha sido realizada com sucesso ou 'false' caso algum problema ocorra.
	 *  Neste caso, o console deve ser consultado. */
	public static boolean insertDefaultUser(final Owner owner) {
		
		try {
			
			String query = ResourceManager.getSQLString(false, "config-insert-duser.sql", owner.getID());
			Connection c = Database.INSTANCE.getConnection();
			Statement st = c.createStatement();
			
			st.executeUpdate(query);
			
			st.close();
			c .close();
			
		}
		catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	/** Remove o usuário padrão do banco de dados.
	 *  @return 'true' caso a operação tenha sido realizada com sucesso ou 'false' caso algum problema ocorra.
	 *  Neste caso, o console deve ser consultado. */
	public static boolean deleteDefaultUser() {
		
		try {
			
			String query = ResourceManager.getSQLString(false, "config-delete-duser.sql");
			Connection c = Database.INSTANCE.getConnection();
			Statement st = c.createStatement();
			
			st.executeUpdate(query);
			
			st.close();
			c .close();
			
		}
		catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	/** Recupera o usuário padrão do banco de dados.
	 *  @return Usuário padrão configurado. */
	public static Owner getDefaultUser() {
		
		Owner owner = null;
		
		try {
			
			String query = ResourceManager.getSQLString(false, "config-select-duser.sql");
			Connection c = Database.INSTANCE.getConnection();
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(query);
			
			if (rs.next()) {
				
				owner = new Owner();
				
				owner.setID  (rs.getInt   ("owner_id_pk"));
				owner.setName(rs.getString("owner_name" ));
				
			}
			
			st.close();
			c .close();
			
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return owner;
		
	}

}
