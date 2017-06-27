package com.example.daumantas.explorer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button register_button, login_button;
    EditText UserName, Password;
    String username, password;
    int MY_SOCKET_TIMEOUT_MS = 80000;
    String login_url = "https://sham-bulk.000webhostapp.com/login.php";
    //String login_url = "https://10.0.2.2/login.php";
    AlertDialog.Builder builder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register_button = (Button)findViewById(R.id.btn_register);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        builder = new AlertDialog.Builder(LoginActivity.this);
        login_button = (Button)findViewById(R.id.btn_login);
        UserName = (EditText)findViewById(R.id.login_username);
        Password = (EditText)findViewById(R.id.login_password);

        if(savedInstanceState != null){
            UserName.setText(savedInstanceState.getString("username"));
            Password.setText(savedInstanceState.getString("password"));
        }

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = UserName.getText().toString();
                password = Password.getText().toString();

                if(username.equals("")||password.equals(""))
                {
                    builder.setTitle("Something went wrong");
                    displayAlert("Enter a valid username and password");
                }
                else
                {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONArray jsonArray = new JSONArray(response);

                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String code = jsonObject.getString("code");

                                if(code.equals("login_failed"))
                                {
                                    builder.setTitle("Login Error..");
                                    displayAlert(jsonObject.getString("message"));
                                }
                                else
                                {
                                    Intent intent = new Intent();
                                    intent.putExtra("name",jsonObject.getString("name"));
                                    intent.putExtra("email",jsonObject.getString("email"));
                                    intent.putExtra("Auth","true");
                                    setResult(2, intent);
                                    finish();
                                }
                            } catch (JSONException e){
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(LoginActivity.this, "Error",Toast.LENGTH_LONG).show();
                            VolleyLog.e("Error: ", error.getMessage());
                            Log.d("mytag", "error");
                            error.printStackTrace();
                        }
                    })
                    {
                        protected Map<String, String> getParams()throws AuthFailureError{
                            Map<String,String>params = new HashMap<String, String>();
                            params.put("user_name",username);
                            params.put("password",password);
                            return params;
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            MY_SOCKET_TIMEOUT_MS,
                            5,
                            5));

                    MySingleton.getInstance(LoginActivity.this).addToRequestque(stringRequest); // checks if there is a queue, if there is, puts request to it
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putString("username", UserName.getText().toString());
        outState.putString("password", Password.getText().toString());
    }

    public void displayAlert(String message)
    {
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserName.setText("");
                Password.setText("");
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
