package com.example.ticketingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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


public class EditViolation extends AppCompatActivity {
    private Spinner tov;
    private static Button btnQuery, cancel;
    private static EditText fn, ln;
    private static TextView tv_civ, defburger, dob;
    private static String cItemcode = "";
    private static JSONParser jParser = new JSONParser();
    private static String urlHost = "https://6f51-2001-4454-5ac-5100-346d-f6e7-e0bc-78b9.ngrok-free.app/WheelTix/UpdateViolation.php";
    private static String TAG_MESSAGE = "message", TAG_SUCCESS = "success";
    private static String online_dataset = "";
    public static String String_isempty = "";
    public static final String FN = "fn";
    public static final String DOB = "dob";
    public static final String LN = "ln";
    public static String ID = "ID";
    private String flnme, dateofbirth, licensenumber, typeofviolation, aydi;
    public static String FullName = "";
    public static String Dateofbirth = "";
    public static String Licensenumber = "";
    private Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_violation);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        fn = (EditText) findViewById(R.id.drivername);
        dob = (TextView) findViewById(R.id.dob);
        ln = (EditText) findViewById(R.id.licensenum);
        tov = (Spinner) findViewById(R.id.tov);
        cancel = (Button) findViewById(R.id.cancel);
        btnQuery = (Button) findViewById(R.id.submit);
        Intent i = getIntent();
        flnme = i.getStringExtra(FN);
        dateofbirth = i.getStringExtra(DOB);
        licensenumber = i.getStringExtra(LN);
        aydi = i.getStringExtra(ID);
        fn.setText(flnme);
        dob.setText(dateofbirth);
        ln.setText(licensenumber);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.choices_array, // Replace with the name of your array resource
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tov.setAdapter(adapter);
        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullName = fn.getText().toString();
                Dateofbirth = dob.getText().toString();
                Licensenumber = ln.getText().toString();
                new uploadDataToURL().execute();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditViolation.this, MainActivity.class);
                startActivity(intent);
            }
        });
        dateButton = findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        tov.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected item from the Spinner
                typeofviolation = parent.getItemAtPosition(position).toString();
                // Do something with the selected value
                Toast.makeText(EditViolation.this, "Selected value: " + typeofviolation, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no item is selected
            }
        });
    }
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(EditViolation.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Create a Calendar instance with the selected date
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, monthOfYear);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Format the selected date as a string
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String selectedDate = dateFormat.format(selectedCalendar.getTime());
                        // Use the selectedDate string as needed
                        dob.setText(selectedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
    private class uploadDataToURL extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        String gens, civil;
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(EditViolation.this);

        public uploadDataToURL() {
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
                cPostSQL = aydi;
                cv.put("ViolationID", cPostSQL);

                cPostSQL = " '" + FullName + "' ";
                cv.put("Fullname", cPostSQL);

                cPostSQL = " '" + Dateofbirth + "' ";
                cv.put("Dateofbirth", cPostSQL);

                cPostSQL = " '" + Licensenumber + "' ";
                cv.put("Licensenumber", cPostSQL);

                cPostSQL = " '" + typeofviolation + "' ";
                cv.put("Typeofviolation", cPostSQL);


                JSONObject json = jParser.makeHTTPRequest(urlHost, "POST", cv);
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
            AlertDialog.Builder alert = new AlertDialog.Builder(EditViolation.this);
            if (s != null) {
                if (isEmpty.equals("") && !s.equals("HTTPSERVER_ERROR")) {
                }
                Toast.makeText(EditViolation.this, s, Toast.LENGTH_SHORT).show();
            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet Connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }
}
