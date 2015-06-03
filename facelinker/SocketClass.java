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
/**
 * Created by MIKAEL on 2015-04-29.
 */
public class SocketClass {

    static Socket socket;
    static PrintWriter socket_out;

    InetAddress IP;

    public SocketClass()
    {  this.socket = null;   }

    public SocketClass( SocketClass receivedSocket )
    {   this.socket = receivedSocket.socket;    }

    public void connect() {

        Thread worker = new Thread() {
            public void run() {
                try {
                    IP = InetAddress.getByName( "sayaten.hottoria.net" );
                                socket = new Socket( IP.getHostAddress(), 9195 );
                    //                  socket = new Socket( "172.20.10.2", 9993 );
        //                              socket = new Socket( "172.30.1.2", 9993 );
                    if( socket == null )
                        Log.d( "ERROR", "socket was not generated." );
                    socket_out = new PrintWriter( socket.getOutputStream(), true );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        worker.start();

    }

    public static Socket getSocket() throws IOException
    {    return socket;    }

    public static void send( String screenname, String password ) {

        Log.w("NETWORK: ", screenname + "|" + password );
        socket_out.print( screenname + "|" + password );

    }

    public static void send( String screenname ) {

        Log.w("NETWORK: ", screenname );
        socket_out.println( screenname );

    }

    public void receive() {

    }

    public void terminate() throws IOException
    {    socket.close();    }


}