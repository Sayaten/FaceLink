package TestSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import Server.Base64Codec;
import Server.ImageCodec;

public class CommunicationTest{
	public static void main2(String[] args) throws IOException{
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
				
		serverSocket = new ServerSocket(9193);
		serverSocket.setReuseAddress(true);
		try{
			clientSocket = serverSocket.accept();
			System.out.println("Client Connect");
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String inputLine = "";
			while (true) {
				inputLine = in.readLine();
			
				if (inputLine.equals("quit"))
					break;
			}
			out.close();
			in.close();
			clientSocket.close();
			serverSocket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main3(String[] args)throws IOException{//receiveBase64Img(String[] args) throws IOException{
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
