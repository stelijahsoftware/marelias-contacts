package org.marelias.contacts;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.OnLongClickListener;
import static android.view.View.VISIBLE;
import static org.marelias.contacts.utils.SharedPreferencesUtils.defaultSocialAppEnabled;
import static org.marelias.contacts.utils.SharedPreferencesUtils.isT9SearchEnabled;
import static org.marelias.contacts.utils.SharedPreferencesUtils.isSocialIntegrationEnabled;
import static org.marelias.contacts.utils.SharedPreferencesUtils.shouldToggleContactActions;
import static org.marelias.contacts.utils.AndroidUtils.getThemeAttributeColor;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.marelias.contacts.components.ImageButtonWithTint;
import org.marelias.contacts.domain.Contact;

public class ContactsListViewAdapter extends ArrayAdapter<Contact> {
    private boolean shouldToggleContactActions;
    private ContactsListActionsListener contactsListActionsListener;
    private LayoutInflater layoutInflater;
    public ContactsListFilter contactsListFilter;
    private boolean socialAppIntegrationEnabled;

    public ContactsListViewAdapter(@NonNull Context context, int resource, ContactsListFilter.AllContactsHolder allContactsHolder) {
        super(context, resource, new ArrayList<>(allContactsHolder.getContacts()));
        init(context);
        createContactsListFilter(allContactsHolder);
    }

    public ContactsListViewAdapter(@NonNull Context context) {
        super(context, R.layout.contact, new ArrayList<>());
        init(context);
    }

    private void init(@NonNull Context context) {
        layoutInflater = LayoutInflater.from(context);
        socialAppIntegrationEnabled = isSocialIntegrationEnabled(context);
        shouldToggleContactActions = shouldToggleContactActions(context);
    }

    public void createContactsListFilter(ContactsListFilter.AllContactsHolder allContactsHolder) {
        contactsListFilter = isT9SearchEnabled(getContext()) ? new ContactsListT9Filter(this, allContactsHolder)
            : new ContactsListTextFilter(this, allContactsHolder);
    }

    private final OnLongClickListener onLongClicked = v -> {
        if (contactsListActionsListener == null)
            return false;
        Contact contact = (Contact) ((View) v.getParent()).getTag();
        contactsListActionsListener.onLongClick(contact);
        return true;
    };

    private final OnClickListener callContact = v -> {
        if (contactsListActionsListener == null)
            return;
        Contact contact = (Contact) ((View) v.getParent()).getTag();
        contactsListActionsListener.onCallClicked(contact);
    };
    private final OnClickListener messageContact = v -> {
        if (contactsListActionsListener == null)
            return;
        Contact contact = (Contact) ((View) v.getParent()).getTag();
        contactsListActionsListener.onMessageClicked(contact);
    };
    private final OnClickListener showContactDetails = v -> {
        if (contactsListActionsListener == null)
            return;
        Contact contact = (Contact) ((View) v.getParent()).getTag();
        contactsListActionsListener.onShowDetails(contact);
    };
    private final OnClickListener openSocialApp = v -> {
        if (contactsListActionsListener == null)
            return;
        Contact contact = (Contact) ((View) v.getParent()).getTag();
        contactsListActionsListener.onSocialAppClicked(contact);
    };
    private final OnLongClickListener socialLongClicked = v -> {
        if (contactsListActionsListener == null)
            return false;
        Contact contact = (Contact) ((View) v.getParent()).getTag();
        contactsListActionsListener.onSocialLongClicked(contact);
        return true;
    };

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact contact = getItem(position);
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.contact, parent, false);
        ((TextView) convertView.findViewById(R.id.textview_full_name)).setText(contact.name);
        ((TextView) convertView.findViewById(R.id.textview_phone_number)).setText(contact.primaryPhoneNumber.phoneNumber);
        ImageButtonWithTint actionButton1 = convertView.findViewById(R.id.button_action1);
        ImageButtonWithTint actionButton2 = convertView.findViewById(R.id.button_action2);
        Context context = getContext();
        String messageButtonContentDescription = context.getString(R.string.message) + contact.name;
        String callButtonContentDescription = context.getString(R.string.call) + contact.name;
        if (shouldToggleContactActions) {
            actionButton1.setOnClickListener(messageContact);
            actionButton1.setContentDescription(messageButtonContentDescription);
            actionButton1.setImageResource(R.drawable.ic_chat_black_24dp);
            actionButton2.setOnClickListener(callContact);
            actionButton2.setImageResource(R.drawable.ic_call_black_24dp);
            actionButton2.setContentDescription(callButtonContentDescription);
        } else {
            actionButton1.setOnClickListener(callContact);
            actionButton1.setContentDescription(callButtonContentDescription);
            actionButton1.setImageResource(R.drawable.ic_call_black_24dp);
            actionButton2.setOnClickListener(messageContact);
            actionButton2.setImageResource(R.drawable.ic_chat_black_24dp);
            actionButton2.setContentDescription(messageButtonContentDescription);
        }
        View socialIcon = convertView.findViewById(R.id.button_social);
        if (socialAppIntegrationEnabled) {
            socialIcon.setOnClickListener(openSocialApp);
            socialIcon.setOnLongClickListener(socialLongClicked);
            socialIcon.setVisibility(VISIBLE);
            socialIcon.setContentDescription(defaultSocialAppEnabled(context) + " " + contact.name);
        } else socialIcon.setVisibility(GONE);

        // Handle groups display - each group on a separate line
        LinearLayout groupsContainer = convertView.findViewById(R.id.groups_container);
        if (groupsContainer != null) {
            List<String> groupNames = contact != null ? contact.getGroupNames() : Collections.emptyList();
            boolean hasGroups = !groupNames.isEmpty();

            if (hasGroups) {
                // Remove all existing group views
                groupsContainer.removeAllViews();

                // Create a row for each group
                int iconSize = (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 14, context.getResources().getDisplayMetrics());
                int iconMargin = (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());

                for (String groupName : groupNames) {
                    LinearLayout groupRow = new LinearLayout(context);
                    groupRow.setOrientation(LinearLayout.HORIZONTAL);
                    groupRow.setGravity(android.view.Gravity.CENTER_VERTICAL);

                    ImageView groupIcon = new ImageView(context);
                    groupIcon.setImageResource(R.drawable.ic_group_merge_contacts_24dp);
                    LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
                    iconParams.setMarginEnd(iconMargin);
                    iconParams.setMarginStart(0);
                    groupIcon.setLayoutParams(iconParams);
                    groupIcon.setContentDescription(context.getString(R.string.group_indicator));

                    TextView groupTextView = new TextView(context);
                    groupTextView.setText(groupName);
                    groupTextView.setTextAppearance(androidx.appcompat.R.style.TextAppearance_AppCompat_Caption);
                    groupTextView.setTextColor(getThemeAttributeColor(android.R.attr.textColorSecondary, context));

                    groupRow.addView(groupIcon);
                    groupRow.addView(groupTextView);
                    groupsContainer.addView(groupRow);
                }

                groupsContainer.setVisibility(VISIBLE);
            } else {
                groupsContainer.setVisibility(GONE);
            }
        }

        convertView.setTag(contact);
        View contactDetails = convertView.findViewById(R.id.contact_details);
        contactDetails.setOnClickListener(showContactDetails);
        contactDetails.setOnLongClickListener(onLongClicked);
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return contactsListFilter;
    }

    public void setContactsListActionsListener(ContactsListActionsListener contactsListActionsListener) {
        this.contactsListActionsListener = contactsListActionsListener;
    }

    public interface ContactsListActionsListener {
        void onCallClicked(Contact contact);

        void onMessageClicked(Contact contact);

        void onShowDetails(Contact contact);

        void onSocialAppClicked(Contact contact);

        void onSocialLongClicked(Contact contact);

        void onLongClick(Contact contact);
    }
}
