package mc.nj.proj;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public List<recentData> data;


    public String uName;
    public String uPassword;
    public Integer uAge;
    public Integer uWeight;
    public Integer uHeight;
    public String uGender;
    public String uMedical;
    public String uAllergy;
    public String uFoodAllergy;
    public String uactLvl;
    public String ubmi;

    public static recentData yrec;
    public static List<recentData> ylist = new ArrayList<recentData>();
    public static String ytype;


    public String beacfdbkresp;

    public FragmentManager fManager = getSupportFragmentManager();
    public FragmentTransaction fTransaction = fManager.beginTransaction();

    public Fragment beaconfragment;//= fManager.findFragmentByTag("beaconfragment");
    public Fragment recentfragment = fManager.findFragmentByTag("recentfragment");
    public Fragment profilefragment = fManager.findFragmentByTag("profilefragment");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        Fragment beaconFragment = getFragmentManager().beginTransaction()
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new getdata().execute();
        SharedPreferences spref = this.getSharedPreferences("BeacDr", Context.MODE_PRIVATE);
        BottomNavigationView nav = (BottomNavigationView) findViewById(R.id.fragmenu);
        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });
        nav.setSelectedItemId(R.id.BeaconFrag);
    }

    public void selectFragment(MenuItem i) {

        switch (i.getItemId()) {
            case R.id.RecentFrag:
                if (recentfragment == null)
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragments, new RecentFragment()).addToBackStack(null).commit();
                else
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragments, recentfragment).addToBackStack(null).commit();
                break;
            case R.id.BeaconFrag:
                String backStateName = "beaconfragment";
                if (beaconfragment == null) {
                    beaconfragment = new BeaconFragment();
//                    Toast.makeText(getApplicationContext(), "if", Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragments, beaconfragment).addToBackStack(backStateName).commit();
                } else {
//                    Toast.makeText(getApplicationContext(), "else", Toast.LENGTH_SHORT).show();
                    FragmentManager manager = getSupportFragmentManager();
                    boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
//                    if (fragmentPopped)
//                        Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_SHORT).show();
//                    else
//                        Toast.makeText(getApplicationContext(), "false", Toast.LENGTH_SHORT).show();
                    if (!fragmentPopped) { //fragment not in back stack, create it.
                        FragmentTransaction ft = manager.beginTransaction();
                        ft.replace(R.id.fragments, new BeaconFragment()).addToBackStack(backStateName).commit();
                    }

                }
                break;
            case R.id.ProfileFrag:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragments, new ProfileFragment()).addToBackStack(null).commit();
                break;
            default:
                Toast.makeText(getApplicationContext(), "None", Toast.LENGTH_LONG).show();
                break;
        }
    }


    public void buttonClick(View v) {
        View profile = (View) findViewById(R.id.ProfileFrag);
        switch (v.getId()) {
            case R.id.signout:
                Intent signout = new Intent(v.getContext(), HomeActivity.class);
                SharedPreferences spref = getSharedPreferences("BeacDr", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = spref.edit();
                editor.remove("id");
                editor.commit();
                startActivity(signout);
                break;
            case R.id.fdbkNo:
                beaconfragment.getView().findViewById(R.id.beaconFeedbackHolder).setVisibility(View.INVISIBLE);
                ((RadioButton) beaconfragment.getView().findViewById(R.id.fdbkNo)).setChecked(false);
                new bcnresp(false).execute();
                break;
            case R.id.fdbkYes:
                DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                Log.d("Yes", "buttonClick: " + ytype);
                ylist.add(new recentData(date.toString(), ytype, "abc"));

                beaconfragment.getView().findViewById(R.id.beaconFeedbackHolder).setVisibility(View.INVISIBLE);
                ((RadioButton) beaconfragment.getView().findViewById(R.id.fdbkYes)).setChecked(false);
                new bcnresp(true).execute();
                break;

        }
    }

    public class bcnresp extends AsyncTask<Void, Void, Void> {

        boolean res;

        public bcnresp(boolean r) {
            this.res = r;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SharedPreferences spref = getSharedPreferences("BeacDr", Context.MODE_PRIVATE);

                String addrURL = HomeActivity.ipaddr + "/find/" + spref.getString("bcnid", "0");
                URL url = new URL(addrURL);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setConnectTimeout(1000);
                client.setReadTimeout(1500);
                client.setDoOutput(true);
                client.setRequestMethod("POST");
                client.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());
                BufferedReader resp;
                String line = new String();
                JSONObject details = new JSONObject();

                details.put("_id", spref.getString("id", "null"));
                details.put("feedback", res);
                details.put("beacon_id", spref.getString("bcnid", "0"));
                osw.write(details.toString());
                osw.flush();
                osw.close();
                resp = new BufferedReader(new InputStreamReader(client.getInputStream()));
                resp.close();
                client.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class getdata extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                SharedPreferences spref = getSharedPreferences("BeacDr", Context.MODE_PRIVATE);
                String loginURL = HomeActivity.ipaddr + "/get_user_data";
                URL url = new URL(loginURL);

                BufferedReader resp;
                StringBuilder response = new StringBuilder();
                String line = new String();
                JSONObject details = new JSONObject();
                JSONObject json;

                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setConnectTimeout(1000);
                client.setReadTimeout(1500);
                client.setDoOutput(true);
                client.setRequestMethod("POST");
                client.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter osw = new OutputStreamWriter(client.getOutputStream());
                details.put("_id", spref.getString("id", "null"));
                osw.write(details.toString());
                osw.flush();
                osw.close();
                resp = new BufferedReader(new InputStreamReader(client.getInputStream()));

                while ((line = resp.readLine()) != null) {
                    response.append(line);
                }
                json = new JSONObject(response.toString());
                resp.close();
                client.disconnect();
                System.out.println(response.toString());

                uName = json.getString("name");
                uPassword = json.getString("pwd");
                uAge = Integer.parseInt(json.getString("age"));
                uGender = json.getString("gender");
                ubmi = json.getString("bmi");
                uHeight = Integer.parseInt(json.getString("height"));
                uWeight = Integer.parseInt(json.getString("weight"));
                uactLvl = json.getString("activitylevel");

                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
//            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean) {
                Log.d("TAG", "onPostExecute: set default");
                uName = "name";
                uPassword = "pwd";
                uAge = 10;
                ubmi = "1000";
                uGender = "Male";
                uHeight = 150;
                uWeight = 120;
                uMedical = "Healthy";
                uAllergy = "Pollen";
                uFoodAllergy = "Pollen";
                uactLvl = "2";
            }
        }
    }
}