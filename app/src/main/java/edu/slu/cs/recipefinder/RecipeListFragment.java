package edu.slu.cs.recipefinder;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Bikram on 4/15/2017.
 */

public class RecipeListFragment  extends Fragment {
    RecipeMain _activity;
    String excludes, prep_time, includes;
    ProgressBar progressBar;
    TextView responseView;
    ListView _recipeList;
    EditText exclude,include;
    ArrayList<String> ingredient_Title;
    ArrayList<Integer> ingredient_ID;
    private static final String TAG = "UserActivity";
    final static String DATA_RECEIVE2 = "data_receive2";

    private TextView greetingTextView;
    private Button btnLogOut;

    public interface OnDataPass {
        public void onDataPass(String data);
    }
    OnDataPass dataPasser;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dataPasser = (OnDataPass) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDataPass.");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
          String user=args.getString(DATA_RECEIVE2);
            greetingTextView.setText("Hello "+user);
//            args.getStringArrayList(DATA_RECEIVE2);
//            greetingTextView.setText("Hello "+ user);

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, parent, false);
        exclude = (EditText) view.findViewById(R.id.exclude);
        //EditText preptime = (EditText) view.findViewById(R.id.include);
        include = (EditText) view.findViewById(R.id.include);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        responseView = (TextView) view.findViewById(R.id.responseView);
        final Button search = (Button) view.findViewById(R.id.search);
        btnLogOut = (Button) view.findViewById(R.id.logout_button);
        Bundle bundle=new Bundle();

        String user=bundle.getString("username");

//        Bundle bundle = this.getArguments();
//        String user = bundle.getString("username");
        greetingTextView = (TextView) view.findViewById(R.id.greeting_text_view);
        greetingTextView.setText("Hello "+ user);

        // Progress dialog
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(getContext(), LoginActivityFragment.class);
//                startActivity(i);
                _activity.switchFragment("recipe login");
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSelect();
                new RetrieveFeedTask().execute();
            }
        });
        _recipeList = (ListView) view.findViewById(R.id.list_recipe);
        _recipeList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view1, int position, long id){
                String url = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/";
                String charset = "UTF-8";
                String title = (String)parent.getItemAtPosition(position);
                int index = ingredient_Title.indexOf(title);
                String recipe_id = (ingredient_ID.get(index)).toString();
                dataPasser.onDataPass(recipe_id);
                _activity.title=title;
            }
        });

        return view;
    }

    protected void getSelect() {
//        EditText exclude = (EditText) view.findViewById(R.id.exclude);
//        EditText include = (EditText) view.findViewById(R.id.include);
        excludes = replace(exclude.getText().toString());
        includes = replace(include.getText().toString());
        Toast.makeText(getContext(), includes, Toast.LENGTH_LONG).show();
    }

    public String replace(String input) {
        String result;
        result = input.replace(",","%2C");
        result = result.replace(" ","+");
        return result;
    }

    protected class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        @Override
        protected String doInBackground(Void... urls) {
            String url = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/searchComplex?";
            String charset = "UTF-8";
            String ex_ingredient = excludes;
            String in_ingredients = includes;
            String query = "number=100" + "&includeIngredients=" + in_ingredients + "&readyInMinutes=30" + "&instructionsRequired=true"
                    + "&excludeIngredients=" + ex_ingredient + "&ranking=1";

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(url + query).openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("X-Mashape-Key", "bO9COj191VmshVzI4cNTPoxIxXoMp1ZfMQtjsnsM1bEzjBrumx");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Accept-Charset", charset);
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            ingredient_Title = new ArrayList<String>();
            ingredient_ID = new ArrayList<Integer>();
            String responses = "";
            if (response == null) {
                response = "THERE WAS AN ERROR";
                responses = "NO RESULT FOUND";
            }
            else {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject ingredient = jsonArray.getJSONObject(i);
                        ingredient_Title.add(ingredient.getString("title"));
                        ingredient_ID.add(Integer.parseInt(ingredient.getString("id")));
                    }
                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, ingredient_Title);
                    _recipeList.setAdapter(arrayAdapter);

                } catch (JSONException e) {
                    Log.d("Test", "Couldn't successfully parse the JSON response!");
                }
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            responseView.setText(responses);

        }
    }


    // This should be called immediately after constuction.
    // The fragment needs to know the activity in order to switch screens.
    public void setActivity(RecipeMain activity) {
        _activity = activity;
    }

    public void delete (String item) {

    }

}




