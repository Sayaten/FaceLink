package com.rubicom.facelinker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import Server.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;


public class SignupFirstActivity extends Activity {

    JoinReq joinReq = null;
    PacketCodec packetCodec = null;
    String sendMsg = null;
    private EditText enterAddress;
    private EditText enterPassword;
    private Button btnNext;
    protected SocketClass socketClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_first);
/*
        socketClass = new SocketClass();
        socketClass.connect();
*/
        enterAddress = ( EditText )findViewById( R.id.enterAddress );
        enterPassword = ( EditText )findViewById( R.id.enterPassword );

        btnNext = ( Button )findViewById( R.id.btnNext );
        btnNext.setOnClickListener( OnClickListener );

        joinReq = new JoinReq();
        packetCodec = new PacketCodec();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup_first, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener OnClickListener = new View.OnClickListener() {
        public void onClick( View view ) {
            int id = view.getId();
            Intent intent;
            switch( id ) {
                case R.id.btnNext:

                    joinReq.setScreen_name( enterAddress.getText().toString() );
                    joinReq.setPassword( enterPassword.getText().toString() );

                    sendMsg = packetCodec.encode_JoinReq( joinReq );

                    Bitmap picture = BitmapFactory.decodeResource( getResources(), R.drawable.find );
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    picture.compress( Bitmap.CompressFormat.PNG, 100, baos );
                    byte [] toByte = baos.toByteArray();
                    String temp = Base64.encodeToString( toByte, Base64.DEFAULT );
       //             socketClass.send( temp + "?" );

       //             try {
       //                 socketClass.send(sendMsg);
       //                 socketClass.receive();
                        intent = new Intent( SignupFirstActivity.this, SignupSecondActivity.class );
                        intent.putExtra( "screen_id", enterAddress.getText().toString() );
                        intent.putExtra( "password", enterPassword.getText().toString() );
                        startActivity(intent);

       //             } catch (IOException e) {
       //                 e.printStackTrace();
       //             }



                    break;


                default:
                    break;
            }
        }
    };

    public void converting() {

    }
}
