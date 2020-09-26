package com.example.dotsboxes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class BoardAdapter extends ArrayAdapter<Integer>{

    private int rand;
    private boolean humanFirst;
    public BoardAdapter(Context context, ArrayList<Integer> images, int rand, boolean humanFirst){

        super(context, 0, images);
        this.rand = rand;
        this.humanFirst = humanFirst;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null && getItem(position) == R.drawable.dot) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dot_board_item, parent, false);
        }
        else if(convertView == null && getItem(position) == R.drawable.horizontal_line){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.horizontal_line_board_item, parent, false);
        }
        else if(convertView == null && getItem(position) == R.drawable.vertical_line){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.vertical_line_board_item, parent, false);
        }
        else if(convertView == null && getItem(position) == 0){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.blank_board_item, parent, false);
        }

        int img = getItem(position);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.img);
        if(img != 0)
            imageView.setImageResource(img);
        if(img == R.drawable.horizontal_line) {
            imageView.setAlpha(0.0f);
        }
        if ( img == R.drawable.vertical_line ){
            imageView.setAlpha(0.0f);
        }

        if(position == rand && !humanFirst)
            imageView.setAlpha(1f);

        return convertView;
    }
}

