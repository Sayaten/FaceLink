package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class ThreadServer implements Runnable {
	private Base64Codec bs64 = new Base64Codec();
	private Socket clientSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private boolean isContinous = false;
	
	String inputData;
	Packet rec_packet;
	
	public ThreadServer(Socket clientSocket, boolean isContinous) throws IOException{
		this.clientSocket = clientSocket;
		this.isContinous = isContinous;
		
		inputData = "";

		System.out.println("Client Connect");
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		out = new PrintWriter(clientSocket.getOutputStream(), true);
	}
	
	public void run(){
		try{
			while(isContinous){
				while (true) {
					inputData = PacketCodec.read_delim(in);
					if (inputData.charAt(inputData.length() - 1) == '?')
						break;
				}
				rec_packet = PacketCodec.decode_Header(inputData);

				isContinous = handler(rec_packet, out);
			}
			in.close();
			out.close();
			clientSocket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean handler(Packet src, PrintWriter out) throws IOException{
		String part_image = "";
		String profile_image = "";
		String ideal_image = "";
		String screen_name = "";
		String password = "";
		String send_user = "";
		String rec_user = "";
		isContinous = true;
		Database db = new Database();
		ResultSet rs;
		byte[] byte_image = null;
		String query = "";
		String output = "";
		int user_id = 0;
		int send_id = 0;
		int rec_id = 0;
		
		if(!db.connect()){
			System.out.println("DB Error!!");
			return isContinous;
		}
		
		System.out.println("Packet Type: "+src.getType());

		switch(src.getType()){
			case Packet.PK_JOIN_REQ:
				JoinReq j_req = PacketCodec.decode_JoinReq(src.getData());
				query = "insert into login_data(screen_name, password) "
						+ "values('"+j_req.getScreen_name()+"','"+j_req.getPassword()+"');";
				try{
					db.getStatement().executeUpdate(query);
				}catch(SQLException e){
					db.printError(e, query);
				}
				JoinAck j_ack = new JoinAck(Packet.SUCCESS);
				output = PacketCodec.encode_JoinAck(j_ack);
				try{ 
					out.println(output);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					out.close();
				}
				break;
			case Packet.PK_LOGIN_REQ:
				LoginReq l_req = PacketCodec.decode_LoginReq(src.getData());
				query = "select password from login_data where screen_name = '" + l_req.getScreen_name() + "'";
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					password = rs.getString("password");
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				LoginAck l_ack = new LoginAck();
				if( password.compareTo("") == 0 || password.compareTo(l_req.getPassword()) != 0){
					l_ack.setResult(Packet.FAIL);
				}
				else{
					l_ack.setResult(Packet.SUCCESS);
					
					query = "select user_id from login_data where screen_name = '" + l_req.getScreen_name() + "'";
					try{
						rs = db.getStatement().executeQuery(query);
						rs.next();
						user_id = rs.getInt("user_id");
						rs.close();
					}catch(SQLException e){
						db.printError(e, query);
					}
					
					query = "select * from user_data where user_id = "+Integer.toString(user_id);
					
					try{
						rs = db.getStatement().executeQuery(query);
						rs.next();
						
						l_ack.setName(rs.getString("name"));
						l_ack.setGender(rs.getString("gender"));
						l_ack.setCountry(rs.getString("country"));
						l_ack.setJob(rs.getString("job"));
						
						rs.close();
						
						byte_image = ImageCodec.loadImageToByteArray("profile", Integer.toString(user_id)+"_profile.jpg");
						profile_image = bs64.encode(byte_image);
						l_ack.setProfile_img(profile_image);
					}catch(SQLException e){
						db.printError(e, query);
					}
				}
				
				output = PacketCodec.encode_LoginAck(l_ack);
				
				try{ 
					out.println(output);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					out.close();
				}
				break;
			case Packet.PK_PRO_WRITE_REQ:
				ProfileModifyReq pw_req = PacketCodec.decode_ProfileWriteReq(src.getData());
				user_id = 0;
				
				query = "select user_id from login_data "
						+ "where screen_name = '"+ pw_req.getScreen_name()+"';";
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					user_id = rs.getInt("user_id");
					rs.close();
				
					byte_image = bs64.decode(pw_req.getProfile_img());
					profile_image = Integer.toString(user_id)+"_profile.jpg";
					ImageCodec.saveImage(byte_image, "profile",profile_image);
					query = "insert into user_data(user_id, name, gender, country, job) "
							+ " values(" 
							+ Integer.toString(user_id)
							+ ",'" + pw_req.getName() + "' "
							+ ",'" + pw_req.getGender() + "' "
							+ ",'" + pw_req.getCountry() + "' "
							+ ",'" + pw_req.getJob() + "')";
					db.getStatement().executeUpdate(query);
				}catch(SQLException e){
					db.printError(e, query);
				}
				ProfileModifyAck pwr_ack = new ProfileModifyAck(Packet.SUCCESS);
				output = PacketCodec.encode_ProfileWriteAck(pwr_ack);
				try{ 
					out.println(output);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					out.close();
				}
				break;
			case Packet.PK_PRO_MODIFY_REQ:
				ProfileModifyReq pm_req = PacketCodec.decode_ProfileModifyReq(src.getData());
				String columns = "";
				String column_data = "";
				
				user_id = 0;
				
				query = "select user_id from login_data "
						+ "where screen_name = '"+ pm_req.getScreen_name()+"';";
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					user_id = rs.getInt("user_id");
					rs.close();
				
					if(pm_req.getProfile_img() != null){
						byte_image = bs64.decode(pm_req.getProfile_img());
						profile_image = Integer.toString(user_id)+"_profile.jpg";
						ImageCodec.saveImage(byte_image, "profile",profile_image);
					}
					
					query = "update user_data set "
							+ ( (pm_req.getName() != null)   ? "name = " : "")
							+ ( (pm_req.getName() != null)   ? "'" + pm_req.getName() + "'" : "")
							+ ( (pm_req.getGender() != null) ? ",gender = " : "")
							+ ( (pm_req.getGender() != null) ? "'" + pm_req.getGender() + "'" : "")
							+ ( (pm_req.getJob() != null)    ? ",job = " : "")
							+ ( (pm_req.getJob() != null)    ? "'" + pm_req.getJob() + "'" : "")
							+ ( (pm_req.getCountry() != null)? ",country = " : "")
							+ ( (pm_req.getCountry() != null)? "'" + pm_req.getCountry() + "'" : "")
							+ " where user_id = " + Integer.toString(user_id);
					db.getStatement().executeUpdate(query);
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				ProfileModifyAck pm_ack = new ProfileModifyAck(Packet.SUCCESS);
				output = PacketCodec.encode_ProfileModifyAck(pm_ack);
				try{ 
					out.println(output);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					out.close();
				}
				break;
			case Packet.PK_PART_REG_REQ:
				PartRegisterReq pr_req = PacketCodec.decode_PartRegisterReq(src.getData());
				user_id = 0;
				
				query = "select user_id from login_data "
						+ "where screen_name = '"+ pr_req.getScreen_name()+"';";
				
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					user_id = rs.getInt("user_id");
					rs.close();
				
					byte_image = bs64.decode(pr_req.getEyes());
					part_image = Integer.toString(user_id)+"_eyes.jpg";
					ImageCodec.saveImage(byte_image, "part", part_image);
					
					byte_image = bs64.decode(pr_req.getNose());
					part_image = Integer.toString(user_id)+"_nose.jpg";
					ImageCodec.saveImage(byte_image, "part", part_image);
					
					byte_image = bs64.decode(pr_req.getMouth());
					part_image = Integer.toString(user_id)+"_mouth.jpg";
					ImageCodec.saveImage(byte_image, "part", part_image);
					
					byte_image = bs64.decode(pr_req.getFace());
					part_image = Integer.toString(user_id)+"_face.jpg";
					ImageCodec.saveImage(byte_image, "part", part_image);
					
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				PartRegisterAck pr_ack = new PartRegisterAck(Packet.SUCCESS);
				output = PacketCodec.encode_PartRegisterAck(pr_ack);
				try{ 
					out.println(output);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					out.close();
				}
				break;
			case Packet.PK_PART_GET_REQ:
				PartGetReq pg_req = PacketCodec.decode_PartGetReq(src.getData());
				
				if(pg_req.getPart_type().compareTo(PartGetReq.STOP) == 0) break;
				
				int count = 1;
				
				query = "select count(*) from login_data";
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					count = rs.getInt(1);
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				user_id = pg_req.getUser_id() % count + 1;
				
				query = "select user_id from login_data";
				
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					for(int i = 1; i < user_id ; ++i){
						rs.next();
					}
					count = rs.getInt("user_id");
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				byte_image = ImageCodec.loadImageToByteArray("part", Integer.toString(user_id) + "_" + pg_req.getPart_type()+".jpg");
				part_image = bs64.encode(byte_image);
				
				PartGetAck pg_ack = new PartGetAck();
				pg_ack.setPart(part_image);
				
				output = PacketCodec.encode_PartGetAck(pg_ack);
				try{ 
					out.println(output);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					out.close();
				}
				
				src.setType(Packet.PK_PART_GET_CON);
				
				break;
			case Packet.PK_IDEAL_REG_REQ:
				IdealTypeRegisterReq itr_req = PacketCodec.decode_IdealTypeRegisterReq(src.getData());
				user_id = 0;
				
				query = "select user_id from login_data "
						+ "where screen_name = '"+ itr_req.getScreen_name()+"';";
				
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					user_id = rs.getInt("user_id");
					rs.close();
				
					byte_image = bs64.decode(itr_req.getIdeal_type());
					ideal_image = Integer.toString(user_id)+"_ideal_type.jpg";
					ImageCodec.saveImage(byte_image, "ideal_type", ideal_image);
					
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				IdealTypeRegisterAck itr_ack = new IdealTypeRegisterAck(Packet.SUCCESS);
				output = PacketCodec.encode_IdealTypeRegisterAck(itr_ack);
				try{ 
					out.println(output);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					out.close();
				}
				break;
			case Packet.PK_IDEAL_SCH_REQ:
				IdealTypeSearchReq its_req = PacketCodec.decode_IdealTypeSearchReq(src.getData());
				ArrayList<ImageSimilarity> image_arr = ComparisonSimilarity.getSimilarImage(its_req.getScreen_name());
				ArrayList<ImageNameSet> ideal_arr = new ArrayList<ImageNameSet> ();
				int begin = 0;
				int end = 0;
				
				QuickSort.quickSort(image_arr, 0, image_arr.size() - 1);
				
				for(int i = 0 ; i < 5 && i < image_arr.size(); ++i){
					end = image_arr.get(i).getName().indexOf('_') - 1;
				
					user_id = Integer.parseInt(image_arr.get(i).getName().substring(begin, end));
				
					query = "select screen_name from login_data "
							+ "where user_id = '"+ Integer.toString(user_id) + "';";
					
					try{
						rs = db.getStatement().executeQuery(query);
						rs.next();
						screen_name = rs.getString("screen_name");
						rs.close();
					}catch(SQLException e){
						db.printError(e, query);
					}
					byte_image = ImageCodec.loadImageToByteArray("profile", image_arr.get(i).getName());
					profile_image = bs64.encode(byte_image);

					ideal_arr.add(new ImageNameSet(screen_name, profile_image));
				}
				
				IdealTypeSearchAck its_ack = new IdealTypeSearchAck();
				its_ack.setIdeal_types(ideal_arr);
				
				output = PacketCodec.encode_IdealTypeSearchAck(its_ack);
				try{ 
					out.println(output);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					out.close();
				}
			case Packet.PK_CONTACT_REQ:
				ContactReq c_req = PacketCodec.decode_ContactReq(src.getData());
				send_id = 0;
				rec_id = 0;
				
				query = "select user_id from login_data where screen_name = '" + c_req.getSend_user() + "'";
				
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					send_id = rs.getInt("user_id");
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}

				query = "select user_id from login_data where screen_name = '" + c_req.getRec_user() + "'";
				
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					rec_id = rs.getInt("user_id");
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				
				try{
					query = "insert into contact(send_id, receive_id, isAccept) "
							+ "values("
							+ Integer.toString(send_id)+","
							+ Integer.toString(rec_id)+","
							+ Integer.toString(ReplyContactReq.STANDBY)+")";
					
					//db.setPreparedStatement(query);
					//db.getPreparedStatement().setInt(1, send_id);
					//db.getPreparedStatement().setInt(2, rec_id);
					//db.getPreparedStatement().setInt(3, ReplyContactReq.STANDBY);
					//db.getPreparedStatement().executeUpdate();
					db.getStatement().executeUpdate(query);
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				ContactAck c_ack = new ContactAck(Packet.SUCCESS);
				output = PacketCodec.encode_ContactAck(c_ack);
				try{ 
					out.println(output);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					out.close();
				}
				break;
			case Packet.PK_REPLY_CON_REQ:
				ReplyContactReq rc_req = PacketCodec.decode_ReplyContactReq(src.getData());
				
				send_id = 0;
				rec_id = 0;
				
				query = "select user_id from login_data where screen_name = '" + rc_req.getSend_user() + "'";
				
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					send_id = rs.getInt("user_id");
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}

				query = "select user_id from login_data where screen_name = '" + rc_req.getRec_user() + "'";
				
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					rec_id = rs.getInt("user_id");
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				if(rc_req.getReply() == ReplyContactReq.REJECT){
					query = "delete from contact where send_id = " + Integer.toString(send_id)
							+" and receive_id = " + Integer.toString(rec_id);
					try{
						db.getStatement().executeUpdate(query);
					}catch(SQLException e){
						db.printError(e, query);
					}
				}else if(rc_req.getReply() == ReplyContactReq.ACCEPT){
					query = "update contact set isAccept = "+Integer.toString(rc_req.getReply())
							+" where send_id = " + Integer.toString(send_id)
							+" and receive_id = " + Integer.toString(rec_id);
					try{
						db.getStatement().executeUpdate(query);
					}catch(SQLException e){
						db.printError(e, query);
					}
				}
				
				ReplyContactAck rc_ack = new ReplyContactAck(Packet.SUCCESS);
				output = PacketCodec.encode_ReplyContactAck(rc_ack);
				try{ 
					out.println(output);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					out.close();
				}
				break;
			case Packet.PK_GET_CON_REQ:
				GetContactReq gc_req = PacketCodec.decode_GetContactReq(src.getData());
				
				query = "select user_id from login_data where screen_name = '" + gc_req.getScreen_name() + "'";
				
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					user_id = rs.getInt("user_id");
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				GetContactAck gc_ack = new GetContactAck();
				
				ArrayList<int[]> con_arr = new ArrayList<int[]> ();
				int[] temp = null;
				
				query = "select send_id, isAccept from contact where user_id = " + Integer.toString(user_id);
				
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					temp = new int[2]; // 0 == send_id  1 == isAccept 
					temp[0] = rs.getInt("send_id");
					temp[1] = rs.getInt("isAccept");
					con_arr.add(temp);
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				for(int i = 0 ; i < con_arr.size() ; ++i){
					query = "select screen_name from login_data where user_id = '" + con_arr.get(i)[0] + "'";
					try{
						rs = db.getStatement().executeQuery(query);
						rs.next();
						send_user = rs.getString("screen_name");
						rs.close();
					}catch(SQLException e){
						db.printError(e, query);
					}
					byte_image = ImageCodec.loadImageToByteArray("profile", Integer.toString(con_arr.get(i)[0])+"_profile.jpg");
					profile_image = bs64.encode(byte_image);
					
					gc_ack.getContacts().add(new ContactInfo( new ImageNameSet(send_user, profile_image), con_arr.get(i)[1] ));
				}
				
				output = PacketCodec.encode_GetContactAck(gc_ack);
				try{ 
					out.println(output);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					out.close();
				}
				break;
			case Packet.PK_CONNECTION_END:
				ConnectionEndReq ce_req = PacketCodec.decode_ConnectionEndReq(src.getData());
				if(ce_req.getIsEnd().compareTo(ConnectionEndReq.END) == 0) isContinous = false;
			default:
				System.out.println("Not Defined Packet Type!!!!");
		}
		return isContinous;
	}
}
