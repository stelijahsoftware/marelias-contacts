package org.marelias.contacts.utils;

import com.github.underscore.U;
import com.opencsv.CSVWriterBuilder;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.marelias.contacts.domain.Contact;

public class AIDLTranslationUtils {

    public static String[] tempArrayDenotingTypeInfo = new String[]{};
    public static String[] nameAndPhoneNumbersToCSV(Contact contact) {
        List<String> csvRow = new ArrayList<>();
        csvRow.add(contact.name);
        csvRow.addAll(U.map(contact.phoneNumbers, ph -> ph.phoneNumber));
        return csvRow.toArray(tempArrayDenotingTypeInfo);
    }

    public static String csvString(List<String[]> contactsAsCSV) {
        StringWriter stringWriter = new StringWriter();
        new CSVWriterBuilder(stringWriter)
            .build()
            .writeAll(contactsAsCSV);
        return stringWriter.toString();
    }

}
