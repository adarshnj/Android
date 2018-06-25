package mc.nj.proj;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

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

    boolean check = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        check = true;
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences spref = this.getActivity().getSharedPreferences("BeacDr", Context.MODE_PRIVATE);
        sUsername = spref.getString("id", "nobody");

        final View profile = inflater.inflate(R.layout.fragment_profile, container, false);
        final String mName = ((MainActivity) this.getActivity()).uName;
        String mPassword = ((MainActivity) this.getActivity()).uPassword;
        Integer mHeight = ((MainActivity) this.getActivity()).uHeight;
        Integer mAge = ((MainActivity) this.getActivity()).uAge;
        Integer mWeight = ((MainActivity) this.getActivity()).uWeight;
        String mactLvl = ((MainActivity) this.getActivity()).uactLvl;
        String mbmi = ((MainActivity) this.getActivity()).ubmi;

        Log.d("TAG", "onCreateView: " + mAge);

        ((TextView) profile.findViewById(R.id.usernameUpdate)).setText("Hello, " + mName);
        ((NumberPicker) profile.findViewById(R.id.ageUpdate)).setMinValue(10);
        ((NumberPicker) profile.findViewById(R.id.ageUpdate)).setMaxValue(80);
        ((NumberPicker) profile.findViewById(R.id.weightUpdate)).setMinValue(50);
        ((NumberPicker) profile.findViewById(R.id.weightUpdate)).setMaxValue(200);
        ((NumberPicker) profile.findViewById(R.id.heightUpdate)).setMinValue(120);
        ((NumberPicker) profile.findViewById(R.id.heightUpdate)).setMaxValue(200);

        ((NumberPicker) profile.findViewById(R.id.ageUpdate)).setValue(mAge);
        ((NumberPicker) profile.findViewById(R.id.weightUpdate)).setValue(mWeight);
        ((NumberPicker) profile.findViewById(R.id.heightUpdate)).setValue(mHeight);
        ((TextView) profile.findViewById(R.id.bmi)).setText(mbmi);
        String nlevel = "level" + mactLvl;
        int level = getResources().getIdentifier(nlevel, "id", "mc.nj.proj");
        ((RadioButton) profile.findViewById(level)).setChecked(true);


        ((Button) profile.findViewById(R.id.update)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sName = mName;
                sHeight = ((NumberPicker) profile.findViewById(R.id.heightUpdate)).getValue();
                sWeight = ((NumberPicker) profile.findViewById(R.id.weightUpdate)).getValue();
                sAge = ((NumberPicker) profile.findViewById(R.id.ageUpdate)).getValue();
//                sMedical = ((Spinner) profile.findViewById(R.id.medicalListUpdate)).getSelectedItem().toString();
//                sAllergy = ((Spinner) profile.findViewById(R.id.allergyListUpdate)).getSelectedItem().toString();
//                sFoodAllergy = ((Spinner) profile.findViewById(R.id.foodallergyListUpdate)).getSelectedItem().toString();
                RadioGroup rg = (RadioGroup) profile.findViewById(R.id.activity_levelUpdate);
                sActLvl = ((RadioButton) profile.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                new check().execute();
            }
        });


        // Inflate the layout for this fragment
        return profile;
    }

    private class check extends AsyncTask<Void, Void, Boolean> {
        public boolean checkB;

        public check() {
            this.checkB = false;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String Url = HomeActivity.ipaddr+"/user_profile_update";
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
                JSONObject details = new JSONObject();
                details.put("_id", sUsername);
                details.put("pwd", sPassowrd);
                details.put("name", sName);
                details.put("age", sAge);
                details.put("weight", sWeight);
                details.put("height", sHeight);
                details.put("gender", sGender);
//                details.put(sMedical.toLowerCase().replace("\\s", ""), true);
//                details.put(sAllergy.toLowerCase().replace("\\s", ""), true);
//                details.put(sFoodAllergy.toLowerCase().replace("\\s", ""), true);
                details.put("Actlvl", sActLvl);

                osw.write(details.toString());
                osw.flush();
                osw.close();
                resp = new BufferedReader(new InputStreamReader(client.getInputStream()));

                while ((line = resp.readLine()) != null) {
                    response.append(line);
                }

                Log.d("TAG", "doInBackground: " + response.toString());
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
            super.onPostExecute(aBoolean);
            if (aBoolean)
                Toast.makeText(getContext(), "Successfully Updated!!", Toast.LENGTH_LONG).show();

        }
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
}