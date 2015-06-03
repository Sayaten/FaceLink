package com.example.android.poisson;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private PoissonView i1;
    private Button button;

    //Display parameters
    public static final int Width = 1200;
    public static final int Height = 800;

    //Program state
    public static final int NOTHING = 0;
    public static final int SELECTING = 1;
    public static final int DRAGGING = 2;
    public static final int BLENDING = 3;
    public int[][] mask;//A 2D array that represents a selected region
    //It encodes the enclosed region and the border of that region
    public ArrayList<Coord> selectionBorder;
    public ArrayList<Coord> selectionArea;
    public Bitmap image;
    public Bitmap selectedImage;
    int xMin, xMax, yMin, yMax;//Bounding box of selected area

    //GUI State Variables
    public int state;
    public boolean dragValid;
    public int lastX, lastY;
    public int dx, dy;
    public boolean selectingLeft;
    public boolean doneAnything = false;

    //Matrix solver
    public MatrixSolver solver;
    public Thread blendingThread;
//	public JProgressBar progressBar;

    //-2 for uninvolved pixels
    //-1 for border pixels
    //Index number for area pixels
    //This function also moves everything over by (dx, dy)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        i1 = ( PoissonView )findViewById( R.id.imageView );
        i1.init();

        button = ( Button )findViewById( R.id.button );
        button.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                i1.blend();
            }
        });

    }






}
