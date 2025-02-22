package com.phill.keychest.controller;

import java.io.IOException;
import java.sql.*;
import com.phill.libs.*;
import com.phill.keychest.bd.*;
import com.phill.keychest.model.*;

/** Realiza a interface de controle de usuário padrão entre o BD e a aplicação.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 1.0, 11/05/2020 */
public class ConfigDAO {
	
	/** Configura o usuário padrão no banco de dados.
	 *  @param owner - usuário selecionado
	 *  @throws IOException quando os arquivos sql estão inacessíveis por algum motivo
	 *  @throws SQLException quando ocorre algum erro de banco de dados */
	public static void insertDefaultUser(final Owner owner) throws IOException, SQLException {
		
		String query = ResourceManager.getSQLString(false, "config-insert-duser.sql", owner.getID());
		Connection c = Database.INSTANCE.getConnection();
		Statement st = c.createStatement();
			
		st.executeUpdate(query);
			
		st.close();
		c .close();
			
	}
	
	/** Remove o usuário padrão do banco de dados.
	 *  @throws IOException quando os arquivos sql estão inacessíveis por algum motivo
	 *  @throws SQLException quando ocorre algum erro de banco de dados */
	public static void deleteDefaultUser() throws IOException, SQLException {
		
		String query = ResourceManager.getSQLString(false, "config-delete-duser.sql");
		Connection c = Database.INSTANCE.getConnection();
		Statement st = c.createStatement();
			
		st.executeUpdate(query);
			
		st.close();
		c .close();
			
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
