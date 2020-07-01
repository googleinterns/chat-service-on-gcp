package com.gpayinterns.chat;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyController
{
    private static VolleyController instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    public static synchronized VolleyController getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new VolleyController(context);
        }
        return instance;
    }

    private VolleyController(Context context)
    {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue()
    {
        if(requestQueue == null)
        {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueueWithRetry(Request<T> req)
    {
        //retry policy
        req.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //in case of failure retry every 5 seconds with an exponential backoff

        getRequestQueue().add(req);
    }
    public <T> void addToRequestQueue(Request<T> req)
    {
        getRequestQueue().add(req);
    }
}
