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

import java.util.List;

import org.marelias.contacts.R;
import org.marelias.contacts.domain.Contact;
import org.marelias.contacts.utils.DomainUtils;

public class AutoContactsExporter implements ContactsHouseKeepingAction {

    @Override
    public void perform(List<Contact> contacts, Context context) {
        if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, context)) return;
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
