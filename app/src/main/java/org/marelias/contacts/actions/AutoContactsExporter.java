package org.marelias.contacts.actions;

import static android.widget.Toast.LENGTH_LONG;
import static org.marelias.contacts.utils.AndroidUtils.hasPermission;
import static org.marelias.contacts.utils.AndroidUtils.toastFromNonUIThread;
import static org.marelias.contacts.utils.SharedPreferencesUtils.hasExportLocation;
import static org.marelias.contacts.utils.SharedPreferencesUtils.hasItBeenAWeekSinceLastExportOfContacts;
import static org.marelias.contacts.utils.SharedPreferencesUtils.markAutoExportComplete;
import static org.marelias.contacts.utils.SharedPreferencesUtils.shouldExportContactsEveryWeek;

import android.Manifest;
import android.content.Context;
import android.os.Build;

import java.util.List;

import org.marelias.contacts.R;
import org.marelias.contacts.domain.Contact;
import org.marelias.contacts.utils.DomainUtils;

public class AutoContactsExporter implements ContactsHouseKeepingAction {

    @Override
    public void perform(List<Contact> contacts, Context context) {
        // Check permissions based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11+, we use Storage Access Framework - no runtime permissions needed
            // Just check if export location is set
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For Android 6-10, we need WRITE_EXTERNAL_STORAGE permission
            if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, context)) {
                return;
            }
        }
        // For Android 5 and below, permissions are granted at install time

        if (!hasExportLocation(context)) return;
        if (!(shouldExportContactsEveryWeek(context) && hasItBeenAWeekSinceLastExportOfContacts(context))) return;
        try {
            DomainUtils.exportAllContacts(context);
            markAutoExportComplete(context);
        } catch (Exception e) {
            e.printStackTrace();
            toastFromNonUIThread(R.string.failed_exporting_contacts, LENGTH_LONG, context);
        }
    }
}
