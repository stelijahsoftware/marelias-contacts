package org.marelias.contacts;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import org.marelias.contacts.domain.Contact;
import org.marelias.contacts.utils.DomainUtils;

public class ContactsListT9Filter extends ContactsListFilter {
    public ContactsListT9Filter(ArrayAdapter<Contact> adapter, AllContactsHolder allContactsHolder) {
        super(adapter, allContactsHolder);
    }

    public void updateMap(Contact contact) {
        contact.setT9Text();
    }

    public void createDataMapping(List<Contact> contacts) {
        List<Contact> threadSafeContacts = new ArrayList<>(contacts);
        for (Contact contact : threadSafeContacts) {
            contact.setT9Text();
        }
    }

    public List<Contact> filter(CharSequence t9Text, List<Contact> contacts) {
        return DomainUtils.filterContactsBasedOnT9Text(t9Text, contacts);
    }
}
