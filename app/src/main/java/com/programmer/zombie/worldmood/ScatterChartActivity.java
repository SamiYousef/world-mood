package com.programmer.zombie.worldmood;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScatterChartActivity extends AppCompatActivity {

    ArrayList<Entry> valueSetPos, valueSetNegative;
    ArrayList<String> labels;

    Handler handler;
    Runnable runnable;

    ScatterDataSet negativeLine, positiveLine;
    int i = 0;

    int schedualMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scatter_chart);

        schedualMinutes = Integer.parseInt(MainActivity.schedualMinutes);

        valueSetPos = new ArrayList<>();
        valueSetNegative = new ArrayList<>();
        labels = new ArrayList<>();

        negativeLine = new ScatterDataSet(valueSetNegative, "Negative Line");
        negativeLine.setColor(Color.RED);

        positiveLine = new ScatterDataSet(valueSetPos, "Positive Line");
        positiveLine.setColor(ColorTemplate.getHoloBlue());

    }

    private void HandlerAction() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    RequestParams params = new RequestParams();
                    params.add("word", MainActivity.tweet);
                    String time = updateTime();
                    params.add("tweetTime", time);
                    invokeWS(params, time);
                    i++;
                    Log.v("DATA", i + "");
                    handler.postDelayed(this, schedualMinutes * 60 * 1000);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, schedualMinutes * 60 * 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HandlerAction();
    }

    private String updateTime() throws ParseException {
        String date = MainActivity.selectedDate;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.000");
        Date d = df.parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MINUTE, i * schedualMinutes);
        String newTime = df.format(cal.getTime());
        Log.v("DATA", newTime);
        return newTime;
    }

    public void invokeWS(final RequestParams params, final String time) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://50.62.134.219:9090/World_View/result/getResult", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("status")) {
                        float pos = Float.parseFloat(obj.getString("AvgPos"));
                        float neg = Float.parseFloat(obj.getString("AvgNeg"));

                        positiveLine.addEntry(new Entry(pos, i));
                        negativeLine.addEntry(new Entry(neg, i));
                        labels.add(time);
                        //update line chart
                        ScatterChart chart = (ScatterChart) findViewById(R.id.scatterChart);

                        ScatterData data = new ScatterData(labels, positiveLine);
                        data.addDataSet(negativeLine);
                        chart.setData(data);
                        chart.setDescription(MainActivity.tweet + "  Scatter Chart");
                        chart.animateXY(2000, 2000);
                        chart.invalidate();
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
