package org.marelias.contacts.views;

import static org.marelias.contacts.utils.SharedPreferencesUtils.SIGNAL;
import static org.marelias.contacts.utils.SharedPreferencesUtils.TELEGRAM;

import android.content.Context;
import android.util.AttributeSet;


import org.marelias.contacts.R;
import org.marelias.contacts.components.ImageButtonWithTint;
import org.marelias.contacts.utils.SharedPreferencesUtils;

public class SocialAppButton extends ImageButtonWithTint {
    public SocialAppButton(Context context) {
        this(context, null);
    }

    public SocialAppButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SocialAppButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        String defaultSocialApp = SharedPreferencesUtils.defaultSocialAppEnabled(context);
        if (defaultSocialApp.equalsIgnoreCase(TELEGRAM)) setImageResource(R.drawable.ic_telegram);
        else if (defaultSocialApp.equalsIgnoreCase(SIGNAL)) setImageResource(R.drawable.ic_signal_app);
        else setImageResource(R.drawable.ic_whatsapp);
    }

}
