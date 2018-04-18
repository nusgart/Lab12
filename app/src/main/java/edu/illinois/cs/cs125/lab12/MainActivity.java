package edu.illinois.cs.cs125.lab12;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Main class for our UI design lab.
 */
public final class MainActivity extends AppCompatActivity {
    /**
     * Default logging tag for messages from the main activity.
     */
    private static final String TAG = "Lab12:Main";

    /**
     * Request queue for our API requests.
     */
    private static RequestQueue requestQueue;

    TextView jsonView, descView;

    /**
     * Run when this activity comes to the foreground.
     *
     * @param savedInstanceState unused
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the queue for our API requests
        requestQueue = Volley.newRequestQueue(this);

        setContentView(R.layout.activity_main);
        jsonView = findViewById(R.id.textViewJSON);
        descView = findViewById(R.id.descriptionView);
        final Button refresh = findViewById(R.id.refreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startAPICall();
            }
        });
        startAPICall();
    }

    /**
     * Run when this activity is no longer visible.
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Make a call to the weather API.
     */
    void startAPICall() {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "http://api.openweathermap.org/data/2.5/weather?zip=61820,us&appid="
                            + BuildConfig.API_KEY,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            try {
                                Log.d(TAG, response.toString(2));
                                jsonView.setText(response.toString(2));
                                jsonView.setEnabled(true);

                                String wd = response.getJSONArray("weather").getJSONObject(0).getString("description");
                                double temp = response.getJSONObject("main").getDouble("temp"); // K
                                // this is the average kinetic energy of each molecule
                                double tempEv = temp * 8.6173303e-5 * 1000;
                                double pressure = response.getJSONObject("main").getInt("pressure") / 10.0; //hPa-->kPa
                                // to convert to atm, would be pressure /= 101.325; //
                                int humidity = response.getJSONObject("main").getInt("humidity"); //percent
                                // wind
                                double windSpeed = response.getJSONObject("wind").getDouble("speed");
                                double windDir = response.getJSONObject("wind").getDouble("deg");
                                String description = String.format("Description: %s\nTemperature: %f Kelvin" +
                                        "\nTemperature: %f milli-eV\nPressure: %f kPa\nPercent Humidity %d%%" +
                                        "\nWind Speed: %f\nWind Direction: %f degrees", wd, temp,
                                        tempEv, pressure, humidity, windSpeed, windDir);
                                descView.setText(description);
                            } catch (Throwable ignored) {
                                Log.e(TAG, "Execption: ", ignored);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.e(TAG, error.toString());
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
