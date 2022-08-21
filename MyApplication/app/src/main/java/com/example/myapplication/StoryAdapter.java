package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StoryAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<StoryData> sample;

    public StoryAdapter(Context context, ArrayList<StoryData> data){
        mContext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public StoryData getItem(int position) {
        return sample.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.storymap, null);

        TextView title = (TextView)view.findViewById(R.id.mainTitle);
        TextView story = (TextView)view.findViewById(R.id.mainStory);

        title.setText(sample.get(position).getTitle());
        story.setText(sample.get(position).getMainStory());

        return view;
    }
}
