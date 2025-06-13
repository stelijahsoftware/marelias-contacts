package org.marelias.contacts.actions;

import static java.util.Calendar.DAY_OF_MONTH;
import static org.marelias.contacts.utils.Common.hasItBeen;

import android.content.Context;

import com.github.underscore.U;

import java.util.List;

import org.marelias.contacts.data.datastore.ContactsDataStore;
import org.marelias.contacts.domain.Contact;

public class DeleteTemporaryContacts implements ContactsHouseKeepingAction{

    @Override
    public void perform(List<Contact> contacts, Context context) {
        U.forEach(ContactsDataStore.getTemporaryContactDetails(),
            tempContactDetails -> {
                if(tempContactDetails.contact == null) {
                    tempContactDetails.delete();
                    return;
                }
                if(hasItBeen(30, DAY_OF_MONTH, tempContactDetails.markedTemporaryOn.getTime()))
                    ContactsDataStore.removeContact(tempContactDetails.contact.getId());
            });
    }
}
