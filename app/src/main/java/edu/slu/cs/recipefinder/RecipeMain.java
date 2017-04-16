package edu.slu.cs.recipefinder;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecipeMain extends FragmentActivity implements RecipeListFragment.OnDataPass
//        ,RecipeViewFragment.OnDataPass1
{
    RecipeListFragment _recipeListFragment;
    RecipeViewFragment _recipeViewFragment;
//    CheckListFragment _checkListFragment;
    SQLiteDatabase _db;
    int recipe_id;
    public static final String PREFS_NAME = "MyPrefsFile";
    String title;
    boolean delete = false;
    boolean getList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_main);

        // Initialize the fragments if they do not exist
        if (_recipeListFragment == null) {
            _recipeListFragment = new RecipeListFragment();
            _recipeListFragment.setActivity(this);
        }
        if (_recipeViewFragment == null) {
            _recipeViewFragment = new RecipeViewFragment();
            _recipeViewFragment.setActivity(this);
        }
//        if (_checkListFragment == null) {
//            _checkListFragment = new CheckListFragment();
//            _checkListFragment.setActivity(this);
//        }

//        if(_profileFrag == null){
//            _profileFrag = new ProfileFrag();
//            _profileFrag.setActivity(this);
//        }
        // Add the initial fragment
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.activity_recipe_main, _recipeListFragment).commit();
    }

    public void switchFragment(String newFragment) {
        if (newFragment == "recipe view") {
            Bundle bundle = new Bundle();
            bundle.putInt("id", recipe_id);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.activity_recipe_main, _recipeViewFragment).commit();
        } else if (newFragment == "recipe list") {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.activity_recipe_main, _recipeListFragment).commit();}
//        } else if (newFragment == "check list") {
//            FragmentManager fm = getSupportFragmentManager();
//            fm.beginTransaction().replace(R.id.recipe_main, _checkListFragment).commit();
//        }else if(newFragment == "profile"){
//            FragmentManager fm = getSupportFragmentManager();
//            fm.beginTransaction().replace(R.id.recipe_main, _profileFrag).commit();
//        }

        }

    public void onDataPass(String data) {
        Bundle args = new Bundle();
        args.putString(RecipeViewFragment.DATA_RECEIVE, data);
        _recipeViewFragment.setArguments(args);
        switchFragment("recipe view");

    }
//    public void onDataPass1(String data) {
//        Bundle args = new Bundle();
//        args.putString(CheckListFragment.DATA_RECEIVE1, data);
//        _checkListFragment.setArguments(args);
//        switchFragment("check list");
//
//    }
//
//    public void passData(String key, String data) {
//        Bundle args = new Bundle();
//        args.putString(key, data);
//        _checkListFragment.setArguments(args);
//    }


    }



