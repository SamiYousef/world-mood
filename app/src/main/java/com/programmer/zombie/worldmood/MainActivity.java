package com.programmer.zombie.worldmood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends AppCompatActivity {

    static String selectedDate;
    static String tweet;
    static String schedualMinutes;
    EditText tweetTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tweetTxt = (EditText) findViewById(R.id.wordTxt);
        tweetTxt.setText(getIntent().getStringExtra("Tag"));
    }


    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }


    public void drawAction(View view) {

        EditText schedualInMinutes = (EditText) findViewById(R.id.schedulerTxt);

        if (isEmpty(tweetTxt) || isEmpty(schedualInMinutes)) {
            Toast.makeText(MainActivity.this, "Missing field ...", Toast.LENGTH_SHORT).show();
            return;
        }

        checkTweet(tweetTxt.getText().toString(), LoginActivity.id);

        DatePicker date = (DatePicker) findViewById(R.id.datePicker);
        TimePicker time = (TimePicker) findViewById(R.id.timePicker);

        schedualMinutes = schedualInMinutes.getText().toString();
        tweet = tweetTxt.getText().toString();

        int day = date.getDayOfMonth();
        int month = date.getMonth() + 1;
        int year = date.getYear();
        int hours = time.getCurrentHour();
        int minutes = time.getCurrentMinute();

        //get time in milisecond from date picker

        //int dd = Integer.parseInt(schedualMinutes);
        selectedDate = year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":00.000";
//        Toast.makeText(MainActivity.this,
//                tweetTxt.getText() + "\n" +
//                        dd + "\n" +
//                        selectedDate + "\n"
//                , Toast.LENGTH_SHORT).show();


        Spinner charType = (Spinner) findViewById(R.id.charType);
        if (charType.getSelectedItem().equals("Line Chart")) {
            Intent intent = new Intent(this, LineChartActivity.class);
            startActivity(intent);
        } else if (charType.getSelectedItem().equals("Bar Chart")) {
            Intent intent = new Intent(this, BarChartActivity.class);
            startActivity(intent);
        } else if (charType.getSelectedItem().equals("Radar Chart")) {
            Intent intent = new Intent(this, RadarChartActivity.class);
            startActivity(intent);
        } else if (charType.getSelectedItem().equals("Scatter Chart")) {
            Intent intent = new Intent(this, ScatterChartActivity.class);
            startActivity(intent);
        }
    }

    private void checkTweet(String tag, String id) {
        RequestParams params = new RequestParams();
        params.add("word", tag);
        params.add("id", id);
        invokeWS(params);
    }

    public void invokeWS(RequestParams params) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://50.62.134.219:9090/World_View/tweet/checkTweet", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
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
