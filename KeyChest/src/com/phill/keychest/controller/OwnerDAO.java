package com.phill.keychest.controller;

import java.sql.*;
import java.util.*;
import com.phill.libs.*;
import com.phill.keychest.bd.*;
import com.phill.keychest.model.*;

/** Realiza a interface da classe "Owner" entre o BD e a aplicação.
 *  @author Felipe André - fass@icomp.ufam.edu.br
 *  @version 1.0, 04/05/2020 */
public class OwnerDAO {
	
	/** Insere um novo usuário no banco de dados.
	 *  @param name - nome de usuário
	 *  @return 'true' caso a operação tenha sido realizada com sucesso ou 'false' caso algum problema ocorra.
	 *  Neste caso, o console deve ser consultado. */
	public static boolean insert(final String name) {
		
		try {
			
			String query = ResourceManager.getSQLString("owner-insert.sql",name);
			Connection c = Database.INSTANCE.getConnection();
			Statement st = c.createStatement();
			
			st.executeUpdate(query);
			
			st.close();
			c .close();
			
		} catch (SQLIntegrityConstraintViolationException exception) {
			return false;
		}
		catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	/** Atualiza dados de um usuário no banco de dados.
	 *  @param owner - usuário selecionado
	 *  @param name - novo nome de usuário
	 *  @return 'true' caso a operação tenha sido realizada com sucesso ou 'false' caso algum problema ocorra.
	 *  Neste caso, o console deve ser consultado. */
	public static boolean update(final Owner owner, final String name) {
		
		try {
			
			String query = ResourceManager.getSQLString("owner-update.sql",name,owner.getID());
			Connection c = Database.INSTANCE.getConnection();
			Statement st = c.createStatement();
			
			st.executeUpdate(query);
			
			st.close();
			c .close();
			
		} catch (SQLIntegrityConstraintViolationException exception) {
			return false;
		}
		catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	/** Remove um usuário do banco de dados (quando este não tem mais nenhuma credencial cadastrada).
	 *  @param owner - usuário selecionado
	 *  @return 'true' caso a operação tenha sido realizada com sucesso ou 'false' caso algum problema ocorra.
	 *  Neste caso, o console deve ser consultado. */
	public static boolean delete(final Owner owner) {
		
		try {
			
			String query = ResourceManager.getSQLString("owner-delete.sql",owner.getID());
			Connection c = Database.INSTANCE.getConnection();
			Statement st = c.createStatement();
			
			st.executeUpdate(query);
			
			st.close();
			c .close();
			
		} catch (SQLIntegrityConstraintViolationException exception) {
			return false;
		}
		catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	/** Lista todos os usuários cadastrados na base de dados.
	 *  @return {@link ArrayList} de todos os usuários do BD. */
	public static ArrayList<Owner> list() {
		
		ArrayList<Owner> ownerList = null;
		
		try {
			
			ownerList = new ArrayList<Owner>();
			
			String query = ResourceManager.getSQLString("owner-select.sql",0);
			Connection c = Database.INSTANCE.getConnection();
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(query);
			
			while (rs.next()) {
				
				Owner owner = new Owner();
				
				owner.setID  (rs.getInt   ("owner_id_pk"));
				owner.setName(rs.getString("owner_name" ));
				
				ownerList.add(owner);
				
			}
			
			st.close();
			c .close();
			
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return ownerList;
		
	}

}
