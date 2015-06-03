package com.rubicom.facelinker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.os.Handler;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class MainActivity extends Activity {

    private Button btnAccess, btnSignUp;
    private EditText editAddress, editPassword;
    protected SocketClass socketClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAccess = ( Button )findViewById( R.id.btnAccess );
        btnAccess.setOnClickListener( OnClickListener );

        btnSignUp = ( Button )findViewById( R.id.btnSignUp );
        btnSignUp.setOnClickListener( OnClickListener );

        editAddress = ( EditText )findViewById( R.id.editAddress );
        editPassword = ( EditText )findViewById( R.id.editPassword );

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                case R.id.btnAccess:
                    socketClass = new SocketClass();
                    socketClass.connect();
                    socketClass.send( editAddress.getText().toString(), editPassword.getText().toString() );


//                    intent = new Intent( MainActivity.this, MainScreenActivity.class );
//                    startActivity( intent );
                    break;
                case R.id.btnSignUp:
                    intent = new Intent( MainActivity.this, SignupFirstActivity.class );
                    startActivity( intent );
                    break;
                default:
            }
        }
    };
}
