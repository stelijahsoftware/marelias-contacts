package org.marelias.contacts.actions;

import static org.marelias.contacts.utils.AndroidUtils.processAsync;

import android.content.Context;

import java.util.List;

import org.marelias.contacts.data.datastore.ContactsDataStore;
import org.marelias.contacts.domain.Contact;
import org.marelias.contacts.interfaces.SampleDataStoreChangeListener;

public class ContactsHouseKeeping {
    private Context context;

    public ContactsHouseKeeping(Context context) {
        this.context = context;
    }

    public void start() {
        if (ContactsDataStore.getAllContacts().isEmpty())//loading contacts meanwhile
            ContactsDataStore.addDataChangeListener(new SampleDataStoreChangeListener<Contact>() {
                @Override
                public void onStoreRefreshed() {
                    if (!ContactsDataStore.getAllContacts().isEmpty())
                        processAsync(() -> performHouseKeeping(ContactsDataStore.getAllContacts()));
                    ContactsDataStore.removeDataChangeListener(this);
                }
            });
    }

    private void performHouseKeeping(List<Contact> allContacts) {
        new AutoContactsExporter().perform(allContacts, context);
        new DeleteTemporaryContacts().perform(allContacts, context);
    }

}
