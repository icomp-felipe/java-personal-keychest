package com.phill.keychest.controller;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import com.phill.libs.*;
import com.phill.keychest.bd.*;
import com.phill.keychest.model.*;

/** Realiza a interface da classe "Owner" entre o BD e a aplicação.
 *  @author Felipe André - fass@icomp.ufam.edu.br
 *  @version 2.0, 30/JAN/2023 */
public class OwnerDAO {
	
	/** Insere/atualiza um usuário no banco de dados.
	 *  @param owner - novo usuário
	 *  @throws IOException quando os arquivos sql estão inacessíveis por algum motivo
	 *  @throws SQLException quando ocorre algum erro de banco de dados */
	public static void commit(final Owner owner) throws IOException, SQLException {
		
		String sql   = owner.isNewRecord() ? "owner-create.sql" : "owner-update.sql";
		String query = ResourceManager.getSQLString(true, sql, owner.getCommitData());
		Connection c = Database.INSTANCE.getConnection();
		Statement st = c.createStatement();
			
		st.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
		
		ResultSet rs = st.getGeneratedKeys();
		
		if (rs.next())
			owner.setID(rs.getInt(1));
			
		st.close();
		c .close();
			
	}
	
	/** Remove um usuário do banco de dados (quando este não tem mais nenhuma credencial cadastrada).
	 *  @param owner - usuário selecionado
	 *  @return 'true' caso a operação tenha sido realizada com sucesso ou 'false' caso algum problema ocorra.
	 *  Neste caso, o console deve ser consultado. */
	public static boolean delete(final Owner owner) {
		
		try {
			
			String query = ResourceManager.getSQLString(false, "owner-delete.sql", owner.getID());
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
			
			String query = ResourceManager.getSQLString(false, "owner-select.sql");
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
