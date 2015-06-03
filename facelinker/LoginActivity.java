package com.rubicom.facelinker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

    private Button btnAccess, btnSignUp;
    private EditText editAddress, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        btnAccess = ( Button )findViewById( R.id.btnAccess );
        btnAccess.setOnClickListener( OnClickListener );

        btnSignUp = ( Button )findViewById( R.id.btnSignUp );
        btnSignUp.setOnClickListener( OnClickListener );

        editAddress = ( EditText )findViewById( R.id.editAddress );
        editPassword = ( EditText )findViewById( R.id.editPassword );

    }

    private View.OnClickListener OnClickListener = new View.OnClickListener() {
        public void onClick( View view ) {
            int id = view.getId();
            Intent intent;
            switch( id ) {
                case R.id.btnAccess:

//                    socketClass.send( editAddress.getText().toString(), editPassword.getText().toString() );
//                    intent = new Intent( MainActivity.this, MainScreenActivity.class );
//                    startActivity( intent );
                    intent = new Intent( LoginActivity.this, MainScreenActivity.class );
                    startActivity( intent );

                    break;
                case R.id.btnSignUp:
                    intent = new Intent( LoginActivity.this, SignupFirstActivity.class );
                    startActivity(intent);
                    break;
                default:
            }
        }
    };
}
