package com.phill.keychest.bd;

import java.io.*;
import java.sql.*;
import com.phill.libs.*;
import com.mchange.v2.c3p0.*;

/** Implementa os métodos de acesso ao banco de dados.
 *  @author Felipe André - fass@icomp.ufam.edu.br
 *  @version 1.1, 08/05/2020 */
public enum Database {
	
	INSTANCE;
	
	private ComboPooledDataSource dataSource;
	private final String driverClass = "org.mariadb.jdbc.Driver" ;
	
	Database() {
		
		// Desativando os logs do sistema
		System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
		System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");
		
	}
	
	/** Método que deve ser OBRIGATORIAMENTE utilizado na primeira conexão ao banco de dados,
	 *  pois define em qual banco o sistema irá se conectar (local ou, futuramente, remoto).
	 *  No mais mais funciona como um singleton, durante a primeira execução, cria a conexão
	 *  ao banco e nas próximas o método é ignorado.
	 *  @param user - usuário do banco de dados
	 *  @param pass - senha de usuário do banco de dados
	 *  @throws SQLException quando a conexão não foi realizada, tanto por usuário/senha.
	 *  inválidos quando falhas de conexão. */
	public void connect(final String serverURL, final String user, final String pass) throws SQLException {
		
		// Na primeira execução deste método crio a conexão
		if (dataSource == null) {
			
			final String databaseURL = "jdbc:mysql://" + serverURL + "/keychest";
			
			dataSource = new ComboPooledDataSource();
			
			try {
				
				dataSource.setDriverClass(driverClass);
				dataSource.setJdbcUrl(databaseURL);
				dataSource.setUser(user);
				dataSource.setPassword(pass);
				
				dataSource.setDebugUnreturnedConnectionStackTraces(true);
				dataSource.setUnreturnedConnectionTimeout(11);
				dataSource.setCheckoutTimeout(3000);
				
				dataSource.setMaxIdleTime(30);
				dataSource.setAcquireIncrement(5);
				dataSource.setInitialPoolSize(0);
				dataSource.setMinPoolSize(0);
				dataSource.setMaxPoolSize(5);
				dataSource.setMaxStatements(10);
				
				// Este método realmente realiza a primeira conexão com o BD
				test();
				
			}
			
			// Se "test()" falhar, uma exceção é gerada
			catch (Exception exception) {
				
				if (dataSource != null) {
					
					dataSource.close();
					DataSources.destroy(dataSource);
					this.dataSource = null;
					
				}
				
				throw new SQLException(exception);
				
			}
			
		}
		
	}
	
	/** Testa a conexão ao BD.
	 * @throws IOException quando o arquivo .sql não é encontrado.
	 * @throws SQLException quando houve falha de conexão. */
	public void test() throws SQLException, IOException {
		
		String query = ResourceManager.getSQLString(false, "test-query.sql");
		Connection c = Database.INSTANCE.getConnection();
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(query);
		
		if (rs.next())
			rs.getInt("owners");
		
		st.close();
		c .close();
		
	}
	
	/** Desconecta a aplicação do banco de dados.
	 * @throws SQLException quando a conexão foi perdida por algum motivo. */
	public void disconnect() throws SQLException {
		
		if (dataSource != null) {
			
			dataSource.close();
			DataSources.destroy(dataSource);
			
			this.dataSource = null;
			
		}
		
	}
	
	/** Recupera uma conexão livre do Pool de Conexões.
	 *  @throws SQLException quando não há conexões livres ou a conexão com o BD foi perdida.
	 *  @return Conexão disponível para utilização. */
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
