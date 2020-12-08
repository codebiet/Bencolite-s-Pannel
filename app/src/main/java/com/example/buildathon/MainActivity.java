package com.example.buildathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buildathon.Fragments.DiscussFragment;
import com.example.buildathon.Fragments.HomeFragment;
import com.example.buildathon.Fragments.ProfileFragment;
import com.example.buildathon.Fragments.SearchFragment;
import com.example.buildathon.Utils.LoadingBar;
import com.example.buildathon.Utils.SessionManagment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.buildathon.Utils.Constants.KEY_NAME;
import static com.example.buildathon.Utils.Constants.KEY_PROFILE_IMG;
import static com.example.buildathon.Utils.Constants.KEY_TITLE;


public class MainActivity extends AppCompatActivity {

    LoadingBar loadingBar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Fragment fragment = null;
    Toolbar toolbar;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    SessionManagment sessionManagment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingBar = new LoadingBar(MainActivity.this);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawerLayout);
        sessionManagment = new SessionManagment(this);
        navigationView=findViewById(R.id.nav_view);
        View header = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        CircleImageView imageView = header.findViewById(R.id.imgDrawerprofile);
        TextView txtname = header.findViewById(R.id.txtDrawerName);
        TextView txtTitle = header.findViewById(R.id.txtDrawerTitle);
        if (sessionManagment.getUserDetails().get(KEY_PROFILE_IMG)!=null)
        {
            Glide.with(this).load(sessionManagment.getUserDetails().get(KEY_PROFILE_IMG)).into(imageView);
        }
        txtname.setText(sessionManagment.getUserDetails().get(KEY_NAME));
        txtTitle.setText(sessionManagment.getUserDetails().get(KEY_TITLE));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.menu_logout)
                {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START))
                        drawerLayout.closeDrawer(GravityCompat.START);
                    loadingBar.show();
                    FirebaseAuth.getInstance().signOut();
                    sessionManagment.logoutSession();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                    return true;
                }
                if (item.getItemId()==R.id.menu_post)
                {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START))
                        drawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(getApplicationContext(),MyPostActivity.class));
                }
                if (item.getItemId()==R.id.menu_liked)
                {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START))
                        drawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(getApplicationContext(),LikedPostsActivity.class));
                }
                return false;
            }
        });
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        if(savedInstanceState==null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new HomeFragment()).addToBackStack("home").commit();

        }

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_home:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new HomeFragment()).addToBackStack("home").commit();
                                break;
                            case R.id.menu_search:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new SearchFragment()).addToBackStack("search").commit();
                                break;
                            case R.id.menu_dash:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new ProfileFragment()).addToBackStack("profile").commit();
                                break;
                            case R.id.menu_discuss:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new DiscussFragment()).addToBackStack("discuss").commit();
                                break;
                        }
                        return true;
                    }
                });
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                bottomNavigationView.setSelectedItemId(R.id.menu_dash);
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new ProfileFragment()).addToBackStack("profile").commit();

            }
        });
    }

    public void loadFragment(Fragment fm, Bundle args){

        fm.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_fragment, fm)
                .addToBackStack(null).commit();
        HomeFragment home = new HomeFragment();
        Log.e("frag_pos", String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        View header = navigationView.getHeaderView(0);
        CircleImageView imageView = header.findViewById(R.id.imgDrawerprofile);
        TextView txtname = header.findViewById(R.id.txtDrawerName);
        TextView txtTitle = header.findViewById(R.id.txtDrawerTitle);
        Glide.with(this).load(sessionManagment.getUserDetails().get(KEY_PROFILE_IMG)).into(imageView);
        txtname.setText(sessionManagment.getUserDetails().get(KEY_NAME));
        txtTitle.setText(sessionManagment.getUserDetails().get(KEY_TITLE));
        super.onStart();
    }
}