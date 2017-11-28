package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.test.mock.MockContentResolver;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 *
 * @author stevko
 * @author pavanjoshi
 *
 */
public class GroupMessengerActivity extends Activity {
    private static final String TAG = GroupMessengerActivity.class.getName();
    private static final String SERVER_PORT = "10000";
    private static final String[] REMOTE_PORTS = {"11108","11112","11116","11120","11124"};

    private Uri buildUri(String scheme, String authority){
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        /*
         * Sets up a telephony manager to access Telephony services to send messages across the network
         *
         */
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        /*
         * Custom Hack to get the AVD's working
         */
        String portNumber = telephonyManager.getLine1Number().substring(telephonyManager.getLine1Number().length() - 4);
        final String self_port = String.valueOf(Integer.valueOf(portNumber)*2);



        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        final TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */
        try{
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(SERVER_PORT));
            new ServerThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,serverSocket);
        }catch(IOException e){
            Log.e(TAG,"Async Task Exception: ServerSocket: I/O Exception");
        }
        final EditText editText = (EditText) findViewById(R.id.editText1);

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString();

                if(!msg.isEmpty()) {

                    new ClientThread().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg);
                    editText.setText("");
                }
                else{
                    Log.i("GroupMessenger","Trying to send an empty msg");
                    Toast.makeText(getApplicationContext(),"No Text Input",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }

    private class ClientThread extends AsyncTask<String, Void, Void>{
        private final String TAG = ClientThread.class.getName();
        @Override
        protected Void doInBackground(String... msgs){

            /*
             * Creating and registering the socket to the SERVER_IP and SERVER_PORT
             */
            try {
                for(int i = 0 ; i < REMOTE_PORTS.length ; i++ ) {
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(REMOTE_PORTS[i]));

                    String msg = msgs[0];
                    int msg_size = msg.length();

                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(msg_size);
                    Log.v(TAG, Integer.toString(msg_size));
                    outputStream.flush();
                    outputStream.write(msg.getBytes());
                    outputStream.flush();


                    outputStream.close();
                }


            }catch(UnknownHostException e){
                Log.e(TAG,"Cannot connnect to the socket: Unknown Host");
                e.printStackTrace();
            }catch(IOException e){
                Log.e(TAG,"Cannot connect to the socket: I/O Error");
                e.printStackTrace();
            }

            return null;
        }
    }
    private class ServerThread extends AsyncTask<ServerSocket,String, Void>{
        private final String TAG = ServerThread.class.getName();
        private int count = 0;
        @Override
        protected Void doInBackground(ServerSocket... serverSocket){

            try {
                while(true) {

                    Socket socket = serverSocket[0].accept();
                    InputStream inputStream = socket.getInputStream();
                    int msg_size = inputStream.read();
                    Log.v(TAG, Integer.toString(msg_size));

                    byte input[] = new byte[msg_size];
                    inputStream.read(input);
                    publishProgress(new String(input));
                    socket.close();
                }

            } catch (IOException e) {
                Log.e(TAG, "Cannot Resolve Socket: I/O Exception");
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onProgressUpdate(String... values) {
            TextView textView = (TextView) findViewById(R.id.textView1);
            String msg = values[0];
            ContentValues contentValues = new ContentValues();
            contentValues.put("key",Integer.toString(count++));
            contentValues.put("value",msg);
            Uri uri = buildUri("content","edu.buffalo.cse.cse486586.groupmessenger1.provider");
            getContentResolver().insert(uri,contentValues);
            textView.append(msg);
            textView.append("\n");

        }
    }
}
