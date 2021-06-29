package com.example.beweather.weathercontroller;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class Controller  {

    public static final String TAG = Controller.class.getSimpleName();
    private static Controller thisInstance;
    private RequestQueue currentRequestQueue;
    private Context context;


    private Controller(Context context) {
        this.context = context;
        currentRequestQueue = getRequestQueue();
    }

    public static synchronized Controller getController(Context context) {
        if (thisInstance == null) {
            thisInstance = new Controller(context);
        }
        return thisInstance;
    }


    public RequestQueue getRequestQueue() {
        if (currentRequestQueue == null) {
            currentRequestQueue = Volley.newRequestQueue(context);
        }
        return currentRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG: tag);
        getRequestQueue().add(req);
    }

    //In this case, the tag was not provided, so we'll use our default
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelCurrentRequests(Object tag) {
        if (currentRequestQueue != null) {
            currentRequestQueue.cancelAll(tag);
        }
    }


}
