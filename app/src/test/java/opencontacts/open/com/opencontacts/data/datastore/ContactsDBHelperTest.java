package opencontacts.open.com.opencontacts.data.datastore;

import static java.util.Arrays.asList;

import com.orm.SugarRecord;

import junit.framework.TestCase;

import opencontacts.open.com.opencontacts.orm.Contact;
import opencontacts.open.com.opencontacts.orm.PhoneNumber;

public class ContactsDBHelperTest extends TestCase {

    private PhoneNumber createPhoneNumber(String number, Contact contact) {
        return new PhoneNumber(number, contact, false);
    }
    // these tests cannot use the phonenumber library so will fallback to minimum 7 digit match or full number match
    public void testShouldBeAbleToMatchCorrectPhoneNumberInDB() {
        Contact expectedContact = new Contact();
        SugarRecord.values = asList(createPhoneNumber("99999999999", expectedContact));
        Contact contactFromDB = ContactsDBHelper.getContactFromDB("99999999999");
        assertEquals(expectedContact, contactFromDB);
    }

    // these tests cannot use the phonenumber library so will fallback to minimum 7 digit match or full number match
    public void testShouldNotMatchShortNumberToANormalNumberInDB() {
        Contact expectedContact = new Contact();
        SugarRecord.values = asList(createPhoneNumber("9999999999", expectedContact));
        Contact contactFromDB = ContactsDBHelper.getContactFromDB("999999");
        assertNull(contactFromDB);
        contactFromDB = ContactsDBHelper.getContactFromDB("99999");
        assertNull(contactFromDB);
        contactFromDB = ContactsDBHelper.getContactFromDB("9999");
        assertNull(contactFromDB);
        contactFromDB = ContactsDBHelper.getContactFromDB("999");
        assertNull(contactFromDB);
    }

    // these tests cannot use the phonenumber library so will fallback to minimum 7 digit match or full number match
    public void testShouldRemoveCountryCodeAndMatchPhoneNumberInDB() {
        Contact expectedContact = new Contact();
        SugarRecord.values = asList(createPhoneNumber("9999999999", expectedContact));
        Contact contactFromDB = ContactsDBHelper.getContactFromDB("+919999999999");
        assertEquals(expectedContact, contactFromDB);
    }
}
