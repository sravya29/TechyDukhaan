package sravya.example.com.techydukhaan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceException;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ProdDetailsActivity extends SharedPref {

    User seller = null;
    Product temp = null;
    TextView name, age, price, type, desc, ownrname, ownrcntct, ownradd, ownrinstn;
    ImageView photo;

    public void GetImage(final ImageView imageView, final Product currentItem) {
        final String image = currentItem.getImg();
        final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        final Handler handler = new Handler();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    long imageLength = 0;
                    ImageManager.GetImage(image, imageStream, imageLength);
                    handler.post(new Runnable() {
                        public void run() {
                            byte[] buffer = imageStream.toByteArray();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception ex) {
                    final String exceptionMessage = ex.getMessage();
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(ProdDetailsActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        th.start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        name = (TextView) findViewById(R.id.tvProdName);
        age = (TextView) findViewById(R.id.tvProdAge);
        price = (TextView) findViewById(R.id.tvProdPrice);
        type = (TextView) findViewById(R.id.tvProdType);
        desc = (TextView) findViewById(R.id.tvProdDsc);
        ownrname = (TextView) findViewById(R.id.ProdOwner);
        ownrcntct = (TextView) findViewById(R.id.ProdCntct);
        ownradd = (TextView) findViewById(R.id.ProdAdd);
        ownrinstn = (TextView) findViewById(R.id.ProdInstn);
        photo = (ImageView) findViewById(R.id.imgProd);

        try {
            AzureMobileServiceAdapter.Initialize(this);
        } catch (Exception e) {
            createAndShowDialog(e.getMessage(), "Reinitialising");
        }

        mClient = AzureMobileServiceAdapter.getInstance().getClient();
        mUserTable = mClient.getTable(User.class);

        try {
            temp = (Product) getIntent().getExtras().get("item");

            name.setText(temp.getName());
            age.setText(temp.getAge());
            price.setText(temp.getPrice());
            type.setText(temp.getCat());
            desc.setText(temp.getDesc());

            GetImage(photo, temp);
            refreshItemsFromTable(temp.getUid());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void refreshItemsFromTable(final String uid) {

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    seller = refreshItemsFromMobileServiceTable(uid);

                    if (seller != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ownrname.setText(seller.getName());
                                ownrcntct.setText(seller.getPhNum());
                                ownradd.setText(seller.getAddr());
                                ownrinstn.setText(seller.getCllg());
                            }
                        });
                    } else {
                        Toast.makeText(ProdDetailsActivity.this, "User not found", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (temp != null)
            refreshItemsFromTable(temp.getUid());
    }

}
