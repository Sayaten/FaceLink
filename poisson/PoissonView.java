package com.example.android.poisson;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by MIKAEL on 2015-06-03.
 */
public class PoissonView extends View {
    public PoissonView(Context context) {
        super(context);
    }
    public PoissonView(Context context, AttributeSet attrs, int defStyle)
    {   super(context, attrs, defStyle);    }

    public PoissonView(Context context, AttributeSet attrs)
    {   super(context, attrs);  }


    private Bitmap bitmap1, bitmap2, c_bitmap1, c_bitmap2;

    //Display parameters
    public static int Width = 1200;
    public static int Height = 800;
    private Canvas canvas;
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
    int xStart, yStart;

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
    public void init() {
        selectionArea = new ArrayList<Coord>();
        selectionBorder = new ArrayList<Coord>();
        bitmap1 = BitmapFactory.decodeResource( getResources(), R.drawable.tttt_shape );
        bitmap2 = BitmapFactory.decodeResource( getResources(), R.drawable.ttt_mouth );
        c_bitmap1 = bitmap1.copy( Bitmap.Config.ARGB_8888, true );
        c_bitmap2 = bitmap2.copy( Bitmap.Config.ARGB_8888, true );

        Width = c_bitmap1.getWidth();
        Height = c_bitmap1.getHeight();

        xStart = Width / 2;
        yStart = Height / 2;

        mask = new int[Width][Height];
        for (int x = 0; x < Width; x++) {
            for (int y = 0; y < Height; y++)
                mask[x][y] = 0;
        }

        dx = 0;
        dy = 0;
    }


    @Override
    protected void onDraw( Canvas canvas ) {
        super.onDraw(canvas);

        this.canvas = canvas;

        canvas.drawBitmap(c_bitmap1, 0, 0, null);
        canvas.drawBitmap(c_bitmap2, xStart, Height / 2, null );

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3.0F);

        if (state == SELECTING || state == DRAGGING) {
            for (int i = 0; i < selectionBorder.size(); i++) {
                //float x = selectionBorder.get(i).x + dx;
                //float y = selectionBorder.get(i).y + dy;
                float x = selectionBorder.get(i).x;
                float y = selectionBorder.get(i).y;
                canvas.drawPoint( x, y, paint );
                invalidate();
            }
        }
    }

    void updateMask() {
        //Clip the motion to the display window
        /*
        if (xMin + dx < 1)
            dx = 1 - xMin;
        if (xMax + dx > Width - 1)
            dx = Width - 1 - xMax;
        if (yMin + dy < 1)
            dy = 1 - yMin;
        if (yMax + dy > Height - 1)
            dy = Height - 1 - yMax;
        */
        //Now update the mask
        for (int x = 0; x < Width; x++) {
            for (int y = 0; y < Height; y++)
                mask[x][y] = -2;
        }
        for (int i = 0; i < selectionBorder.size(); i++) {
            int x = selectionBorder.get(i).x;
            int y = selectionBorder.get(i).y;
            //int x = selectionBorder.get(i).x + dx;
            //int y = selectionBorder.get(i).y + dy;
            //selectionBorder.get(i).x = x;
            //selectionBorder.get(i).y = y;
            mask[x][y] = -1;
        }
        for (int i = 0; i < selectionArea.size(); i++) {
            int x = selectionArea.get(i).x;// + dx;
            int y = selectionArea.get(i).y;// + dy;
            //selectionArea.get(i).x = x;
            //selectionArea.get(i).y = y;
            mask[x][y] = i;
        }
        xMin += dx; xMax += dx;
        yMin += dy; yMax += dy;
        dx = 0;
        dy = 0;
    }

    void fillOutside(int paramx, int paramy) {
        ArrayList<Coord> stack = new ArrayList<Coord>();
        stack.add(new Coord(paramx, paramy));
        while (stack.size() > 0) {
            Coord c = stack.remove(stack.size()-1);
            int x = c.x, y = c.y;
            if (x < 0 || x >= Width || y < 0 || y >= Height)
                continue;
            if (mask[x][y] == -1) //Stop at border pixels
                continue;
            if (mask[x][y] == 0) //Don't repeat nodes that have already been visited
                continue;
            mask[x][y] = 0;
            stack.add(new Coord(x-1, y));
            stack.add(new Coord(x+1, y));
            stack.add(new Coord(x, y-1));
            stack.add(new Coord(x, y + 1));
        }
    }

    public void getSelectionArea() {
        selectionArea.clear();
        updateMask();
        //Find bounding box of selected region
        /*
        xMin = Width;
        xMax = 0;
        yMin = Height;
        yMax = 0;
        */
        /*
        for (int i = 0; i < selectionBorder.size(); i++) {
            int x = selectionBorder.get(i).x;
            int y = selectionBorder.get(i).y;
            if (x < xMin)
                xMin = x;
            if (x > xMax)
                xMax = x;
            if (y < yMin)
                yMin = y;
            if (y > yMax)
                yMax = y;
        }
        */
        xMin = xStart;
        xMax = bitmap2.getWidth() + xStart;
        yMin = yStart;
        yMax = bitmap2.getHeight() + yStart;

        //int selWidth = xMax - xMin;
        //int selHeight = yMax - yMin;

        //c_bitmap2 = Bitmap.createBitmap( selWidth, selHeight, Bitmap.Config.ARGB_8888 );

        //Find a pixel outside of the bounding box, which is guaranteed
        //to be outside of the selection
        boolean found = false;
        for (int x = 0; x < c_bitmap2.getWidth() && !found; x++) {
            for (int y = 0; y < c_bitmap2.getHeight() && !found; y++) {
                if ((x < xMin || x > xMax) && (y < yMin || y > yMax)) {
                    found = true;
                    fillOutside(x, y);
                }
            }
        }
        //Pixels in selection area have mask value of -2, outside have mask value of 0
/*
        for (int x = xStart + 1; x < c_bitmap2.getWidth() + xStart - 1; x++) {
            for (int y = yStart + 1; y < yStart + c_bitmap2.getHeight() - 1; y++) {
                if (x - xMin >= 0 && y - yMin >= 0 && x - xMin < selWidth && y - yMin < selHeight)
                    c_bitmap2.setPixel(x-xStart, y-yStart, c_bitmap2.getPixel(x - xStart,y-yStart)&0x00FFFFFF);
                if (mask[x][y] == 0) {
                    mask[x][y] = -2;
                }
                else if (mask[x][y] != -1) {
                    mask[x][y] = selectionArea.size();//Make mask index of this coord
                    selectionArea.add(new Coord(x, y));
                    int color = (255 << 24) | c_bitmap2.getPixel(x - xStart, y - yStart);
                    if (x - xMin >= 0 && y - yMin >= 0) {
                        c_bitmap2.setPixel(x - xStart, y - yStart, color);
                    }
                }
            }
        }
*/
        for (int x = xStart ; x< bitmap2.getWidth() + xStart ; ++x){
            for (int y = yStart ; y< bitmap2.getWidth() + xStart ; ++y){
                if (mask[x][y] == 0) {
                    mask[x][y] = -2;
                }
                else if (mask[x][y] != -1) {
                    mask[x][y] = selectionArea.size();//Make mask index of this coord
                    selectionArea.add(new Coord(x, y));
                }
            }
        }
        updateMask();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //int x = x;
            //int y = Height / 2;
            int x = xStart;
            int y = yStart;
            if (state == SELECTING) {
                while(x <= c_bitmap2.getWidth() + xStart){
                    selectionBorder.add(new Coord(x, y));
                    x += 10;
                }
                if(x > c_bitmap2.getWidth() + xStart){
                    x = c_bitmap2.getWidth() + xStart;
                }

                while(y <= yStart + c_bitmap2.getHeight()){
                    selectionBorder.add(new Coord(x,y));
                    y += 10;
                }
                if(y > yStart + c_bitmap2.getHeight()){
                    y = yStart + c_bitmap2.getHeight();
                }

                while(x >= xStart){
                    selectionBorder.add(new Coord(x, y));
                    x -= 10;
                }
                if(x < xStart){
                    x = xStart;
                }

                while(y >= yStart){
                    selectionBorder.add(new Coord(x,y));
                    y -= 10;
                }
                if(y < yStart){
                    y = yStart;
                }
            }
            lastX = x;
            lastY = y;

            invalidate();

            state = SELECTING;

        }

        if(event.getAction() == MotionEvent.ACTION_UP) {
            int N = selectionBorder.size();
            if (N == 0 || (state != SELECTING && state != DRAGGING))
                return false;

            if (state == SELECTING) {
                for (int n = 0; n < N; n++) {
                    int startx = selectionBorder.get(n).x;
                    int starty = selectionBorder.get(n).y;
                    int totalDX = selectionBorder.get((n+1)%N).x - startx;
                    int totalDY = selectionBorder.get((n+1)%N).y - starty;
                    int numAdded = Math.abs(totalDX) + Math.abs(totalDY);
                    for (int t = 0; t < numAdded; t++) {
                        double frac = (double)t / (double)numAdded;
                        int x = (int)Math.round(frac*totalDX) + startx;
                        int y = (int)Math.round(frac*totalDY) + starty;
                        selectionBorder.add(new Coord(x, y));
                    }
                }
                updateMask();
                getSelectionArea();
                state = DRAGGING;
                dragValid = false;
                dx = 0;
                dy = 0;
            }
            else if (state == DRAGGING) {
                dragValid = false;
                updateMask();
            }
        }


        return true;
    }

    public void nextIteration() {
        for (int i = 0; i < 100; i++)
            solver.nextIteration();
        synchronized(c_bitmap2) {
            solver.updateImage(c_bitmap2);
        }
    }

    public void finalizeBlending() {
        //canvas.drawBitmap( c_bitmap2, xMin, yMin, null );
        //c_bitmap2 = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888 );
        selectionBorder.clear();
        selectionArea.clear();
        state = NOTHING;
    }

    class IterationBlender implements Runnable {
        public void run() {
            int iteration = 0;
            double error;
            double Norm = 1.0;
            do {
                error = solver.getError();
                if (iteration == 1)
                    Norm = Math.log(error);
                //if (iteration >= 1) {
                //    //The Jacobi method converges exponentially
                //    double progress = 1.0 - Math.log(error) / Norm;
                //}
                iteration++;
                nextIteration();
            }
            while (error > 1.0 && state == BLENDING);
            System.out.println("Did " + iteration + "iterations");
            finalizeBlending();
        }
    }

    public void blend(){
        state = BLENDING;
        updateMask();
        solver = new MatrixSolver(mask, selectionArea, c_bitmap1, c_bitmap2,
                xStart, yStart, Width, Height, false);
        IterationBlender blender = new IterationBlender();
        blendingThread = new Thread(blender);
        blendingThread.start();
    }
}
