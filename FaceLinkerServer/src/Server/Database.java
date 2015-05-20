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
	private static final int UPDATE = 0x01;
	private static final int QUERY = 0x02;
	
	Connection con;
	Statement st;
	ResultSet rs;
	
	public Database(){
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
	}
	
	public boolean connect(){
		try{
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/FaceLinker","root","FLDatabaseRoot123");
			st = con.createStatement();
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			return false;
		}
		return true;
	}
	
	public Statement getStatement(){
		return st;
	}
	
	// Key == column, value == data
	public boolean update(String table, HashMap<String, String> data, String condition){
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
	
	public boolean insert(String table, HashMap<String, String> data){
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
	
	public HashMap<String, Object> select(String table, String[] columns, String condition){
		String query = "select ";
		HashMap<String, Object> result = new HashMap<String, Object>();
		ResultSet res;
		
		for(int i = 1 ; i <= columns.length ; ++i){
			query += columns[i];
			if(i != columns.length) query += ",";
		}
		query += " from " + table;
		if( condition != null ) query += " where " + condition;
		try{
			res = st.executeQuery(query);
			for(String column : columns){
				result.put(column, res.getObject(column));
			}
		}catch(SQLException e){
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("Query: " + query);
			return null;
		}
		return result;
	}
	
	public void printError(SQLException e, String query){
		System.out.println("SQLException: " + e.getMessage());
		System.out.println("SQLState: " + e.getSQLState());
		System.out.println("Query: " + query);
	}
	
	//public HashMap<String, Object> execute(){
	//s	
	//}
}
