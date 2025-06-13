package org.marelias.contacts.orm;

import static android.text.TextUtils.isEmpty;

import static java.util.Collections.emptyList;

import com.orm.SugarRecord;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

import org.marelias.contacts.utils.DomainUtils;

/**
 * Created by sultanm on 7/22/17.
 */

public class PhoneNumber extends SugarRecord implements Serializable {
    @Nullable
    public String phoneNumber;
    public Contact contact;
    public boolean isPrimaryNumber = false;
    public String numericPhoneNumber; // for comparision during calls

    public PhoneNumber() {

    }

    public PhoneNumber(String mobileNumber, Contact contact, boolean isPrimaryNumber) {
        this.phoneNumber = mobileNumber;
        this.contact = contact;
        this.isPrimaryNumber = isPrimaryNumber;
        this.numericPhoneNumber = DomainUtils.getAllNumericPhoneNumber(mobileNumber);
    }

    private PhoneNumber(String phoneNumber) { // dummy number
        this.phoneNumber = phoneNumber;
        this.numericPhoneNumber = phoneNumber;
    }

    public static PhoneNumber createDummyPhoneNumber(String phoneNumber) {
        return new PhoneNumber(phoneNumber);
    }
    public static List<PhoneNumber> getMatchingNumbers(String numericPhoneNumber) {
        if(isEmpty(numericPhoneNumber)) return emptyList();
        return PhoneNumber.find(PhoneNumber.class, "numeric_Phone_Number like ?", "%" + numericPhoneNumber);
    }
}
