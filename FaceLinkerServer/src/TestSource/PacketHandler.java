package TestSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Server.Database;
import Server.JoinReq;
import Server.Packet;
import Server.PacketCodec;
import Server.Packet;

public class PacketHandler {
	public static String read_delim(BufferedReader in) throws IOException{
		char charBuf[] = new char[1];
		String readMsg = "";
		short isdelim = 0;
		
		// read character before read delimiter
		while(in.read(charBuf, 0, 1) != -1){
			// Packet.PK_DELIM == '\n'
			if(charBuf[0] == '?'){
				readMsg += charBuf[0];
				isdelim = 1;
				break;
			} else {
				readMsg += charBuf[0];
				continue;
			}
		}
		
		// if there isn't delimiter
		if(isdelim == 0 && charBuf[0]  != '\0'){
			System.out.println("MSG DELIM IS NOT FOUND!!");
		}
		return readMsg;
	}
	
	public static Packet decode_Header(String src) throws IOException{
		String type, data;
		Scanner s = new Scanner(src).useDelimiter(Packet.FIELD_DELIM);
		
		type = s.next();
		s.useDelimiter(Packet.PK_DELIM);
		s.skip(Packet.FIELD_DELIM);
		
		data = s.next();
		
		return new Packet(type, data);
	}
	
	public static void handler(Packet src) throws IOException{
		PacketCodec codec = new PacketCodec();
		Database db = new Database();
		String query = "";
		if(!db.connect()){
			System.out.println("DB Error!!");
			return;
		}
		switch(src.getType()){
			case Packet.PK_JOIN_REQ:
				JoinReq data = codec.decode_JoinReq(src.getData());
				query = "insert into login_data(screen_name, password) "
						+ "values("+data.getScreen_name()+","+data.getPassword()+");";
				try{
					db.getStatement().executeUpdate(query);
				}catch(SQLException e){
					db.printError(e, query);
				}
				query = "select user_id from login_data "
						+ "where screen_name = '"+data.getScreen_name()+"';";
				try{
					ResultSet res = db.getStatement().executeQuery(query);
					int user_id = res.getInt("user_id");
					res.close();
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				break;
			case Packet.PK_PRO_WRITE_REQ:
				break;
		}
	}
}
