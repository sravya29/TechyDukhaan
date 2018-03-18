package sravya.example.com.techydukhaan;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    String[] types = {"REFERENCE BOOKS", "STATIONARY", "EQUIPMENTS", "GUIDE BOOKS", "WRITTEN NOTES", "OTHERS"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        List<Category> lst = new ArrayList<Category>();

        lst.add(new Category(R.drawable.books, types[0]));
        lst.add(new Category(R.drawable.electronics, types[1]));
        lst.add(new Category(R.drawable.civil, types[2]));
        lst.add(new Category(R.drawable.software, types[3]));
        lst.add(new Category(R.drawable.mechanical, types[4]));
        lst.add(new Category(R.drawable.civil, types[5]));

        ListView catlv = (ListView) findViewById(R.id.lvCat);
        CategoryAdapter adap = new CategoryAdapter(MainActivity.this, R.layout.cat_item, lst);
        catlv.setAdapter(adap);

        catlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String src = ((TextView)view.findViewById(R.id.tvCatName)).getText().toString();

                Intent in = new Intent(MainActivity.this, ResultsActivity.class);
                in.putExtra("src", src);
                startActivity(in);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
        } else if (id == R.id.nav_my_products) {
            startActivity(new Intent(MainActivity.this, MyProductsActivity.class));
        } else if (id == R.id.nav_logout) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void RequestItem(View v){
        EditText search = (EditText) findViewById(R.id.search);
        String src = search.getText().toString();
        search.setText("");

        Intent i = new Intent(this, ResultsActivity.class);
        i.putExtra("src", src);
        startActivity(i);
    }
}
