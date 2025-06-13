package org.marelias.contacts.orm;

import com.orm.SugarRecord;

public class Favorite extends SugarRecord {
    public Contact contact;

    public Favorite() {
    }

    public Favorite(Contact contact) {
        this.contact = contact;
    }
}
