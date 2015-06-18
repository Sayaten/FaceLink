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
	private int user_id = 0;
	String inputData;
	Packet rcvPacket;

	public ThreadServer(Socket clientSocket, boolean isContinous) throws IOException{
		this.clientSocket = clientSocket;
		this.isContinous = isContinous;
		
		inputData = "";

		System.out.println("Client Connect");
	}

	public ThreadServer(ServerSocket serverSocket, boolean isContinous) throws IOException{
		clientSocket = serverSocket.accept();
		this.isContinous = isContinous;
		
		inputData = "";

		System.out.println("Client Connect");
	}

	
	public void run(){
		try{
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			
			while(isContinous){
				// read packet and decode
				inputData = PacketCodec.readDelimiter(in);
				rcvPacket = PacketCodec.decodeHeader(inputData);
				isContinous = handler(rcvPacket, out);
			}

			in.close();
			out.close();
			clientSocket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean handler(Packet src, PrintWriter out) throws IOException{
		String partImage = "";
		String profileImage = "";
		String idealImage = "";
		String screen_name = "";
		String password = "";
		String send_user = "";
		String rec_user = "";
		String thumbnailImage = "";
		String sendPacket = "";
		isContinous = true;
		Database db = new Database();
		ResultSet rs;
		byte[] byteImage = null;
		String query = "";
		String output = "";
		int send_id = 0;
		int rec_id = 0;
		
		if(!db.connect()){
			System.out.println("DB Error!!");
			return isContinous;
		}
		
		System.out.println("Packet Type: "+src.getType());

		switch(src.getType()){
			// join request
			case Packet.PK_JOIN_REQ:
				JoinReq j_req = PacketCodec.decodeJoinReq(src.getData());
				query = "insert into login_data(screen_name, password) "
						+ "values('"+j_req.getScreen_name()+"','"+j_req.getPassword()+"');";
				try{
					db.getStatement().executeUpdate(query);
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				query = "select user_id from login_data where screen_name = '" + j_req.getScreen_name() + "'";
				
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					user_id = rs.getInt("user_id");
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				// send ack packet of join request
				JoinAck j_ack = new JoinAck(Packet.SUCCESS);
				output = PacketCodec.encodeJoinAck(j_ack);
				sendPacket = PacketCodec.addPacketSize(output);
				try{ 
					out.println(sendPacket);
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
				
			// login request
			case Packet.PK_LOGIN_REQ:
				LoginReq l_req = PacketCodec.decodeLoginReq(src.getData());
				query = "select password from login_data where screen_name = '" + l_req.getScreen_name() + "'";
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					password = rs.getString("password");
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				// send ack packet of login request
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
						
						byteImage = ImageCodec.loadImageToByteArray("profile", Integer.toString(user_id)+"_thumbnail.jpg");
						thumbnailImage = bs64.encode(byteImage);
						l_ack.setProfile_img(thumbnailImage);
					}catch(SQLException e){
						db.printError(e, query);
					}
				}
				
				output = PacketCodec.encodeLoginAck(l_ack);
				
				sendPacket = PacketCodec.addPacketSize(output);
				try{ 
					out.println(sendPacket);
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			
			// profile write request 
			case Packet.PK_PRO_WRITE_REQ:
				ProfileModifyReq pw_req = PacketCodec.decodeProfileWriteReq(src.getData());
				user_id = 0;
				
				query = "select user_id from login_data "
						+ "where screen_name = '"+ pw_req.getScreen_name()+"';";
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					user_id = rs.getInt("user_id");
					rs.close();
				
					byteImage = bs64.decode(pw_req.getProfile_img());
					profileImage = Integer.toString(user_id)+"_profile.jpg";
					thumbnailImage = Integer.toString(user_id)+"_thumbnail.jpg";
					ImageCodec.saveImage(byteImage, "profile",profileImage);
					ImageCodec.saveThumbnailImage(byteImage, "profile", thumbnailImage, 0.7f);
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
				
				// send ack packet of profile write request
				ProfileModifyAck pwr_ack = new ProfileModifyAck(Packet.SUCCESS);
				output = PacketCodec.encodeProfileWriteAck(pwr_ack);
				sendPacket = PacketCodec.addPacketSize(output);
				try{ 
					out.println(sendPacket);
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
				
			// profile modify request
			case Packet.PK_PRO_MODIFY_REQ:
				ProfileModifyReq pm_req = PacketCodec.decodeProfileModifyReq(src.getData());
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
						byteImage = bs64.decode(pm_req.getProfile_img());
						profileImage = Integer.toString(user_id)+"_profile.jpg";
						thumbnailImage = Integer.toString(user_id)+"_thumbnail.jpg";
						ImageCodec.saveImage(byteImage, "profile",profileImage);
						ImageCodec.saveThumbnailImage(byteImage, "profile", thumbnailImage, 0.7f);
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
				
				// send ack packet of profile modify request
				ProfileModifyAck pm_ack = new ProfileModifyAck(Packet.SUCCESS);
				output = PacketCodec.encodeProfileModifyAck(pm_ack);
				sendPacket = PacketCodec.addPacketSize(output);
				try{ 
					out.println(sendPacket);
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
				
			// part of face register request
			case Packet.PK_PART_REG_REQ:
				PartRegisterReq pr_req = PacketCodec.decodePartRegisterReq(src.getData());
				user_id = 0;
				
				query = "select user_id from login_data "
						+ "where screen_name = '"+ pr_req.getScreen_name()+"';";
				
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					user_id = rs.getInt("user_id");
					rs.close();
				
					byteImage = bs64.decode(pr_req.getEyes());
					partImage = Integer.toString(user_id)+"_eyes.jpg";
					ImageCodec.saveImage(byteImage, "part", partImage);
					
					byteImage = bs64.decode(pr_req.getNose());
					partImage = Integer.toString(user_id)+"_nose.jpg";
					ImageCodec.saveImage(byteImage, "part", partImage);
					
					byteImage = bs64.decode(pr_req.getMouth());
					partImage = Integer.toString(user_id)+"_mouth.jpg";
					ImageCodec.saveImage(byteImage, "part", partImage);
					
					byteImage = bs64.decode(pr_req.getFace());
					partImage = Integer.toString(user_id)+"_face.jpg";
					ImageCodec.saveImage(byteImage, "part", partImage);
					
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				// send ack packet of part register request
				PartRegisterAck pr_ack = new PartRegisterAck(Packet.SUCCESS);
				output = PacketCodec.encodePartRegisterAck(pr_ack);
				sendPacket = PacketCodec.addPacketSize(output);
				try{ 
					out.println(sendPacket);
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			
			// part of face get request
			case Packet.PK_PART_GET_REQ:
				PartGetReq pg_req = PacketCodec.decodePartGetReq(src.getData());
				
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
				
				byteImage = ImageCodec.loadImageToByteArray("part", Integer.toString(user_id) + "_" + pg_req.getPart_type()+".jpg");
				partImage = bs64.encode(byteImage);
				
				// send part of face
				PartGetAck pg_ack = new PartGetAck();
				pg_ack.setPart(partImage);
				
				output = PacketCodec.encodePartGetAck(pg_ack);
				sendPacket = PacketCodec.addPacketSize(output);
				try{ 
					out.println(sendPacket);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				break;
				
			// ideal type register request
			case Packet.PK_IDEAL_REG_REQ:
				IdealTypeRegisterReq itr_req = PacketCodec.decodeIdealTypeRegisterReq(src.getData());
				user_id = 0;
				
				query = "select user_id from login_data "
						+ "where screen_name = '"+ itr_req.getScreen_name()+"';";
				
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					user_id = rs.getInt("user_id");
					rs.close();
				
					byteImage = bs64.decode(itr_req.getIdeal_type());
					idealImage = Integer.toString(user_id)+"_ideal_type.jpg";
					ImageCodec.saveImage(byteImage, "ideal_type", idealImage);
					
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				// send ack packet of ideal type register request
				IdealTypeRegisterAck itr_ack = new IdealTypeRegisterAck(Packet.SUCCESS);
				output = PacketCodec.encodeIdealTypeRegisterAck(itr_ack);
				sendPacket = PacketCodec.addPacketSize(output);
				try{ 
					out.println(sendPacket);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				break;
			
			// search ideal type request
			case Packet.PK_IDEAL_SCH_REQ:
				IdealTypeSearchReq its_req = PacketCodec.decodeIdealTypeSearchReq(src.getData());
				int begin = 0;
				int end = 0;
				
				ArrayList<ImageNameSet> ideal_arr = new ArrayList<ImageNameSet> ();
				ArrayList<ImageSimilarity> image_arr = new ArrayList<ImageSimilarity> ();
						
				query = "select user_id from login_data where screen_name = '" + its_req.getScreen_name() + "'";		
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					user_id = rs.getInt("user_id");
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}
				try{
					// compare images
					ComparisonSimilarity.getSimilarImage(image_arr, Integer.toString(user_id) + "_ideal_type.jpg");
				}catch(Exception e){
					System.out.println("Image comparison error");
				}
				
				// sorting array list in descending order
				QuickSort.quickSort(image_arr, 0, image_arr.size() - 1);
				
				// using maximum 3 images
				for(int i = 0 ; i < 3 && i < image_arr.size(); ++i){
					begin = image_arr.get(i).getName().lastIndexOf('/') + 1;
					end = image_arr.get(i).getName().indexOf('_');
					
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
					byteImage = ImageCodec.loadImageToByteArray(image_arr.get(i).getName());
					thumbnailImage = bs64.encode(byteImage);

					ideal_arr.add(new ImageNameSet(screen_name, thumbnailImage));
				}
				
				// send result of searching ideal type
				IdealTypeSearchAck its_ack = new IdealTypeSearchAck();
				its_ack.setIdeal_types(ideal_arr);
				
				output = PacketCodec.encodeIdealTypeSearchAck(its_ack);
				sendPacket = PacketCodec.addPacketSize(output);
				try{ 
					out.println(sendPacket);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				break;
				
			// contact request
			case Packet.PK_CONTACT_REQ:
				ContactReq c_req = PacketCodec.decodeContactReq(src.getData());
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
							+ Integer.toString(ContactInfo.STANDBY)+")";
					db.getStatement().executeUpdate(query);
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				// send ack of contact request
				ContactAck c_ack = new ContactAck(Packet.SUCCESS);
				output = PacketCodec.encodeContactAck(c_ack);
				sendPacket = PacketCodec.addPacketSize(output);
				try{ 
					out.println(sendPacket);
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			
			// replying contact request
			case Packet.PK_REPLY_CON_REQ:
				ReplyContactReq rc_req = PacketCodec.decodeReplyContactReq(src.getData());
				
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
				
				if(rc_req.getReply() == ContactInfo.REJECT){
					query = "delete from contact where send_id = " + Integer.toString(send_id)
							+" and receive_id = " + Integer.toString(rec_id);
					try{
						db.getStatement().executeUpdate(query);
					}catch(SQLException e){
						db.printError(e, query);
					}
				}else if(rc_req.getReply() == ContactInfo.ACCEPT){
					query = "update contact set isAccept = "+Integer.toString(rc_req.getReply())
							+" where send_id = " + Integer.toString(send_id)
							+" and receive_id = " + Integer.toString(rec_id);
					try{
						db.getStatement().executeUpdate(query);
					}catch(SQLException e){
						db.printError(e, query);
					}
				}
				
				// send ack of replying contact request
				ReplyContactAck rc_ack = new ReplyContactAck(Packet.SUCCESS);
				output = PacketCodec.encodeReplyContactAck(rc_ack);
				sendPacket = PacketCodec.addPacketSize(output);
				try{ 
					out.println(sendPacket);
				}catch(Exception e){
					e.printStackTrace();
				}

				break;
			
			// get contact request
			case Packet.PK_GET_CON_REQ:
				GetContactReq gc_req = PacketCodec.decodeGetContactReq(src.getData());
				
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
				
				// get received contact
				query = "select send_id, isAccept from contact where receive_id = " + Integer.toString(user_id)
						+ " and isAccept = " + Integer.toString(ContactInfo.STANDBY);
				
				try{
					rs = db.getStatement().executeQuery(query);
					while(rs.next()){
						temp = new int[2]; // 0 == send_id  1 == isAccept 
						temp[0] = rs.getInt("send_id");
						temp[1] = rs.getInt("isAccept");
						con_arr.add(temp);
					}
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				// get accepted contact 
				query = "select receive_id, isAccept from contact where send_id = " + Integer.toString(user_id)
						+ " and isAccept = " + Integer.toString(ContactInfo.ACCEPT);
				
				try{
					rs = db.getStatement().executeQuery(query);
					while(rs.next()){
						temp = new int[2]; // 0 == send_id  1 == isAccept 
						temp[0] = rs.getInt("receive_id");
						temp[1] = rs.getInt("isAccept");
						con_arr.add(temp);
					}
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
					byteImage = ImageCodec.loadImageToByteArray("profile", Integer.toString(con_arr.get(i)[0])+"_thumbnail.jpg");
					thumbnailImage = bs64.encode(byteImage);
					
					gc_ack.getContacts().add(new ContactInfo( new ImageNameSet(send_user, thumbnailImage), con_arr.get(i)[1] ));
				}
				
				// send ack packet of get contact request
				output = PacketCodec.encodeGetContactAck(gc_ack);
				sendPacket = PacketCodec.addPacketSize(output);
				try{ 
					out.println(sendPacket);
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			
			// socket connection end request
			case Packet.PK_CONNECTION_END:
				ConnectionEndReq ce_req = PacketCodec.decodeConnectionEndReq(src.getData());
				if(ce_req.getIsEnd().compareTo(ConnectionEndReq.END) == 0) isContinous = false;
				break;
				
			// profile get reuqest
			case Packet.PK_PRO_GET_REQ:
				ProfileGetReq prog_req = PacketCodec.decodeProfileGetReq(src.getData());
				query = "select user_id from login_data where screen_name = '" + prog_req.getScreen_name() + "'";
				
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					user_id = rs.getInt("user_id");
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				ProfileGetAck prog_ack = new ProfileGetAck(Packet.SUCCESS);
				
				query = "select * from user_data where user_id = " + Integer.toString(user_id);
				
				try{
					rs = db.getStatement().executeQuery(query);
					rs.next();
					prog_ack.setName(rs.getString("name"));
					prog_ack.setGender(rs.getString("gender"));
					prog_ack.setJob(rs.getString("job"));
					prog_ack.setCountry(rs.getString("country"));
					rs.close();
				}catch(SQLException e){
					db.printError(e, query);
				}
				
				byteImage = ImageCodec.loadImageToByteArray("profile", Integer.toString(user_id) + "_thumbnail.jpg");
				thumbnailImage = bs64.encode(byteImage);
				prog_ack.setProfile_img(thumbnailImage);
				
				// send profile data 
				output = PacketCodec.encodeProfileGetAck(prog_ack);
				sendPacket = PacketCodec.addPacketSize(output);
				try{ 
					out.println(sendPacket);
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			default:
				System.out.println("Not Defined Packet Type!!!!");
		}
		return isContinous;
	}
}
