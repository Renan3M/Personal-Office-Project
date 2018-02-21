package com.personaltools.renan3m.personaloffice.Widgets.tools;

import android.graphics.drawable.GradientDrawable;

import java.lang.reflect.Field;

/**
 * Created by renan on 18/02/2018.
 */

public class RingDrawable extends GradientDrawable {

    private Class<?> mGradientState;

    public RingDrawable() {
        this(Orientation.TOP_BOTTOM, null);
    }

    public RingDrawable(int innerRadius, int thickness, float innerRadiusRatio, float thicknessRatio) {
        this(Orientation.TOP_BOTTOM, null, innerRadius, thickness, innerRadiusRatio, thicknessRatio);
    }

    public RingDrawable(GradientDrawable.Orientation orientation, int[] colors) {
        super(orientation, colors);
        setShape(RING);
    }

    public RingDrawable(GradientDrawable.Orientation orientation, int[] colors, int innerRadius, int thickness, float innerRadiusRatio, float thicknessRatio) {
        this(orientation, colors);
        try {
            setInnerRadius(innerRadius);
            setThickness(thickness);
            setInnerRadiusRatio(innerRadiusRatio);
            setThicknessRatio(thicknessRatio);
        } catch (Exception e) {
            // fail silently - change to your own liking
            e.printStackTrace();
        }
    }

    public void setInnerRadius(int radius) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        if (mGradientState == null) mGradientState = resolveGradientState();
        Field innerRadius = resolveField(mGradientState, "mInnerRadius");
        innerRadius.setInt(getConstantState(), radius);
    }

    public void setThickness(int thicknessValue) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        if (mGradientState == null) mGradientState = resolveGradientState();
        Field thickness = resolveField(mGradientState, "mThickness");
        thickness.setInt(getConstantState(), thicknessValue);
    }

    public void setInnerRadiusRatio(float ratio) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        if (mGradientState == null) mGradientState = resolveGradientState();
        Field innerRadiusRatio = resolveField(mGradientState, "mInnerRadiusRatio");
        innerRadiusRatio.setFloat(getConstantState(), ratio);
    }

    public void setThicknessRatio(float ratio) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        if (mGradientState == null) mGradientState = resolveGradientState();
        Field thicknessRatio = resolveField(mGradientState, "mThicknessRatio");
        thicknessRatio.setFloat(getConstantState(), ratio);
    }

    private Class<?> resolveGradientState() {
        Class<?>[] classes = GradientDrawable.class.getDeclaredClasses();
        for (Class<?> singleClass : classes) {
            if (singleClass.getSimpleName().equals("GradientState")) return singleClass;
        }
        throw new RuntimeException("GradientState could not be found in current GradientDrawable implementation");
    }

    private Field resolveField(Class<?> source, String fieldName) throws SecurityException, NoSuchFieldException {
        Field field = source.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

}
