package it.garybrady.travel;

/**
 * Created by Gary on 27/02/14.
 */
import static it.garybrady.travel.CommonUtilities.SENDER_ID;
import static it.garybrady.travel.CommonUtilities.SERVER_URL;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RegisterActivity extends Activity {
    // alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();

    // Internet detector
    ConnectionDetector cd;

    // UI elements
    EditText txtName;
    EditText txtEmail;
    EditText txtPassword,txtPasswordRepeat;

    // Register button
    Button btnRegister;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(RegisterActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }

        // Check if GCM configuration is set
        if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
                || SENDER_ID.length() == 0) {
            // GCM sernder id / server url is missing
            alert.showAlertDialog(RegisterActivity.this, "Configuration Error!",
                    "Please set your Server URL and GCM Sender ID", false);
            // stop executing code by return
            return;
        }

        txtName = (EditText) findViewById(R.id.txtName);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword=(EditText) findViewById(R.id.txtPassword);
        txtPasswordRepeat=(EditText) findViewById(R.id.txtPasswordRepeat);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        /*
         * Click event on Register button
         * */
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Read EditText dat
                String name = txtName.getText().toString();
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();
                String repeat = txtPasswordRepeat.getText().toString();
                if(!checkFields(name,email,password,repeat)){
                    Toast.makeText(getApplicationContext(),"Please fill in all fields correctly", Toast.LENGTH_LONG).show();
                }else{
                    // Check if user filled the form
                    if(name.trim().length() > 0 && email.trim().length() > 0&& password.trim().length() > 0){
                        // Launch Main Activity
                        Intent i = new Intent(getApplicationContext(), GcmActivity.class);

                        // Registering user on our server
                        // Sending registraiton details to MainActivity
                        i.putExtra("name", name);
                        i.putExtra("email", email);
                        i.putExtra("password", password);
                        startActivity(i);
                        finish();
                    }else{
                        // user doen't filled that data
                        // ask him to fill the form
                        alert.showAlertDialog(RegisterActivity.this, "Registration Error!", "Please enter your details", false);
                    }
                }
            }
        });
    }

    public boolean checkFields(String name, String email, String pass, String repeat){
        TextView tvName,tvEmail,tvPassword, tvPasswordRepeat;
        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvPassword = (TextView) findViewById(R.id.tvPass);
        tvPasswordRepeat = (TextView) findViewById(R.id.tvPassRepeat);

        tvName.setTextColor(Color.BLACK);
        tvEmail.setTextColor(Color.BLACK);
        tvPassword.setTextColor(Color.BLACK);
        tvPasswordRepeat.setTextColor(Color.BLACK);

        int x=0;


        if(!isValidEmail(email)){
           tvEmail.setTextColor(Color.RED);
            x++;
        }
        if (!usernameCheck(name)||name.length()<6||name.contains(" ")){
            tvName.setTextColor(Color.RED);
            x++;
        }
        if (pass.length()<6){
            tvPassword.setTextColor(Color.RED);
            tvPasswordRepeat.setTextColor(Color.RED);
            x++;
        }
        if (!repeat.equals(pass)){
            tvPasswordRepeat.setTextColor(Color.RED);
            x++;
        }

        if (x>0){
            return false;
        }else if(!cd.isConnectingToInternet()){
            Toast.makeText(getApplicationContext(),"Internet Connection Required:\n Please check connectivity", Toast.LENGTH_LONG).show();
            return false;
        } else{
            return true;
        }

    }



    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public boolean usernameCheck(String user){

        String temp = null;
        String result = "";
        InputStream isr = null;
        String name="http://192.3.177.209/busMap/gcm/checkName.php?username="+user;




        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(name);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
        }
        catch(Exception e){
            Log.e("log_tag", "Error in http connection " + e.toString());

        }
        //convert response to string
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            isr.close();

            result=sb.toString();
        }
        catch(Exception e){
            Log.e("log_tag", "Error  converting result "+e.toString());
        }

        //parse json data
        try {
            String s = "";
            JSONArray jArray = new JSONArray(result);

            for(int i=0; i<jArray.length();i++){
                JSONObject json = jArray.getJSONObject(i);
                temp=json.getString("id");

            }
            if (temp!=null){
            Toast.makeText(getApplicationContext(),"Username Taken", Toast.LENGTH_LONG).show();
            }
            return false;

        } catch (Exception e) {

            Log.e("log_tag", "Error Parsing Data "+e.toString());
            return true;
        }


    }

    public boolean regCheck(String reg){

        String temp = null;
        String result = "";
        InputStream isr = null;
        String name="http://192.3.177.209/busMap/gcm/checkName.php?reg="+reg;




        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(name);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
        }
        catch(Exception e){
            Log.e("log_tag", "Error in http connection " + e.toString());

        }
        //convert response to string
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            isr.close();

            result=sb.toString();
        }
        catch(Exception e){
            Log.e("log_tag", "Error  converting result "+e.toString());
        }

        //parse json data
        try {
            String s = "";
            JSONArray jArray = new JSONArray(result);

            for(int i=0; i<jArray.length();i++){
                JSONObject json = jArray.getJSONObject(i);
                temp=json.getString("id");

            }
            Toast.makeText(getApplicationContext(),temp, Toast.LENGTH_LONG).show();
            return false;

        } catch (Exception e) {

            Log.e("log_tag", "Error Parsing Data "+e.toString());
            return true;
        }


    }

}
