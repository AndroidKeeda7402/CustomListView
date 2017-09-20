package com.androidkeeda.customlistview.code;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Coding Geek on 2/21/2017.
 */
public class MainActivity extends AppCompatActivity implements RecyclerView.OnScrollChangeListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView listView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private TimeLineListAdapter listAdapter;
    private List<TimeLineItem> timeLineItems;
    private int requestCount = 1;
    int pageCount, totalPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById();

        getData();

        timeLineItems = new ArrayList<>();

        adapter = new TimeLineListAdapter(timeLineItems, this);
        listView.setAdapter(adapter);
    }

    public void findViewById(){
        listView = (RecyclerView) findViewById(R.id.list);
        listView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
    }

    public void getTimeLineData(final String page) {


        String tag_string_req = "req_register";
        // making fresh volley request and getting json
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.timeline, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("status");
                        String message = jObj.getString("message");
                        if (error) {
                            totalPages = jObj.getInt("totalPages");
                            pageCount = jObj.getInt("page");

                            int limit = jObj.getInt("limit");
                            parseJsonFeed(response);
                        }

                    } catch (Exception e) {

                    }

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", page);
                params.put("limit", "5");

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void parseJsonFeed(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONArray feedArray = jsonObj.getJSONArray("data");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                TimeLineItem item = new TimeLineItem();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("name"));
                item.setLname(feedObj.getString("lname"));

                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");

                if (image.equals("")) {
                    item.setImge(image);
                } else {
                    item.setImge(AppConfig.storyPic + image);
                }

                item.setStatus(feedObj.getString("story_text"));
                item.setProfilePic(AppConfig.profilePic + feedObj.getString("profile_pic"));
                item.setTimeStamp(feedObj.getString("time_stamp"));
                item.setIsLike(feedObj.getInt("is_like"));
                item.setTotalLikes(feedObj.getString("total_likes"));
                item.setTotalComment(feedObj.getString("total_comments"));

                /*// url might be null sometimes
                String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);*/

                timeLineItems.add(item);
            }

            // notify data changes to list adapater
            adapter.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //This method will get data from the web API
    private void getData() {
        //Adding the method to the queue by calling the method getDataFromServer
        getTimeLineData(String.valueOf(requestCount));
        //Incrementing the request counter
        requestCount++;
    }

    //This method would check that the recyclerview scroll has reached the bottom or not
    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)

                return true;
        }
        return false;
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        //Ifscrolled at last then
        if (isLastItemDisplaying(listView)) {
            //Calling the method getdata again
            getData();
        }
    }
}
