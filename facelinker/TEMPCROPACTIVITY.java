package com.rubicom.facelinker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class TEMPCROPACTIVITY extends Activity  {

    private Button createBtn, createEyesBtn, createNoseBtn, createMouthBtn, createShapeBtn, recUpBtn, recDownBtn,recLeftBtn, recRightBtn, completeBtn;
    private DrawView drawView;
    private ImageView eyesView, noseView, mouthView, shapeView;
    private Bitmap profile_image, eyes, nose, mouth, shape;
    private int buttonsState;
    private String screen_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tempcropactivity);

        buttonsState = 0;

        Intent intent = getIntent();
        screen_id = intent.getExtras().getString("screen_id");
        profile_image = ( Bitmap )intent.getExtras().get("profile_image");

        drawView = ( DrawView )findViewById( R.id.DrawView );
        eyesView = ( ImageView )findViewById( R.id.imageView1 );
        noseView = ( ImageView )findViewById( R.id.imageView2 );
        mouthView = ( ImageView )findViewById( R.id.imageView3 );
        shapeView = ( ImageView )findViewById( R.id.imageView4 );

        drawView.giveBitmap( profile_image );

        createBtn = ( Button )findViewById( R.id.createBtn );
        createEyesBtn = ( Button )findViewById( R.id.createEyesBtn );
        createNoseBtn = ( Button )findViewById( R.id.createNoseBtn );
        createMouthBtn = ( Button )findViewById( R.id.createMouthBtn );
        createShapeBtn = ( Button )findViewById( R.id.createShapeBtn );
        recUpBtn = ( Button )findViewById( R.id.recUpBtn );
        recDownBtn = ( Button )findViewById( R.id.recDownBtn );
        recLeftBtn = ( Button )findViewById( R.id.recLeftBtn );
        recRightBtn = ( Button )findViewById( R.id.recRightBtn );
        completeBtn = ( Button )findViewById( R.id.completeBtn );

        createBtn.setOnClickListener( OnClickListener );
        createEyesBtn.setOnClickListener( OnClickListener );
        createNoseBtn.setOnClickListener( OnClickListener );
        createMouthBtn.setOnClickListener( OnClickListener );
        createShapeBtn.setOnClickListener( OnClickListener );
        recUpBtn.setOnClickListener( OnClickListener );
        recDownBtn.setOnClickListener( OnClickListener );
        recLeftBtn.setOnClickListener( OnClickListener );
        recRightBtn.setOnClickListener( OnClickListener );
        completeBtn.setOnClickListener( OnClickListener );

    }

    public void check( int state ) {
        if( state >= 4 )
            completeBtn.setEnabled( true );
        if( state >= 5 )
            state = 5;
    }

    private View.OnClickListener OnClickListener = new View.OnClickListener() {

        public void onClick( View view ) {
            int id = view.getId();
            switch( id ) {
                case R.id.recUpBtn:
                    drawView.onUp();
                    break;
                case R.id.recDownBtn:
                    drawView.onDown();
                    break;
                case R.id.recRightBtn:
                    drawView.onRight();
                    break;
                case R.id.recLeftBtn:
                    drawView.onLeft();
                    break;
                case R.id.createEyesBtn:
                    drawView.onEyesSelected();
                    break;
                case R.id.createNoseBtn:
                    drawView.onNoseSelected();
                    break;
                case R.id.createMouthBtn:
                    drawView.onMouthSelected();
                    break;
                case R.id.createShapeBtn:
                    drawView.onShapeSelected();
                    break;
                case R.id.createBtn:
                    if( drawView.State == 0 ) {
                        eyes = drawView.onClip();
                        eyesView.setImageBitmap(eyes);
                        buttonsState++;
                        check( buttonsState );
                    }
                    else if( drawView.State == 1 ) {
                        nose = drawView.onClip();
                        noseView.setImageBitmap( nose );
                        buttonsState++;
                        check( buttonsState );
                    }
                    else if( drawView.State == 2 ) {
                        mouth = drawView.onClip();
                        mouthView.setImageBitmap( mouth );
                        buttonsState++;
                        check( buttonsState );
                    }
                    else if( drawView.State == 3 ) {
                        shape = drawView.onClip();
                        shapeView.setImageBitmap( shape );
                        buttonsState++;
                        check( buttonsState );
                    }
                    break;
                case R.id.completeBtn:
                    Intent intent = new Intent( TEMPCROPACTIVITY.this, MainScreenActivity.class );
                    intent.putExtra("screen_id", screen_id);
                    intent.putExtra("profile_image", profile_image);
                    intent.putExtra("eyes", eyes);
                    intent.putExtra("nose", nose);
                    intent.putExtra("mouth", mouth);
                    intent.putExtra("shape", shape);
                    startActivity( intent );
                    break;

            }
        }
    };


}

