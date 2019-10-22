package com.example.sse.customlistview_sse;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;

public class SortByTitle extends MyCustomAdapter {
    private int ints[];
    private String title[];

    public SortByTitle(Context aContext, SharedPreferences sp) {
        super(aContext, sp);
        title = getEpisodes().clone();
        ints = new int[title.length];
        for(int i = 0; i < ints.length; i++){
            ints[i] = i;
        }
        for(int i = 0; i < ints.length; i++){
            for(int j = 0; j < ints.length - 1; j++){
                if( title[j].compareTo(title[j+1]) > 0 ){
                    String str = title[j];
                    title[j] = title[j+1];
                    title[j+1] = str;
                    int temp = ints[j];
                    ints[j] = ints[j+1];
                    ints[j+1] = temp;
                }
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        position = ints[position];
        return super.getView(position, convertView, parent);
    }
}
