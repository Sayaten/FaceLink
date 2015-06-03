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
	static Base64Codec bs64 = new Base64Codec();
	public static void main(String[] args) throws IOException{
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
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
				rec_packet = PacketCodec.decode_Header(inputData);
				
				handler(rec_packet, out);	
				
				in.close();
				out.close();
				clientSocket.close();
				serverSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	public static void handler(Packet src, PrintWriter out) throws IOException{
		Database db = new Database();
		byte[] BS_res = null;
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
				JoinReq JR_data = PacketCodec.decode_JoinReq(src.getData());
				query = "insert into login_data(screen_name, password) "
						+ "values('"+JR_data.getScreen_name()+"','"+JR_data.getPassword()+"');";
				try{
					db.getStatement().executeUpdate(query);
				}catch(SQLException e){
					db.printError(e, query);
				}
				JoinAck joinack = new JoinAck(Packet.JR_SUCCESS);
				output = PacketCodec.encode_JoinAck(joinack);
				try{ 
					out.println(output);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					out.close();
				}
				break;
			case Packet.PK_PRO_WRITE_REQ:
				ProfileWriteReq PWR_data = PacketCodec.decode_ProfileWriteReq(src.getData());
				int user_id = 0;
				
				query = "select user_id from login_data "
						+ "where screen_name = '"+ PWR_data.getScreen_name()+"';";
				try{
					ResultSet rs = db.getStatement().executeQuery(query);
					rs.next();
					user_id = rs.getInt("user_id");
					rs.close();
				
					BS_res = bs64.decode(PWR_data.getProfile_img());
					String profileImg = Integer.toString(user_id)+"_profile.jpg";
					ImageCodec.saveImage(BS_res, profileImg);
					query = "insert into user_data(user_id, name, gender, country, job, profile_img) "
							+ "values(?,?,?,?,?,?)";
					db.setPreparedStatement(query);
					db.getPreparedStatement().setInt(1, user_id);
					db.getPreparedStatement().setString(2, PWR_data.getName());
					db.getPreparedStatement().setString(3, PWR_data.getGender());
					db.getPreparedStatement().setString(4, PWR_data.getCountry());
					db.getPreparedStatement().setString(5, PWR_data.getJob());
					db.getPreparedStatement().setString(6, ImageCodec.IMG_DIR+profileImg);
					db.getPreparedStatement().executeUpdate();
				}catch(SQLException e){
					db.printError(e, query);
				}
				ProfileWriteAck pwrack = new ProfileWriteAck(Packet.PWR_SUCCESS);
				output = PacketCodec.encode_ProfileWriteAck(pwrack);try{ 
				out.println(output);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					out.close();
				}
				break;
			default:
				
		}
	}
}
