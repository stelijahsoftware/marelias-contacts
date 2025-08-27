package org.marelias.contacts.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.underscore.U;

import java.util.List;

import ezvcard.VCard;
import org.marelias.contacts.R;
import org.marelias.contacts.data.datastore.VCardImporterAsyncTask;
import org.marelias.contacts.utils.CrashUtils;

public class ImportVcardActivity extends AppCompatActivity {

    private VCardImporterAsyncTask parser;
    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissionGranted()) {
                parser.execute();
            } else {
                showPermissionDeniedDialog();
            }
        }
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.error)
            .setMessage(R.string.could_not_process_import_without_storage_permission)
            .setNeutralButton(R.string.okay, null)
            .setOnDismissListener(dialog -> finish())
            .create()
            .show();
    }

    private ProgressBar progressBarComponent;
    private TextView textView_vCardsIgnored, textView_vCardsImported;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_vcard);
        progressBarComponent = findViewById(R.id.progressBar_vCard_Import);
        textView_vCardsIgnored = findViewById(R.id.textview_vcards_ignored);
        textView_vCardsImported = findViewById(R.id.textview_vcards_imported);
        progressBarComponent.setIndeterminate(false);
        progressBarComponent.setProgress(0);
        progressBarComponent.setVisibility(View.VISIBLE);

        Uri uri = getIntent().getData();
        if (uri == null) {
            showErrorDialog("No file URI provided");
            return;
        }

        parser = new VCardImporterAsyncTask(uri, new VCardImporterAsyncTask.ImportProgressListener() {
            @Override
            public void onTotalNumberOfCardsToBeImportedDetermined(int totalNumberOfCards) {
                progressBarComponent.setMax(totalNumberOfCards);
            }

            @Override
            public void onNumberOfCardsProcessedUpdate(int imported, int ignored) {
                progressBarComponent.setProgress(imported + ignored);
                textView_vCardsImported.setText(getString(R.string.total_cards_imported, imported));
                textView_vCardsIgnored.setText(getString(R.string.total_cards_ignored, ignored));
            }

            @Override
            public void onFinish(List<Pair<VCard, Throwable>> vCardsAndTheirExceptions) {
                progressBarComponent.setProgress(progressBarComponent.getMax());
                if (vCardsAndTheirExceptions.isEmpty()) return;
                if (vCardsAndTheirExceptions.get(0).first == null) {
                    finish();
                    return;
                }
                View reportErrorsButton = findViewById(R.id.report_errors);
                reportErrorsButton.setVisibility(View.VISIBLE);
                reportErrorsButton.setOnClickListener(v -> CrashUtils.reportError(formatImportErrorsAsString(vCardsAndTheirExceptions), ImportVcardActivity.this));
            }
        }, this);

        new AlertDialog.Builder(this)
            .setTitle(R.string.import_contacts)
            .setMessage(R.string.do_you_want_to_import)
            .setPositiveButton(R.string.okay, (x, y) -> {
                if (canAccessFile(uri)) {
                    parser.execute();
                } else {
                    requestPermissionIfNeeded();
                }
            })
            .setOnCancelListener(x -> finish())
            .show();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
            .setTitle(R.string.error)
            .setMessage(message)
            .setNeutralButton(R.string.okay, null)
            .setOnDismissListener(dialog -> finish())
            .create()
            .show();
    }

    private String formatImportErrorsAsString(List<Pair<VCard, Throwable>> vCardsAndTheirExceptions) {
        return U.reduce(vCardsAndTheirExceptions, (accumulatingStringBuffer, currentvCardAndException) ->
            accumulatingStringBuffer.append(currentvCardAndException.first.write())
                .append("\n\n")
                .append(Log.getStackTraceString(currentvCardAndException.second))
                .append("\n\n\n\n"), new StringBuffer()).toString();
    }

    /**
     * Check if we can access the file based on the URI scheme and Android version
     */
    private boolean canAccessFile(Uri uri) {
        if (uri == null) {
            Log.w("ImportVcardActivity", "URI is null");
            return false;
        }

        String scheme = uri.getScheme();
        Log.d("ImportVcardActivity", "File URI scheme: " + scheme + ", URI: " + uri);

        // For content:// URIs (Storage Access Framework), we can access them directly
        if ("content".equals(scheme)) {
            Log.d("ImportVcardActivity", "Content URI detected - should be accessible");
            return true;
        }

        // For file:// URIs, we need to check permissions based on Android version
        if ("file".equals(scheme)) {
            // For Android 11+ (API 30+), file:// URIs are restricted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Log.d("ImportVcardActivity", "File URI access restricted on Android 11+ (API " + Build.VERSION.SDK_INT + ")");
                return false;
            }

            // For Android 6+ (API 23+), we need runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean hasPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                Log.d("ImportVcardActivity", "File URI on Android " + Build.VERSION.SDK_INT + " - permission granted: " + hasPermission);
                return hasPermission;
            }

            // For older versions, permission is granted at install time
            Log.d("ImportVcardActivity", "File URI on older Android version - permission granted at install time");
            return true;
        }

        Log.w("ImportVcardActivity", "Unknown URI scheme: " + scheme);
        return false;
    }

    private void requestPermissionIfNeeded() {
        Uri uri = getIntent().getData();
        if (uri == null) {
            showErrorDialog("No file URI provided");
            return;
        }

        String scheme = uri.getScheme();

        if ("file".equals(scheme)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // For Android 11+, file:// URIs are restricted, suggest using SAF
                new AlertDialog.Builder(this)
                    .setTitle("File Access Restricted")
                    .setMessage("On Android 11 and later, direct file access is restricted. Please use the file picker or share menu to select your vCard file instead of opening it directly from a file manager.")
                    .setNeutralButton(R.string.okay, null)
                    .setOnDismissListener(dialog -> finish())
                    .create()
                    .show();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // For Android 6-10, request READ_EXTERNAL_STORAGE permission
                new AlertDialog.Builder(this)
                    .setTitle(R.string.grant_storage_permission)
                    .setMessage(R.string.grant_storage_permisson_detail)
                    .setNeutralButton(R.string.okay, null)
                    .setOnDismissListener(dialog -> requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE))
                    .create()
                    .show();
            }
        } else if ("content".equals(scheme)) {
            // For content URIs, we should be able to access them directly
            // If we can't, it might be a permission issue with the content provider
            Log.w("ImportVcardActivity", "Cannot access content URI: " + uri);
            showErrorDialog("Cannot access the selected file. Please try selecting it again.");
        }
    }

    /**
     * Check if we have the necessary permissions to proceed
     */
    private boolean permissionGranted() {
        Uri uri = getIntent().getData();
        if (uri == null) return false;

        return canAccessFile(uri);
    }

}
