package com.example.ticketingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;

public class GetStarted extends AppCompatActivity {
    private static JSONParser jsonParser = new JSONParser();
    private static String urlHost = "https://843a-49-145-171-0.ngrok-free.app/Capstone/mobileofficer/login.php";
    private static String TAG_MESSAGE = "message", TAG_SUCCESS = "success", TAG_STATUS = "status", TAG_ASSIGNMENT = "assignment";
    private EditText userInput, passInput;
    Button login;

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
        setContentView(R.layout.activity_getstarted);
        userInput = findViewById(R.id.username);
        passInput = findViewById(R.id.password);
        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userInput.getText().toString();
                String password = passInput.getText().toString();
                new VerifyLogin().execute(username, password);
            }
        });
    }

    private class VerifyLogin extends AsyncTask<String, String, String> {
        ProgressDialog pDialog = new ProgressDialog(GetStarted.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Logging In...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                ContentValues cv = new ContentValues();
                cv.put("username", params[0]);
                cv.put("password", params[1]);
                JSONObject json = jsonParser.makeHTTPRequest(urlHost, "POST", cv);
                if (json != null) {
                    String status = json.getString(TAG_STATUS);
                    String assignment = json.getString(TAG_ASSIGNMENT);
                    saveAssignment(assignment);
                    saveStatus(status);
                    return json.getString(TAG_MESSAGE);
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
            pDialog.dismiss();
            AlertDialog.Builder alert = new AlertDialog.Builder(GetStarted.this);
            if (s != null && !s.equals("HTTPSERVER_ERROR")) {
                if (s.equals("success")) {
                    saveUsername(userInput.getText().toString());
                    Intent intent = new Intent(GetStarted.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    alert.setMessage("Invalid Username or Password");
                    alert.setTitle("Error");
                    alert.show();
                }
            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet Connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }

    private void saveUsername(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Username", username);
        editor.apply();
    }
    private void saveStatus(String status) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Status", status);
        editor.apply();
    }
    private void saveAssignment(String assignment) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Assignment", assignment);
        editor.apply();
    }
}