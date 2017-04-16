package edu.slu.cs.recipefinder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by Bikram on 4/15/2017.
 */

public class CheckListFragment extends Fragment {
    final static String DATA_RECEIVE1 = "data_receive1";
    ListView lv;
    RecipeMain _activity;
    String receiveData;
    TextView textView;
    CustomAdapter adapter;
    ArrayList<String> data =new ArrayList<>();

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checklist_fragment, parent, false);
        boolean getList = _activity.getList;
        adapter = new CustomAdapter(getContext(), data);
        lv = (ListView) view.findViewById(R.id.listView1);
        lv.setItemsCanFocus(true);
        lv.setAdapter(adapter);
        Bundle args = getArguments();
        if (args != null && getList==true) {
            receiveData=args.getString(DATA_RECEIVE1);
            String[] shoplist = receiveData.split("\n");
            for (int i = 0; i < shoplist.length; i++) {
                data.add(shoplist[i]);
            }
        }

        Button clear = (Button) view.findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.clear();
                adapter.notifyDataSetChanged();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = (String) parent.getItemAtPosition(position);
                data.remove(text);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(),"Removed: " + text, Toast.LENGTH_SHORT).show();
            }
        });

        Button backbutton = (Button) view.findViewById(R.id.back_button);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.switchFragment("recipe view");
            }
        });
        return view;
    }


    // This should be called immediately after construction.
    // The fragment needs to know the activity in order to switch screens.
    public void setActivity(RecipeMain activity) {
        _activity = activity;
    }
}

