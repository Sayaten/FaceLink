package Server;

import java.io.IOException;
import java.util.Scanner;

public class PacketCodec {
	// About Join Request
	// encode Join Request Packet data
	public String encode_JoinReq(JoinReq pk_data){
		String data = Packet.PK_JOIN_REQ + Packet.FIELD_DELIM 
				+ pk_data.getScreenID() + Packet.FIELD_DELIM
				+ pk_data.getPassword() + Packet.FIELD_DELIM
				+ Packet.PK_DELIM;
				
		return data;
	}
	// decode Join Request Packet data
	public JoinReq decode_JoinReq(String pk_data) throws IOException{
		Scanner s = new Scanner(pk_data).useDelimiter("\\"+Packet.FIELD_DELIM);
		JoinReq dst = new JoinReq();
		
		dst.setScreenID(s.next());
		dst.setPassword(s.next());
		
		return dst;
	}
	
	// About Join Ack
	// encode Join Response Packet data
	public String encode_JoinAck(JoinAck pk_data){
		String data = Packet.PK_JOIN_ACK + Packet.FIELD_DELIM
				+ Integer.toString(pk_data.getResult()) + Packet.FIELD_DELIM
				+ Packet.PK_DELIM;
		
		return data;
	}

	// decode Join Response Packet data
	public JoinAck decoeee_JoinAck(String pk_data){
		Scanner s = new Scanner(pk_data).useDelimiter("\\"+Packet.FIELD_DELIM);
		JoinAck dst = new JoinAck();
		
		dst.setResult(Integer.parseInt(s.next()));
		
		return dst;
	}
}
