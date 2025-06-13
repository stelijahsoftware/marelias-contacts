package org.marelias.contacts.fragments;

import static org.marelias.contacts.utils.ThemeUtils.applyOptedTheme;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AppBaseFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        applyOptedTheme(getActivity());
        super.onCreate(savedInstanceState);
    }

    public boolean handleBackPress() {
        return false;
    }
}
