package com.programmer.zombie.worldmood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void registerAction(View view) {
        EditText name = (EditText) findViewById(R.id.registerName);
        EditText age = (EditText) findViewById(R.id.registerAge);
        EditText pass = (EditText) findViewById(R.id.registerPass);
        EditText country = (EditText) findViewById(R.id.registerCountry);
        RadioButton male = (RadioButton) findViewById(R.id.registerMale);
        RadioButton female = (RadioButton) findViewById(R.id.registerFemale);

        RequestParams params = new RequestParams();
        params.add("name", name.getText().toString());
        params.add("age", age.getText().toString());
        params.add("country", country.getText().toString());
        params.add("password", pass.getText().toString());
        String gender = (male.isChecked()) ? "1" : "0";
        params.add("sex", gender);
        invokeWS(params);
    }

    public void invokeWS(RequestParams params) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://50.62.134.219:9090/World_View/register/doregister", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("status")) {
                        //after register ..
                        Toast.makeText(RegisterActivity.this, "Registered successfully ..", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
