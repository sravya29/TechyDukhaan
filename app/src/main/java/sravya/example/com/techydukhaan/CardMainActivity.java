package sravya.example.com.techydukhaan;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.TextView;

public class CardMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
         GridLayout mainGrid = (GridLayout)findViewById(R.id.mainGrid);

        setEvent(mainGrid);
    }


    private void setEvent (GridLayout mainGrid) {
        CardView cardView1 = (CardView) mainGrid.getChildAt(1);
        final String src1 = ((TextView)findViewById(R.id.tvCatRefBooks)).getText().toString();
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(CardMainActivity.this, ResultsActivity.class);
                intent1.putExtra("src", src1);
                startActivity(intent1);
            }
        });

        CardView cardView2 = (CardView) mainGrid.getChildAt(2);
        final String src2 = ((TextView)findViewById(R.id.tvCatStnry)).getText().toString();
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(CardMainActivity.this, ResultsActivity.class);
                intent2.putExtra("src", src2);
                startActivity(intent2);
            }
        });

        CardView cardView3 = (CardView) mainGrid.getChildAt(3);
        final String src3 = ((TextView)findViewById(R.id.tvCatEqmnts)).getText().toString();
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(CardMainActivity.this, ResultsActivity.class);
                intent3.putExtra("src", src3);
                startActivity(intent3);
            }
        });

        CardView cardView4 = (CardView) mainGrid.getChildAt(4);
        final String src4 = ((TextView)findViewById(R.id.tvCatGBs)).getText().toString();
        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent4 = new Intent(CardMainActivity.this, ResultsActivity.class);
                intent4.putExtra("src", src4);
                startActivity(intent4);
            }
        });

        CardView cardView5 = (CardView) mainGrid.getChildAt(5);
        final String src5 = ((TextView)findViewById(R.id.tvCatWns)).getText().toString();
        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent5 = new Intent(CardMainActivity.this, ResultsActivity.class);
                intent5.putExtra("src", src5);
                startActivity(intent5);
            }
        });

        CardView cardView6 = (CardView) mainGrid.getChildAt(6);
        final String src6 = ((TextView)findViewById(R.id.tvCatOthers)).getText().toString();
        cardView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent6 = new Intent(CardMainActivity.this, ResultsActivity.class);
                intent6.putExtra("src", src6);
                startActivity(intent6);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.card_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
