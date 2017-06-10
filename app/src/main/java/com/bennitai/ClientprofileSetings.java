package com.bennitai;

import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bennitai.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClientprofileSetings extends AppCompatActivity {
     RequestQueue queue ;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientprofile_setings);
        queue = Volley.newRequestQueue(this);
        ImageButton goBack = (ImageButton)findViewById(R.id.Fromclient_goback);
         goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

         TextView editText = (TextView)findViewById(R.id.client_profile_edit);
         editText.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View view) {
                 startActivity(new Intent(ClientprofileSetings.this, saveClientProfileSettings.class));
             }
         });
        final String url = "http://bennit-ai-dev0206.mybluemix.net/api/v1/clientprofile";
         // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest( Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        try {
                            // Parsing json object response
                            // response will be a json object
                            String name = response.getString("name");
                            String familyname = response.getString("familyName");
                            String email = response.getString("email");
                            String employer = response.getString("employer");
                            String role = response.getString("role");
                            String area = response.getString("area");
                            String goalDetails = response.getString("goalDetails");
                            boolean trackGoal = response.getBoolean("trackGoal");
                            boolean enableTextToSpeech = response.getBoolean("enableTextToSpeech");
                            boolean productivityNotification_status = response.getJSONObject("productivityNotifications").getBoolean("enable");
                            CharSequence timetogetNot = response.getJSONObject("productivityNotifications").getString("timeToGetNotification");
                            //JSONObject engagementObject = response.getJSONObject("engagementNotification");
                            boolean enableNotificationStartDay = response.getJSONObject("engagementNotifications").getBoolean("enableNotificationStartDay");
                            boolean enableNotificationEndOfDay = response.getJSONObject("engagementNotifications").getBoolean("enableNotificationEndOfDay");
                            CharSequence timestartOfDay = response.getJSONObject("engagementNotifications").getString("timeToGetStartOfDayNotification");
                            CharSequence timeendOfDay = response.getJSONObject("engagementNotifications").getString("timeToGetEndOfDayNotification");
                            Log.i("employer",employer);
                            Log.i("role",role);
                            Log.i("area",area);

                            //JSONArray interests = response.getJSONArray("interests");
                            TextView name_view = (TextView)findViewById(R.id.name_view);
                            TextView email_view = (TextView)findViewById(R.id.email_view);
                            TextView employerView = (TextView)findViewById(R.id.employer_text_value);
                            TextView RoleView = (TextView)findViewById(R.id.Role_text_value);
                            TextView AreaView = (TextView)findViewById(R.id.Area_text_value);
                            TextView GoalView = (TextView)findViewById(R.id.Goal_text_value);
                            Switch enableT2Sswitch = (Switch)findViewById(R.id.enable_t2s_switch);
                            Switch enableSODswitch = (Switch)findViewById(R.id.enable_sOd_switch);
                            Switch enableEODswitch = (Switch)findViewById(R.id.enable_eOd_switch);
                            Switch trackGOalSwitch = (Switch)findViewById(R.id.trackGoal_switch);
                            Switch ProdNotSwitch = (Switch)findViewById(R.id.ProdNot_switch);
                            TextClock SOD = (TextClock)findViewById(R.id.SODtextClock);
                            TextClock EOD = (TextClock)findViewById(R.id.EODtextClock);
                            TextClock TON = (TextClock)findViewById(R.id.PNtextClock);
                            name_view.setText(name);
                            email_view.setText(email);
                            employerView.setText(employer);
                            RoleView.setText(role);
                            AreaView.setText(area);
                            GoalView.setText(goalDetails);
                            enableT2Sswitch.setChecked(enableTextToSpeech);
                            enableSODswitch.setChecked(enableNotificationStartDay);
                            enableEODswitch.setChecked(enableNotificationEndOfDay);
                            ProdNotSwitch.setChecked(productivityNotification_status);
                            trackGOalSwitch.setChecked(trackGoal);
                            SOD.setFormat24Hour(timestartOfDay);
                            EOD.setFormat24Hour(timeendOfDay);
                            TON.setFormat24Hour(timetogetNot);
                             //items.setTitle("Intersrs");
                             //items.setDescription(String.valueOf(interests.getJSONObject(0)));
                             //listofitems.add(items);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error:getting json var"+e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }
        );

            // add it to the RequestQueue
                    queue.add(getRequest);
//        ArrayAdapter adapter = new ArrayAdapter<String>(this,
  //              R.layout.client_basic_profile , mobileArray);


    }


}
