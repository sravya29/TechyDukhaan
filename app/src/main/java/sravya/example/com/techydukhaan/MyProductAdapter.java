package sravya.example.com.techydukhaan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class MyProductAdapter extends ArrayAdapter<Product> implements Serializable {
    Context mContext;
    int mLayoutResourceId;
    View row;
    private MobileServiceClient mClient;
    private MobileServiceTable<Product> mProductTable;
    private MyProductAdapter mAdapter;
    private  Activity mActivity;
    Object var = null;

    public MyProductAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;

        mClient = AzureMobileServiceAdapter.getInstance().getClient();
        mProductTable = mClient.getTable(Product.class);

        mAdapter = this;
    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void GetImage(final ImageView imageView, final Product currentItem){
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
                    var = 1;
                }
                catch(Exception ex) {
                    final String exceptionMessage = ex.getMessage();
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(mActivity, exceptionMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }});
        th.start();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        row = convertView;

        final Product currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);

        ((TextView) row.findViewById(R.id.myprodname)).setText(currentItem.getName());
        ((TextView) row.findViewById(R.id.myprodprice)).setText(currentItem.getPrice());

        GetImage(((ImageView) row.findViewById(R.id.myprodimg)), currentItem);

        while (var == null);

        row.findViewById(R.id.delbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(currentItem);
            }
        });

        row.findViewById(R.id.editbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, UpdateProductActivity.class);
                i.putExtra("item", currentItem);
                mActivity.startActivity(i);
            }
        });

        return row;
    }

    public void deleteItem(final Product item) {
        if (mClient == null) {
            return;
        }

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    deleteItemInTable(item);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.remove(item);
                        }
                    });

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);

    }

    public void deleteItemInTable(Product item) throws ExecutionException, InterruptedException {
        mProductTable.delete(item);
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void createAndShowDialogFromTask(final Exception exception, String title) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }

    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

}
