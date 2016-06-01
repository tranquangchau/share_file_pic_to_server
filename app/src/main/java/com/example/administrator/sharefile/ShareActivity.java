package com.example.administrator.sharefile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ShareActivity extends AppCompatActivity {

    TextView messageText;
    ProgressDialog dialog = null;
    int serverResponseCode = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        messageText  = (TextView)findViewById(R.id.messageText1);

       /* String uriToImage = "";
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        Log.d("File_non", uriToImage);
        shareIntent.setType("image/jpeg");
        //startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
        Log.d("Resource", getResources().getText(R.string.send_to).toString());*/


        //final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        //shareIntent.setType("image/jpg");
        //final File photoFile = new File(getFilesDir(), "foo.jpg");
        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        String receivedType = receivedIntent.getType();
        //String name = intent.getData().getLastPathSegment();
        if(receivedType.startsWith("image/")){
            //handle sent image
        }
        Uri receivedUri = (Uri)receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (receivedUri != null) {
            //set the picture
            //RESAMPLE YOUR IMAGE DATA BEFORE DISPLAYING
            //picView.setImageURI(receivedUri);//just for demonstration
            Log.d("File_non", receivedUri.toString());
            //final String uploadFilePath = "/mnt/sdcard/";
            //string.replace("=\"ppshein\"", "");

            String m = receivedUri.toString();
            m=m.replace("file:///storage/sdcard0/","/mnt/sdcard/");
            //m=m.replace(" ","\\ ");
            m=m.replace("%20"," ");
            Log.d("File_non", m);
            //shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
            //startActivity(Intent.createChooser(shareIntent, "Share image using"));
            uploadFile(m);
            //share_file(m);
        }


    }
    public String upLoadServerUri;

    public int uploadFile(String sourceFileUri) {

        dialog = ProgressDialog.show(ShareActivity.this, "", "Uploading file...", true);
        String fileName = sourceFileUri;

        //final String uploadFilePath = "/mnt/sdcard/";
        final String uploadFilePath = sourceFileUri;
        final String uploadFileName = "";
        //final String upLoadServerUri = getResources().getText(R.string.url_link_default).toString();


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

                //get data save url
                SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                String myStrValue = sp.getString("url_server_link", "null");
                upLoadServerUri=myStrValue;
                if(myStrValue=="null"){
                    upLoadServerUri=getResources().getText(R.string.url_link_default).toString();
                }

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
                String m=fileName.replace(" ","");
                Log.d("File_name", m);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=uploaded_file;filename="
                        + m + "" + lineEnd);

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

                            String msg = "File Upload Completed.\n\n See uploaded to server : \n\n"
                                    + upLoadServerUri
                                    +uploadFileName;

                            messageText.setText(msg);
                           // Toast.makeText(MainActivity.this, "File Upload Complete.",
                            //        Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(MainActivity.this, "MalformedURLException",
                            //    Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("Got Exception : see logcat ");
                        //Toast.makeText(MainActivity.this, "Got Exception : see logcat ",
                        //        Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.menu_share, menu);
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
