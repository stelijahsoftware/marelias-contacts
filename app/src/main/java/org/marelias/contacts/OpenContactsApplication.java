package org.marelias.contacts;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static org.marelias.contacts.utils.domain.AppShortcuts.addShortcutsIfNotAddedAlreadyAsync;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.multidex.MultiDexApplication;

import com.orm.SugarContext;

import org.marelias.contacts.actions.ContactsHouseKeeping;
import org.marelias.contacts.data.datastore.CallLogDataStore;
import org.marelias.contacts.data.datastore.ContactsDataStore;
import org.marelias.contacts.utils.CrashUtils;
import org.marelias.contacts.utils.DomainUtils;

public class OpenContactsApplication extends MultiDexApplication {

    public static final String MISSED_CALLS_CHANEL_ID = "6477";

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
        ContactsDataStore.init(getApplicationContext());
        CallLogDataStore.init(getApplicationContext());
        DomainUtils.init(getApplicationContext());
        createNotificationChannels();
        CrashUtils.setUpCrashHandler(getApplicationContext());
        new ContactsHouseKeeping(this).start();
        addShortcutsIfNotAddedAlreadyAsync(getApplicationContext());
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(MISSED_CALLS_CHANEL_ID, getString(R.string.notification_channel_missed_calls), IMPORTANCE_HIGH);
            channel.setDescription(getString(R.string.notification_channel_missed_calls_description));
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}
