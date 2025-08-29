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
