package com.example.dotsboxes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity {

    static int size;
    DrawerLayout drawer;
    NavigationView navigationView;
    CheckBox checkBox;
    Button minusButton, plusButton;
    TextView boardSize;
    static boolean imFirst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        size = 9;

        checkBox = (CheckBox) findViewById(R.id.first_move_check_box);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                imFirst = isChecked;
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout_settings);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view_settings);
        navigationView.setCheckedItem(R.id.settings_menu_item);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Intent intent;
                switch (menuItem.getItemId()){
                    case R.id.two_players_menu_item:
                        intent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(intent);
                        if(drawer.isDrawerOpen(GravityCompat.START))
                            drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.easy_difficulty_menu_item:
                        intent = new Intent(SettingsActivity.this, EasyDifficultyActivity.class);
                        startActivity(intent);
                        if(drawer.isDrawerOpen(GravityCompat.START))
                            drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.normal_difficulty_menu_item:
                        intent = new Intent(SettingsActivity.this, NormalDifficultyActivity.class);
                        intent.putExtra("checkBox", checkBox.isChecked());
                        startActivity(intent);
                        if(drawer.isDrawerOpen(GravityCompat.START))
                            drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.hard_difficulty_menu_item:
                        intent = new Intent(SettingsActivity.this, HardDifficultyActivity.class);
                        intent.putExtra("checkBox", checkBox.isChecked());
                        startActivity(intent);
                        if(drawer.isDrawerOpen(GravityCompat.START))
                            drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.very_hard_menu_item:
                        intent = new Intent(SettingsActivity.this, VeryHardDifficultyActivity.class);
                        intent.putExtra("checkBox", checkBox.isChecked());
                        startActivity(intent);
                        if(drawer.isDrawerOpen(GravityCompat.START))
                            drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.normal_vs_hard_menu_item:
                        intent = new Intent(SettingsActivity.this, NormalVsHardActivity.class);
                        intent.putExtra("checkBox", checkBox.isChecked());
                        startActivity(intent);
                        if(drawer.isDrawerOpen(GravityCompat.START))
                            drawer.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
        });

        minusButton = (Button) findViewById(R.id.minus_button);
        plusButton = (Button) findViewById(R.id.plus_button);
        boardSize = (TextView) findViewById(R.id.board_size);

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(size > 7) {
                    size -= 2;
                    boardSize.setText(String.valueOf(size / 2) + " X " + String.valueOf(size / 2));
                }
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(size < 13) {
                    size += 2;
                    boardSize.setText(String.valueOf(size / 2) + " X " + String.valueOf(size / 2));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.settings_menu_item);
    }
}
