package com.example.ticketingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ManageViolation extends AppCompatActivity {

    private static Button btnQuery;

    TextView textView, txtDefault, txtDefault_DOB, txtDefault_LN, txtDefault_TOV, txtDefault_ID;
    private static EditText edtitemcode;
    private static JSONParser jsonParser = new JSONParser();
    private static String urlHostID = "http://192.168.1.7/WheelTix/selectViolationID.php";
    private static String urlHostFN = "http://192.168.1.7/WheelTix/selectViolationFN.php";
    private static String urlHostDOB = "http://192.168.1.7/WheelTix/selectViolationDOB.php";
    private static String urlHostLN = "http://192.168.1.7/WheelTix/selectViolationLicense.php";
    private static String urlHostTOV = "http://192.168.1.7/WheelTix/selectViolationTOV.php";
    private static String TAG_MESSAGE = "message", TAG_SUCCESS = "success";
    private static String online_dataset = "";
    private static String cItemcode = "";


    //how to globalize string
    public static String wew = "";
    public static String gender = "";
    public static String civilstats = "";

    private String fn, dob, tov, ln, aydi;

    String cItemSelected, cItemSelected_DOB, cItemSelected_LN, cItemSelected_TOV, cItemSelected_ID;
    ArrayAdapter<String> adapter_fnames;
    ArrayAdapter<String> adapter_dob;
    ArrayAdapter<String> adapter_ln;
    ArrayAdapter<String> adapter_tov;
    ArrayAdapter<String> adapter_ID;
    ArrayList<String> list_fnames;
    ArrayList<String> list_dob;
    ArrayList<String> list_tov;
    ArrayList<String> list_ln;
    ArrayList<String> list_ID;
    AdapterView.OnItemLongClickListener longItemListener_fnames;
    Context context = this;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_violation);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        btnQuery = (Button) findViewById(R.id.btnQuery);
        edtitemcode = (EditText) findViewById(R.id.edtitemcode);
        txtDefault = (TextView) findViewById(R.id.tv_default);
        listView = (ListView) findViewById(R.id.listview);
        textView = (TextView) findViewById(R.id.textView4);
        txtDefault_DOB = (TextView) findViewById(R.id.txt_DOB);
        txtDefault_LN = (TextView) findViewById(R.id.txt_LN);
        txtDefault_TOV = (TextView) findViewById(R.id.txt_TOV);
        txtDefault_ID = (TextView) findViewById(R.id.txt_ID);

        txtDefault.setVisibility(View.GONE);
        txtDefault_DOB.setVisibility(View.GONE);
        txtDefault_TOV.setVisibility(View.GONE);
        txtDefault_LN.setVisibility(View.GONE);
        txtDefault_ID.setVisibility(View.GONE);

        Toast.makeText(ManageViolation.this, "Nothing Selected", Toast.LENGTH_SHORT).show();

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cItemcode = edtitemcode.getText().toString();
                new uploadDataToURL().execute();
                new DOB().execute();
                new LN().execute();
                new TOV().execute();
                new id().execute();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cItemSelected = adapter_fnames.getItem(position);
                cItemSelected_DOB = adapter_dob.getItem(position);
                cItemSelected_LN = adapter_ln.getItem(position);
                cItemSelected_TOV = adapter_tov.getItem(position);
                cItemSelected_ID = adapter_ID.getItem(position);
                androidx.appcompat.app.AlertDialog.Builder alert_confirm =
                        new androidx.appcompat.app.AlertDialog.Builder(context);
                alert_confirm.setMessage("Edit the records of" + " " + cItemSelected);
                alert_confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        txtDefault.setText(cItemSelected);
                        txtDefault_DOB.setText(cItemSelected_DOB);
                        txtDefault_LN.setText(cItemSelected_LN);
                        txtDefault_TOV.setText(cItemSelected_TOV);
                        txtDefault_ID.setText(cItemSelected_ID);
                        fn = txtDefault.getText().toString().trim();
                        dob = txtDefault_DOB.getText().toString().trim();
                        ln = txtDefault_LN.getText().toString().trim();
                        aydi = txtDefault_ID.getText().toString().trim();
                        Intent intent = new Intent(ManageViolation.this, EditViolation.class);
                        intent.putExtra(EditViolation.FN, fn);
                        intent.putExtra(EditViolation.DOB, dob);
                        intent.putExtra(EditViolation.LN, ln);
                        intent.putExtra(EditViolation.ID, aydi);
                        startActivity(intent);
                    }
                });
                alert_confirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alert_confirm.show();
            }
        });
    }

    private class uploadDataToURL extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = " ", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(ManageViolation.this);

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
                cPostSQL = cItemcode;
                cv.put("code", cPostSQL);
                JSONObject json = jsonParser.makeHTTPRequest(urlHostFN, "POST", cv);
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
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(ManageViolation.this);
            if (s != null) {
                if (isEmpty.equals("") && !s.equals("HTTPSERVER_ERROR")) { }
                String wew = s;
                String str = wew;
                final String fnames[] = str.split("-");
                list_fnames = new ArrayList<String>(Arrays.asList(fnames));
                adapter_fnames = new ArrayAdapter<String>(ManageViolation.this, android.R.layout.simple_list_item_1, list_fnames);
                listView.setAdapter(adapter_fnames);
                textView.setText(listView.getAdapter().getCount() + " " + "record(s) found.");
            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet Connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }
    private class DOB extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(ManageViolation.this);

        public DOB() {
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
                cPostSQL = cItemcode;
                cv.put("code", cPostSQL);
                JSONObject json = jsonParser.makeHTTPRequest(urlHostDOB, "POST", cv);
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
        protected void onPostExecute(String Gender) {
            super.onPostExecute(Gender);
            pDialog.dismiss();
            String isEmpty = "";
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(ManageViolation.this);
            if (Gender != null) {
                if (isEmpty.equals("") && !Gender.equals("HTTPSERVER_ERROR")) { }
                String gender = Gender;
                String str = gender;
                final String Genders[] = str.split("-");
                list_dob = new ArrayList<String>(Arrays.asList(Genders));
                adapter_dob = new ArrayAdapter<String>(ManageViolation.this,
                        android.R.layout.simple_list_item_1, list_dob);
            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet Connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }
    private class LN extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(ManageViolation.this);

        public LN() {
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
                cPostSQL = cItemcode;
                cv.put("code", cPostSQL);
                JSONObject json = jsonParser.makeHTTPRequest(urlHostLN, "POST", cv);
                if(json != null) {
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
        protected void onPostExecute(String CS) {
            super.onPostExecute(CS);
            pDialog.dismiss();
            String isEmpty = "";
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(ManageViolation.this);
            if (CS != null) {
                if (isEmpty.equals("") && !CS.equals("HTTPSERVER_ERROR")) { }
                String CivitStat = CS;
                String str = CivitStat;
                final String Civs[] = str.split("-");
                list_ln= new ArrayList<String>(Arrays.asList(Civs));
                adapter_ln = new ArrayAdapter<String>(ManageViolation.this,
                        android.R.layout.simple_list_item_1, list_ln);
            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet Connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }
    private class TOV extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(ManageViolation.this);

        public TOV() {
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
                cPostSQL = cItemcode;
                cv.put("code", cPostSQL);
                JSONObject json = jsonParser.makeHTTPRequest(urlHostTOV, "POST", cv);
                if(json != null) {
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
        protected void onPostExecute(String CS) {
            super.onPostExecute(CS);
            pDialog.dismiss();
            String isEmpty = "";
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(ManageViolation.this);
            if (CS != null) {
                if (isEmpty.equals("") && !CS.equals("HTTPSERVER_ERROR")) { }
                String CivitStat = CS;
                String str = CivitStat;
                final String Civs[] = str.split("-");
                list_tov= new ArrayList<String>(Arrays.asList(Civs));
                adapter_tov = new ArrayAdapter<String>(ManageViolation.this,
                        android.R.layout.simple_list_item_1, list_tov);
            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet Connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }
    private class id extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(ManageViolation.this);

        public id() {
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
                cPostSQL = cItemcode;
                cv.put("code", cPostSQL);
                JSONObject json = jsonParser.makeHTTPRequest(urlHostID, "POST", cv);
                if(json != null) {
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
        protected void onPostExecute(String aydi) {
            super.onPostExecute(aydi);
            pDialog.dismiss();
            String isEmpty = "";
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(ManageViolation.this);
            if (aydi != null) {
                if (isEmpty.equals("") && !aydi.equals("HTTPSERVER_ERROR")) { }
                Toast.makeText(ManageViolation.this, "Data selected", Toast.LENGTH_SHORT);
                String AYDI = aydi;
                String str = AYDI;
                final String ayds[] = str.split("-");
                list_ID = new ArrayList<String>(Arrays.asList(ayds));
                adapter_ID = new ArrayAdapter<String>(ManageViolation.this,
                        android.R.layout.simple_list_item_1, list_ID);
            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet Connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }
}