package edu.slu.cs.recipefinder;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Bikram on 4/15/2017.
 */

public class RecipeViewFragment extends Fragment {
    RecipeMain _activity;
    int recipe_id;
    final static String DATA_RECEIVE = "data_receive";
    EditText editText;
    String grocery_name;
    TextView ingredients, instructions;

    public interface OnDataPass1 {
        public void onDataPass1(String data);
    }

    OnDataPass1 dataPasser1;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dataPasser1 = (OnDataPass1) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDataPass.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_fragment, parent, false);
        Button backButton = (Button) view.findViewById(R.id.view_button);
        Button nextButton = (Button) view.findViewById(R.id.check_button);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(_activity.title);
        ingredients = (TextView) view.findViewById(R.id.ingredients);
        instructions = (TextView) view.findViewById(R.id.instruction);
        Button checkListButton = (Button) view.findViewById(R.id.checklist_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _activity.switchFragment("recipe list");
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.getList=false;
                _activity.switchFragment("check list");
            }
        });

        checkListButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _activity.getList=true;
                dataPasser1.onDataPass1(grocery_name);
                _activity.switchFragment("check list");
            }
        });

        new RetrieveFeedTask().execute();
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public String replace(String input) {
        String result="";
        int count = 1;
        String[] split_line = input.split(".");
        result = input.replace(".","\r\n");
        return result;
    }

    protected class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        @Override
        protected void onPreExecute() {
            instructions.setText("");
        }

        @Override
        protected String doInBackground(Void... urls) {
            Bundle args = getArguments();
            String url = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/" + args.getString(DATA_RECEIVE) + "/information";
            String charset = "UTF-8";

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
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
            String recipe_inst ="";
            String recipe_ingr="";
            grocery_name= "";
            if (response == null) {
                recipe_ingr = "NONE FOUND";
                recipe_inst="NONE FOUND";
            }
            else {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    recipe_inst += replace(jsonResponse.getString("instructions"));
                    JSONArray jsonArray = jsonResponse.getJSONArray("extendedIngredients");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject ingredient = jsonArray.getJSONObject(i);
                        grocery_name+=ingredient.getString("name") +"\n";
                        recipe_ingr += ingredient.getString("originalString") +"\n";
                    }


//                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, ingredient_Title);
//                    _recipeList.setAdapter(arrayAdapter);

                } catch (JSONException e) {
                    Log.d("Test", "Couldn't successfully parse the JSON response!");
                }
            }
            ingredients.setText(recipe_ingr);
            instructions.setText(recipe_inst);
            Log.i("RESPONSEVIEW", response);

        }
    }



    // This should be called immediately after constuction.
    // The fragment needs to know the activity in order to switch screens.
    public void setActivity(RecipeMain activity) {
        _activity = activity;
    }

}