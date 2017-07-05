package com.example.daumantas.explorer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    Button register_btn;
    EditText Name,Email,UserName,Password,conPassword;
    String name,email,username,password,conpassword;
    AlertDialog.Builder builder;
    String reg_url = "http://explorer.we2host.lt//register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register_btn = (Button)findViewById(R.id.btn_register);
        Name = (EditText)findViewById(R.id.register_name);
        Email = (EditText)findViewById(R.id.register_email);
        UserName = (EditText)findViewById(R.id.register_username);
        Password = (EditText)findViewById(R.id.register_password);
        conPassword = (EditText)findViewById(R.id.register_conf_password);
        builder = new AlertDialog.Builder(RegisterActivity.this);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = Name.getText().toString();
                email = Email.getText().toString();
                username = UserName.getText().toString();
                password = Password.getText().toString();
                conpassword = conPassword.getText().toString();
                if(name.equals("")||email.equals("")||username.equals("")||password.equals("")||conpassword.equals(""))
                {
                    builder.setTitle("Something went wrong..");
                    builder.setMessage("Please fill all the fields...");
                    displayAlert("input_error");
                }
                else
                {
                    if(!password.equals(conpassword))
                    {

                        builder.setTitle("Something went wrong..");
                        builder.setMessage("Passwords does not match:" + password + ", " +conpassword);
                        displayAlert("input_error");
                    }
                    else
                    {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, reg_url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONArray jsonArray = new JSONArray(response);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            String code = jsonObject.getString("code");
                                            String message = jsonObject.getString("message");
                                            builder.setTitle("Server Response...");
                                            builder.setMessage(message);
                                            displayAlert(code);
                                        } catch (JSONException e){
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener(){
                            public void onErrorResponse(VolleyError error){
                                error.printStackTrace();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError{
                                Map<String,String>params = new HashMap<String, String>();
                                params.put("name",name);
                                params.put("email",email);
                                params.put("user_name",username);
                                params.put("password", password);

                                return params;
                            }
                        };
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                5000,
                                5,
                                5));
                        MySingleton.getInstance(RegisterActivity.this).addToRequestque(stringRequest);
                    }
                }
            }

        });
    }
    public void displayAlert(final String code)
    {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which){
                if(code=="input_error"){
                    Password.setText("");
                    conPassword.setText("");
                }
                else if(code=="reg_success")
                {
                    finish();
                }
                else if(code=="reg_filed")
                {
                    Name.setText("");
                    Email.setText("");
                    UserName.setText("");
                    Password.setText("");
                    conPassword.setText("");
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
