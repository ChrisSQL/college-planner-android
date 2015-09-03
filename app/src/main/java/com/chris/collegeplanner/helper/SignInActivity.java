package com.chris.collegeplanner.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

//import org.apache.http.HttpResponse;

public class SignInActivity extends AsyncTask<String,Void,String>{

    private TextView statusField,roleField;
    private Context context;
    private int byGetOrPost = 0;
    //flag 0 means get and 1 means post.(By default it is get.)
    public SignInActivity(Context context, TextView statusField,
                          TextView roleField, int flag) {
        this.context = context;
        this.statusField = statusField;
        this.roleField = roleField;
        byGetOrPost = flag;
    }

    protected void onPreExecute(){

    }
    @Override
    protected String doInBackground(String... arg0) {
//        if(byGetOrPost == 0){ //means by Get Method
//            try{
//                String username = (String)arg0[0];
//                String password = (String)arg0[1];
//                String link = "http://www.chrismaher.info/AndroidProjects2/login.php?username="
//                        +username+"&password="+password;
//                URL url = new URL(link);
//                HttpClient client = new DefaultHttpClient();
//                HttpGet request = new HttpGet();
//                request.setURI(new URI(link));
//                HttpResponse response = client.execute(request);
//                BufferedReader in = new BufferedReader
//                        (new InputStreamReader(response.getEntity().getContent()));
//
//                StringBuffer sb = new StringBuffer("");
//                String line="";
//                while ((line = in.readLine()) != null) {
//                    sb.append(line);
//                    break;
//                }
//                in.close();
//                return sb.toString();
//            }catch(Exception e){
//                return new String("Exception: " + e.getMessage());
//            }
//        }
//        else{
//            try{
//                String username = (String)arg0[0];
//                String password = (String)arg0[1];
//                String link="http://www.chrismaher.info/AndroidProjects2/loginpost.php";
//                String data  = URLEncoder.encode("username", "UTF-8")
//                        + "=" + URLEncoder.encode(username, "UTF-8");
//                data += "&" + URLEncoder.encode("password", "UTF-8")
//                        + "=" + URLEncoder.encode(password, "UTF-8");
//                URL url = new URL(link);
//                URLConnection conn = url.openConnection();
//                conn.setDoOutput(true);
//                OutputStreamWriter wr = new OutputStreamWriter
//                        (conn.getOutputStream());
//                wr.write( data );
//                wr.flush();
//                BufferedReader reader = new BufferedReader
//                        (new InputStreamReader(conn.getInputStream()));
//                StringBuilder sb = new StringBuilder();
//                String line = null;
//                // Read Server Response
//                while((line = reader.readLine()) != null)
//                {
//                    sb.append(line);
//                    break;
//                }
//                return sb.toString();
//            }catch(Exception e){
               return new String("");
//
//            }
//        }
    }
    @Override
    protected void onPostExecute(String result){


        this.statusField.setText("Login Successful");
        this.roleField.setText(result);

        if(result != null){

            

        }
        else{



        }
    }
}
