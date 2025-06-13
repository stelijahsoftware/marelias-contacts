package org.marelias.contacts.data.datastore;

public interface DataStoreState {
    int NONE = 0;
    int LOADING = 1;
    int LOADED = 2;
    int REFRESHING = 3;
}
