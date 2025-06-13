package org.marelias.contacts;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import org.marelias.contacts.domain.Contact;

public class ContactsListTextFilter extends ContactsListFilter {
    public ContactsListTextFilter(ArrayAdapter<Contact> adapter, AllContactsHolder allContactsHolder) {
        super(adapter, allContactsHolder);
    }

    @Override
    public void updateMap(Contact contact) {
        contact.setTextSearchTarget();
    }

    @Override
    public void createDataMapping(List<Contact> contacts) {
        List<Contact> threadSafeContacts = new ArrayList<>(contacts);
        for (Contact contact : threadSafeContacts) {
            contact.setTextSearchTarget();
        }
    }

    @Override
    public List<Contact> filter(CharSequence searchText, List<Contact> contacts) {
        ArrayList<Contact> filteredContacts = new ArrayList<>();
        for (Contact contact : contacts) {
            if (contact.textSearchTarget == null) {
                contact.setTextSearchTarget();
            }
            if (contact.textSearchTarget.contains(searchText.toString().toUpperCase())) {
                filteredContacts.add(contact);
            }
        }
        return filteredContacts;
    }

}
