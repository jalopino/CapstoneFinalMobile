package com.example.ticketingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.SSLEngineResult;

//Add encryption protection on registration and login

public class Register extends AppCompatActivity {
    private static JSONParser jParser = new JSONParser();
    private static JSONParser jsonParser = new JSONParser();
    private static String urlHost = "http://192.168.68.107/WheelTix/register.php";
    private static String urlHostUser = "http://192.168.68.107/WheelTix/selectUser.php";
    private static String TAG_MESSAGE = "message" , TAG_SUCCESS = "success";
    private static String online_dataset = "";
    private static String email = "";
    private static String username = "";
    private static String password = "";
    private static EditText userInput, passInput, emailInput;
    Button register, cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        setContentView(R.layout.activity_register);
        userInput = findViewById(R.id.username);
        passInput = findViewById(R.id.password);
        emailInput = findViewById(R.id.email);
        register = findViewById(R.id.register);
        cancel = findViewById(R.id.cancel);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = userInput.getText().toString();
                password = passInput.getText().toString();
                email = emailInput.getText().toString();
                new checkUser().execute();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, GetStarted.class);
                startActivity(intent);
            }
        });
    }
    private class uploadDatatoURL extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(Register.this);

        public uploadDatatoURL() { }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.setMessage(cMessage);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            int nSuccess;
            try {
                ContentValues cv = new ContentValues();
                cPostSQL = " '" + email + "' , '" + username + "' , '" + password + "'  ";
                cv.put("code", cPostSQL);

                JSONObject json = jParser.makeHTTPRequest(urlHost, "POST", cv);
                if(json != null) {
                    nSuccess = json.getInt(TAG_SUCCESS);
                    if(nSuccess == 1) {
                        online_dataset = json.getString(TAG_MESSAGE);
                        return online_dataset;
                    } else {
                        return json.getString(TAG_MESSAGE);
                    }
                } else {
                    return "HTTPSERVER_ERROR";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            String isEmpty = "";
            AlertDialog.Builder alert = new AlertDialog.Builder(Register.this);
            if (s != null) {
                if (isEmpty.equals("") && !s.equals("HTTPSERVER_ERROR")) { }
                Toast.makeText(Register.this, s, Toast.LENGTH_SHORT).show();
            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet Connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }

    private class checkUser extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = " ", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(Register.this);

        public checkUser() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.setMessage(cMessage);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            int nSuccess;
            try {
                ContentValues cv = new ContentValues();
                cPostSQL = username;
                cv.put("code", cPostSQL);
                JSONObject json = jsonParser.makeHTTPRequest(urlHostUser, "POST", cv);
                if (json != null) {
                    nSuccess = json.getInt(TAG_SUCCESS);
                    if (nSuccess == 1) {
                        online_dataset = json.getString(TAG_MESSAGE);
                        return online_dataset;
                    } else {
                        return json.getString(TAG_MESSAGE);
                    }
                } else {
                    return "HTTPSERVER_ERROR";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            String isEmpty = "";
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(Register.this);
            if (s != null) {
                if (isEmpty.equals("") && !s.equals("HTTPSERVER_ERROR")) { }
                String wew = s;
                String str = wew;
                final String fnames[] = str.split("-");
                if (fnames[0].equals(username)) {
                    alert.setMessage("Username is already in use!");
                    alert.setTitle("Error");
                    alert.show();
                } else {
                    new uploadDatatoURL().execute();
                }
            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet Connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }
}