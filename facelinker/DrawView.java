package com.rubicom.facelinker;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class DrawView extends ImageView {

    private int cX = 0;
    private int cY = 0;
    private int dX = 0;
    private int dY = 0;
    private int Width = 0;
    private int Height = 0;
    private boolean isInitialized = false;
    private Paint paint;

    public Bitmap bitmap, bitmap2;
    public int State = 0;

    public DrawView(Context context)
    {   super(context); }

    public DrawView(Context context, AttributeSet attrs, int defStyle)
    {   super(context, attrs, defStyle);    }

    public DrawView(Context context, AttributeSet attrs)
    {   super(context, attrs);  }


    public void giveBitmap( Bitmap receivedBitmap ) {

        bitmap = receivedBitmap;

    }
    public void initialize() {

        bitmap = Bitmap.createScaledBitmap( bitmap, this.getWidth(), this.getHeight(), true );
        this.setImageBitmap( bitmap );

        Width = this.getWidth();
        Height = this.getHeight();

        paint = new Paint();


        cX = Width / 2 - ( Width / 2 ) / 4;
        cY = Height / 2 - ( Height / 2 ) / 4;
        dX = cX + 150;
        dY = cY + 50;

        isInitialized = true;

    }


    @Override
    public void onDraw( Canvas canvas ) {

        super.onDraw(canvas);

        if( !isInitialized )
            initialize();

        paint.setStyle( Paint.Style.STROKE );
        paint.setColor( Color.CYAN );
        canvas.drawRect( cX, cY, dX, dY, paint );

    }

    public void onUp() {
        cY -= 5;
        dY += 5;
        invalidate();
    }

    public void onDown() {
        cY += 5;
        dY -= 5;
        invalidate();
    }

    public void onRight() {
        cX -= 5;
        dX += 5;
        invalidate();
    }

    public void onLeft() {
        cX += 5;
        dX -= 5;
        invalidate();
    }

    public void onEyesSelected() {
        State = 0;
        cX = Width / 2 - ( Width / 2 ) / 4;
        cY = Height / 2 - ( Height / 2 ) / 4;
        dX = cX + 150;
        dY = cY + 50;
        invalidate();
    }

    public void onNoseSelected() {
        State = 1;
        cX = Width / 2 - ( Width / 2 ) / 4;
        cY = Height / 2 - ( Height / 2 ) / 4;
        dX = cX + 50;
        dY = cY + 100;
        invalidate();
    }

    public void onMouthSelected() {
        State = 2;
        cX = Width / 2 - ( Width / 2 ) / 4;
        cY = Height / 2 - ( Height / 2 ) / 4;
        dX = cX + 100;
        dY = cY + 50;
        invalidate();
    }

    public void onShapeSelected() {
        State = 3;
        cX = Width / 2 - ( Width / 2 ) / 4;
        cY = Height / 2 - ( Height / 2 ) / 4;
        dX = cX + 200;
        dY = cY + 300;
        invalidate();
    }

    public Bitmap onClip() {
        bitmap2 = Bitmap.createBitmap( bitmap, cX, cY, dX - cX, dY - cY );
        return bitmap2;
    }

    @Override
    public boolean onTouchEvent( MotionEvent event ) {

        if( event.getAction() == MotionEvent.ACTION_DOWN ) {
            switch( State ) {
                case 0:
                    cX = ( int ) event.getX() - 100;
                    cY = ( int ) event.getY() - 50;
                    dX = cX + 150;
                    dY = cY + 50;
                    invalidate();
                    break;
                case 1:
                    cX = ( int ) event.getX() - 100;
                    cY = ( int ) event.getY() - 50;
                    dX = cX + 50;
                    dY = cY + 100;
                    invalidate();
                    break;
                case 2:
                    cX = ( int ) event.getX() - 100;
                    cY = ( int ) event.getY() - 50;
                    dX = cX + 100;
                    dY = cY + 50;
                    invalidate();
                    break;
                case 3:
                    cX = ( int ) event.getX() - 100;
                    cY = ( int ) event.getY() - 50;
                    dX = cX + 200;
                    dY = cY + 300;
                    invalidate();
                    break;
            }
        }

        if( event.getAction() == MotionEvent.ACTION_MOVE ) {
            switch( State ) {
                case 0:
                    cX = ( int ) event.getX() - 100;
                    cY = ( int ) event.getY() - 50;
                    dX = cX + 150;
                    dY = cY + 50;
                    invalidate();
                    break;
                case 1:
                    cX = ( int ) event.getX() - 100;
                    cY = ( int ) event.getY() - 50;
                    dX = cX + 50;
                    dY = cY + 100;
                    invalidate();
                    break;
                case 2:
                    cX = ( int ) event.getX() - 100;
                    cY = ( int ) event.getY() - 50;
                    dX = cX + 100;
                    dY = cY + 50;
                    invalidate();
                    break;
                case 3:
                    cX = ( int ) event.getX() - 100;
                    cY = ( int ) event.getY() - 50;
                    dX = cX + 200;
                    dY = cY + 300;
                    invalidate();
                    break;
            }
        }
        return true;
    }
}
