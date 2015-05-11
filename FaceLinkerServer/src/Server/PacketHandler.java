package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

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
	
	public Packet decode_Header(String src) throws IOException{
		String type, data;
		Scanner s = new Scanner(src).useDelimiter(Packet.FIELD_DELIM);
		
		type = s.next();
		s.useDelimiter(Packet.PK_DELIM);
		s.skip(Packet.FIELD_DELIM);
		
		data = s.next();
		
		return new Packet(type, data);
	}
	
	public void handler(Packet src) throws IOException{
		PacketCodec codec = new PacketCodec();
		switch(src.getType()){
			case Packet.PK_JOIN_REQ:
				
				break;
			case Packet.PK_PRO_WRITE_REQ:
				break;
		}
	}
}
