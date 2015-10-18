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
import android.widget.Toast;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.SearchListResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


public class LoginActivity extends Activity {



    static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
    static final int REQUEST_AUTHORIZATION = 1;
    static final int REQUEST_ACCOUNT_PICKER = 2;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    private static GoogleAccountCredential credential;
    private YouTube youtube;
    YouTube.Search.List search;

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

    Intent intent;
    private void goToMainActivity() {
        intent = new Intent(this, MainActivity.class);
        new Thread() {
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

//        youtube = new YouTube.Builder(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), credential)
//                .setApplicationName(getString(R.string.app_name)).build();
//
//        searchOnYoutube("video keywords");

    }

    SearchListResponse searchResponse;

    private void searchOnYoutube(final String keywords) {

        new Thread(){
            @Override
            public void run() {
                try {
                    search = youtube.search().list("id,snippet");
                    search.setType("video");
                    search.setMaxResults((long) 10);
                    search.setOauthToken(credential.getToken());
//                    search.setKey("AIzaSyAff-Ub7zxh7Ts1zpZC8T2HuzNmO3axF2c");
                    search.setQ(keywords);
                    searchResponse = search.execute();
                } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            availabilityException.getConnectionStatusCode());
                } catch (UserRecoverableAuthIOException userRecoverableException) {
                    startActivityForResult(
                            userRecoverableException.getIntent(), REQUEST_AUTHORIZATION);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (GoogleAuthException e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        updateVideosFound();
                        if (searchResponse != null) {
                            Toast.makeText(LoginActivity.this, "Searched", Toast.LENGTH_SHORT).show();
                        }
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
//            InsertPlaylistAsyncTask.run(this);
            Toast.makeText(this, "Success0", Toast.LENGTH_SHORT).show();
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
//                    AsyncLoadTasks.run(this);
                    Toast.makeText(this, "Success1", Toast.LENGTH_SHORT).show();
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
//                        AsyncLoadTasks.run(this);
                        Toast.makeText(this, "Success2", Toast.LENGTH_SHORT).show();
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
