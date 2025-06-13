package org.marelias.contacts.domain;

import java.util.List;

import org.marelias.contacts.orm.CallLogEntry;

public class GroupedCallLogEntry {
    public List<CallLogEntry> callLogEntries;
    public CallLogEntry latestCallLogEntry;

    public GroupedCallLogEntry(List<CallLogEntry> callLogEntries, CallLogEntry latestCallLogEntry) {
        this.callLogEntries = callLogEntries;
        this.latestCallLogEntry = latestCallLogEntry;
    }
}
