package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class CommunicationServer {
	public static void main(String[] args) throws IOException{
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		
		String inputData = "";
		
		// 9193 9194 9195
		serverSocket = new ServerSocket(9195);
		serverSocket.setReuseAddress(true);
		try{
			clientSocket = serverSocket.accept();
			System.out.println("Client Connect");
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			while(true){
				inputData = PacketHandler.read_delim(in);
				if(inputData.charAt(inputData.length()-1) == '?') break;
			}
			inputData.replace('?', '\0');
			Base64Codec bc = new Base64Codec();
			byte[] res = bc.decode(inputData);
			ImageCodec.saveImage(res, "test.PNG");
			System.out.print("클라이언트로부터 받은 문자열 : " + inputData);
			in.close();
			clientSocket.close();
			serverSocket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
