package mc.nj.proj;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private String TAG = "Signup Beac";
    private String sUsername;
    private String sPassword;
    JSONObject details = new JSONObject();
    String Url = HomeActivity.ipaddr+"/check_username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        boolean checkPage = false;
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab0 = (FloatingActionButton) findViewById(R.id.fabD0);
        fab0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sUsername = ((AutoCompleteTextView) findViewById(R.id.username)).getText().toString();
                sPassword = ((AutoCompleteTextView) findViewById(R.id.password)).getText().toString();
                verifyPage();
            }

        });


    }

    private void verifyPage() {
        if (TextUtils.isEmpty(sUsername) || sPassword.length() < 5)
            Toast.makeText(getApplicationContext(), "Enter Valid Data \n Username not available \n or \n Password less than 5 characters", Toast.LENGTH_LONG).show();
        else
            new check().execute();
    }


    private class check extends AsyncTask<Void, Void, Boolean> {
        public boolean checkB;

        public check() {
            this.checkB = false;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL(Url);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setConnectTimeout(1000);
                client.setReadTimeout(1500);
                client.setDoOutput(true);
                client.setRequestMethod("POST");
                client.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());
                BufferedReader resp;
                StringBuilder response = new StringBuilder();
                String line = new String();
                JSONObject json;

                details.put("_id", sUsername);
                osw.write(details.toString());
                osw.flush();
                osw.close();
                resp = new BufferedReader(new InputStreamReader(client.getInputStream()));

                while ((line = resp.readLine()) != null) {
                    response.append(line);
                }
                Log.d(TAG, "doInBackground: " + response.toString());
                json = new JSONObject(response.toString());
                resp.close();
                client.disconnect();

                if (json.getBoolean("account_exists") == false)
                    return true;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return checkB;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            finish();
            if (aBoolean) {
                Intent signupData = new Intent(getApplicationContext(), SignupData.class);
                signupData.putExtra("username", sUsername);
                signupData.putExtra("pwd", sPassword);
                startActivity(signupData);

            } else {
                Toast.makeText(getApplicationContext(), "Enter Valid Data \n Username not available \n or \n Password less than 6 characters", Toast.LENGTH_LONG).show();
                Intent stay = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(stay);
            }

        }
    }
}