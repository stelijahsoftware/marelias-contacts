package org.marelias.contacts.activities;

import static java.util.Arrays.asList;
import static org.marelias.contacts.utils.AndroidUtils.goToUrl;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import org.marelias.contacts.BuildConfig;
import org.marelias.contacts.R;

public class AboutActivity extends AppBaseActivity {
    @Override
    int getLayoutResource() {
        return R.layout.activity_about;
    }

    @Override
    int title() {
        return R.string.about;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatTextView) findViewById(R.id.version)).setText(BuildConfig.VERSION_NAME);
    }
}
