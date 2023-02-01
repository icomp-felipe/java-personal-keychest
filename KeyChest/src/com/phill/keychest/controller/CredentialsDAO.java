package com.phill.keychest.controller;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import com.phill.libs.*;
import com.phill.keychest.bd.*;
import com.phill.keychest.model.*;

/** Realiza a interface da classe "Credentials" entre o BD e a aplicação.
 *  @author Felipe André - fass@icomp.ufam.edu.br
 *  @version 2.0, 01/FEV/2023 */
public class CredentialsDAO {
	
	/** Insere uma nova credencial no banco de dados.
	 *  @param credentials - credencial
	 *  @throws IOException quando os arquivos sql estão inacessíveis por algum motivo
	 *  @throws SQLException quando ocorre algum erro de banco de dados */
	public static void commit(final Credentials credentials) throws IOException, SQLException {
		
		String sql   = credentials.isNewRecord() ? "credentials-create.sql" : "credentials-update.sql";
		String query = ResourceManager.getSQLString(true, sql, credentials.getCommitFields());
		Connection c = Database.INSTANCE.getConnection();
		Statement st = c.createStatement();
			
		st.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
		
		ResultSet rs = st.getGeneratedKeys();
		
		if (rs.next())
			credentials.setID(rs.getInt(1));
		
		st.close();
		c .close();
			
	}
	
	/** Remove uma credencial do banco de dados.
	 *  @param credentials - credencial
	 *  @throws IOException quando os arquivos sql estão inacessíveis por algum motivo
	 *  @throws SQLException quando ocorre algum erro de banco de dados */
	public static void delete(final Credentials credentials) throws IOException, SQLException {
		
		String query = ResourceManager.getSQLString(false, "credentials-delete.sql", credentials.getID());
		Connection c = Database.INSTANCE.getConnection();
		Statement st = c.createStatement();
			
		st.executeUpdate(query);
			
		st.close();
		c .close();
			
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
			
			String query = ResourceManager.getSQLString(false, "credentials-select.sql", service, owner_id);
			Connection c = Database.INSTANCE.getConnection();
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(query);
			
			while (rs.next()) {
				
				final Owner       cred_owner  = new Owner();
				final Credentials credentials = new Credentials();
				
				credentials.setID         (rs.getInt   ("cred_id_pk"   ));
				credentials.setService    (rs.getString("cred_service" ));
				credentials.setLogin      (rs.getString("cred_login"   ));
				credentials.setPassword   (rs.getString("cred_password"));
				credentials.setCreatedDate(rs.getString("cred_created_time"));
				credentials.setUpdatedDate(rs.getString("cred_last_updated"));

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
