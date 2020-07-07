package com.gpayinterns.chat;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.collection.LruCache;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleyController
{
    private static VolleyController instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    private ImageLoader imageLoader;

    public static synchronized VolleyController getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new VolleyController(context);
        }
        return instance;
    }

    private VolleyController(final Context context)
    {
        ctx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache = new LruBitmapCache();

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
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
        req.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //set a timeout of 100 seconds with 0 retries.
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader()
    {
        return imageLoader;
    }
}
