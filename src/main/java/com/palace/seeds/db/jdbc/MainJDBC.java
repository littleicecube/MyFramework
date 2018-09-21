package com.palace.seeds.db.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Test;

public class MainJDBC {

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testJDBC(){
	   String url = "jdbc:mysql://localhost:3306/test" ;    
	   String username = "root" ;   
	   String password = "111111" ;   
	   try {
		   Connection con =  DriverManager.getConnection(url , username , password ) ;
		} catch (SQLException e) {
			e.printStackTrace();
		}   
	   
	}
}
