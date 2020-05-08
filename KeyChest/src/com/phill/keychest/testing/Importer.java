package com.phill.keychest.testing;

import java.io.File;

import com.phill.libs.PhillFileUtils;

public class Importer {

	public static void main(String[] args) {
		
		String raw = PhillFileUtils.readFileToString(new File("/home/felipe/keys_jr.csv"));
		
		String[] lines = raw.split("\n");
		
		for (int i=1; i<lines.length; i++) {

			String[] columns = lines[i].split(";");
			
			String service = columns[1].replace("\"","");
			String login   = columns[2].replace("\"","");
			String senha   = columns[3].replace("\"","");
			
			String query = String.format("insert into `credentials`(`cred_id_pk`,`cred_service`,`cred_login`,`cred_password`,`cred_owner_id`) values (null,'%s','%s','%s',2);",service,login,senha).replace("'NULL'","null");
			System.out.println(query);
			
		}
			
		
		
	}

}
