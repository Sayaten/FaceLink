package com.rubicom.facelinker;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Iterator;

import Server.*;
/**
 * Created by MIKAEL on 2015-04-29.
 */
public class SocketClass {

    private final static String HOTTORIA = "sayaten.hottoria.net";
    private final static int PORT = 9195;

    private static Socket socket;

    static PrintWriter socket_out;
    static BufferedReader socket_in;
    Thread worker;
    static String result;
    static int temp;
    InetAddress IP;

    public SocketClass() {
        this.socket = null;
        worker = new Thread() {
            public void run() {
                try {
                    IP = InetAddress.getByName( "sayaten.hottoria.net" );
                    socket = new Socket( IP.getHostAddress(), 9193 );
//                    socket = new Socket( "172.20.10.2", 9993 );
//                    socket = new Socket( "172.30.1.3", 9995 );
//                    socket = new Socket( "39.115.18.74", 9995 );
                    if( socket == null )
                        Log.d( "ERROR", "socket was not generated." );
                    socket_in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
                    socket_out = new PrintWriter( socket.getOutputStream(), true );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public SocketClass( Socket receivedSocket )
    {   this.socket = receivedSocket;    }

    public void connect()
    {   worker.start(); }

    public static Socket getSocket() throws IOException
    {    return socket;    }

    public static void send( String screenname, String password ) {

        Log.w("NETWORK: ", screenname + "|" + password );
        socket_out.println(screenname + "|" + password);

    }

    public static void send( String screenname ) throws IOException {

        Log.w("NETWORK: ", screenname );
        socket_out.println( screenname );

    }

    public static void receive() throws IOException {
        Thread worker = new Thread() {
            public void run() {
                try {
                    result = PacketCodec.read_delim( socket_in );
                    Log.w("RESULT: ", result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        worker.start();
    }

    public static void setTemp(int temp2)
    {   temp = temp2;   }

    public int getResult()
    {
        Log.w("USERIDFORRESULT: ", ""+temp);
        return temp;
    }

    public void terminate() throws IOException
    {    socket.close();    }


}