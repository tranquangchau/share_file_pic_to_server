package com.example.administrator.sharefile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    TextView messageText;
    Button uploadButton,btn_save,btn_delete;
    EditText e_te;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    Spinner Sload;
    String upLoadServerUri = null;

    /**********  File Path *************/
    final String uploadFilePath = "/mnt/sdcard/";
    final String uploadFileName = "a.jpg";
    Integer index_sload=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadButton = (Button)findViewById(R.id.uploadButton);
        btn_save = (Button)findViewById(R.id.button);
        btn_delete = (Button)findViewById(R.id.delete);
        e_te = (EditText)findViewById(R.id.editText);
        messageText  = (TextView)findViewById(R.id.messageText);
        Sload = (Spinner) findViewById(R.id.spinner);

        Sload.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                try {
                    if(index_sload>0){
                        String s1 = String.valueOf(Sload.getSelectedItem());
                        e_te.setText(s1);
                    }
                    index_sload++;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        messageText.setText("Uploading file path :- '/mnt/sdcard/" + uploadFileName + "'");

        loadDataFromAndroid();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = ProgressDialog.show(MainActivity.this, "", "Uploading file...", true);

                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                messageText.setText("uploading started.....");
                            }
                        });

                        uploadFile(uploadFilePath + uploadFileName);

                    }
                }).start();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(e_te.getText().toString()).equals("")) {
                    SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("url_server_link", e_te.getText().toString());
                    editor.commit();
                    //saveDataAndroid(e_te.getText().toString());
                    Log.d("saveok", "save_ok");
                    messageText.setText("Save Setting ok");
                }
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences score0Editor = getSharedPreferences("playlists_all", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = score0Editor.edit();
                String[] playlists = list_sf.split(",");
//        String[] playlists_in = {"frog", "toad", "squirrel"};
                StringBuilder sb = new StringBuilder();
                // String new_save ="";
                String s1 = String.valueOf(Sload.getSelectedItem());
                for (int i = 0; i < playlists.length; i++) {
                    //sb.append(playlists[i]).append(",");
                    if (!s1.equals(playlists[i])) {
                        sb.append(playlists[i]).append(",");
                    }
                }
                myString = String.valueOf("");
                list_sf = String.valueOf(sb);
                editor1.putString("playlists", list_sf);
                editor1.commit();
                loadArrayString();
            }
        });
    }

    String list_sf="";
    public void loadArrayString(){
        //Retrieve the values
        SharedPreferences myScores = getSharedPreferences("playlists_all", Activity.MODE_PRIVATE);
        list_sf = myScores.getString("playlists", null);
        Log.d("myScores set", String.valueOf(list_sf));
        if(list_sf!=null){
            String[] playlists = list_sf.split(",");
            load_select_spinner(playlists);
        }

    }
    private void saveDataAndroid(String s1){
        //Set the values
        SharedPreferences score0Editor = getSharedPreferences("playlists_all", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = score0Editor.edit();

        String[] playlists = list_sf.split(",");
//        String[] playlists_in = {"frog", "toad", "squirrel"};
        StringBuilder sb = new StringBuilder();
       // String new_save ="";
        boolean check_exit=false;
        for (int i = 0; i < playlists.length; i++) {
            //sb.append(playlists[i]).append(",");
            if(s1.equals(playlists[i])){
                check_exit=true;
            }
            sb.append(playlists[i]).append(",");
        }
        if(!check_exit){
            sb.append(s1).append(",");
        }
        myString= String.valueOf(s1);
        list_sf= String.valueOf(sb);
        editor1.putString("playlists", list_sf);
        editor1.commit();
        loadArrayString();

    }
    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    //String colors[] = {"Red","Blue","White","Yellow","Black", "Green","Purple","Orange","Grey"};
    private void load_select_spinner(String colors[]) {
//        List<String> list = new ArrayList<String>();
//        list.add("list 1");
//        list.add("list 2");
//        list.add("list 3");
       // ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, colors);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, colors);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sload.setAdapter(dataAdapter);
        Log.d("myString","1_"+ myString);
        if(!myString.equals("")){
            Sload.setSelection(getIndex(Sload, myString));
            Log.d("setSelection", "ok");
        }


    }
    String myString="";
    private void loadDataFromAndroid() {
        /************* Php script path ****************/
        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String myStrValue = sp.getString("url_server_link", "null");
        Log.d("myStrValue", myStrValue);
        upLoadServerUri = myStrValue;
        if(myStrValue=="null"){
            upLoadServerUri = getResources().getText(R.string.url_link_default).toString();
        }
        e_te.setText(upLoadServerUri);
        loadArrayString();
    }

    public int uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    + uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                    messageText.setText("Source File not exist :"
                            +uploadFilePath + "" + uploadFileName);
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=uploaded_file;filename="
                                + fileName + "" + lineEnd);

                        dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +" http://www.androidexample.com/media/uploads/"
                                    +uploadFileName;

                            messageText.setText(msg);
                            Toast.makeText(MainActivity.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(MainActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(MainActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload_Excepti", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
