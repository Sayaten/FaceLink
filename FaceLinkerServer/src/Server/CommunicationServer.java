package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CommunicationServer {
	static ServerSocket serverSocket;
	static Socket clientSocket;
	static PrintWriter out = null;
	static BufferedReader in = null;
	
	public static void main(String[] args) throws IOException{
		serverSocket = null;
		clientSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		
		String inputData;
		Packet rec_packet;
		
		while(true)
		{
			inputData = "";
			// 9193 9194 9195
			serverSocket = new ServerSocket(9193);
			serverSocket.setReuseAddress(true);

			// client connect and receive data
			try {
				clientSocket = serverSocket.accept();
				System.out.println("Client Connect");
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				
				while (true) {
					inputData = PacketCodec.read_delim(in);
					if (inputData.charAt(inputData.length() - 1) == '?') break;
				}
				//inputData.replace('?', '\0');
				in.close();
				rec_packet = PacketCodec.decode_Header(inputData);
				
				handler(rec_packet);	
				
				clientSocket.close();
				serverSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	public static void handler(Packet src) throws IOException{
		PacketCodec codec = new PacketCodec();
		Database db = new Database();
		String query = "";
		String output = "";
		if(!db.connect()){
			System.out.println("DB Error!!");
			return;
		}
		System.out.println("Packet Type: "+src.getType());
		System.out.println("Packet Data: "+src.getData());
		switch(src.getType()){
			case Packet.PK_JOIN_REQ:
				int user_id = 0;
				JoinReq data = codec.decode_JoinReq(src.getData());
				query = "insert into login_data(screen_name, password) "
						+ "values('"+data.getScreen_name()+"','"+data.getPassword()+"');";
				try{
					db.getStatement().executeUpdate(query);
				}catch(SQLException e){
					db.printError(e, query);
				}
				query = "select user_id from login_data "
						+ "where screen_name = '"+data.getScreen_name()+"';";
				try{
					ResultSet rs = db.getStatement().executeQuery(query);
					rs.next();
					user_id = rs.getInt("user_id");
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}
				JoinAck joinack = new JoinAck(Packet.JR_SUCCESS, user_id);
				output = codec.encode_JoinAck(joinack);
				try{ 
					out.println(output);
				}catch(Exception e){
					System.out.println(output);
					e.printStackTrace();
				}finally{
					out.close();
				}
				break;
			case Packet.PK_PRO_WRITE_REQ:
				
				break;
			default:
				
		}
	}
}
