package org.marelias.contacts.data.datastore;

import static org.marelias.contacts.data.datastore.ContactsDBHelper.getContact;
import static org.marelias.contacts.data.datastore.ContactsDBHelper.replacePhoneNumbersInDB;
import static org.marelias.contacts.data.datastore.ContactsDataStore.updateContact;
import static org.marelias.contacts.orm.VCardData.STATUS_NONE;
import static org.marelias.contacts.orm.VCardData.STATUS_UPDATED;
import static org.marelias.contacts.utils.DomainUtils.getPinyinTextFromChinese;
import static org.marelias.contacts.utils.VCardUtils.getNameFromVCard;
import static org.marelias.contacts.utils.VCardUtils.getVCardFromString;
import static org.marelias.contacts.utils.VCardUtils.writeVCardToString;

import android.content.Context;
import androidx.core.util.Pair;

import java.io.IOException;

import ezvcard.VCard;
import org.marelias.contacts.orm.Contact;
import org.marelias.contacts.orm.VCardData;
import org.marelias.contacts.utils.Triplet;
import org.marelias.contacts.utils.VCardUtils;

public class ContactsSyncHelper {
    public static void replaceContactWithServers(Triplet<String, String, VCard> hrefEtagAndVCard, VCardData vCardData, Context context) {
        updateContact(vCardData.contact.getId(), "", hrefEtagAndVCard.z, context);
        VCardData updatedVCardData = VCardData.getVCardData(vCardData.contact.getId());
        updatedVCardData.href = hrefEtagAndVCard.x;
        updatedVCardData.status = STATUS_NONE;
        updatedVCardData.save();
    }

    public static void merge(Triplet<String, String, VCard> hrefEtagAndVCard, VCardData vCardData, Context context) {
        VCard vCardInDb = null;
        try {
            vCardInDb = getVCardFromString(vCardData.vcardDataAsString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        VCard mergedCard = VCardUtils.mergeVCards(vCardInDb, hrefEtagAndVCard.z, context);
        Contact dbContact = ContactsDBHelper.getDBContactWithId(vCardData.contact.getId());
        Pair<String, String> nameFromVCard = getNameFromVCard(mergedCard, context);
        dbContact.firstName = nameFromVCard.first;
        dbContact.lastName = nameFromVCard.second;
        dbContact.pinyinName = getPinyinTextFromChinese(dbContact.getFullName());
        dbContact.save();
        replacePhoneNumbersInDB(dbContact, hrefEtagAndVCard.z, getContact(vCardData.contact.getId()).primaryPhoneNumber.phoneNumber);
        vCardData.vcardDataAsString = writeVCardToString(mergedCard);
        vCardData.etag = hrefEtagAndVCard.y;
        vCardData.href = hrefEtagAndVCard.x;
        vCardData.uid = hrefEtagAndVCard.z.getUid().getValue();
        vCardData.status = STATUS_UPDATED;
        vCardData.save();
    }
}
