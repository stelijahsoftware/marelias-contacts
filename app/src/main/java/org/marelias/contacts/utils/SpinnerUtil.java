package org.marelias.contacts.utils;

import static org.marelias.contacts.components.TintedDrawablesStore.getTintedDrawable;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.underscore.U;

import java.util.List;

import org.marelias.contacts.R;

public class SpinnerUtil {

    public static void setItem(String text, List<String> items, Spinner spinner) {
        int indexOfType = items.indexOf(text);
        if (indexOfType == -1) {
            // If text is not in the list, add it temporarily and select it
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            if (adapter != null) {
                adapter.add(text);
                spinner.setSelection(adapter.getCount() - 1);
            }
        } else {
            spinner.setSelection(indexOfType);
        }
    }

    public static void setupSpinner(List<String> items, Spinner spinner, Context context) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (items.size() > 0) spinner.setSelection(0);
    }
}
