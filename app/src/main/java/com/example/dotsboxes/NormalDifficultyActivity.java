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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Random;

public class NormalDifficultyActivity extends AppCompatActivity {

    enum player {HUMAN, AI}

    player p;
    int row, col, rand;
    LinearLayout redPointsTexts;
    LinearLayout bluePointsTexts;
    TextView redPlayerPointsTextView, bluePlayerPointsTextView;
    boolean stam;
    Board mainBoard;
    ArrayList<Integer> board;
    ArrayList<Integer> lines;
    ArrayList<Integer> preConquredLines;
    GridView gridView;
    DrawerLayout drawer;
    NavigationView navigationView;
    Runnable runnable;
    MediaPlayer mediaPlayer;
    Random r;

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


        runnable = new Runnable() {
            @Override
            public void run() {
                aiMove();
                printNewBoard(false);
                checkWinner();
                setNextTurn();
                gridView.setEnabled(true);
            }
        };

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.normal_difficulty_menu_item);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Intent intent;
                switch (menuItem.getItemId()) {
                    case R.id.two_players_menu_item:
                        intent = new Intent(NormalDifficultyActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.easy_difficulty_menu_item:
                        intent = new Intent(NormalDifficultyActivity.this, EasyDifficultyActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.hard_difficulty_menu_item:
                        intent = new Intent(NormalDifficultyActivity.this, HardDifficultyActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.very_hard_menu_item:
                        intent = new Intent(NormalDifficultyActivity.this, VeryHardDifficultyActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.settings_menu_item:
                        intent = new Intent(NormalDifficultyActivity.this, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.normal_vs_hard_menu_item:
                        intent = new Intent(NormalDifficultyActivity.this, NormalVsHardActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
                return true;
            }
        });

        stam = true;

        p = player.HUMAN;

        gridView = (GridView) findViewById(R.id.grid_view);

        int size = 9;
        if(SettingsActivity.size != 0)
            size = SettingsActivity.size;
        row = size;
        col = size;
        gridView.setNumColumns(size);


        board = new ArrayList<Integer>();

        lines = new ArrayList<Integer>();

        preConquredLines = new ArrayList<Integer>();


        r = new Random();
        rand = r.nextInt(row * col);
        if(rand % 2 == 0) {
            if (rand > 0)
                rand--;
            else
                rand++;
        }

        resetBoard();

        BoardAdapter boardAdapter = new BoardAdapter(this, board, rand, SettingsActivity.imFirst);

        redPlayerPointsTextView = findViewById(R.id.red_player_points);
        bluePlayerPointsTextView = findViewById(R.id.blue_player_points);
        redPlayerPointsTextView.setText("0");
        bluePlayerPointsTextView.setText("0");

        redPointsTexts = findViewById(R.id.red_player_points_texts);
        bluePointsTexts = findViewById(R.id.blue_player_points_texts);

        redPointsTexts.setAlpha(1f);
        bluePointsTexts.setAlpha(0.5f);

        gridView.setAdapter(boardAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (view.getAlpha() == 0.0f) {
                    gridView.setEnabled(false);
                    mainBoard.aiLineMove(position);
                    printNewBoard(true);
                    if (!checkWinner() && mainBoard.turn == Board.Player.AI) {
                        setNextTurn();
                        (new Thread(runnable)).start();
                    }
                    else
                        gridView.setEnabled(true);
                }
            }
        });
    }

    public void setNextTurn(){

        if(mainBoard.turn == Board.Player.AI){
            redPointsTexts.setAlpha(0.5f);
            bluePointsTexts.setAlpha(1f);
        }
        else{
            redPointsTexts.setAlpha(1f);
            bluePointsTexts.setAlpha(0.5f);
        }
    }

    public void printNewBoard(boolean isHuman){

        ArrayList<Integer> newBoardChanges = mainBoard.getOutput();
        for (int i = 0; i < newBoardChanges.size(); i++){

            final View view = gridView.getChildAt(newBoardChanges.get(i));
            if(board.get(newBoardChanges.get(i)) == R.drawable.horizontal_line || board.get(newBoardChanges.get(i)) == R.drawable.vertical_line){
                if(!isHuman)
                    waitBeforeDisplay(700);
                view.setAlpha(1f);
                clickSound();
            }
            else if(board.get(newBoardChanges.get(i)) == 0){
                if(isHuman){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setBackgroundColor(Color.RED);
                            redPlayerPointsTextView.setText(Integer.toString(Integer.parseInt(redPlayerPointsTextView.getText().toString()) + 1));
                        }
                    });
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setBackgroundColor(Color.BLUE);
                            bluePlayerPointsTextView.setText(Integer.toString(Integer.parseInt(bluePlayerPointsTextView.getText().toString()) + 1));
                        }
                    });
                }
            }
        }
        newBoardChanges.clear();
    }

    public ArrayList<Board.FakeBoard> generateToFakeBoard(ArrayList<Integer> board){

        ArrayList<Board.FakeBoard> result = new ArrayList<>();
        for(int i = 0; i < board.size(); i++){
            if(board.get(i) == R.drawable.horizontal_line)
                result.add(Board.FakeBoard.EMPTY_LINE);
            else if(board.get(i) == R.drawable.vertical_line)
                result.add(Board.FakeBoard.EMPTY_LINE);
            else if(board.get(i) == R.drawable.dot)
                result.add(Board.FakeBoard.DOT);
            else if(board.get(i) == 0)
                result.add(Board.FakeBoard.BLANK);
        }
        return result;
    }

    public void clickSound() {

        mediaPlayer = MediaPlayer.create(NormalDifficultyActivity.this, R.raw.touch2);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(onCompletionListener);
    }


    public void resetBoard() {

        board.clear();

        for (int i = 0; i < row; i++) {

            for (int j = 0; j < col; j++) {
                if (i % 2 == 0) {
                    if (j % 2 == 0) {
                        board.add(R.drawable.dot);
                    } else {
                        board.add(R.drawable.horizontal_line);
                    }
                } else {
                    if (j % 2 == 0) {
                        board.add(R.drawable.vertical_line);
                    } else {
                        board.add(0);
                    }
                }
            }
        }

        mainBoard = new Board(generateToFakeBoard(board), Board.Player.HUMAN, col, 0, 0);

        if (!SettingsActivity.imFirst) {
            mainBoard.getMoves().remove(Integer.valueOf(rand));
            mainBoard.getBoard().set(rand, Board.FakeBoard.SELECTED_LINE);
        }

    }

    public boolean checkWinner() {

        if (mainBoard.checkWinner()) { // if all the squares in the board are conquered
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mainBoard.humanPoints > mainBoard.computerPoints)
                        Toast.makeText(getApplicationContext(), "Red Player Wins!", Toast.LENGTH_LONG).show();
                    if (mainBoard.humanPoints < mainBoard.computerPoints)
                        Toast.makeText(getApplicationContext(), "Blue Player Wins!", Toast.LENGTH_LONG).show();
                    if (mainBoard.humanPoints == mainBoard.computerPoints)
                        Toast.makeText(getApplicationContext(), "Its a tie!", Toast.LENGTH_LONG).show();
                }
            });
            return true;
        }
        return false;
    }



    public void aiMove() {

        if (mainBoard.canConquer()) {
            mainBoard.pathMove();
        }
        else
            mainBoard.minDamageMove();
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void waitBeforeDisplay(int miliSeconds) {

        try {
            Thread.sleep(miliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
