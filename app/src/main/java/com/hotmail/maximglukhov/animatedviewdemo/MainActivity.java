package com.hotmail.maximglukhov.animatedviewdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        setupActionBar();
        setupNavigationDrawer();
    }

    private void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }
    }

    private void setupNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                MainActivity.this, mDrawerLayout, mToolbar,
                R.string.open_drawer, R.string.close_drawer);

        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        // Make navigation drawer icon sync animation with the navigation drawer.
        actionBarDrawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // Select the first time in the menu.
        Menu navigationViewMenu = mNavigationView.getMenu();
        this.onNavigationItemSelected(navigationViewMenu.getItem(0));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawers();

        Fragment contentFragment = null;
        switch (item.getItemId()) {
            case R.id.demo_checkmark:
                contentFragment = CheckMarkDemoFragment.newInstance();
                break;
            case R.id.demo_windowsxprogressbar:
                contentFragment = WindowsXProgressBarDemoFragment.newInstance();
                break;
        }

        if (contentFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            // Replace the current fragment with one corresponding to the selected item.
            fragmentTransaction.replace(R.id.frame, contentFragment);
            fragmentTransaction.commit();
        }

        return false;
    }
}
