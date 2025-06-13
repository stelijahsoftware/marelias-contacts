package org.marelias.contacts.actions;

import android.content.Context;

import java.util.List;

import org.marelias.contacts.domain.Contact;

public interface ContactsHouseKeepingAction {
    void perform(List<Contact> contacts, Context context);
}
