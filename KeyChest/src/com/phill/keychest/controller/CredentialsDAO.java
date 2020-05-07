package com.phill.keychest.controller;

import java.sql.*;
import java.util.*;
import com.phill.libs.*;
import com.phill.keychest.bd.*;
import com.phill.keychest.model.*;

public class CredentialsDAO {
	
	public static boolean insert(final Credentials credentials) {
		
		try {
			
			String query = ResourceManager.getSQLString("credentials-insert.sql",credentials.getInsertFields());
			Connection c = Database.LOCAL.getConnection();
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
	
	public static boolean update(final Credentials credentials) {
		
		try {
			
			String query = ResourceManager.getSQLString("credentials-update.sql",credentials.getUpdateFields());
			Connection c = Database.LOCAL.getConnection();
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
	
	public static boolean delete(final Credentials credentials) {
		
		try {
			
			String query = ResourceManager.getSQLString("credentials-delete.sql",credentials.getID());
			Connection c = Database.LOCAL.getConnection();
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
	
	public static ArrayList<Credentials> list(final String service, final Owner owner) {
		
		ArrayList<Credentials> credentialsList = null;
		
		String owner_id = (owner == null) ? "%" : Integer.toString(owner.getID());
		
		try {
			
			credentialsList = new ArrayList<Credentials>();
			
			String query = ResourceManager.getSQLString("credentials-select.sql",service,owner_id);
			Connection c = Database.LOCAL.getConnection();
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
