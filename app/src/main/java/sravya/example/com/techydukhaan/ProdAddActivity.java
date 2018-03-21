package sravya.example.com.techydukhaan;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class ProdAddActivity extends SharedPref {
    Spinner protype, sage;
    Button upload, submit;
    EditText descript, price, name;
    ImageView pic;
    private Uri imageUri;
    String imageName = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_add);

        try {
            AzureMobileServiceAdapter.Initialize(this);
        } catch (Exception e) {
            createAndShowDialog(e.getMessage(), "Error");
        }

        try {

            mClient = AzureMobileServiceAdapter.getInstance().getClient();
            mProductTable = mClient.getTable(Product.class);

            protype = (Spinner) findViewById(R.id.cat);
            sage = (Spinner) findViewById(R.id.sage);
            upload = (Button) findViewById(R.id.btnUpload);
            submit = (Button) findViewById(R.id.btnSubmit);
            descript = (EditText) findViewById(R.id.descript);
            price = (EditText) findViewById(R.id.price);
            name = (EditText) findViewById(R.id.etProdName);
            pic = (ImageView) findViewById(R.id.pic);


            ArrayAdapter<String> adapter = new ArrayAdapter<>(ProdAddActivity.this, android.R.layout.simple_spinner_dropdown_item, types);
            protype.setAdapter(adapter);

            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(ProdAddActivity.this, android.R.layout.simple_spinner_dropdown_item, ages);
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

    private void UploadImage(){
        try {
            final InputStream imageStream = getContentResolver().openInputStream(this.imageUri);
            final int imageLength = imageStream.available();
            final Handler handler = new Handler();
            Thread th = new Thread(new Runnable() {
                public void run() {
                    try {
                        imageName = ImageManager.UploadImage(imageStream, imageLength);
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(ProdAddActivity.this, "Image Uploaded Successfully. Name = "
                                        + imageName, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    catch(Exception ex) {
                        final String exceptionMessage = ex.getMessage();
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(ProdAddActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
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

    public void addItem(View view) {
        if (mClient == null) {
            return;
        }

        UploadImage();

        while (imageName == null);


        final Product item = new Product(imageName, ages[sage.getSelectedItemPosition()], types[protype.getSelectedItemPosition()],
                name.getText().toString(), descript.getText().toString(), "", "", price.getText().toString());

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final Product retProd = addItemInTable(item);
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

    public Product addItemInTable(Product item) throws ExecutionException, InterruptedException {
        return mProductTable.insert(item).get();
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