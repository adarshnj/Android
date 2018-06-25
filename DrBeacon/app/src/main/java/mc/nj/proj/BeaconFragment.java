package mc.nj.proj;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.*;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BeaconFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BeaconFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class BeaconFragment extends Fragment implements BeaconConsumer {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    //    int count = -1;
    Identifier bcnIns;

    View beacon;
    boolean once = false;
    private String TAG = "beac tag :";
    boolean check = false;


    private BeaconManager beaconManager;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BeaconFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BeaconFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BeaconFragment newInstance(String param1, String param2) {
        BeaconFragment fragment = new BeaconFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return getActivity().bindService(intent, serviceConnection, i);
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.bind(this);
        setRetainInstance(true);
        check = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (check) {
            beacon = inflater.inflate(R.layout.fragment_beacon, container, false);

            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Arvin Regular.ttf");

            ((TextView) beacon.findViewById(R.id.beaconData)).setTypeface(font);
            ((TextView) beacon.findViewById(R.id.feedbk_bcn)).setTypeface(font);

            ImageView imageView = (ImageView) beacon.findViewById(R.id.activity);
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
            Glide.with(this).load("https://media.giphy.com/media/MaXOUjkV73aO4/giphy.gif").into(imageViewTarget);

            ImageView imagetView = (ImageView) beacon.findViewById(R.id.activityType);
            GlideDrawableImageViewTarget imagetViewTarget = new GlideDrawableImageViewTarget(imagetView);
            Glide.with(this).load("drawable/beacon.png").into(imagetViewTarget);
            check = false;
        }
        return beacon;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onBeaconServiceConnect() {
        Identifier namespaceId = Identifier.parse("00000000000000000009");
        final Region region = new Region("myBeacons", namespaceId, null, null);

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.d(TAG, "didEnterRegion: ");

                try {
                    beaconManager.startRangingBeaconsInRegion(region);
                    once = true;
//                    count = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // send beacon and user id to node and wait for reply
                // on successful response send notification
            }

            @Override
            public void didExitRegion(Region region) {
                SharedPreferences spref = getActivity().getSharedPreferences("BeacDr", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = spref.edit();
                editor.remove("bcnid");
                editor.commit();

//                ((TextView) beacon.findViewById(R.id.beaconData)).setText("Looking for Information Around you");
//                ImageView imageView = (ImageView) beacon.findViewById(R.id.activity);
//                GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
//                Glide.with(getApplicationContext()).load("https://media.giphy.com/media/MaXOUjkV73aO4/giphy.gif").into(imageViewTarget);
//                ImageView imagetView = (ImageView) beacon.findViewById(R.id.activityType);
//                GlideDrawableImageViewTarget imagetViewTarget = new GlideDrawableImageViewTarget(imagetView);
//                Glide.with(getApplicationContext()).load("drawable/beacon.png").into(imagetViewTarget);


                Log.d(TAG, "didExitRegion: ");
                try {
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });


        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {

                SharedPreferences spref = getActivity().getSharedPreferences("BeacDr", Context.MODE_PRIVATE);
                if (once)
//                if (count % 5 == 0)
                {
                    for (Beacon bcn : collection) {
                        if (bcn.getId2() != null) {
                            bcnIns = bcn.getId2();
                            Log.d(TAG, "didRangeBeaconsInRegion: " + bcnIns);
                            once = false;
//                            count++;
                            SharedPreferences.Editor editor = spref.edit();
                            editor.putString("bcnid", bcnIns.toString().substring(bcnIns.toString().length() - 1));
                            editor.commit();
                            if (spref.getString("id", "null") != "null")
                                new beacon(bcnIns.toString().substring(bcnIns.toString().length() - 1)).execute();
                            break;
                        }
                    }
                }
            }
        });

        try

        {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (
                RemoteException e)

        {
        }

    }

    public class beacon extends AsyncTask<Void, Void, Boolean> {
        String beaconNo;
        StringBuilder response = new StringBuilder();
        JSONObject json;

        beacon(String id) {
            this.beaconNo = id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {


            boolean ret = false;
            try {
                String addrURL = HomeActivity.ipaddr + "/find/" + beaconNo;
                SharedPreferences spref = getActivity().getSharedPreferences("BeacDr", Context.MODE_PRIVATE);

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
                details.put("beacon_id", beaconNo);
                osw.write(details.toString());
                osw.flush();
                osw.close();
                resp = new BufferedReader(new InputStreamReader(client.getInputStream()));

                while ((line = resp.readLine()) != null) {
                    response.append(line);
                }
                json = new JSONObject(response.toString());
                Log.d(TAG, "doInBackground: " + response.toString());

                if (json.getString("ignore") == "false") {
                    ret = true;
                }
                resp.close();
                client.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {

                try {
                    // Set fragment data
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.next)
                                    .setContentTitle("Beacon Detected")
                                    .setAutoCancel(true)
                                    .setContentText(json.getString("notify"))
                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    Intent beaconsend = new Intent(getActivity(), HomeActivity.class);
                    PendingIntent contIn = PendingIntent.getActivity(getApplicationContext(), 0, beaconsend, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(contIn);
                    mNotificationManager.notify(001, mBuilder.build());

                    ((LinearLayout) beacon.findViewById(R.id.beaconFeedbackHolder)).setVisibility(View.VISIBLE);

                    ImageView activityImage = (ImageView) beacon.findViewById(R.id.activity);
                    ImageView activityTypeImg = (ImageView) beacon.findViewById(R.id.activityType);
                    TextView bcnData = (TextView) beacon.findViewById(R.id.beaconData);
                    TextView feedbackText = (TextView) beacon.findViewById(R.id.feedbk_bcn);
                    RadioGroup rg = ((RadioGroup) beacon.findViewById(R.id.bcnFdbk));
                    RadioButton feedback = (RadioButton) beacon.findViewById(rg.getCheckedRadioButtonId());

//                Fragment profile = (Fragment) findViewById(R.id.ProfileFrag);
//                ((RadioGroup) beacon.findViewById(R.id.bcnFdbk)).setVisibility(View.VISIBLE);

                    String image = json.getString("activitytype");
                    bcnData.setText(json.getString("message"));
                    feedbackText.setText(json.getString("feedback"));
                    Glide.with(getContext()).load(json.getString("imageurl")).into(activityImage);

                    ((MainActivity)getActivity()).ytype = json.getString("notify");

                    StringBuilder foodData = new StringBuilder();
                    switch (image) {
                        case "food":
                            activityTypeImg.setImageResource(R.drawable.food);
                            try {
                                foodData = new StringBuilder(json.getString("message"));
                                foodData.append("\n\n");
                                JSONObject menu = json.getJSONObject("response");
                                for (int i = 0; i < menu.names().length(); i++) {
                                    String keys = menu.names().getString(i);
                                    String values = menu.get(menu.names().getString(i)).toString();
                                    foodData.append(keys);
                                    foodData.append(" @ ");
                                    foodData.append(values);
                                    foodData.append("\n");
                                    Log.v(TAG, "key = " + keys + " value = " + values);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            bcnData.setText(foodData.toString());
                            break;
                        case "drink":
                            activityTypeImg.setImageResource(R.drawable.drink);
                            break;
                        case "activity":
                            activityTypeImg.setImageResource(R.drawable.a1);
                            break;
                        case "pollen":
                            activityTypeImg.setImageResource(R.drawable.allergy);
                            break;
                        case "cycle":
                            activityTypeImg.setImageResource(R.drawable.cycle);
                            break;
                        case "yoga":
                            activityTypeImg.setImageResource(R.drawable.yoga);
                            break;
                        case "gym":
                            activityTypeImg.setImageResource(R.drawable.gym);
                            break;

                        default:
                            activityTypeImg.setImageResource(R.drawable.beacon);
                            break;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
