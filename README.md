# Mar-Elias OpenContacts for Android Forked from [https://gitlab.com/sultanahamer/OpenContacts](https://gitlab.com/sultanahamer/OpenContacts)

Privacy to your contacts.

# About
This app saves contacts in its own database seperate from android contacts. This way no other app would be able to access contacts. Can be used in place of your default phone(dialer) app.

We can export / import contacts from Android contacts app into this app.
Maintains call log as well coz Android call log app would not be able to show name of contact
Also shows the person name upon recieving call

# TODO:
X - put groups button on top (remove it from inside '...' list)
X - Remove floating button
- Remove call log
X - verify the cursor commit (removed, does not actually delete groups)

X - PUT ALL LICENCES BELOW IN APP

- make the bar on top a bottom bar, put groups button in it (https://gitlab.com/sultanahamer/OpenContacts/-/issues/266)
- Add goups icon next to contact which has already been added to a group
- Add category to display all names not added to a group

# License:
GPLv3. In addition to the licenses of the used libraries ./app/build.gradle, namely:
- com.googlecode.ez-vcard:ez-vcard:0.11.2 (<a href="https://mvnrepository.com/artifact/com.googlecode.ez-vcard/ez-vcard">BSD</a>)
- com.opencsv:opencsv:5.7.1 (<a href="https://mvnrepository.com/artifact/com.opencsv/opencsv">APACHE 2.0</a>)
- com.github.xyxyLiu:Edit-Spinner:1.1.0 (<a href="https://github.com/xyxyLiu/Edit-Spinner/blob/master/LICENSE">APACHE</a>)
- com.thomashaertel:multispinner:0.1.1@aar (<a href="https://github.com/thomashaertel/MultiSpinner/blob/master/LICENSE">MIT</a>)
- com.github.chennaione:sugar:1.5 (<a href="https://github.com/chennaione/sugar">MIT</a>)
- com.github.javadev:underscore:1.39 (<a href="https://github.com/javadev/underscore-java/blob/main/LICENSE">MIT</a>)
- io.michaelrocks:libphonenumber-android:8.13.7 (<a href="https://github.com/MichaelRocks/libphonenumber-android/blob/master/LICENSE.txt">APACHE</a>)

# Permissions:
1. android.permission.READ_PHONE_STATE
This permission is required because the app has a PhoneStateReceiver that listens for phone state changes.
The app shows caller ID information when someone calls, displaying the contact's name in an overlay. Without this permission, the app can't detect incoming calls or get the phone number to look up contact information.

2. android.permission.READ_CALL_LOG
This permission is used for the missed call notification feature:
The app waits for Android to write the call to the call log before showing missed call notifications. This ensures the notification appears after the system has properly recorded the call, preventing duplicate or premature notifications.

3. android.permission.SYSTEM_ALERT_WINDOW
This permission is essential for the caller ID overlay feature:
The app displays a floating caller ID window that appears over other apps and even when the screen is locked. This requires the SYSTEM_ALERT_WINDOW permission to draw over other content.

4. android.permission.POST_NOTIFICATIONS (send notifications about missed calls)

These permissions enable the app's core caller ID and call handling features:
READ_PHONE_STATE: Detects incoming calls and gets phone numbers
READ_CALL_LOG: Ensures proper timing for missed call notifications
SYSTEM_ALERT_WINDOW: Shows the floating caller ID overlay

Without these permissions, the app would lose its ability to:
Display caller information during incoming calls
Show missed call notifications
Provide the floating caller ID overlay feature

