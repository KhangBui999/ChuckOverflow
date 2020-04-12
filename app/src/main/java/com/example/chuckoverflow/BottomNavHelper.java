package com.example.chuckoverflow;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavHelper {

    private Activity activity;
    private MenuItem item;

    public BottomNavigationView getBottomNav(Activity activity, int itemId) {
        this.activity = activity;
        BottomNavigationView mNavMenu = activity.findViewById(itemId); //links xml element
        mNavMenu.getMenu().clear(); //clears default BottomNavigationView element
        mNavMenu.inflateMenu(R.menu.bottom_nav_menu); //sets BottomNavigationView layout
        mNavMenu.setBackgroundResource(R.color.colorNavBackground); //changes color of navigation

        mNavMenu.setItemTextColor(ContextCompat.getColorStateList(activity,
                R.color.nav_menu_selector)); //sets a ColorStateList for selected icons

        mNavMenu.setItemIconTintList(ContextCompat.getColorStateList(activity,
                R.color.nav_menu_selector)); //sets a ColorStateList for selected icons

        if(activity instanceof SavedActivity) {
            mNavMenu.setSelectedItemId(R.id.menu_saved);
        }

        //Sets the listener for each icon/button
        mNavMenu.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.menu_home:
                    launchMainActivity();
                    item.setCheckable(true);
                    return true;
                case R.id.menu_saved:
                    launchListActivity();
                    item.setCheckable(true);
                    return true;
                case R.id.menu_settings:
                    launchSettingActivity();
                    item.setCheckable(true);
                    return true;
            }
            return false;
        });

        return mNavMenu;
    }

    private void launchMainActivity() {
        if(!(activity instanceof MainActivity)) {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    private void launchListActivity() {
        if(!(activity instanceof SavedActivity)) {
            Intent intent = new Intent(activity, SavedActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    private void launchSettingActivity() {

    }
}
