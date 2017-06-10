package com.bennitai;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Benjamin on 2/22/2017.
 */



public class UserDetails {

    public JSONObject m_userDetails = new JSONObject();

    public UserDetails()
    {
        try {
            m_userDetails.put("_id", "FakeMobile_id");
            m_userDetails.put("email","FakeMobile_email");
            m_userDetails.put("name","FakeMobile_name");
            m_userDetails.put("familyname","FakeMobile_familyname");

            // / Device
            m_userDetails.put("deviceid","");
            m_userDetails.put("nodevice","");
            m_userDetails.put("listendeviceid","");

            // / EAP Registration
            m_userDetails.put("workplace","");
            m_userDetails.put("role","");
            m_userDetails.put("area","");
            m_userDetails.put("expertise","");
            m_userDetails.put("activitylist","");
            m_userDetails.put("favactivity","");
            m_userDetails.put("favcolor","");
            m_userDetails.put("trackgoal", false);
            m_userDetails.put("goaldetail","");

            // / EAP Score
            m_userDetails.put("bennitscore",10);
            m_userDetails.put("scoreregistration",false);
            m_userDetails.put("scorechangedetails",false);
            m_userDetails.put("scorestart", false);
            m_userDetails.put("scoreleaving", false);
            m_userDetails.put("scorelog", false);
            m_userDetails.put("scoreidea", false);
            m_userDetails.put("scorefeedback", false);
            m_userDetails.put("scoresafety", false);
            m_userDetails.put("scoreevent", false);
            m_userDetails.put("scorereport", false);

            // / Bennit
            m_userDetails.put("machine","");
            m_userDetails.put("areaflow","");
            m_userDetails.put("hybridareaflow","");
            m_userDetails.put("routing","");
            m_userDetails.put("scheduling","");
            m_userDetails.put("schedulingcombined","");
            m_userDetails.put("mix","");
            m_userDetails.put("shiftnumrun","");
            m_userDetails.put("shifttimenight","");
            m_userDetails.put("shifttimemorning","");
            m_userDetails.put("shifttimeafternoon","");
            m_userDetails.put("numoperations","");
            m_userDetails.put("numgroup","");
        }
        catch (JSONException je)
        {
            Log.println(Log.ERROR,"UserDetails","Failed to create userDetails"+je.getMessage());
        }
    }


    // / TODO: store profile picture, gender.


}
