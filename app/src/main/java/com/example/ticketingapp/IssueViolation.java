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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class IssueViolation extends AppCompatActivity {
    private static JSONParser jParser = new JSONParser();
    private static String urlHostIssue = "https://bc8b-49-145-165-141.ngrok-free.app/Capstone/mobileofficer/issueViolation.php";
    private static String TAG_MESSAGE = "message" , TAG_SUCCESS = "success";
    private static String online_dataset = "";
    private static String drivername = "";
    private static String licensenum = "";
    private static String officername = "";
    private static String phone = "";
    private static String tov = "";
    private static String date = "";
    private static EditText driverInput, licenseInput, driverPhone;
    private static Spinner tovInput;
    private static TextView dateToday, officerName;
    Button submit, cancel;
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
        setContentView(R.layout.activity_issue_violation);
        tovInput = findViewById(R.id.tov);
        officerName = findViewById(R.id.officer);
        driverPhone = findViewById(R.id.driverphone);
        driverInput = findViewById(R.id.drivername);
        licenseInput = findViewById(R.id.licensenum);
        submit = findViewById(R.id.submit);
        cancel = findViewById(R.id.cancel);
        dateToday = findViewById(R.id.date);
        String username = getUsername();
        if (username != null) {
            officerName.setText(username);
        }
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month starts from 0
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = String.format("%d-%02d-%02d", year, month, day);
        dateToday.setText(currentDate);

        // Create an ArrayAdapter using a string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.choices_array, // Replace with the name of your array resource
                android.R.layout.simple_spinner_item
        );

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        tovInput.setAdapter(adapter);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                officername = officerName.getText().toString();
                phone = driverPhone.getText().toString();
                drivername = driverInput.getText().toString();
                licensenum = licenseInput.getText().toString();
                date = dateToday.getText().toString();
                new uploadDatatoURL().execute();
            }
        });
        tovInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected item from the Spinner
                tov = parent.getItemAtPosition(position).toString();
                // Do something with the selected value
                Toast.makeText(IssueViolation.this, "Selected value: " + tov, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no item is selected
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IssueViolation.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private class uploadDatatoURL extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(IssueViolation.this);

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
                cv.put("officername", officername);
                cv.put("drivername", drivername);
                cv.put("licensenum", licensenum);
                cv.put("tov", tov);
                cv.put("date", date);
                cv.put("phone", phone);

                JSONObject json = jParser.makeHTTPRequest(urlHostIssue, "POST", cv);
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
            AlertDialog.Builder alert = new AlertDialog.Builder(IssueViolation.this);
            if (s != null) {
                if (isEmpty.equals("") && !s.equals("Success")) { }
                Toast.makeText(IssueViolation.this, s, Toast.LENGTH_SHORT).show();
            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet Connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }
    private String getUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("Username", null); // Return null or a default value if not found
    }
}