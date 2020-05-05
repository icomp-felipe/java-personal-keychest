package com.phill.keychest.bd;

import java.sql.*;
import com.mchange.v2.c3p0.*;

public enum Database {
	
	LOCAL("localhost");
	
	private final String serverURL;
	private ComboPooledDataSource dataSource;
	private final String driverClass = "org.mariadb.jdbc.Driver" ;
	
	Database(final String serverURL) {
		
		// Recupera a URL do Banco de Dados
		this.serverURL = serverURL;
		
		// Desativando os logs do sistema
		System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
		System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");
		
	}
	
	/** Método que deve ser OBRIGATORIAMENTE utilizado na primeira conexão ao banco de dados,
	 *  pois define em qual banco o sistema irá se conectar (local ou, futuramente, remoto).
	 *  No mais mais funciona como um singleton, durante a primeira execução, cria a conexão
	 *  ao banco e nas próximas o método é ignorado.  */
	public boolean connect(final String user, final String pass) throws SQLException {
		
		// Na primeira execução deste método crio a conexão
		if (dataSource == null) {
			
			final String databaseURL = "jdbc:mysql://" + this.serverURL + "/keychest";
			
			dataSource = new ComboPooledDataSource();
			
			try {
				
				dataSource.setDriverClass(driverClass);
				dataSource.setJdbcUrl(databaseURL);
				dataSource.setUser(user);
				dataSource.setPassword(pass);
				
				dataSource.setDebugUnreturnedConnectionStackTraces(true);
				dataSource.setUnreturnedConnectionTimeout(11);
				dataSource.setCheckoutTimeout(10000);
				
				dataSource.setMaxIdleTime(30);
				dataSource.setAcquireIncrement(5);
				dataSource.setInitialPoolSize(0);
				dataSource.setMinPoolSize(0);
				dataSource.setMaxPoolSize(5);
				dataSource.setMaxStatements(10);
				
			}
			catch (Exception exception) {
				
				dataSource.close();
				DataSources.destroy(dataSource);
				
				this.dataSource = null;
				throw new SQLException(exception);
				
			}
			
		}
		
		return true;
	}
	
	/** Desconecta a aplicação do banco de dados */
	public void disconnect() throws SQLException {
		
		if (dataSource != null) {
			
			dataSource.close();
			DataSources.destroy(dataSource);
			
			this.dataSource = null;
			
		}
		
	}
	
	/** Recupera uma conexão livre do Pool de Conexões */
	public Connection getConnection() throws SQLException {
		
		Connection connection = null;
		
		if (dataSource == null)
			throw new SQLException("x Primeiro inicie a conexão pelo método connect(final String user, final String pass)");
		
		try {
			connection = dataSource.getConnection();
		}
		catch (SQLException exception) {
			
			dataSource.close();
			DataSources.destroy(dataSource);
			
			this.dataSource = null;
			throw new SQLException(exception);
			
		}
		
		return connection;
	}

}
