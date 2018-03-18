package sravya.example.com.techydukhaan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.startsWith;
import static sravya.example.com.techydukhaan.LoginActivity.SHAREDPREFFILE;
import static sravya.example.com.techydukhaan.LoginActivity.TOKENPREF;
import static sravya.example.com.techydukhaan.LoginActivity.USERIDPREF;

public class ResultsActivity extends AppCompatActivity {

    private MobileServiceClient mClient;
    private MobileServiceTable<Product> mProductTable;
    private MobileServiceTable<User> mUserTable;
    private ResultAdapter mAdapter;
    List<Product> ans = new ArrayList<>();
    String src;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        try {
            AzureMobileServiceAdapter.Initialize(this);
        } catch (Exception e) {
            createAndShowDialog(e.getMessage(), "Reinitialising");
        }

        mClient = AzureMobileServiceAdapter.getInstance().getClient();
        loadUserTokenCache(mClient);

        mProductTable = mClient.getTable(Product.class);
        mUserTable = mClient.getTable(User.class);

        ListView prodlv = (ListView) findViewById(R.id.lvResultProducts);
        mAdapter = new ResultAdapter(this, R.layout.resultprodslayout);
        mAdapter.setmActivity(this);
        prodlv.setAdapter(mAdapter);

        src = (String) getIntent().getExtras().get("src");
        refreshItemsFromTable(src);

    }

    private void refreshItemsFromTable(final String src) {

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<Product> results = refreshItemsFromMobileServiceTable(src);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (Product item : results) {
                                mAdapter.add(item);
                            }

                            if (results.size() == 0)
                                Toast.makeText(ResultsActivity.this, "No Products Found!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Refresh Error");
                }

                return null;
            }
        };

        if (mProductTable != null)
            runAsyncTask(task);

    }

    private synchronized List<Product> refreshItemsFromMobileServiceTable(String src) throws ExecutionException, InterruptedException {
        List<Product> listprods = mProductTable
                .where()
                .field("name").eq(src)
                .or()
                .field("cat").eq(src)
                .execute().get();


        List<User> listusers = mUserTable
                .where()
                .field("cllg").eq(src)
                .or()
                .field("addr").eq(src)
                .execute().get();

        for (User u : listusers) {
            List<Product> temp = mProductTable
                    .where()
                    .field("uid")
                    .eq(u.getUid())
                    .execute().get();
            listprods.addAll(temp);
        }

        ans.clear();

        if (mClient.getCurrentUser() != null) {
            for (Product p : listprods) {
                if (!(p.getUid().equals(mClient.getCurrentUser().getUserId())))
                    ans.add(p);
            }
        }

        Set<Product> hs = new HashSet<>();
        hs.addAll(ans);
        ans.clear();
        ans.addAll(hs);

        return ans;
    }

    private void createAndShowDialogFromTask(final Exception exception, final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, title);
            }
        });
    }

    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if (exception.getCause() != null) {
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (src != null)
            refreshItemsFromTable(src);
    }

    private void loadUserTokenCache(MobileServiceClient client) {
        client.setCurrentUser(null);
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        if ((prefs.getString(USERIDPREF, null) == null) || (prefs.getString(TOKENPREF, null) == null))
            return;

        MobileServiceUser user = new MobileServiceUser(prefs.getString(USERIDPREF, null));
        user.setAuthenticationToken(prefs.getString(TOKENPREF, null));
        client.setCurrentUser(user);
    }

}
