package opencontacts.open.com.opencontacts.utils;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import static opencontacts.open.com.opencontacts.orm.PhoneNumber.createDummyPhoneNumber;
import static opencontacts.open.com.opencontacts.utils.DomainUtils.filterContactsBasedOnT9Text;
import static opencontacts.open.com.opencontacts.utils.DomainUtils.getNumericKeyPadNumberForString;

import junit.framework.TestCase;

import java.util.List;

import opencontacts.open.com.opencontacts.domain.Contact;

public class DomainUtilsTest extends TestCase {
    public void testShouldTranslateToPinyin() {
        assertEquals("zhong wen", DomainUtils.getPinyinTextFromChinese("中文"));
    }
    private Contact contactWithName(String name) {
        Contact dummyContact = Contact.createDummyContact(-1);
        dummyContact.name = name;
        dummyContact.phoneNumbers = emptyList();
        return dummyContact;
    }

    private Contact contactWithNumber(String number) {
        Contact dummyContact = Contact.createDummyContact(-1);
        dummyContact.phoneNumbers = asList(createDummyPhoneNumber(number));
        return dummyContact;
    }

    public void testShouldFilterNameUsingT9(){
        List<Contact> filteredContacts = filterContactsBasedOnT9Text("23", asList(
            contactWithName("abcd"),
            contactWithName("efgh"),
            contactWithName("ghij")
        ));
        assertEquals(1, filteredContacts.size());
        assertEquals("abcd", filteredContacts.get(0).name);
    }

    public void testShouldFilterNameWithSpaceUsingT9(){
        List<Contact> filteredContacts = filterContactsBasedOnT9Text("303", asList(
            contactWithName("abcd efgh"),
            contactWithName("efgh"),
            contactWithName("ghij")
        ));
        assertEquals(1, filteredContacts.size());
        assertEquals("abcd efgh", filteredContacts.get(0).name);
    }
    public void testShouldFilterPhoneNumberUsingT9(){
        List<Contact> filteredContacts = filterContactsBasedOnT9Text("23", asList(
            contactWithNumber("1234"),
            contactWithNumber("5678")
        ));

        assertEquals(1, filteredContacts.size());
        assertEquals("1234", filteredContacts.get(0).phoneNumbers.get(0).phoneNumber);
    }

    public void testT9Translation() {
        assertEquals("22233344455566677778889999022233344455566677778889999022",getNumericKeyPadNumberForString("abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }



}
