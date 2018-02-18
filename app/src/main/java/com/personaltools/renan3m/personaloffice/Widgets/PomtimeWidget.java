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
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.personaltools.renan3m.personaloffice.R;

import java.lang.reflect.Field;

/**
 * Created by renan on 11/02/2018.
 */

public class PomtimeWidget extends ProgressBar {

    private Paint paintBit;
    private Bitmap appleImg;

    private int leftSideAp;
    private int topSideAp;

    String height;
    String width;

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

    private void init (@Nullable AttributeSet set){ // tenho q decodificar esse set (apesar de ainda n ter nenhum atributo)

        // AQUI ESTÃO MEUS ATRIBUTOS, TENHO Q ARMAZENAR O INNERRADIUS EM UM ATRIBUTO, PARA NO MÉTODO ONSIZECHANGE EU ALTERA-LO
        // PARA QUE CAIBA NA TELA DA VIEW.

        // AQUI EU SÓ POSSO RECUPERAR O QUE ELE JÁ SETOU?


        // Como mudar o innerRadius do nosso widget para receber esses novos parametros?
        getResources().getInteger(R.integer.innerRatius);


        paintBit = new Paint(Paint.ANTI_ALIAS_FLAG);

        appleImg = BitmapFactory.decodeResource(getResources(),R.drawable.pomtime_img);




    /*    getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);

                float xpad = (float)(getPaddingLeft() + getPaddingRight());
                float ypad = (float)(getPaddingTop() + getPaddingBottom());



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
     //   setRInteger(com.personaltools.renan3m.personaloffice.R.class,"innerRatius", new Integer(9));   Que diabos!
        super.onDraw(canvas); // Isso desenha a ProgressBar

        appleImg = getResizedBitmap(appleImg,(getWidth()) - getPaddingLeft() +
                getPaddingRight(),(getHeight()) - getPaddingTop() + getPaddingBottom());
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

        Log.e("PomtimeWidget", "onSizeChanged called");



    }
    public static void setRInteger(Class rClass, String rFieldName, Object newValue){
        setR(rClass,"integer",rFieldName,newValue);
    }

    public static void setR(Class rClass, String innerClassName, String rFieldName, Object newValue){
        setStatic(rClass.getName() + '$' + innerClassName, rFieldName, newValue);
    }

    public static boolean setStatic(String aClassName, String staticFieldName, Object toSet){
        try {
            return setStatic(Class.forName(aClassName),staticFieldName,toSet);
        }catch (ClassNotFoundException e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setStatic(Class<?> aClass, String staticFieldName, Object toSet){
        try {
            Field declaredField = aClass.getDeclaredField(staticFieldName);
            declaredField.setAccessible(true);
            declaredField.set(null, toSet);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}