package sravya.example.com.techydukhaan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static sravya.example.com.techydukhaan.LoginActivity.SHAREDPREFFILE;
import static sravya.example.com.techydukhaan.LoginActivity.TOKENPREF;
import static sravya.example.com.techydukhaan.LoginActivity.USERIDPREF;

public class EditProfileActivity extends SharedPref {

    EditText name;
    EditText cllg;
    EditText addr;
    EditText phNum;
    EditText mail;
    Button update;
    User me = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        try {
            AzureMobileServiceAdapter.Initialize(this);
        } catch (Exception e) {
            createAndShowDialog(e.getMessage(), "Reinitialising");
        }

        mClient = AzureMobileServiceAdapter.getInstance().getClient();
        init();

        if (!loadUserTokenCache(mClient))
            Toast.makeText(this, "Please login to view your profile.", Toast.LENGTH_SHORT).show();
        else
            refreshItemsFromTable(mClient.getCurrentUser().getUserId());

    }

    private void init() {
        name = (EditText) findViewById(R.id.userName);
        cllg = (EditText) findViewById(R.id.college);
        addr = (EditText) findViewById(R.id.address);
        phNum = (EditText) findViewById(R.id.phone);
        mail = (EditText) findViewById(R.id.mail);
        update = (Button) findViewById(R.id.btnUpdateProfile);
        mUserTable = mClient.getTable(User.class);
    }

    public void updateUser(View view) {
        if (mClient == null) {
            return;
        }

        refreshItemsFromTable(mClient.getCurrentUser().getUserId());

        while (me == null) ;

        me.setName(name.getText().toString());
        me.setCllg(cllg.getText().toString());
        me.setAddr(addr.getText().toString());
        me.setPhNum(phNum.getText().toString());
        me.setMail(mail.getText().toString());

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final User retProd = updateItemInTable(me);
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                finish();
            }
        };

        runAsyncTask(task);

    }

    public User updateItemInTable(User item) throws ExecutionException, InterruptedException {
        return mUserTable.update(item).get();
    }

    private void refreshItemsFromTable(final String uid) {

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    me = refreshItemsFromMobileServiceTable(uid);

                    if (me != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                name.setText(me.getName());
                                cllg.setText(me.getCllg());
                                addr.setText(me.getAddr());
                                phNum.setText(me.getPhNum());
                                mail.setText(me.getMail());
                            }
                        });
                    } else {
                        Toast.makeText(EditProfileActivity.this, "No user Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "User Error");
                }

                return null;
            }
        };

        if (mUserTable != null)
            runAsyncTask(task);

    }

    private User refreshItemsFromMobileServiceTable(String uid) throws ExecutionException, InterruptedException {
        return mUserTable.where().field("id").eq(uid).execute().get().get(0);
    }

}
