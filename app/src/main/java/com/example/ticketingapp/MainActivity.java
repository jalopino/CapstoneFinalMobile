package com.example.ticketingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String UPDATE_STATUS_URL = "https://843a-49-145-171-0.ngrok-free.app/capstone/mobileofficer/updateStatus.php"; // Change this URL
    private static JSONParser jsonParser = new JSONParser();
    Button manage, issue, timein, timeout;
    TextView status, assignment;
    int i = 0;
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
        setContentView(R.layout.activity_main);
        i = i + 1;
        String statusString = getStatus();
        String assignmentString = getAssignment();
        timein = findViewById(R.id.timein);
        timeout = findViewById(R.id.timeout);
        assignment = findViewById(R.id.assignment);
        assignment.setText(assignmentString);
        status = findViewById(R.id.status);
        status.setText(statusString);
        if (statusString.equals("On leave")) {
            timein.setVisibility(View.VISIBLE);
            timeout.setVisibility(View.GONE);
        } else if (statusString.equals("On duty")) {
            timein.setVisibility(View.GONE);
            timeout.setVisibility(View.VISIBLE);
        }
        manage = findViewById(R.id.edit);
        issue = findViewById(R.id.issue);
        android.app.AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        if (i > 2) {
            alert.setMessage("You are now Logged In");
            alert.setTitle("Success");
            alert.show();
        }
        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManageViolation.class);
                startActivity(intent);
            }
        });
        issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IssueViolation.class);
                startActivity(intent);
            }
        });
        timein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus("On duty");
            }
        });

        timeout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus("On leave");
            }
        });
    }
    private class UpdateStatusTask extends AsyncTask<ContentValues, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(ContentValues... params) {
            ContentValues contentValues = params[0];
            JSONParser jsonParser = new JSONParser();

            // Adjust the URL and parameters as needed
            return jsonParser.makeHTTPRequest(UPDATE_STATUS_URL, "POST", contentValues);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                try {
                    boolean success = result.getBoolean("success");
                    String message = result.getString("message");
                    String newStatus = result.getString("status");
                    status.setText(newStatus);

                    if (success) {
                        showToast("Status updated successfully");
                        if (newStatus.equals("On leave")) {
                            timein.setVisibility(View.VISIBLE);
                            timeout.setVisibility(View.GONE);
                        } else if (newStatus.equals("On duty")) {
                            timein.setVisibility(View.GONE);
                            timeout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        showToast("Error updating status: " + message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Error parsing server response");
                }
            } else {
                showToast("No response from the server");
            }
        }
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void updateStatus(String status) {
        ContentValues contentValues = new ContentValues();
        String username = getUsername();
        contentValues.put("username", username); // Replace with the actual officer ID
        contentValues.put("status", status);
        new UpdateStatusTask().execute(contentValues);
    }
    private String getUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("Username", null); // Return null or a default value if not found
    }
    private String getStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("Status", null); // Return null or a default value if not found
    }
    private String getAssignment() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("Assignment", null); // Return null or a default value if not found
    }
}