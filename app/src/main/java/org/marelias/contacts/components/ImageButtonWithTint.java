package org.marelias.contacts.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import org.marelias.contacts.R;

public class ImageButtonWithTint extends androidx.appcompat.widget.AppCompatImageButton {
    public ImageButtonWithTint(Context context) {
        this(context, null);
    }

    public ImageButtonWithTint(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageButtonWithTint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr == 0 ? R.attr.imageButtonWithTint : defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageButtonWithTint);
        int resourceId = typedArray.getResourceId(R.styleable.ImageButtonWithTint_android_src, -1);
        typedArray.recycle();
        if (resourceId == -1) return;
        Drawable drawable = TintedDrawablesStore.getTintedDrawable(resourceId, context);
        setImageDrawable(drawable);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageDrawable(TintedDrawablesStore.getTintedDrawable(resId, getContext()));
    }
}
