package sravya.example.com.techydukhaan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import static sravya.example.com.techydukhaan.LoginActivity.SHAREDPREFFILE;
import static sravya.example.com.techydukhaan.LoginActivity.TOKENPREF;
import static sravya.example.com.techydukhaan.LoginActivity.USERIDPREF;

public class ResultAdapter extends ArrayAdapter<Product> implements Serializable {
    Context mContext;
    int mLayoutResourceId;
    View row;
    private MobileServiceClient mClient;
    private MobileServiceTable<Request> mResultTable;
    private ResultAdapter mAdapter;
    private  Activity mActivity;
    Object var = null;

    public ResultAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;

        mClient = AzureMobileServiceAdapter.getInstance().getClient();
        mResultTable = mClient.getTable(Request.class);

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

        ((TextView) row.findViewById(R.id.resprodname)).setText(currentItem.getName());
        ((TextView) row.findViewById(R.id.resprodprice)).setText(currentItem.getPrice());

        GetImage(((ImageView) row.findViewById(R.id.resprodimg)), currentItem);

        while (var == null) ;

        row.findViewById(R.id.btnRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadUserTokenCache(mClient)) {
                    addItem(currentItem.getPid(), mClient.getCurrentUser().getUserId());
                    //Intent i=new Intent(mActivity,ProductDetailActivity.class);
                    //i.putExtra("item",currentItem);
                    //mActivity.startActivity(i);
                } else {
                    Toast.makeText(mContext, "Please Login to view details.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return row;
    }

    public void addItem(final String pid, final String uid) {
        if (mClient == null) {
            return;
        }

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final Request item = new Request();
                    item.setPid(pid);
                    item.setUid(uid);
                    addItemInTable(item);
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(mContext, "Your request has been forwarded to owner", Toast.LENGTH_SHORT).show();
            }
        };

        runAsyncTask(task);

    }

    public Request addItemInTable(Request item) throws ExecutionException, InterruptedException {
        return mResultTable.insert(item).get();
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

    private boolean loadUserTokenCache(MobileServiceClient client) {
        SharedPreferences prefs = mActivity.getSharedPreferences(SHAREDPREFFILE, Context.MODE_PRIVATE);
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

}
