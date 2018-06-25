package mc.nj.proj;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by ಆದರ್ಶ್ NJ on 4/11/2017.
 */

public class SignupData extends AppCompatActivity {

    private String TAG = "signupdata Beac";
    private String sUsername;
    private String sPassowrd;
    private String sName;
    private Integer sAge;
    private Integer sHeight;
    private Integer sWeight;
    private String sGender;
    private String sMedical;
    private String sAllergy;
    private String sFoodAllergy;
    private String sActLvl;

    JSONObject details = new JSONObject();

    private String Url = HomeActivity.ipaddr+"/user_profile_update";
    private HashMap<Integer, String> activityDesc = new HashMap<Integer, String>();
    private boolean checkPage1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPage1 = false;

        activityDesc.put(1, "Sedentary\n\n");
        activityDesc.put(2, "Moderately active\n\n");
        activityDesc.put(3, "Highly active\n\n");
        activityDesc.put(4, "Challenging\n\n");

        sUsername = getIntent().getStringExtra("username");
        sPassowrd = getIntent().getStringExtra("pwd");
        setContentView(R.layout.activity_signup1);
        final Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((NumberPicker) findViewById(R.id.age)).setMinValue(10);
        ((NumberPicker) findViewById(R.id.age)).setMaxValue(80);
        ((NumberPicker) findViewById(R.id.weight)).setMinValue(50);
        ((NumberPicker) findViewById(R.id.weight)).setMaxValue(200);
        ((NumberPicker) findViewById(R.id.height)).setMinValue(120);
        ((NumberPicker) findViewById(R.id.height)).setMaxValue(200);

        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fabD1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPage1 = verifypage1();
                if (checkPage1) {
                    setContentView(R.layout.activity_signup2);
                    final Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
                    setSupportActionBar(toolbar2);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                    RadioGroup activityLevel = (RadioGroup) findViewById(R.id.activity_level);
                    activityLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            ((CardView) findViewById(R.id.actDetCard)).setVisibility(1);
                            RadioButton rb = (RadioButton) findViewById(checkedId);
                            TextView actDetails = (TextView) findViewById(R.id.activity_details);
                            actDetails.setText(activityDesc.get(Integer.parseInt(rb.getText().toString())));
                        }
                    });
                    final Button loginButton = (Button) findViewById(R.id.submit);
                    loginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            verifysignup();
                        }
                    });
                } else {
                    Toast.makeText(v.getContext(), "Enter Valid Data", Toast.LENGTH_LONG).show();
                    Intent stay = new Intent(getApplicationContext(), SignupData.class);
                    startActivity(stay);

                }
            }
        });
    }

    private boolean verifypage1() {
        sName = ((AutoCompleteTextView) findViewById(R.id.name)).getText().toString();
        sHeight = ((NumberPicker) findViewById(R.id.height)).getValue();
        sWeight = ((NumberPicker) findViewById(R.id.weight)).getValue();
        sAge = ((NumberPicker) findViewById(R.id.age)).getValue();
        if(((RadioGroup) findViewById(R.id.genderText)).getCheckedRadioButtonId() != -1)
                sGender = ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.genderText)).getCheckedRadioButtonId())).getText().toString();
        else
            return false;
        if (TextUtils.isEmpty(sName) || TextUtils.isEmpty(sGender))
            return false;
        else
            return true;

    }

    private void verifysignup() {
        sMedical = ((Spinner) findViewById(R.id.medicalList)).getSelectedItem().toString();
        sAllergy = ((Spinner) findViewById(R.id.allergyList)).getSelectedItem().toString();
        sFoodAllergy = ((Spinner) findViewById(R.id.foodallergyList)).getSelectedItem().toString();

        RadioGroup rg = (RadioGroup) findViewById(R.id.activity_level);

        sActLvl = ((RadioButton) findViewById(rg.getCheckedRadioButtonId())).getText().toString();

        if (sMedical == null || sAllergy == null || sActLvl == null || sFoodAllergy == null) {
            Toast.makeText(getApplicationContext(), "Not yet Registered", Toast.LENGTH_LONG).show();
        } else {
            new check().execute();
        }
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
                details.put("pwd", sPassowrd);
                details.put("name", sName);
                details.put("age", sAge);
                details.put("weight", sWeight);
                details.put("height", sHeight);
                details.put("gender", sGender);
                details.put(sMedical.toLowerCase().replace("\\s", ""), true);
                details.put(sAllergy.toLowerCase().replace("\\s", ""), true);
                details.put(sFoodAllergy.toLowerCase().replace("\\s", ""), true);
                details.put("activitylevel", sActLvl);

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

                if (json.getString("success") == "true")
                    return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return checkB;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            finish();
            if (aBoolean) {

                SharedPreferences spref = getSharedPreferences("BeacDr", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = spref.edit();
                editor.putString("id", sUsername);
                editor.commit();

                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);

            } else {
                Toast.makeText(getApplicationContext(), "Sorry, Unable to Register! Try Again later", Toast.LENGTH_LONG).show();
                Intent stay = new Intent(getApplicationContext(), SignupData.class);
                startActivity(stay);
            }

        }
    }


}
