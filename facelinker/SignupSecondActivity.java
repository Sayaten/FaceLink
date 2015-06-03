package com.rubicom.facelinker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;


import Server.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class SignupSecondActivity extends Activity {

    private static final String TEMP_PHOTO_FILE = "temp.jpg";
    private static final int REQ_CODE_PICK_IMAGE = 0;
    private EditText enterName, enterJob, enterCountry;
    private RadioButton radioMale, radioFemale;
    private ImageButton imageButton;
    private Button btnComplete;
//    SocketClass socketClass;
    PacketCodec packetCodec = null;
    JoinAck joinAck;
    ProfileWriteReq profileWriteReq;
    String sendMsg;
    private static String screen_id;
    private static String password;
    private Bitmap picture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_second);

        Intent intent = getIntent();
        screen_id = intent.getExtras().getString("screen_id");
        password = intent.getExtras().getString("password");
        Log.d("id&password", ""+screen_id+","+password);


  //      socketClass = new SocketClass();
  //      socketClass.connect();

        enterName = ( EditText )findViewById( R.id.enterName );
        enterJob = ( EditText )findViewById( R.id.enterJob );
        enterCountry = ( EditText )findViewById( R.id.enterCountry );

        radioMale = ( RadioButton )findViewById( R.id.radioMale );
        radioFemale = ( RadioButton )findViewById( R.id.radioFemale );

        imageButton = ( ImageButton )findViewById( R.id.imageButton );
        imageButton.setOnClickListener( OnClickListener );

        btnComplete = ( Button )findViewById( R.id.btnComplete );
        btnComplete.setOnClickListener( OnClickListener );

        profileWriteReq = new ProfileWriteReq();
        packetCodec = new PacketCodec();


    }

    private Uri getTempUri()
    {   return Uri.fromFile( getTempFile() );   }

    private File getTempFile() {
        if( isSDCARDMOUNTED() ) {
            File file = new File( Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE );
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }
        else
            return null;
    }

    private boolean isSDCARDMOUNTED() {
        String status = Environment.getExternalStorageState();
        if( status.equals( Environment.MEDIA_MOUNTED ) )
            return true;
        return false;
    }

    protected void onActivityResult( int requestCode, int resultCode, Intent imageData ) {
        super.onActivityResult( requestCode, resultCode, imageData );

        switch( requestCode ) {
            case REQ_CODE_PICK_IMAGE:
                if( resultCode == RESULT_OK ) {
                    if( imageData != null ) {
                        String filePath = Environment.getExternalStorageDirectory() + "/temp.jpg";
                        System.out.println( "path"+filePath );
                        Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                        imageButton.setImageBitmap( selectedImage );
                    }
                }
                break;
        }
    }


    public void buildProfile() {

        profileWriteReq.setScreen_name( screen_id );
        profileWriteReq.setName(enterName.getText().toString());
        if( radioMale.isChecked() == true && radioFemale.isChecked() == false )
            profileWriteReq.setGender( "M" );
        else if(radioMale.isChecked() == false && radioFemale.isChecked() == true )
            profileWriteReq.setGender( "F" );
        profileWriteReq.setJob(enterJob.getText().toString());
        profileWriteReq.setCountry(enterCountry.getText().toString());

        BitmapDrawable drawable = ( BitmapDrawable )( ( ImageButton ) findViewById( R.id.imageButton ) ).getDrawable();
        picture = drawable.getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        picture.compress( Bitmap.CompressFormat.JPEG, 100, baos );

        byte [] toByte = baos.toByteArray();
        String temp = Base64.encodeToString(toByte, Base64.DEFAULT);
        profileWriteReq.setProfile_img( temp );

        sendMsg = packetCodec.encode_ProfileWriteReq( profileWriteReq );

    }

    public View.OnClickListener OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick( View view ) {
            int id = view.getId();
            Intent intent;
            switch( id ) {
                case R.id.imageButton:
                    intent = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                    intent.setType( "image/*" );
                    intent.putExtra( "crop", "true" );
                    intent.putExtra( MediaStore.EXTRA_OUTPUT, getTempUri() );
                    intent.putExtra( "outputFormat", Bitmap.CompressFormat.JPEG.toString() );
                    intent.putExtra( "outputX", 60 );
                    intent.putExtra( "outputY", 80 );
                    startActivityForResult( intent, REQ_CODE_PICK_IMAGE );
                    break;
                case R.id.btnComplete:
                    buildProfile();
                    intent = new Intent( SignupSecondActivity.this, TEMPCROPACTIVITY.class );
                    intent.putExtra("screen_id", screen_id );
                    intent.putExtra("profile_image", picture );
                    startActivity( intent );
                    break;

            }
        }
    };

}

