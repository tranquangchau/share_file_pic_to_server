package com.example.administrator.sharefile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class AShareActivity extends AppCompatActivity {

    TextView messageText;
    ProgressDialog dialog = null;
    int serverResponseCode = 0;
    static String sal= "AShareActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Log.d(sal, "onCreate");

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

        try {
            Log.d(sal, "Start try");
            //final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            //shareIntent.setType("image/jpg");
            //final File photoFile = new File(getFilesDir(), "foo.jpg");
            Intent receivedIntent = getIntent();
            String receivedAction = receivedIntent.getAction();
            String receivedType = receivedIntent.getType();
            Log.d(sal, "receivedType " + receivedType);

                if ("text/plain".equals(receivedType)) {
                    shareTextUrl(receivedIntent); // Handle text being sent
                } else if (receivedType.startsWith("image/")) {
                    Log.d(sal, "type_recen "+receivedType);
//                    handleSendImage(receivedIntent); // Handle single image being sent
                    //String name = intent.getData().getLastPathSegment();
                    if(receivedType.startsWith("image/")){
                        //handle sent image
                    }
                    Uri receivedUri = (Uri)receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
                    String Url_file=receivedUri.toString();
                    Log.d(sal, "Url_file "+ Url_file.toString());
                    Log.d(sal, "receivedUri1 "+ receivedUri.toString());
                    if(receivedUri.toString().contains("content://com.mobisystems")){
                        //http://stackoverflow.com/a/20059657
                        Log.d(sal, "receivedUri2  "+ "content://com.mobisystems");
                        Url_file = getRealPathFromUri(AShareActivity.this,receivedUri);
                    }else if(receivedUri.toString().contains("content://com.google.android.apps")){
                        Log.d(sal, "trung_voi  "+ "content://com.google.android.apps");
                        Uri url1= Uri.parse(receivedUri.toString());
                        Log.d(sal, "url1  " + url1);
                        String mimeType = getContentResolver().getType(url1);
                        Log.d(sal, "mimeType  "+ mimeType);
                        //String m = getImageUrlWithAuthority(AShareActivity.this, url1);
                        //Log.d(sal, "mfile  "+ m); //kiem hinh anh

                        Bitmap bitmap = null;
                        InputStream is = null;
                        is = getContentResolver().openInputStream(url1);
                        bitmap = BitmapFactory.decodeStream(is);

                        String root = Environment.getExternalStorageDirectory().toString();
                        File myDir = new File(root + "/saved_images");
                        myDir.mkdirs();
                        Random generator = new Random();
                        int n = 10000;
                        n = generator.nextInt(n);
                        String fname = "Image-"+ n +".jpg";
                        File file = new File (myDir, fname);
                        if (file.exists ()) file.delete ();
                        try {
                            //finalBitmap = rotate(bmp,50);
                            FileOutputStream out = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();
                            out.close();
                            Log.d(sal, "file  " + file); //hinh anh
                            Url_file= String.valueOf(file);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //String m2 =getRealPathFromURI(AShareActivity.this,Uri.parse(m));
                        //Url_file= m2;

                        //download
//                downloadFile(Url_file,"sdcard");

                        //upload file
                    }else{
                        //file:///storage/sdcard0/Images_SellChat/photo-2369.jpg
                        Log.d(sal, "receivedUri  "+ "else");
                    }
                    if (Url_file != null) {
                        //set the picture
                        //RESAMPLE YOUR IMAGE DATA BEFORE DISPLAYING
                        //picView.setImageURI(Url_file);//just for demonstration
                        Log.d(sal,"File_non"+ Url_file.toString());
                        //final String uploadFilePath = "/mnt/sdcard/";
                        //string.replace("=\"ppshein\"", "");

                        String m = Url_file.toString();
                        m=m.replace("file:///storage/sdcard0/","/mnt/sdcard/");
                        //m=m.replace(" ","\\ ");
                        m=m.replace("%20"," ");
                        if(m.contains("content://media/external/images")){
                            m = getRealPathFromUri(AShareActivity.this, Uri.parse(m));
                        }
                        Log.d(sal,"File_non1"+ m);
                        //shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
                        //startActivity(Intent.createChooser(shareIntent, "Share image using"));
                        //get data save url
                        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                        String myStrValue = sp.getString("url_server_link", "null");
                        Log.d(sal,"myStrValue_sharefile"+ myStrValue);

                        upLoadServerUri=myStrValue;
                        if(myStrValue=="null"){
                            upLoadServerUri=getResources().getText(R.string.url_link_default).toString();
                        }
                        messageText.setText("Server="+upLoadServerUri);

                        uploadFile(m);
                        //share_file(m);
                    }
                }



        }catch (Exception e){
            Log.d(sal,"Exception 1 Error here");
           e.printStackTrace();
        }
        //android.os.Process.killProcess(android.os.Process.myPid());
        //super.onDestroy();
        finish();
    }

    // Method to share either text or URL.
    private void shareTextUrl(Intent intent) {
        String sharedText = intent.getStringExtra(intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
            Log.d("sharetext11111",sharedText);
            //String urlString = "http://192.168.1.151/text/index.php";
            String urlString = "http://tranquangchau.net/text/index.php";
            HashMap<String, String> hash = new HashMap<>();
            hash.put("text", sharedText);
            hash.put("device",getDeviceName());
            hash.put("valuetext", "con tho");
            String aa= performPostCall(urlString,hash);
            List<String> allNames = new ArrayList<String>();
            JSONObject jsonResponse;
            try {
                jsonResponse = new JSONObject(aa);
                JSONArray cast = jsonResponse.getJSONArray("info");
                for (int i=0; i<cast.length(); i++) {
                    JSONObject actor = cast.getJSONObject(i);
                    String status = actor.getString("status");
                    String detail = actor.getString("detail");
                    if(status=="success"){
                        messageText.setText(status+" : "+detail+"\n"+ sharedText);
                    }else{
                        messageText.setText(status+" : "+detail+"\n"+ sharedText);
                    }

                }

            }catch (Exception e){
                Log.d("Errr",e.getMessage().toString());
            }

            Log.d("String aa",aa);
        }else{
            Log.d("sharetext2222222",sharedText);
        }
    }
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }


    public String  performPostCall(String requestURL,
                                   HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        Log.d("vvv",result.toString());
        return result.toString();
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }
    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }




    /**
     * http://stackoverflow.com/a/31019925
     * @param context
     * @param uri
     * @return
     */
    public static String getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                //is.reset();
                return writeToTempImageAndGetPathUri(context, bmp).toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //bytes.reset();
        String path="";
        Log.d(sal,inImage.toString());
        if(inImage.toString().equals("")){
            Log.d(sal,"inImage Null");
        }
        if(inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)){
            path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        }if(inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)){
            path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        }if(inImage.compress(Bitmap.CompressFormat.valueOf("GIF"), 100, bytes)){
            path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        }

        //String path = MediaStore.Images.Media.insertImage("sdcard", inImage, "Title", null);
        return Uri.parse(path);
    }
    public static void downloadFile(String fileURL, String saveDir)
            throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[1200];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }
    //http://stackoverflow.com/a/3414749
    //how to convert `content://media/external/images/media/Y` to `file:///storage/sdcard0/Pictures/X.jpg`
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public String upLoadServerUri;

    public int uploadFile(String sourceFileUri) {

        dialog = ProgressDialog.show(AShareActivity.this, "", "Uploading file...", true);
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

            Log.e(sal,"uploadFile Source File not exist :"
                    + uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                    messageText.setText("Source File not exist :"
                            +uploadFilePath + "" + uploadFileName);
                    Toast.makeText(AShareActivity.this, "Source File not exist.", Toast.LENGTH_SHORT).show();
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
                String m=fileName.replace(" ","");
                Log.d(sal,"File_name"+ m);
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

                Log.d(sal,"dos "+ dos.toString());
                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                //String s_reson=  conn.getContentEncoding().toString();
               // Log.d(sal,"s_reson "+s_reson);
                Log.i(sal,"uploadFile"+ "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded to server : \n\n"
                                    + upLoadServerUri
                                    +uploadFileName;

                            messageText.setText(msg);
                           Toast.makeText(AShareActivity.this, "UploadComplete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                //Log.d(sal,"fileInputStream "+fileInputStream.read());
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                Log.d(sal,"Exception 2"+"Error here");
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(AShareActivity.this, "MalformedURLException",
                               Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e(sal,"Upload file to server"+ "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                Log.d(sal,"Exception 3"+ "Error here");
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(AShareActivity.this, "Got Exception : see logcat ",
                               Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e(sal,"Upload_Excepti"+ "Exception : "
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
