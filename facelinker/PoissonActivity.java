package com.rubicom.facelinker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.rubicom.facelinker.PoissonView;


public class PoissonActivity extends Activity {

    private PoissonActivity thisObject;
    private ProgressDialog ringProgressDialog;
    private PoissonView i1;
    private ImageView showView;
    private Button btnPoissonMerge, btnPoissonUp, btnPoissonDown, btnPoissonRight, btnPoissonLeft, temp, btnPoissonOK;
    private int viewState;
    private Bitmap GIVING_BITMAP1, GIVING_BITMAP2, TEMP_BITMAP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poisson);

        i1 = ( PoissonView )findViewById( R.id.poissonView );
        GIVING_BITMAP1 = BitmapFactory.decodeResource(getResources(), R.drawable.tttt_shape);
        GIVING_BITMAP2 = BitmapFactory.decodeResource( getResources(), R.drawable.ttt_mouth );
        i1.init( GIVING_BITMAP1, GIVING_BITMAP2 );

        viewState = 0;

        showView = ( ImageView )findViewById( R.id.showView );
        btnPoissonMerge = ( Button )findViewById( R.id.btnPoissonMerge );
        btnPoissonUp = ( Button )findViewById( R.id.btnPoissonUp );
        btnPoissonDown = ( Button )findViewById( R.id.btnPoissonDown );
        btnPoissonRight = ( Button )findViewById( R.id.btnPoissonRight );
        btnPoissonLeft = ( Button )findViewById( R.id.btnPoissonLeft );
        temp = ( Button )findViewById( R.id.temp );
        btnPoissonOK = ( Button )findViewById( R.id.btnPoissonOK );

        btnPoissonMerge.setOnClickListener( OnClickListener );
        btnPoissonUp.setOnClickListener( OnClickListener );
        btnPoissonDown.setOnClickListener( OnClickListener );
        btnPoissonRight.setOnClickListener( OnClickListener );
        btnPoissonLeft.setOnClickListener( OnClickListener );
        temp.setOnClickListener( OnClickListener );
        btnPoissonOK.setOnClickListener( OnClickListener );

    }

    private View.OnClickListener OnClickListener = new View.OnClickListener() {

        public void onClick( View view ) {

            int id = view.getId();

            switch ( id ) {
                case R.id.btnPoissonMerge:
                    i1.onMerge();
                    TEMP_BITMAP = i1.capture();
                    break;
                case R.id.btnPoissonUp:
                    i1.onUp();
                    break;
                case R.id.btnPoissonDown:
                    i1.onDown();
                    break;
                case R.id.btnPoissonRight:
                    i1.onRight();
                    break;
                case R.id.btnPoissonLeft:
                    i1.onLeft();
                    break;
                case R.id.temp:
                    TEMP_BITMAP = i1.capture();
                    showView.setImageBitmap( TEMP_BITMAP );
                    i1.init( GIVING_BITMAP1, GIVING_BITMAP2 );
                    break;
                case R.id.btnPoissonOK:
                    i1.catching();
                    btnPoissonMerge.setEnabled( true );
            }

        }
    };





}
