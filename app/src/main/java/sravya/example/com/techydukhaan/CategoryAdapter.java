package sravya.example.com.techydukhaan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Category> {
    Context context;
    int resource;
    List<Category> catlist;

    public CategoryAdapter(@NonNull Context context, int resource, @NonNull List<Category> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.catlist = objects;
    }


    @Override
    public int getCount() {
        return catlist.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder")
        View view = LayoutInflater.from(context).inflate(resource, parent, false);

        ((ImageView)view.findViewById(R.id.imgCat)).
                setImageResource((catlist.get(position)).getImage());

        ((TextView)view.findViewById(R.id.tvCatName))
                .setText((catlist.get(position)).getName());

        return view;
    }
}
