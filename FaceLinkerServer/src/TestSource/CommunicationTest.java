package TestSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CommunicationTest{
	public static void main(String[] args) throws IOException{
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
				System.out.println("클라이언트로부터 받은 문자열 : " + inputLine);
				out.println(inputLine);
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
}
