package edu.sjsu.cmpe277.lab2.mytube.app;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTubeScopes;

import java.io.IOException;
import java.util.Collections;


public class LoginActivity extends Activity {

    static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
    static final int REQUEST_AUTHORIZATION = 1;
    static final int REQUEST_ACCOUNT_PICKER = 2;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    private static GoogleAccountCredential credential;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Large sign-in
        ((SignInButton) findViewById(R.id.sign_in_button)).setSize(SignInButton.SIZE_WIDE);

        credential = GoogleAccountCredential.usingOAuth2(LoginActivity.this, Collections.singleton(YouTubeScopes.YOUTUBE));
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        // Set up button click listeners
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkGooglePlayServicesAvailable()) {
                    haveGooglePlayServices();
                }
            }
        });

        handler = new Handler();
    }

    private void goToMainActivity() {
        new Thread() {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            @Override
            public void run() {

                try {
                    intent.putExtra("TOKEN", credential.getToken());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                });
            }
        }.start();

    }

    private boolean checkGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }

    private void haveGooglePlayServices() {
        // check if there is already an account selected
        if (credential.getSelectedAccountName() == null) {
            // ask user to choose account
            chooseAccount();
        } else {
//            Toast.makeText(this, "Success0", Toast.LENGTH_SHORT).show();
            goToMainActivity();
        }
    }

    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    haveGooglePlayServices();
                } else {
                    checkGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == Activity.RESULT_OK) {
//                    Toast.makeText(this, "Success1", Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                } else {
                    chooseAccount();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
//                        Toast.makeText(this, "Success2", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    }
                }
                break;
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            public void run() {
                Dialog dialog =
                        GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, LoginActivity.this,
                                REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }
}
