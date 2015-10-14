package com.aktyagi.movies;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private GridViewAdapter mAdapter;

    public MainActivityFragment() {
        mAdapter = new GridViewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView)rootView.findViewById(R.id.mainGridView);
        gridView.setAdapter(mAdapter);
        return rootView;
    }

    class GridViewAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView==null) {
                viewHolder = new ViewHolder();
                LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.grid_cell, parent, false);
                TextView textView = (TextView) convertView.findViewById(R.id.grid_cell_textView);
                viewHolder.mTextView = textView;
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.mTextView.setText(new Integer(position).toString());
            return convertView;
        }

        public class ViewHolder{
            public TextView     mTextView;
            public ImageView    mImageView;
            ViewHolder() {
                mTextView = null;
                mImageView = null;
            }
        }
    }
}
