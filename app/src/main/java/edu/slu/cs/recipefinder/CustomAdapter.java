package edu.slu.cs.recipefinder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Bikram on 4/15/2017.
 */

public class CustomAdapter extends ArrayAdapter {
    ArrayList<String> modelItems= new ArrayList();
    Context context;
    RecipeMain _activity;
    AdapterView.OnItemClickListener onItemClickListener;
    public CustomAdapter(Context context, ArrayList<String> resource) {
        super(context,R.layout.row,resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.modelItems=resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.row, parent, false);
        TextView textview = (TextView) convertView.findViewById(R.id.textView);
        //CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
        textview.setText(modelItems.get(position));
        return convertView;
    }
}
