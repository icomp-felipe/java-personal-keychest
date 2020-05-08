package com.phill.keychest.controller;

import java.sql.*;
import java.util.*;
import com.phill.libs.*;
import com.phill.keychest.bd.*;
import com.phill.keychest.model.*;

/** Realiza a interface da classe "Credentials" entre o BD e a aplicação.
 *  @author Felipe André - fass@icomp.ufam.edu.br
 *  @version 1.0, 04/05/2020 */
public class CredentialsDAO {
	
	/** Insere uma nova credencial no banco de dados.
	 *  @param credentials - credencial
	 *  @return 'true' caso a operação tenha sido realizada com sucesso ou 'false' caso algum problema ocorra.
	 *  Neste caso, o console deve ser consultado. */
	public static boolean insert(final Credentials credentials) {
		
		try {
			
			String query = ResourceManager.getSQLString("credentials-insert.sql",credentials.getInsertFields());
			Connection c = Database.INSTANCE.getConnection();
			Statement st = c.createStatement();
			
			st.executeUpdate(query);
			
			st.close();
			c .close();
			
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	/** Atualiza dados de uma credencial no banco de dados.
	 *  @param credentials - credencial
	 *  @return 'true' caso a operação tenha sido realizada com sucesso ou 'false' caso algum problema ocorra.
	 *  Neste caso, o console deve ser consultado. */
	public static boolean update(final Credentials credentials) {
		
		try {
			
			String query = ResourceManager.getSQLString("credentials-update.sql",credentials.getUpdateFields());
			Connection c = Database.INSTANCE.getConnection();
			Statement st = c.createStatement();
			
			st.executeUpdate(query);
			
			st.close();
			c .close();
			
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	/** Remove uma credencial do banco de dados.
	 *  @param credentials - credencial
	 *  @return 'true' caso a operação tenha sido realizada com sucesso ou 'false' caso algum problema ocorra.
	 *  Neste caso, o console deve ser consultado. */
	public static boolean delete(final Credentials credentials) {
		
		try {
			
			String query = ResourceManager.getSQLString("credentials-delete.sql",credentials.getID());
			Connection c = Database.INSTANCE.getConnection();
			Statement st = c.createStatement();
			
			st.executeUpdate(query);
			
			st.close();
			c .close();
			
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	/** Lista credenciais de acordo com os filtros informados via parâmetro.
	 *  @param service - nome do serviço
	 *  @param owner - usuário do serviço
	 *  @return {@link ArrayList} de credenciais que satisfazem a busca com os parâmetros informados. */
	public static ArrayList<Credentials> list(final String service, final Owner owner) {
		
		ArrayList<Credentials> credentialsList = null;
		
		String owner_id = (owner == null) ? "%" : Integer.toString(owner.getID());
		
		try {
			
			credentialsList = new ArrayList<Credentials>();
			
			String query = ResourceManager.getSQLString("credentials-select.sql",service,owner_id);
			Connection c = Database.INSTANCE.getConnection();
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(query);
			
			while (rs.next()) {
				
				final Owner       cred_owner  = new Owner();
				final Credentials credentials = new Credentials();
				
				credentials.setID      (rs.getInt   ("cred_id_pk"   ));
				credentials.setService (rs.getString("cred_service" ));
				credentials.setLogin   (rs.getString("cred_login"   ));
				credentials.setPassword(rs.getString("cred_password"));

				cred_owner.setID  (rs.getInt   ("cred_owner_id"));
				cred_owner.setName(rs.getString("owner_name"   ));
				
				credentials.setOwner(cred_owner);
				
				credentialsList.add(credentials);
				
			}
			
			st.close();
			c .close();
			
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return credentialsList;
		
	}

}
