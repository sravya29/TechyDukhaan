package sravya.example.com.techydukhaan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
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

public class MyProductsActivity extends AppCompatActivity {

    private MobileServiceClient mClient;
    private MobileServiceTable<Product> mProductTable;
    private MyProductAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_products);

        try {
            AzureMobileServiceAdapter.Initialize(this);
        } catch (Exception e) {
            createAndShowDialog(e.getMessage(), "Reinitialising");
        }

        mClient = AzureMobileServiceAdapter.getInstance().getClient();

        if (!loadUserTokenCache(mClient)) {
            Toast.makeText(this, "Please login to view your products.", Toast.LENGTH_SHORT).show();
        }

        else {

            loadUserTokenCache(mClient);

            mProductTable = mClient.getTable(Product.class);

            ListView prodlv = (ListView) findViewById(R.id.lvMyProducts);
            mAdapter = new MyProductAdapter(MyProductsActivity.this, R.layout.myproductslist_itemview);
            mAdapter.setmActivity(this);
            prodlv.setAdapter(mAdapter);

            ((FloatingActionButton) findViewById(R.id.btnAddProd)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MyProductsActivity.this, ProdAddActivity.class);
                    startActivity(i);
                }
            });

            refreshItemsFromTable();
        }

    }

    private boolean loadUserTokenCache(MobileServiceClient client){
        SharedPreferences prefs = getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
        String userId = prefs.getString(USERIDPREF, null);
        if (userId == null)
            return false;
        String token = prefs.getString(TOKENPREF, null);
        if (token == null)
            return false;

        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);
        client.setCurrentUser(user);

        return true;
    }

    private void refreshItemsFromTable() {

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<Product> results = refreshItemsFromMobileServiceTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (Product item : results) {
                                mAdapter.add(item);
                            }
                            if (results.size() == 0)
                                Toast.makeText(MyProductsActivity.this, "No Products Found!", Toast.LENGTH_SHORT).show();
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

    private List<Product> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mProductTable
                .where()
                .field("uid")
                .eq(mClient.getCurrentUser().getUserId())
                .execute().get();
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
        refreshItemsFromTable();
    }

}
