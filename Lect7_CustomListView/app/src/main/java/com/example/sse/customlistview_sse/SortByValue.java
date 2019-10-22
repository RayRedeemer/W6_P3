package com.example.sse.customlistview_sse;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;

public class SortByValue extends MyCustomAdapter{
    private int[] ints;
    private double[] rating;

    public SortByValue(Context aContext, SharedPreferences sp) {
        super(aContext, sp);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        rating = new double[getEpisodes().length];
        for(int i = 0; i < getEpisodes().length; i++){
            rating[i] = sp.getFloat("pos" + String.valueOf(i),3f);
        }
        ints = new int[rating.length];
        for(int i = 0; i < ints.length; i++){
            ints[i] = i;
        }
        for(int i = 0; i < ints.length; i++){
            for(int j = 0; j < ints.length - 1; j++){
                if( rating[j] > rating[j+1]){
                    double str = rating[j];
                    rating[j] = rating[j+1];
                    rating[j+1] = str;
                    int temp = ints[j];
                    ints[j] = ints[j+1];
                    ints[j+1] = temp;
                }
            }
        }
        position = ints[position];
        return super.getView(position, convertView, parent);
    }
}
