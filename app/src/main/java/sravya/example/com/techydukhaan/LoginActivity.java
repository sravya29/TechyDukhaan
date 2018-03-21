package sravya.example.com.techydukhaan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceActivityResult;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends SharedPref {

    public static final int GOOGLE_LOGIN_REQUEST_CODE = 1;
    static int a = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            AzureMobileServiceAdapter.Initialize(LoginActivity.this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        mClient = AzureMobileServiceAdapter.getInstance().getClient();
        mUserTable = mClient.getTable(User.class);

        String sender = (String) getIntent().getExtras().get("sender");

        if (sender != null) {
            if (sender.equals("splash")) {
                if (a == 0 && loadUserTokenCache(mClient)) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        }

    }

    public void authenticate(View v) {
        if (a == 0) {
            if (!loadUserTokenCache(mClient)) {
                ClearCache();
                mClient.login(MobileServiceAuthenticationProvider.Google, "techydukhaan1234", GOOGLE_LOGIN_REQUEST_CODE);
            } else {
                Toast.makeText(this, "Already logged in.", Toast.LENGTH_SHORT).show();
            }
        }
        else if (a == -1) {
            Toast.makeText(LoginActivity.this, "Restart app to re login.", Toast.LENGTH_SHORT).show();
        }
    }

    public void logout(View v) {

        Toast.makeText(LoginActivity.this, "Restart app to re login.", Toast.LENGTH_SHORT).show();

        a = -1;

        mClient.logout();
        ClearCache();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == GOOGLE_LOGIN_REQUEST_CODE) {
                MobileServiceActivityResult result = mClient.onActivityResult(data);
                if (result.isLoggedIn()) {
                    cacheUserToken(mClient.getCurrentUser());
                    search(mClient.getCurrentUser().getUserId());
                } else {
                    Toast.makeText(this, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void search(final String uid) {

        if (mClient == null) {
            return ;
        }

        final User me = new User();

        me.setName("");
        me.setCllg("");
        me.setAddr("");
        me.setPhNum("");
        me.setMail("");
        me.setUid(uid);

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final MobileServiceList<User> retUsers = getItemInTable(me);
                    if (retUsers == null)
                        addUser(uid);
                    else if(retUsers.size() == 0){
                        addUser(uid);
                    }
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

        };


        runAsyncTask(task);

        }

    private MobileServiceList<User> getItemInTable(User me) {
        try {
            return mUserTable.where()
                    .field("id").eq(me.getUid()).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addUser(String uid) {
        if (mClient == null) {
            return;
        }

        final User me = new User();

        me.setName("");
        me.setCllg("");
        me.setAddr("");
        me.setPhNum("");
        me.setMail("");
        me.setUid(uid);

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    addItemInTable(me);
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);

    }

    public User addItemInTable(User item) throws ExecutionException, InterruptedException {
        return mUserTable.insert(item).get();
    }

}
