package sravya.example.com.techydukhaan;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class UpdateProductActivity extends Activity {
    private MobileServiceClient mClient;
    private MobileServiceTable<Product> mProductTable;
    Spinner protype, sage;
    Button upload, edit;
    EditText descript, price, name;
    ImageView pic;
    Product item;
    private Uri imageUri;
    private int imageLength;
    String imageName = null;
    String[] types = {"REFERENCE BOOKS", "STATIONARY", "EQUIPMENTS", "GUIDE BOOKS", "WRITTEN NOTES", "OTHERS"};
    String[] ages = {"< 2 months", "2 months", "4 months", "8 months", "1 year", "> 1 year"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        try {

            item = (Product) getIntent().getExtras().get("item");

            mClient = AzureMobileServiceAdapter.getInstance().getClient();
            mProductTable = mClient.getTable(Product.class);

            protype = (Spinner) findViewById(R.id.editCat);
            sage = (Spinner) findViewById(R.id.editsage);
            upload = (Button) findViewById(R.id.editBtnUpload);
            edit = (Button) findViewById(R.id.btnedit);

            descript = (EditText) findViewById(R.id.editdescript);
            price = (EditText) findViewById(R.id.editprice);
            name = (EditText) findViewById(R.id.editProdName);
            pic = (ImageView) findViewById(R.id.editpic);

            descript.setText(item.getDesc());
            price.setText(item.getPrice());
            name.setText(item.getName());


            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
            protype.setAdapter(adapter);

            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ages);
            sage.setAdapter(adapter1);

            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
                }
            });


        } catch (Exception e) {
            createAndShowDialog(e, "Error");
        }

    }

    private void UploadImage(final String name){
        try {
            final InputStream imageStream = getContentResolver().openInputStream(this.imageUri);
            final int imageLength = imageStream.available();
            final Handler handler = new Handler();
            Thread th = new Thread(new Runnable() {
                public void run() {
                    try {
                        ImageManager.DeleteImage(name);
                        imageName = ImageManager.UploadImage(imageStream, imageLength);
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(UpdateProductActivity.this, "Image Uploaded Successfully. Name = "
                                        + imageName, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    catch(Exception ex) {
                        final String exceptionMessage = ex.getMessage();
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(UpdateProductActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }});
            th.start();
        }
        catch(Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void updateItem(View view) {
        if (mClient == null) {
            return;
        }

        UploadImage(item.getImg());

        while (imageName == null);


        item.setName(name.getText().toString());
        item.setAge(ages[sage.getSelectedItemPosition()]);
        item.setCat(types[protype.getSelectedItemPosition()]);
        item.setDesc(descript.getText().toString());
        item.setPrice(price.getText().toString());
        item.setImg(imageName);


        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final Product retProd = updateItemInTable(item);
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

    public Product updateItemInTable(Product item) throws ExecutionException, InterruptedException {
        return mProductTable.update(item).get();
    }

    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    this.imageUri = imageReturnedIntent.getData();
                    this.pic.setImageURI(this.imageUri);
                    this.upload.setEnabled(true);
                }
        }
    }

}