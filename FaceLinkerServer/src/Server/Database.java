package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Database {
	Connection con = null;
	Statement st = null;
	ResultSet rs = null;
	
	public Database(){
		
	}
	
	public boolean Connect(){
		try{
			con = DriverManager.getConnection("jdbc::mysql://localhost","root","FLDatabaseRoot123");
			st = con.createStatement();
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			return false;
		}
		return true;
	}
	
	// Key == column, value == data
	public boolean Update(String table, HashMap<String, String> data, String condition){
		String query = "update "+table+" set ";
		Set<String> keys = data.keySet();
		Iterator<String> iter = keys.iterator();
		
		try{
			while(iter.hasNext()){
				query += iter.next() + "=" + data.get(iter);
				if(iter.hasNext()) query += ",";
			}
			if(condition != null) query += " where " + condition;
			st.executeUpdate(query);
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("Query: " + query);
			return false;
		}
		return true;
	}
	
	public boolean Insert(String table, HashMap<String, String> data){
		String query = "insert into "+table+"(";
		Set<String> keys = data.keySet();
		Iterator<String> iter = keys.iterator();
		
		try{
			while(iter.hasNext()){
				query += iter.next();
				if(iter.hasNext()) query += ",";
			}
			query += ") values (";
			iter = keys.iterator();
			while(iter.hasNext()){
				query += data.get(iter);
				if(iter.hasNext()) query += ",";
			}
			query += ")";
			st.executeUpdate(query);
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("Query: " + query);
			return false;
		}
		return true;
	}
	
	public boolean delete(String table, String condition){
		String query = "delete * from "+table+" where " + condition;
		
		try{
			st.executeUpdate(query);
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("Query: " + query);
			return false;
		}
		return true;
	}
	
}
