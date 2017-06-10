package com.bennitai;

//import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
//import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
//import com.ibm.watson.developer_cloud.service.exception.BadRequestException;
import com.worklight.common.Logger;
import com.worklight.wlclient.api.*;
import com.worklight.wlclient.auth.AccessToken;
import com.worklight.wlclient.api.WLResourceRequest;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import com.bennitai.MainActivity.*;


public class BennitAIConversation {

    private static JSONObject m_messagePayload = new JSONObject();

    private static JSONObject conversationContext = new JSONObject();
    private static UserDetails m_UserDetails = new UserDetails();
    //private String m_latestMessageFromBennit = "";
    //private callbackFromBennitai bennitaiCallback;

    WLClient m_client;// = WLClient.createInstance(this);

    public BennitAIConversation(WLClient client, JSONObject googleCredentials)
    {
        m_client = client;
        //JSONObject credentials = googleCredentials;
        //this._id = null;
//        this.email = null;
//        this.name = null;
//        this.familyname = null;
        try {
//            credentials.put("_id", "123");
//            credentials.put("email", "mobileFakeEmail@gmail.com");
//            credentials.put("name","FakeMobileName");
//            credentials.put("familyname","FakeMobileFamilyname");
            m_UserDetails.m_userDetails.put("_id",googleCredentials.getString("_id"));
            m_UserDetails.m_userDetails.put("name",googleCredentials.getString("name"));
            m_UserDetails.m_userDetails.put("email",googleCredentials.getString("email"));
            m_UserDetails.m_userDetails.put("familyname",googleCredentials.getString("familyname"));
            m_messagePayload.put("context",m_UserDetails.m_userDetails);
        }
        catch (JSONException je)
        {}

    }

    public enum messageType{
        CHAT("text"), /// This is used to send
        USERID("userid");

        private final String value;
        messageType(final String value){this.value = value;}
        @Override
        public String toString() {return value;}
    }



    private void send(String adapterEndpoint, final callbackFromBennitai callback  )
    {
        /// Set the adapter endpoint
        URI adapterPath = null;
        try
        {
            adapterPath = new URI("/adapters/BennitAI_AdapterV2/apiMobileMessageDev_v2");
            //adapterPath = new URI("/adapters/JavaScriptHTTP/apiMessage");//adapterEndpoint
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            ///Create request
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.POST);//,"login" TODO: add google auth and scoped resources

            /// Set header
            request.addHeader("Content-Type", "application/x-www-form-urlencoded");
            /// Set query
            request.setQueryParameter("params", "['" + m_messagePayload.toString() + "']");
            String paramstest = m_messagePayload.toString();
        /// Send  request
        request.send(new WLResponseListener() {
            @Override
            public void onSuccess(final WLResponse wlResponse) {
                try {
                    /// Update the context. This is important as it keeps track of nodes in conversation service.
                    m_messagePayload.put("context", wlResponse.getResponseJSON().getJSONObject("context"));

                    /// Get reply from bennit
                    boolean Suggestion_status  = false;
                    String isSuggestion_Active = wlResponse.getResponseJSON().getJSONObject("output").getString("suggestiononly");
                    Log.e("Suggestion_Active", isSuggestion_Active);
                    JSONArray Suggestion_Arr = wlResponse.getResponseJSON().getJSONObject("output").getJSONArray("suggestions");
                    if( isSuggestion_Active.equals("true") )
                    {
                        Suggestion_status = true;
                        for(int i=0;i< Suggestion_Arr.length();i++ )
                            Log.e("Suggestion values", Suggestion_Arr.getString(i) );
                    }

                    String BennitReply  = wlResponse.getResponseJSON().getJSONObject("output").getString("text");
                    /// Remove first 2 and last 2 characters
                    //BennitReply = BennitReply.substring(2, BennitReply.length()-2);   //Not required in version 2

                    callback.gotReplyFromBennitAi(BennitReply,Suggestion_status, Suggestion_Arr );


                }
                catch (Exception e)
                {
                    /// TODO: Handle exception correctly
                    //return e.toString();
                    //callback.gotReplyFromBennitAi("Exception in onSuccess"+ e.getMessage()+"   Bennitreply: ");
                }
                finally {
                    //callback.gotReplyFromBennitAi("This is an option");
                }
            }
            /// Handle the failure case
            @Override
            public void onFailure(final WLFailResponse wlFailResponse) {
                Log.i("MobileFirst Quick Start", "Failure: " + wlFailResponse.getErrorMsg());
                callback.gotReplyFromBennitAi("Adapter response Failure: " + wlFailResponse.getErrorMsg(), false , null);

            }
        });
        }
        catch (Exception e)
        {

        }
        //return m_latestMessageFromBennit;

    }


    public void sendToBennitaiChat(String message, final callbackFromBennitai callback  )
    {
        /// Build up message
        try {
            /// Since this is sent to bennitai chat it's a TEXT
            m_messagePayload.put(messageType.CHAT.toString(),message);
        }catch (JSONException e)
        {
            ///TODO: Deal with exception correctly
        }
        /// This sends m_messagePayload to the adapter endpoint given
        send("/adapters/JavaScriptHTTP/apiMessage", callback);
    }
}
