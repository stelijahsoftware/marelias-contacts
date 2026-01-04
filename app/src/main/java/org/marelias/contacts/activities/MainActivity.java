package org.marelias.contacts.activities;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
//import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static org.marelias.contacts.utils.AndroidUtils.STORAGE_LOCATION_CHOOSER_RESULT;
import static org.marelias.contacts.utils.AndroidUtils.getMenuItemClickHandlerFor;
import static org.marelias.contacts.utils.AndroidUtils.getNumberToDial;
import static org.marelias.contacts.utils.AndroidUtils.getThemeAttributeColor;
import static org.marelias.contacts.utils.AndroidUtils.hasPermission;
import static org.marelias.contacts.utils.AndroidUtils.isAddContactIntent;
import static org.marelias.contacts.utils.AndroidUtils.isValidDialIntent;
import static org.marelias.contacts.utils.AndroidUtils.pickADirectory;
import static org.marelias.contacts.utils.AndroidUtils.runOnMainDelayed;
import static org.marelias.contacts.utils.AndroidUtils.setColorFilterUsingColor;
import static org.marelias.contacts.utils.AndroidUtils.wrapInConfirmation;
import static org.marelias.contacts.utils.DomainUtils.handleExportLocationChooserResult;
import static org.marelias.contacts.utils.SharedPreferencesUtils.getDefaultTab;
import static org.marelias.contacts.utils.SharedPreferencesUtils.hasExportLocation;
import static org.marelias.contacts.utils.SharedPreferencesUtils.markPermissionsAsked;
import static org.marelias.contacts.utils.SharedPreferencesUtils.setSortContactsByName;
import static org.marelias.contacts.utils.SharedPreferencesUtils.shouldSortContactsByName;
//import static org.marelias.contacts.utils.SharedPreferencesUtils.shouldBottomMenuOpenByDefault;

import static org.marelias.contacts.utils.SharedPreferencesUtils.shouldKeyboardResizeViews;
//import static org.marelias.contacts.utils.SharedPreferencesUtils.shouldLaunchDefaultTab;
//import static org.marelias.contacts.utils.SharedPreferencesUtils.shouldShowBottomMenu;
//import static org.marelias.contacts.utils.ThemeUtils.getPrimaryColor;
import static org.marelias.contacts.utils.domain.AppShortcuts.TAB_INDEX_INTENT_EXTRA;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.underscore.U;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.marelias.contacts.R;
import org.marelias.contacts.actions.ExportMenuItemClickHandler;
import org.marelias.contacts.fragments.AppBaseFragment;
import org.marelias.contacts.fragments.ContactsFragment;
import org.marelias.contacts.fragments.DialerFragment;
import org.marelias.contacts.interfaces.SelectableTab;
import org.marelias.contacts.utils.AndroidUtils;
import org.marelias.contacts.utils.DomainUtils;
import org.marelias.contacts.utils.SharedPreferencesUtils;
import org.marelias.contacts.domain.Contact;
import org.marelias.contacts.interfaces.SampleDataStoreChangeListener;
import org.marelias.contacts.data.datastore.ContactsDataStore;
import androidx.appcompat.widget.AppCompatTextView;
//import pro.midev.expandedmenulibrary.ExpandedMenuItem;
//import pro.midev.expandedmenulibrary.ExpandedMenuView;


public class MainActivity extends AppBaseActivity {
    public static final int CONTACTS_TAB_INDEX = 0;
    public static final int DIALER_TAB_INDEX = 1;
    public static final String INTENT_EXTRA_LONG_CONTACT_ID = "contact_id";
    private static final int PREFERENCES_ACTIVITY_RESULT = 773;
    private static final int IMPORT_FILE_CHOOSER_RESULT = 467;
    private ViewPager viewPager;
    private SearchView searchView;
    private ContactsFragment contactsFragment;
    private DialerFragment dialerFragment;
    private MenuItem searchItem;
//    private ExpandedMenuView bottomMenu;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMPORT_FILE_CHOOSER_RESULT) {
            if (data == null) return;
            startActivity(
                new Intent(this, ImportVcardActivity.class)
                    .setData(data.getData())
            );
            return;
        }
        if (requestCode == PREFERENCES_ACTIVITY_RESULT) {
            recreate();
            return;
        }
        if (requestCode == STORAGE_LOCATION_CHOOSER_RESULT) {
            handleExportLocationChooserResult(data, this);
            return;
        };
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private boolean handleIntent(Intent intent) {
        if (isValidDialIntent(intent)) showDialerWithNumber(getNumberToDial(intent));
        else if (isAddContactIntent(intent)) AndroidUtils.getAlertDialogToAddContact(intent.getStringExtra(ContactsContract.Intents.Insert.PHONE), this).show();
        else if (isTabSpecified(intent)) gotoTabSpecified(intent);
        else return false;
        return true;
    }

    private void showDialerWithNumber(String number) {
        runOnMainDelayed(() -> {
            viewPager.setCurrentItem(DIALER_TAB_INDEX);
            dialerFragment.setNumber(number);
        }, 500);
    }

    private boolean isTabSpecified(Intent intent) {
        return intent.getIntExtra(TAB_INDEX_INTENT_EXTRA, -1) != -1;
    }

    private void gotoTabSpecified(Intent intent) {
        int tabIndexToShow = intent.getIntExtra(TAB_INDEX_INTENT_EXTRA, -1);
        runOnMainDelayed(() -> viewPager.setCurrentItem(tabIndexToShow), 300);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gotoDefaultTab();
        updateBirthdayBanner();
    }

    private void gotoDefaultTab() {
        // post delayed as view pager is prioritising the fragment launched first as fragment 0 in the list
        // affecting the fragments order etc resulting in cast exception when recreating activity while reusing fragments
        runOnMainDelayed(() -> {
            if (viewPager == null) return;
            viewPager.setCurrentItem(getDefaultTab(this));
        }, 100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreferencesUtils.shouldAskForPermissions(this)) {
            AndroidUtils.askForPermissionsIfNotGranted(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                pickADirectory(this); // for export location
            }
            View startButton = findViewById(R.id.start_button);
            startButton.setVisibility(VISIBLE);
            startButton.setOnClickListener(x -> this.recreate());
            markPermissionsAsked(this);
            return;
        } else {
            setupTabs();
            //setupBottomMenu();
            if (shouldKeyboardResizeViews(this))
                getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE);
            if (handleIntent(getIntent())) ;
            else gotoDefaultTab();
            setupBirthdayBannerListener();
            updateBirthdayBanner();
        }
        markPermissionsAsked(this);
    }

    private void setupBirthdayBannerListener() {
        // Listen for when contacts are loaded/refreshed to update banner
        ContactsDataStore.addDataChangeListener(new SampleDataStoreChangeListener<Contact>() {
            @Override
            public void onStoreRefreshed() {
                updateBirthdayBanner();
            }
        });
    }

    private void updateBirthdayBanner() {
        // Process in background to avoid blocking UI
        AndroidUtils.processAsync(() -> {
            List<Contact> contactsWithBirthdayToday = DomainUtils.getContactsWithBirthdayToday(this);
            android.util.Log.d("MainActivity", "Found " + contactsWithBirthdayToday.size() + " contacts with birthday today");

            // Update UI on main thread
            runOnMainDelayed(() -> {
                View birthdayBanner = findViewById(R.id.birthday_banner);

                if (birthdayBanner == null) {
                    android.util.Log.e("MainActivity", "Birthday banner view not found!");
                    return;
                }

                if (contactsWithBirthdayToday.isEmpty()) {
                    birthdayBanner.setVisibility(GONE);
                    android.util.Log.d("MainActivity", "No birthdays today, hiding banner");
                    return;
                }

                // Show banner with all contact names
                AppCompatTextView bannerText = birthdayBanner.findViewById(R.id.birthday_banner_text);
                if (bannerText != null) {
                    StringBuilder namesBuilder = new StringBuilder();
                    for (int i = 0; i < contactsWithBirthdayToday.size(); i++) {
                        Contact contact = contactsWithBirthdayToday.get(i);
                        String firstName = contact.firstName != null ? contact.firstName : "";
                        String lastName = contact.lastName != null ? contact.lastName : "";
                        String fullName = (firstName + " " + lastName).trim();
                        if (fullName.isEmpty()) {
                            fullName = contact.name != null ? contact.name : "";
                        }

                        if (i > 0) {
                            if (i == contactsWithBirthdayToday.size() - 1) {
                                namesBuilder.append(" and ");
                            } else {
                                namesBuilder.append(", ");
                            }
                        }
                        namesBuilder.append(fullName);
                    }

                    String bannerMessage = getString(R.string.birthday_banner_prefix) + " " + namesBuilder.toString();
                    bannerText.setText(bannerMessage);

                    // Set up close button
                    androidx.appcompat.widget.AppCompatImageButton closeButton = birthdayBanner.findViewById(R.id.birthday_banner_close);
                    if (closeButton != null) {
                        closeButton.setOnClickListener(v -> {
                            birthdayBanner.setVisibility(GONE);
                        });
                    }

                    birthdayBanner.setVisibility(VISIBLE);
                    android.util.Log.d("MainActivity", "Birthday banner shown for: " + bannerMessage);
                } else {
                    android.util.Log.e("MainActivity", "Birthday banner text view not found!");
                }
            }, 0);
        });
    }

/*
    private void setupBottomMenu() {
        bottomMenu = findViewById(R.id.bottom_menu);
        if (!shouldShowBottomMenu(this)) {
            bottomMenu.setVisibility(GONE);
            bottomMenu = null;
            return;
        }

        ExpandedMenuItem searchItem = new ExpandedMenuItem(R.drawable.ic_search_black_24dp, "Search", getPrimaryColor(this));
        ExpandedMenuItem groupItem = new ExpandedMenuItem(R.drawable.ic_group_merge_contacts_24dp, "Groups", getPrimaryColor(this));
        ExpandedMenuItem dialpadItem = new ExpandedMenuItem(R.drawable.dial_pad, "Dial", getPrimaryColor(this));
        ExpandedMenuItem addContactItem = new ExpandedMenuItem(R.drawable.ic_add_circle_outline_24dp, "Add contact", getPrimaryColor(this));
        bottomMenu.setIcons(searchItem, groupItem, addContactItem, dialpadItem);
        bottomMenu.setOnItemClickListener(i -> {
            switch (i) {
                case 0:
                    searchContacts();
                    break;
                case 1:
                    launchGroupsActivity();
                    break;
                case 2:
                    launchAddContact();
                    break;
                case 3:
                    viewPager.setCurrentItem(DIALER_TAB_INDEX);
                    break;
            }
        });
        if (shouldBottomMenuOpenByDefault(this)) bottomMenu.expandMenu();
        else bottomMenu.collapseMenu();
    }
*/

    @Override
    int title() {
        return R.string.app_name;
    }

    @Override
    int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        setMenuItemsListeners(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setMenuItemsListeners(Menu menu) {
        menu.findItem(R.id.button_new).setOnMenuItemClickListener(getMenuItemClickHandlerFor(this::launchAddContact));
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setContentDescription("Search for contact");
        searchView.setOnSearchClickListener(v -> {
            viewPager.setCurrentItem(CONTACTS_TAB_INDEX);
        });

        menu.findItem(R.id.action_import).setOnMenuItemClickListener(getMenuItemClickHandlerFor(this::importContacts));
        menu.findItem(R.id.action_merge).setOnMenuItemClickListener(getMenuItemClickHandlerFor(() ->
            startActivity(new Intent(this, MergeContactsActivity.class))
        ));

        if (contactsFragment != null)
            contactsFragment.configureSearchInMenu(searchView);
        menu.findItem(R.id.action_export).setOnMenuItemClickListener(item -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // For Android 11+, we need to use Storage Access Framework
                if (!hasExportLocation(this)) {
                    Toast.makeText(this, R.string.choose_create_export_location, Toast.LENGTH_LONG).show();
                    return true;
                }
                return new ExportMenuItemClickHandler(this).onMenuItemClick(item);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // For Android 6-10, we need WRITE_EXTERNAL_STORAGE permission
                if (!hasPermission(WRITE_EXTERNAL_STORAGE, this)) {
                    Toast.makeText(this, R.string.grant_storage_permisson_detail, Toast.LENGTH_LONG).show();
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 123);
                    return true;
                }
                if(!hasExportLocation(this)) {
                    Toast.makeText(this, R.string.choose_create_export_location, Toast.LENGTH_LONG).show();
                    return true;
                }
                return new ExportMenuItemClickHandler(this).onMenuItemClick(item);
            } else {
                // For Android 5 and below, permissions are granted at install time
                if(!hasExportLocation(this)) {
                    Toast.makeText(this, R.string.choose_create_export_location, Toast.LENGTH_LONG).show();
                    return true;
                }
                return new ExportMenuItemClickHandler(this).onMenuItemClick(item);
            }
        });
        menu.findItem(R.id.action_about).setOnMenuItemClickListener(getMenuItemClickHandlerFor(() ->
            startActivity(new Intent(MainActivity.this, AboutActivity.class))
        ));

        menu.findItem(R.id.action_groups).setOnMenuItemClickListener(getMenuItemClickHandlerFor(this::launchGroupsActivity));

        menu.findItem(R.id.action_birthdays).setOnMenuItemClickListener(getMenuItemClickHandlerFor(this::showBirthdaysDialog));

        MenuItem sortMenuItem = menu.findItem(R.id.action_sort_by_name);
        if (sortMenuItem != null) {
            updateSortMenuItemTitle(sortMenuItem);
            sortMenuItem.setOnMenuItemClickListener(item -> {
                boolean currentSortByName = shouldSortContactsByName(this);
                setSortContactsByName(!currentSortByName, this);
                updateSortMenuItemTitle(item);
                // Refresh the contacts list
                if (contactsFragment != null && contactsFragment.getContactsListView() != null) {
                    contactsFragment.getContactsListView().refreshSorting();
                }
                return true;
            });
        }

        menu.findItem(R.id.action_preferences).setOnMenuItemClickListener(getMenuItemClickHandlerFor(() ->
            startActivityForResult(new Intent(MainActivity.this, PreferencesActivity.class), PREFERENCES_ACTIVITY_RESULT)
        ));

    }

    private void launchAddContact() {
        Intent addContact = new Intent(MainActivity.this, EditContactActivity.class);
        addContact.putExtra(EditContactActivity.INTENT_EXTRA_BOOLEAN_ADD_NEW_CONTACT, true);
        startActivity(addContact);
    }

    private void launchGroupsActivity() {
        startActivity(new Intent(MainActivity.this, GroupsActivity.class));
    }

    private void showBirthdaysDialog() {
        AndroidUtils.processAsync(() -> {
            List<DomainUtils.ContactWithBirthday> contactsWithBirthdays = DomainUtils.getAllContactsWithBirthdays(this);

            runOnMainDelayed(() -> {
                if (contactsWithBirthdays.isEmpty()) {
                    Toast.makeText(this, R.string.no_birthdays_found, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create list of SpannableStrings for the dialog with bold dates
                List<CharSequence> birthdayItems = new ArrayList<>();
                for (DomainUtils.ContactWithBirthday item : contactsWithBirthdays) {
                    String firstName = item.contact.firstName != null ? item.contact.firstName : "";
                    String lastName = item.contact.lastName != null ? item.contact.lastName : "";
                    String fullName = (firstName + " " + lastName).trim();
                    if (fullName.isEmpty()) {
                        fullName = item.contact.name != null ? item.contact.name : "";
                    }
                    String fullText = fullName + " - " + item.formattedDate;
                    SpannableString spannableString = new SpannableString(fullText);
                    // Make the date part bold
                    int dateStartIndex = fullText.indexOf(item.formattedDate);
                    if (dateStartIndex >= 0) {
                        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            dateStartIndex, dateStartIndex + item.formattedDate.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    birthdayItems.add(spannableString);
                }

                // Create and show dialog
                new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle(R.string.birthdays)
                    .setItems(birthdayItems.toArray(new CharSequence[0]), (dialog, which) -> {
                        DomainUtils.ContactWithBirthday selected = contactsWithBirthdays.get(which);
                        // Open contact details when clicked
                        Intent intent = new Intent(this, ContactDetailsActivity.class);
                        intent.putExtra(INTENT_EXTRA_LONG_CONTACT_ID, selected.contact.id);
                        startActivity(intent);
                    })
                    .setNegativeButton("OK", null)
                    .show();
            }, 0);
        });
    }

    private void searchContacts() {
        if (searchItem.isActionViewExpanded()) searchItem.collapseActionView();
        searchItem.expandActionView();
    }

    @Override
    public void onBackPressed() {
        AppBaseFragment currentFragment = null;
        try {
            currentFragment = getCurrentFragment();
        }catch (Exception ignored) {}
        if (currentFragment == null) {
            super.onBackPressed();
            return;
        }
        if (currentFragment.handleBackPress()) return;
        super.onBackPressed();
    }

    private AppBaseFragment getCurrentFragment() {
        return (AppBaseFragment) ((FragmentPagerAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
    }

    private void importContacts() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT)
                        .setType("*/*"),
                    IMPORT_FILE_CHOOSER_RESULT);
            } else startActivityForResult(
                new Intent(Intent.ACTION_PICK),
                IMPORT_FILE_CHOOSER_RESULT);
        } catch (Exception e) {
            makeText(this, R.string.no_app_found_for_action_open_document, LENGTH_SHORT).show();
        }
    }

    private void setupTabs() {
        viewPager = findViewById(R.id.view_pager);
        List<Fragment> fragmentsList = getSupportFragmentManager().getFragments();
        if (!fragmentsList.isEmpty()) {
            contactsFragment = U.find(fragmentsList, frag -> frag instanceof ContactsFragment).map(f -> (ContactsFragment)f ).or(new ContactsFragment());
            dialerFragment = U.find(fragmentsList, frag -> frag instanceof DialerFragment).map(f -> (DialerFragment)f ).or(new DialerFragment());
        } else {
            contactsFragment = new ContactsFragment();
            dialerFragment = new DialerFragment();
        }

        final List<SelectableTab> fragments = new ArrayList<>(Arrays.asList(contactsFragment, dialerFragment));
        final List<String> tabTitles = Arrays.asList(getString(R.string.contacts), "");

        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabTitles.get(position);
            }

            @Override
            public Fragment getItem(int position) {
                return (Fragment) fragments.get(position);
            }
        };
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(2); //crazy with viewPager in case used with tablayout

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        Pair<Drawable, Drawable> dialerTabDrawables = getDialerTabDrawables();
        tabLayout.getTabAt(DIALER_TAB_INDEX).setIcon(dialerTabDrawables.first);
        tabLayout.getTabAt(DIALER_TAB_INDEX).setContentDescription(getString(R.string.dialer));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fragments.get(tab.getPosition()).onSelect();
                if (tab.getPosition() == DIALER_TAB_INDEX) {
                    tab.setIcon(dialerTabDrawables.second);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                fragments.get(tab.getPosition()).onUnSelect();
                if (tab.getPosition() == DIALER_TAB_INDEX) {
                    tab.setIcon(dialerTabDrawables.first);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private Pair<Drawable, Drawable> getDialerTabDrawables() {
        int tabSelectedColor = getThemeAttributeColor(android.R.attr.textColorPrimary, MainActivity.this);
        int tabUnSelectedColor = getThemeAttributeColor(android.R.attr.textColorSecondary, MainActivity.this);
        Drawable dialpadIconOnUnSelect = ContextCompat.getDrawable(this, R.drawable.dial_pad).mutate();
        Drawable dialpadIconOnSelect = ContextCompat.getDrawable(this, R.drawable.dial_pad).mutate();
        setColorFilterUsingColor(dialpadIconOnSelect, tabSelectedColor);
        setColorFilterUsingColor(dialpadIconOnUnSelect, tabUnSelectedColor);
        return Pair.create(dialpadIconOnUnSelect, dialpadIconOnSelect);
    }

    public void collapseSearchView() {
        if (searchItem != null)
            searchItem.collapseActionView(); // happens when app hasn't even got menu items callback
    }

    private void updateSortMenuItemTitle(MenuItem item) {
        if (item != null) {
            item.setTitle(shouldSortContactsByName(this) ? R.string.sort_by_date : R.string.sort_by_name);
        }
    }

/*
    public void hideBottomMenu() {
        if (bottomMenu == null) return;
        bottomMenu.setVisibility(GONE);
    }

    public void showBottomMenu() {
        if (bottomMenu == null || !shouldShowBottomMenu(this)) return;
        bottomMenu.setVisibility(VISIBLE);
    }
*/
}
