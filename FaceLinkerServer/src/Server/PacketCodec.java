package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

public class PacketCodec {
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
	// About join request
	// Dncode join request packet data
	public String encode_JoinReq(JoinReq pk_data){
		String data = Packet.PK_JOIN_REQ + Packet.FIELD_DELIM 
				+ pk_data.getScreen_name() + Packet.FIELD_DELIM
				+ pk_data.getPassword() + Packet.FIELD_DELIM
				+ Packet.PK_DELIM;
				
		return data;
	}
	// Decode join request packet data
	public JoinReq decode_JoinReq(String pk_data) throws IOException{
		Scanner s = new Scanner(pk_data).useDelimiter("\\"+Packet.FIELD_DELIM);
		JoinReq dst = new JoinReq();
		
		dst.setScreen_name(s.next());
		dst.setPassword(s.next());
		
		return dst;
	}
	
	// About join ack
	// Decode join response packet data
	public String encode_JoinAck(JoinAck pk_data){
		String data = Packet.PK_JOIN_ACK + Packet.FIELD_DELIM
				+ Integer.toString(pk_data.getResult()) + Packet.FIELD_DELIM
				+ Integer.toString(pk_data.getUser_id()) + Packet.FIELD_DELIM
				+ Packet.PK_DELIM;
		
		return data;
	}

	// Decode join response packet data
	public JoinAck decode_JoinAck(String pk_data){
		Scanner s = new Scanner(pk_data).useDelimiter("\\"+Packet.FIELD_DELIM);
		JoinAck dst = new JoinAck();
		
		dst.setResult(Integer.parseInt(s.next()));
		dst.setUser_id(Integer.parseInt(s.next()));
		
		return dst;
	}
	
	// About profile write request
	// Encode profile write request packet data
	public String encode_ProfileWriteReq(ProfileWriteReq pk_data){
		String data = Packet.PK_PRO_WRITE_REQ + Packet.FIELD_DELIM
				+ Integer.toString(pk_data.getUser_id()) + Packet.FIELD_DELIM
				+ pk_data.getName() + Packet.FIELD_DELIM
				+ pk_data.getGender() + Packet.FIELD_DELIM
				+ pk_data.getJob() + Packet.FIELD_DELIM
				+ pk_data.getCountry() + Packet.FIELD_DELIM
				+ pk_data.getProfile_img() + Packet.FIELD_DELIM
				+ Packet.PK_DELIM;
		return data;
	}

	// Decode profile write request packet data
	public ProfileWriteReq decode_ProfileWriteReq(String pk_data){
		Scanner s = new Scanner(pk_data).useDelimiter("\\"+Packet.FIELD_DELIM);
		ProfileWriteReq dst = new ProfileWriteReq();
		
		dst.setUser_id(s.nextInt());
		dst.setName(s.next());
		dst.setGender(s.next());
		dst.setJob(s.next());
		dst.setCountry(s.next());
		dst.setProfile_img(s.next());
		
		return dst;
	}
	
	// About profile write ack
	// Encode profile write response packet data
	public String encode_ProfileWriteAck(ProfileWriteAck pk_data){
		String data = Packet.PK_PRO_WRITE_ACK + Packet.FIELD_DELIM
				+ Integer.toString(pk_data.getResult()) + Packet.FIELD_DELIM
				+ Packet.PK_DELIM;
		return data;
	}
	
	// Encode profile write response packet data
	public ProfileWriteAck decode_ProfileWriteAck(String pk_data){
		Scanner s = new Scanner(pk_data).useDelimiter(Packet.FIELD_DELIM);
		ProfileWriteAck dst = new ProfileWriteAck();
		
		dst.setResult(Integer.parseInt(s.next()));

		return dst;
	}
}
