package com.rubicom.facelinker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.net.Socket;


public class MainScreenActivity extends Activity {

    private String screen_id;
    private Bitmap profile_image;
    private ImageButton btnFind;
    private ImageView smallProfile, bigProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        smallProfile = ( ImageView )findViewById( R.id.imgSmallProfile );
        bigProfile = ( ImageView )findViewById( R.id.imgBigProfile );
        btnFind = (ImageButton)findViewById( R.id.btnFind );
        btnFind.setOnClickListener( OnClickListener );

        Intent intent = getIntent();
        screen_id = intent.getExtras().getString("screen_id");
        profile_image = (Bitmap)intent.getExtras().get("profile_image");

        smallProfile.setImageBitmap( profile_image );
        bigProfile.setImageBitmap( profile_image );


    }

    public View.OnClickListener OnClickListener = new View.OnClickListener() {

        public void onClick( View view ) {

            int id = view.getId();
            switch( id ) {
                case R.id.btnFind:
                    Intent intent = new Intent( MainScreenActivity.this, PoissonActivity.class );
                    startActivity( intent );
                    break;
            }

        }

    };
}
