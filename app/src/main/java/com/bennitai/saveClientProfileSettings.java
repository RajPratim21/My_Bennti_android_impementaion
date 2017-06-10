package com.bennitai;

import android.app.DownloadManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class saveClientProfileSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_client_profile_settings);

        /* RequestQueue queue;
        queue = Volley.newRequestQueue(this);

        final String url = "http://bennit-ai-dev0206.mybluemix.net/api/v1/clientprofile";
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", "raj");
            jsonObject.put("name", "raj");
            jsonObject.put("familyName", "bhattacharya");
            jsonObject.put("email", "rajpratim1234@gmail.com");
            jsonObject.put("employer", "Bennit.AI");
            jsonObject.put("workplace", "India");
            jsonObject.put("role", "Full stack dev");
            jsonObject.put("area", "AI");
            jsonObject.put("expertise", "Android");
            jsonObject.put("interests", new String[]{"AI", "Business"});
            jsonObject.put("favActivity", new String[]{"Development", "Buisness"});
            jsonObject.put("favColor", new String[]{"Blue", "Red"});
            jsonObject.put("trackGoal", true);
            jsonObject.put("goalDetails", "Bennit Android app");
            jsonObject.put("enableTextToSpeech", true);
            JSONObject prodNot = new JSONObject();
            prodNot.put("enable", true);
            prodNot.put("timeToGetNotification","2017-06-07T06:05:53.298Z");
            jsonObject.put("productivityNotifications", prodNot);
            JSONObject engnot = new JSONObject();
            engnot.put("enableNotificationStartDay", false);
            engnot.put("timeToGetStartOfDayNotification", "2017-06-07T06:05:53.308Z");
            engnot.put("enableNotificationEndOfDay", true);
            engnot.put("timeToGetEndOfDayNotification", "2017-06-07T06:05:53.308Z");
            jsonObject.put("engagementNotifications", engnot);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url,jsonObject,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("Response", String.valueOf(response));
                        Toast.makeText(getApplicationContext(), (CharSequence) response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());

                        Toast.makeText(getApplicationContext(), String.valueOf(error), Toast.LENGTH_SHORT).show();

                    }
                }
        ){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("description","");
                return headers;

            }
          @Override
            public String getBodyContentType() {
                return "application/json";
            }
            @Override
            public byte[] getBody() {

                // final JSONObject jsonsend =  new JSONObject( "{\n  \"userId\": \"gqc\",\n  \"name\": \"aaBOphTeneJ\",\n  \"familyName\": \"mwvNCRSt\",\n  \"email\": \"ongEcypz\",\n  \"employer\": \"xBJ\",\n  \"workplace\": \"SowtQ\",\n  \"role\": \"Pe\",\n  \"area\": \"K\",\n  \"expertise\": \"SOJ\",\n  \"interests\": [\n    \"dE\"\n  ],\n  \"favActivity\": [\n    \"tgkO\"\n  ],\n  \"favColor\": [\n    \"LPzeyATpzEv\"\n  ],\n  \"trackGoal\": false,\n  \"goalDetails\": \"yb\",\n  \"enableTextToSpeech\": false,\n  \"productivityNotifications\": {\n    \"enable\": true,\n    \"timeToGetNotification\": \"2017-06-02T11:39:25.049Z\"\n  },\n  \"engagementNotifications\": {\n    \"enableNotificationStartDay\": false,\n    \"timeToGetStartOfDayNotification\": \"2017-06-02T11:39:25.049Z\",\n    \"enableNotificationEndOfDay\": false,\n    \"timeToGetEndOfDayNotification\": \"2017-06-02T11:39:25.049Z\"\n  }\n}" );
                Log.i("json", jsonObject.toString());
                return jsonObject.toString().getBytes();

            }
        };


        queue.add(putRequest);
        */
    }

}
