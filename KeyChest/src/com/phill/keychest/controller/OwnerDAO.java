package com.phill.keychest.controller;

import java.sql.*;
import java.util.*;
import com.phill.libs.*;
import com.phill.keychest.bd.*;
import com.phill.keychest.model.*;

public class OwnerDAO {
	
	public static boolean insert(final String name) {
		
		try {
			
			String query = ResourceManager.getSQLString("owner-insert.sql",name);
			Connection c = Database.LOCAL.getConnection();
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
	
	public static boolean update(final Owner owner, final String name) {
		
		try {
			
			String query = ResourceManager.getSQLString("owner-update.sql",name,owner.getID());
			Connection c = Database.LOCAL.getConnection();
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
	
	public static boolean delete(final Owner owner) {
		
		try {
			
			String query = ResourceManager.getSQLString("owner-delete.sql",owner.getID());
			Connection c = Database.LOCAL.getConnection();
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
	
	public static ArrayList<Owner> list() {
		
		ArrayList<Owner> ownerList = null;
		
		try {
			
			ownerList = new ArrayList<Owner>();
			
			String query = ResourceManager.getSQLString("owner-select.sql",0);
			Connection c = Database.LOCAL.getConnection();
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
