package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
	private static Thread thread = null;
	private static ServerSocket serverSocket = null;
	private static Socket clientSocket = null;
	private static final int PORT = 9193; // can use 9193 9194 9195
	private static ThreadServer threadServer = null;
	
	public static void main(String[] args) throws IOException{
		while(true)
		{
			serverSocket = new ServerSocket(PORT);
			serverSocket.setReuseAddress(true);
			
			while(true) {
				clientSocket = serverSocket.accept();
				if(clientSocket != null){
					threadServer = new ThreadServer(clientSocket, true);
					thread = new Thread(threadServer);
					thread.start();
				}
			}
		}	
	}
}
