package com.bennitai;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

//import com.ibm.mobilefirstplatform.clientsdk.android.security.mca.internal.encryption.StringEncryption;
//import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
//import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
//import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
//import com.ibm.watson.developer_cloud.service.exception.BadRequestException;
//import com.ibm.watson.developer_cloud.service.exception.UnauthorizedException;

import java.util.ArrayList;

//import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.bennitai.pedometer.PedometerSettings;
import com.bennitai.pedometer.Settings;
import com.bennitai.pedometer.StepService;
import com.worklight.wlclient.api.WLClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity   {

    private static final String USER_USER = "user";
    private static final String USER_BENNIT = "bennit";
    HorizontalScrollView horizontalScrollView;

    private BennitAIConversation m_BennitAIConversationService;
    private ArrayList<ConversationMessage> conversationLog;
    private WLClient m_client;
    private JSONObject m_googleCredentials;
    TextView entryText;

    //Pedometer variables

    private static final String TAG = "Pedometer";
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private int mStepValue;
    private boolean mQuitting = false; // Set when user selected Quit from menu, can be used by onPause, onStop, onDestroy
    private boolean mIsRunning;



    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mStepValue = 0;




        conversationLog = new ArrayList<>();
        m_client = WLClient.createInstance(this);
        m_googleCredentials = new JSONObject();

        Bundle bundle =  getIntent().getExtras();
        //String test = bundle.getString("_id");
        try
        {
            m_googleCredentials.put("_id",bundle.getString("_id"));
            m_googleCredentials.put("name", bundle.getString("name"));
            m_googleCredentials.put("email", bundle.getString("email"));
            m_googleCredentials.put("familyname", bundle.getString("familyname"));
        }
        catch (JSONException je)
        {

        }


        m_BennitAIConversationService = new BennitAIConversation(m_client, m_googleCredentials);

        // If we have a savedInstanceState, recover the previous Context and Message Log. TODO: Fix the saved instance stuff
        if (savedInstanceState != null) {
            //conversationContext = (Map<String,Object>)savedInstanceState.getSerializable("context");
            conversationLog = (ArrayList<ConversationMessage>)savedInstanceState.getSerializable("backlog");

            // Repopulate the UI with the previous Messages.
            if (conversationLog != null) {
                for (ConversationMessage message : conversationLog) {
                    addMessageFromUser(message);
                }
            }

            final ScrollView scrollView = (ScrollView)findViewById(R.id.message_scrollview);
            scrollView.scrollTo(0, scrollView.getBottom());
        } else {
            /// Send empty messgae that has just the google credentials.
            ConversationTask BAIConversationTask = new ConversationTask ();
            BAIConversationTask.execute("");
            // Validate that the user's credentials are valid and that we can continue.
            // This also kicks off the first Conversation Task to obtain the intro message from Watson.
            //ValidateCredentialsTask vct = new ValidateCredentialsTask();
            //vct.execute();
        }

        TextView Dashbord_view = (TextView)findViewById(R.id.chat_dashbord_textview);
        Dashbord_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(MainActivity.this,
                        ProductivityDashbord.class);
                startActivity(myIntent);

            }
        });

        ImageButton clientProfle_button = (ImageButton)findViewById(R.id.chat_clientsetings);
        clientProfle_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this,
                        ClientprofileSetings.class);
                startActivity(myIntent);

            }
        });

        ImageButton Safety_Button = (ImageButton)findViewById(R.id.alertButton);
        Safety_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Service Stop","Stoping..... Service");
                Toast.makeText( MainActivity.this ,"Stoping step services...", Toast.LENGTH_SHORT ).show();
                stopStepService();
                unbindStepService();
             }
        });

        ImageButton sendButton = (ImageButton)findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entryText = (TextView)findViewById(R.id.entry_text);
                String text = entryText.getText().toString();

                if (!text.isEmpty()) {
                    // Add the message to the UI.
                    addMessageFromUser(new ConversationMessage(text, USER_USER));

                    //Record the message in the conversation log.
                    conversationLog.add(new ConversationMessage(text, USER_USER));

                    // Send the message to Bennit AI backend.
                    ConversationTask BAIConversationTask = new ConversationTask ();
                    BAIConversationTask.execute(text);
                    //ConversationTask ct = new ConversationTask();
                    //ct.execute(text);

                    entryText.setText("");
                }
            }
        });
    }



    @Override
    protected void onStart() {
        Log.i(TAG, "[ACTIVITY] onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "[ACTIVITY] onResume");
        super.onResume();

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("Step_service_running_status", true);
        editor.commit();



        // Read from preferences if the service was running on the last onPause
        mIsRunning = mPedometerSettings.isServiceRunning();

        // Start the service if this is considered to be an application start (last onPause was long ago)
        if (!mIsRunning && mPedometerSettings.isNewStart()) {
            startStepService();
            bindStepService();
        }
        else if (mIsRunning) {
            bindStepService();
        }

        mPedometerSettings.clearServiceRunning();



    }

    @Override
    protected void onPause() {
        Log.i(TAG, "[ACTIVITY] onPause");
        if (mIsRunning) {
            unbindStepService();
        }
        if (mQuitting) {
            mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
        }
        else {
            mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
        }

        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "[ACTIVITY] onStop");
        super.onStop();
    }

    protected void onDestroy() {
        Log.i(TAG, "[ACTIVITY] onDestroy");
        super.onDestroy();
    }

    protected void onRestart() {
        Log.i(TAG, "[ACTIVITY] onRestart");
        super.onRestart();
    }



    private StepService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();

        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };


    private void startStepService() {
        if (! mIsRunning) {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            startService(new Intent(MainActivity.this,
                    StepService.class));
        }
    }

    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        bindService(new Intent(MainActivity.this,
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        unbindService(mConnection);
    }

    private void stopStepService() {
        Log.i(TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(TAG, "[SERVICE] stopService");
            stopService(new Intent(MainActivity.this,
                    StepService.class));
        }
        mIsRunning = false;
    }

    private void resetValues(boolean updateDisplay) {
        if (mService != null && mIsRunning) {

        }
        else {
            SharedPreferences state = getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);
                stateEditor.commit();
            }
        }
    }

    private static final int MENU_SETTINGS = 8;
    private static final int MENU_QUIT     = 9;

    private static final int MENU_PAUSE = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_RESET = 3;

    /* Creates the menu items */
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mIsRunning) {
            menu.add(0, MENU_PAUSE, 0, R.string.pause)
                    .setIcon(android.R.drawable.ic_media_pause)
                    .setShortcut('1', 'p');
        }
        else {
            menu.add(0, MENU_RESUME, 0, R.string.resume)
                    .setIcon(android.R.drawable.ic_media_play)
                    .setShortcut('1', 'p');
        }
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                .setShortcut('2', 'r');
        menu.add(0, MENU_SETTINGS, 0, R.string.settings)
                .setIcon(android.R.drawable.ic_menu_preferences)
                .setShortcut('8', 's')
                .setIntent(new Intent(this, Settings.class));
        menu.add(0, MENU_QUIT, 0, R.string.quit)
                .setIcon(android.R.drawable.ic_lock_power_off)
                .setShortcut('9', 'q');
        return true;
    }

    /* Handles item selections */

    // TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
    };

    private static final int STEPS_MSG = 1;

    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int)msg.arg1;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.clear_session_action) {
            // Clear the conversation log, the conversation context, and clear the UI.
            //conversationContext = null;
            conversationLog = new ArrayList<>();

            LinearLayout messageContainer = (LinearLayout) findViewById(R.id.message_container);
            messageContainer.removeAllViews();

            // Restart the conversation with the same empty text string sent to Watson Conversation.
//            ConversationTask ct = new ConversationTask();
//            ct.execute("");

            return true;
        }


        switch (item.getItemId()) {
            case MENU_PAUSE:
                unbindStepService();
                stopStepService();
                return true;
            case MENU_RESUME:
                startStepService();
                bindStepService();
                return true;
            case MENU_RESET:
                 resetValues(true);
                return true;
            case MENU_QUIT:
                 return true;
        }



        return super.onOptionsItemSelected(item);
    }

    @Override

    @SuppressWarnings("unchecked")
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Retain the conversation context and the log of previous messages, if they exist.
//        if (conversationContext != null) {
//            HashMap map = new HashMap(conversationContext);
//            savedInstanceState.putSerializable("context", map);
//        }
            Log.i("Saved Instance stae","Ruunning");
         if (conversationLog != null) {
            savedInstanceState.putSerializable("backlog", conversationLog);
        }
    }

    /**
     * Displays an AlertDialogFragment with the given parameters.
     * @param errorTitle Error Title from values/strings.xml.
     * @param errorMessage Error Message either from values/strings.xml or response from server.
     * @param canContinue Whether the application can continue without needing to be rebuilt.
     */
    private void showDialog(int errorTitle, String errorMessage, boolean canContinue) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(errorTitle, errorMessage, canContinue);
        newFragment.show(getFragmentManager(), "dialog");
    }

    /**
     * Adds a message dialog view to the UI.
     * @param message ConversationMessage containing a message and the sender.
     */
    private void addMessageFromUser(ConversationMessage message) {
        View messageView;
        LinearLayout messageContainer = (LinearLayout) findViewById(R.id.message_container);
        //Log.e("Msssage structure", message.getDa );
        if (message.getUser().equals(USER_BENNIT)) {
            messageView = this.getLayoutInflater().inflate(R.layout.watson_text, messageContainer, false);
            TextView watsonMessageText = (TextView)messageView.findViewById(R.id.watsonTextView);
            watsonMessageText.setText(message.getMessageText());
        } else {
            messageView = this.getLayoutInflater().inflate(R.layout.user_text, messageContainer, false);
            TextView userMessageText = (TextView)messageView.findViewById(R.id.userTextView);
            userMessageText.setText(message.getMessageText());
        }

        messageContainer.addView(messageView);

        // Scroll to the bottom of the view so the user sees the update.
        final ScrollView scrollView = (ScrollView)findViewById(R.id.message_scrollview);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void  addSuggestion(final JSONArray SuggestionArr){
        View HorizontalView;



        LinearLayout messageContainer = (LinearLayout) findViewById(R.id.message_container);
        HorizontalView = this.getLayoutInflater().inflate(R.layout.suggestion_layout, messageContainer, false);
        horizontalScrollView = (HorizontalScrollView)HorizontalView.findViewById(R.id.hsv);
        TextView Suggestion_temp, dummy_temp;
        LinearLayout hslayout = (LinearLayout) HorizontalView.findViewById(R.id.innerLay);

        try {
              for (int i = 0; i < SuggestionArr.length(); i++) {
                  final String Suggestion_text = SuggestionArr.getString(i);
                  Suggestion_temp = new TextView(this);

                  Suggestion_temp.setLayoutParams( new TableRow.LayoutParams(
                          LinearLayout.LayoutParams.WRAP_CONTENT,
                          LinearLayout.LayoutParams.WRAP_CONTENT));
                  Suggestion_temp.setBackgroundResource(R.drawable.suggestion);
                  Suggestion_temp.setPadding(30, 30, 30, 30);
                  Suggestion_temp.setTextColor(Color.rgb(0xfb, 0xc0,0x2d));
                  Suggestion_temp.setGravity(Gravity.CENTER_VERTICAL);
                  Suggestion_temp.setTextSize(15);
                  Suggestion_temp.setText(SuggestionArr.getString(i));
                  Suggestion_temp.setOnClickListener(new View.OnClickListener() {

                      @Override
                      public void onClick(View view) {


                          addMessageFromUser(new ConversationMessage(Suggestion_text, USER_USER));

                          //Record the 22 in the conversation log.
                          conversationLog.add(new ConversationMessage(Suggestion_text, USER_USER));

                          // Send the message to Bennit AI backend.
                          ConversationTask BAIConversationTask = new ConversationTask ();
                          BAIConversationTask.execute(Suggestion_text);
                          //ConversationTask ct = new ConversationTask();
                          //ct.execute(text);

                          entryText.setText("");
                      }
                  });

                  hslayout.addView(Suggestion_temp);
                  dummy_temp = new TextView(this);
                  dummy_temp.setText("    ");
                  hslayout.addView(dummy_temp);
              }
          }
        catch (Exception e)
        {
        }
        messageContainer.addView(HorizontalView);

        final ScrollView scrollView = (ScrollView)findViewById(R.id.message_scrollview);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }


    /**
     * Asynchronously contacts the Watson Conversation Service to see if provided Credentials are valid.
     */
//    private class ValidateCredentialsTask extends AsyncTask<Void, Void, Boolean> {
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//
//            // Mark whether or not the validation completes.
//            boolean success = true;
//
//            try {
//                conversationService.getToken().execute();
//            } catch (Exception ex) {
//
//                success = false;
//
//                // See if the user's credentials are valid or not, along with other errors.
//                if (ex.getClass().equals(UnauthorizedException.class) ||
//                        ex.getClass().equals(IllegalArgumentException.class)) {
//                    showDialog(R.string.error_title_invalid_credentials,
//                            getString(R.string.error_message_invalid_credentials), false);
//                } else if (ex.getCause() != null &&
//                        ex.getCause().getClass().equals(UnknownHostException.class)) {
//                    showDialog(R.string.error_title_bluemix_connection,
//                            getString(R.string.error_message_bluemix_connection), true);
//                } else {
//                    showDialog(R.string.error_title_default, ex.getMessage(), true);
//                }
//            }
//
//            return success;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean success) {
//            // If validation succeeded, then get the opening message from Watson Conversation
//            // by sending an empty input string to the ConversationTask.
//            if (success) {
//                ConversationTask ct = new ConversationTask();
//                ct.execute("");
//            }
//        }
//    }




    /**
     * Asynchronously sends the user's message to Watson Conversation and receives Watson's response.
     */
    private class ConversationTask extends AsyncTask<String, Void, String> implements callbackFromBennitai{

        @Override
        public String gotReplyFromBennitAi(final String  BennitReply, final Boolean isSuggestion, final JSONArray Suggestion_Arr) {
            try {
                runOnUiThread(new Runnable() {
                    //private String response = BennitReply;
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        try {
                            //Record the message in the conversation log.
                            conversationLog.add(new ConversationMessage(BennitReply, USER_BENNIT));
                            addMessageFromUser(new ConversationMessage(BennitReply,  USER_BENNIT));
                            if(isSuggestion)
                            {
                                addSuggestion(Suggestion_Arr);
                            }
                        } catch (Exception e) {
                            /// TODO: Handle exception correctly
                        }
                    }
                });

            }// Record the message from Watson in the conversation log.
            catch(Exception e)
            {

            }
            //conversationLog.add(new ConversationMessage("testMessage from callback", USER_BENNIT));
            return BennitReply;

        }


        @Override
        protected String doInBackground(String... params) {
            String entryText = params[0];

            try {
                // Send the message to the workspace designated in watson_credentials.xml.
                m_BennitAIConversationService.sendToBennitaiChat(entryText, this);

//                conversationContext = messageResponse.getContext();
//                return messageResponse.getText().get(0);
            } catch (Exception ex) {
                // A failure here is usually caused by an incorrect workspace in watson_credentials.
                showDialog(R.string.error_title_default, ex.getMessage(), true);

                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Add the message from Watson to the UI.
            //addMessageFromUser(new ConversationMessage(result, USER_BENNIT));

            // Record the message from Watson in the conversation log.
            //conversationLog.add(new ConversationMessage(result, USER_BENNIT));
        }
    }
}
