package com.example.dotsboxes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    enum player{RED_PLAYER, BLUE_PLAYER};
    player p;
    int row, col, squareSum = 0, p1Points = 0, p2Points = 0;

    LinearLayout redPointsTexts;
    LinearLayout bluePointsTexts;
    TextView redPlayerPointsTextView, bluePlayerPointsTextView;
    ArrayList<Integer> arrayList;
    GridView gridView;
    DrawerLayout drawer;
    NavigationView navigationView;
    MediaPlayer mediaPlayer;

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

            releaseMediaPlayer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.two_players_menu_item);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Intent intent;
                switch (menuItem.getItemId()){
                    case R.id.easy_difficulty_menu_item:
                        intent = new Intent(MainActivity.this, EasyDifficultyActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.normal_difficulty_menu_item:
                        intent = new Intent(MainActivity.this, NormalDifficultyActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.hard_difficulty_menu_item:
                        intent = new Intent(MainActivity.this, HardDifficultyActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.very_hard_menu_item:
                        intent = new Intent(MainActivity.this, VeryHardDifficultyActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.settings_menu_item:
                        intent = new Intent(MainActivity.this, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.normal_vs_hard_menu_item:
                        intent = new Intent(MainActivity.this, NormalVsHardActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
                return true;
            }
        });

        gridView = (GridView) findViewById(R.id.grid_view);

        int size = 9;
        if(SettingsActivity.size != 0)
            size = SettingsActivity.size;
        row = size;
        col = size;
        gridView.setNumColumns(size);

        p = player.RED_PLAYER; // game starts with Player 1

        arrayList = new ArrayList<Integer>();

        resetBoard();

        BoardAdapter boardAdapter = new BoardAdapter(this, arrayList, -1, false);

        redPlayerPointsTextView = findViewById(R.id.red_player_points);
        bluePlayerPointsTextView = findViewById(R.id.blue_player_points);
        redPlayerPointsTextView.setText("0");
        bluePlayerPointsTextView.setText("0");

        redPointsTexts = findViewById(R.id.red_player_points_texts);
        bluePointsTexts = findViewById(R.id.blue_player_points_texts);

        redPointsTexts.setAlpha(1f);
        bluePointsTexts.setAlpha(0.5f);

        gridView.setAdapter(boardAdapter);

      /*  plusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(row < 17 && col < 17) {
                    p1Points = 0;
                    p2Points = 0;
                    squareSum = 0;
                    row += 2;
                    col += 2;
                    gridView.setNumColumns(col);
                    turnView.setBackgroundColor(Color.RED);
                    p = Player.RED_PLAYER;
                    resetBoard();
                }
                else
                    Toast.makeText(getApplicationContext(), "Max bored size", Toast.LENGTH_LONG).show();

            }
        });

        minusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(row > 7 && col > 7) {
                    p1Points = 0;
                    p2Points = 0;
                    squareSum = 0;
                    row -= 2;
                    col -= 2;
                    gridView.setNumColumns(col);
                    turnView.setBackgroundColor(Color.RED);
                    p = Player.RED_PLAYER;
                    resetBoard();
                }
                else
                    Toast.makeText(getApplicationContext(), "Min bored size", Toast.LENGTH_LONG).show();
            }
        });
       */

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(view.getAlpha() == 0.0f) {
                    view.setAlpha(1f);
                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.touch2);
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(onCompletionListener);

                    int numOfConqueredSquares = conquerBoxes(position);
                    squareSum += numOfConqueredSquares;
                    setNextTurn(numOfConqueredSquares);
                    checkWinner();
                }
                }
            });
        }



    public int CheckUpperSquare(int r, int c){
        if(r != 0){
            if(( gridView.getChildAt(turnToPosition(r - 1, c - 1))).getAlpha() == 1f
                && ( gridView.getChildAt(turnToPosition(r - 2, c))).getAlpha() == 1f
                && ( gridView.getChildAt(turnToPosition(r - 1, c + 1))).getAlpha() == 1f ){
                if(p == player.RED_PLAYER) {
                    p1Points++;
                    redPlayerPointsTextView.setText(Integer.toString(p1Points));
                    (gridView.getChildAt(turnToPosition(r - 1, c))).setBackgroundColor(Color.RED);
                }
                else if(p == player.BLUE_PLAYER) {
                    p2Points++;
                    bluePlayerPointsTextView.setText(Integer.toString(p2Points));
                    (gridView.getChildAt(turnToPosition(r - 1, c))).setBackgroundColor(Color.BLUE);
                }
                return 1;
            }
            }
        return 0;
        }

    public int CheackBottomSquare(int r, int c){
        if(r != row - 1){
            int a = turnToPosition(r + 1, c - 1);
            View view = gridView.getChildAt(turnToPosition(r + 1, c - 1));
            if(( gridView.getChildAt(turnToPosition(r + 1, c - 1))).getAlpha() == 1f
                    && ( gridView.getChildAt(turnToPosition(r + 2, c))).getAlpha() == 1f
                    && ( gridView.getChildAt(turnToPosition(r + 1, c + 1))).getAlpha() == 1f ) {
                if (p == player.RED_PLAYER) {
                    p1Points++;
                    redPlayerPointsTextView.setText(Integer.toString(p1Points));
                    (gridView.getChildAt(turnToPosition(r + 1, c))).setBackgroundColor(Color.RED);
                } else if (p == player.BLUE_PLAYER) {
                    p2Points++;
                    bluePlayerPointsTextView.setText(Integer.toString(p2Points));
                    (gridView.getChildAt(turnToPosition(r + 1, c))).setBackgroundColor(Color.BLUE);
                }
                return 1;
            }
            }
        return 0;
    }

    public int CheckRightSquare(int r, int c){
        if(c != col - 1){
            if(( gridView.getChildAt(turnToPosition(r - 1, c + 1))).getAlpha() == 1f
                    && ( gridView.getChildAt(turnToPosition(r, c + 2))).getAlpha() == 1f
                    && ( gridView.getChildAt(turnToPosition(r + 1, c + 1))).getAlpha() == 1f ){
                if(p == player.RED_PLAYER) {
                    p1Points++;
                    redPlayerPointsTextView.setText(Integer.toString(p1Points));
                    (gridView.getChildAt(turnToPosition(r, c + 1))).setBackgroundColor(Color.RED);
                }
                else if(p == player.BLUE_PLAYER) {
                    p2Points++;
                    bluePlayerPointsTextView.setText(Integer.toString(p2Points));
                    (gridView.getChildAt(turnToPosition(r, c + 1))).setBackgroundColor(Color.BLUE);
                }
                return 1;
            }
        }
        return 0;
    }

    public int CheackLeftSquare(int r, int c){
        if(c != 0){
            if(( gridView.getChildAt(turnToPosition(r - 1, c - 1))).getAlpha() == 1f
                    && ( gridView.getChildAt(turnToPosition(r, c - 2))).getAlpha() == 1f
                    && ( gridView.getChildAt(turnToPosition(r + 1, c - 1))).getAlpha() == 1f ){
                if(p == player.RED_PLAYER) {
                    p1Points++;
                    redPlayerPointsTextView.setText(Integer.toString(p1Points));
                    (gridView.getChildAt(turnToPosition(r, c - 1))).setBackgroundColor(Color.RED);
                }
                else if(p == player.BLUE_PLAYER) {
                    p2Points++;
                    bluePlayerPointsTextView.setText(Integer.toString(p2Points));
                    (gridView.getChildAt(turnToPosition(r, c - 1))).setBackgroundColor(Color.BLUE);
                }
                return 1;
            }
        }
        return 0;
    }

    public int turnToPosition(int r, int c){ return r * col + c; }

    public void resetBoard(){

        arrayList.clear();

        for (int i = 0; i < row; i++){

            for (int j = 0; j < col; j++){
                if(i % 2 == 0){
                    if(j % 2 == 0){
                        arrayList.add(R.drawable.dot);
                    }
                    else{
                        arrayList.add(R.drawable.horizontal_line);
                    }
                }
                else{
                    if (j % 2 == 0){
                        arrayList.add(R.drawable.vertical_line);
                    }
                    else{
                        arrayList.add(0);
                    }
                }
            }
        }

    }

    public void checkWinner(){

        if(squareSum == ((row - 1) / 2) * ((col - 1) / 2)){ // if all the squares in the board are conquered
            if(p1Points > p2Points)
                Toast.makeText(getApplicationContext(), "Red Player Wins!", Toast.LENGTH_LONG).show();
            if(p1Points < p2Points)
                Toast.makeText(getApplicationContext(), "Blue Player Wins!", Toast.LENGTH_LONG).show();
            if(p1Points == p2Points)
                Toast.makeText(getApplicationContext(), "Its a tie!", Toast.LENGTH_LONG).show();

        }
    }

    public void setNextTurn(int numOfConqueredSquares){

        if (numOfConqueredSquares == 0) {
            if (p == MainActivity.player.RED_PLAYER) {
                p = MainActivity.player.BLUE_PLAYER;
                redPointsTexts.setAlpha(0.5f);
                bluePointsTexts.setAlpha(1f);
            } else if (p == MainActivity.player.BLUE_PLAYER) {
                p = MainActivity.player.RED_PLAYER;
                bluePointsTexts.setAlpha(0.5f);
                redPointsTexts.setAlpha(1f);
            }
        }
    }

    public int conquerBoxes(int position){

        int numOfConqueredSquares = 0;
        int num = arrayList.get(position);
        int r = position / col, c = position % col;
        if (num == R.drawable.horizontal_line) {
            numOfConqueredSquares += CheckUpperSquare(r, c);
            numOfConqueredSquares += CheackBottomSquare(r, c);
        } else if (num == R.drawable.vertical_line) {
            numOfConqueredSquares += CheckRightSquare(r, c);
            numOfConqueredSquares += CheackLeftSquare(r, c);
        }

        return numOfConqueredSquares;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();;
    }

    private void releaseMediaPlayer(){
        // If the media Player is not null, then it may be currently playing a sound.
        if (mediaPlayer != null) {
            // Regardless of the current state of the media Player, release its resources
            // because we no longer need it.
            mediaPlayer.release();

            // Set the media Player back to null. For our code, we've decided that
            // setting the media Player to null is an easy way to tell that the media Player
            // is not configured to play an audio file at the moment.
            mediaPlayer = null;
        }
    }
}
