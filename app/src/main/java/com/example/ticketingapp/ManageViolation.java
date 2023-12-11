package com.example.ticketingapp;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ManageViolation extends AppCompatActivity {

    private RecyclerView recyclerView;

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
        setContentView(R.layout.activity_manage_violation);
        recyclerView = findViewById(R.id.recyclerViewViolations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String officerUsername = getUsername();
        fetchViolations(officerUsername);
    }

    private void fetchViolations(String officerUsername) {
        new AsyncTask<Void, Void, List<Violation>>() {
            @Override
            protected List<Violation> doInBackground(Void... voids) {
                JSONParser jsonParser = new JSONParser();
                ContentValues contentValues = new ContentValues();
                contentValues.put("code", officerUsername); // Add the officer's username here

                JSONObject jsonObject = jsonParser.makeHTTPRequest("https://bc8b-49-145-165-141.ngrok-free.app/Capstone/mobileofficer/selectViolationID.php", "POST", contentValues);
                List<Violation> violationsList = new ArrayList<>();

                try {
                    if (jsonObject != null && jsonObject.getInt("success") == 1) {
                        // Inside doInBackground method of AsyncTask in fetchViolations
                        JSONArray violationsArray = jsonObject.getJSONArray("violations");
                        for (int i = 0; i < violationsArray.length(); i++) {
                            JSONObject violationObject = violationsArray.getJSONObject(i);
                            Violation violation = new Violation(
                                    violationObject.getInt("violation_id"),
                                    violationObject.getInt("user_id"),
                                    violationObject.getString("name"), // Make sure these keys match your JSON
                                    violationObject.getString("violation"),
                                    violationObject.getString("date")
                                    // ... set other fields
                            );
                            violationsList.add(violation);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return violationsList;
            }

            @Override
            protected void onPostExecute(List<Violation> violations) {
                super.onPostExecute(violations);
                if (!violations.isEmpty()) {
                    ViolationAdapter adapter = new ViolationAdapter(violations);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(ManageViolation.this, "No violations found", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
    private String getUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("Username", null); // Return null or a default value if not found
    }
}