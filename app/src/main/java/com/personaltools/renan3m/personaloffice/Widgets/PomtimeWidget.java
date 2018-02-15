package com.personaltools.renan3m.personaloffice.Widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.personaltools.renan3m.personaloffice.R;

/**
 * Created by renan on 11/02/2018.
 */

public class PomtimeWidget extends ProgressBar {

    private Paint paintBit;
    private Bitmap appleImg;
    private int leftSideAp;
    private int topSideAp;

    public PomtimeWidget(Context context) {
        super(context);

        init(null);
    }


    public PomtimeWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }


    public PomtimeWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init (@Nullable AttributeSet set){ // tenho q decodificar esse set amanhã tb (apesar de ainda n ter nenhum atributo)

        paintBit = new Paint(Paint.ANTI_ALIAS_FLAG);

        appleImg = BitmapFactory.decodeResource(getResources(),R.drawable.pomtime_img);


        // A view ainda não terminou de calcular seu tamanho, por isso a necessidade do listener. (A menos que faça no Draw)
/*        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);

                appleImg = getResizedBitmap(appleImg,getWidth()/2,getHeight()/2);

                if (leftSideAp == 0 && topSideAp == 0){

                    leftSideAp = getWidth()/2 - appleImg.getWidth()/2; // e o padding?
                    topSideAp = getHeight()/2 - appleImg.getHeight()/2; // e o padding?
                }
            }
        });
*/
    }



    // O width e height do Pomtime você não precisa se preocupar em criar atributo, será o usuario
    // que definirá com o atributo android:layout_width e android:layout_height. Porém a maçãzinha
    // de dentro, você deve indicar de alguma forma que deseja de que ela fique dentro da progressBar.


    // Quero ter um ImageView e um ProgressBar. desenhando primeiro o ProgressBar e depois o imageView no centro dele.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas); // Isso desenha a ProgressBar

        appleImg = getResizedBitmap(appleImg,(getWidth()*2/3) - getPaddingLeft() +
                getPaddingRight(),(getHeight()*2/3) - getPaddingTop() + getPaddingBottom());
        canvas.drawBitmap(appleImg, getWidth()/2-appleImg.getWidth()/2, getHeight()/2-appleImg.getHeight()/2, paintBit);

    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int requiredWidth, int requiredHeight){
        Matrix matrix = new Matrix();

        RectF source = new RectF(0,0,bitmap.getWidth(),bitmap.getHeight());
        RectF destination = new RectF(0,0,requiredWidth,requiredHeight);

        matrix.setRectToRect(source,destination,Matrix.ScaleToFit.CENTER);

        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Account for padding
        float xpad = (float)(getPaddingLeft() + getPaddingRight());
        float ypad = (float)(getPaddingTop() + getPaddingBottom());


        float ww = (float)w - xpad; // novo width - padding
        float hh = (float)h - ypad; // novo height - padding



    }
}