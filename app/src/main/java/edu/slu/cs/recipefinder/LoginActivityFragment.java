package edu.slu.cs.recipefinder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bikram on 4/29/2017.
 */

public class LoginActivityFragment extends Fragment{
    private static final String TAG = "LoginActivity";
    private static final String URL_FOR_LOGIN = "http://192.168.1.192/android_login_example/login.php";
    ProgressDialog progressDialog;
    private EditText loginInputEmail, loginInputPassword;
    private Button btnlogin;
    private Button btnLinkSignup;
    RecipeMain _activity;

    public interface OnDataPass2 {
        public void onDataPass2(String data);
    }

    OnDataPass2 dataPasser2;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dataPasser2 = (OnDataPass2) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDataPass.");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, parent, false);
        loginInputEmail = (EditText) view.findViewById(R.id.login_input_email);
        loginInputPassword = (EditText) view.findViewById(R.id.login_input_password);
        btnlogin = (Button) view.findViewById(R.id.btn_login);
        btnLinkSignup = (Button) view.findViewById(R.id.btn_link_signup);
        // Progress dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(loginInputEmail.getText().toString(),
                        loginInputPassword.getText().toString());

            }
        });

        btnLinkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(getContext(), RegisterActivityFragment.class);
//                startActivity(i);
                _activity.switchFragment("recipe register");
            }
        });
        return view;
    }

    private void loginUser( final String email, final String password) {
        // Tag used to cancel the request
        String cancel_req_tag = "login";
        progressDialog.setMessage("Logging you in...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String user = jObj.getJSONObject("user").getString("name");
                        // Launch User activity
//                        Intent intent = new Intent(getActivity(),RecipeListFragment.class);
//                        intent.putExtra("username", user);
//                        getActivity().startActivity(intent);
                        dataPasser2.onDataPass2(user);
                        _activity.switchFragment("recipe list");

//                        finish();
                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }

        };
//        // Adding request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq,cancel_req_tag);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }


// This should be called immediately after constuction.
// The fragment needs to know the activity in order to switch screens.
public void setActivity(RecipeMain activity) {
    _activity = activity;
}



}
