package com.androidkeeda.customlistview.code;

/**
 * Created by Coding Geek on 2/21/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeLineListAdapter extends RecyclerView.Adapter<TimeLineListAdapter.ViewHolder> {
    private List<TimeLineItem> timeLineItems;
    String message, storyId, token;
    private Context context;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public TimeLineListAdapter(List<TimeLineItem> timeLineItems, Context context) {
        super();
        this.context = context;
        this.timeLineItems = timeLineItems;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //Getting the particular item from the list
        TimeLineItem item =  timeLineItems.get(position);
        holder.setIsRecyclable(false);
        if (item.getTotalLikes().equals("0")){
            holder.txtLike.setText("");
        }else {
            holder.txtLike.setText(item.getTotalLikes());
        }

        if (item.getTotalComment().equals("0")){
            holder.txtComment.setText("");
        }else {
            holder.txtComment.setText("("+item.getTotalComment()+")");
        }

        if (item.getIsLike() == 0){

        }else {
            holder.imageLike.setImageBitmap(null);
            holder.imageLike.setBackgroundResource(R.drawable.islike);
            holder.txtLike.setTextColor(Color.parseColor("#000000"));
        }

        holder.name.setText(item.getName() + " " + item.getLname());
        //holder.name.setText(item.getName() + " " + item.getLname());

        holder.timestamp.setText(item.getTimeStamp());

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(item.getStatus());
            holder.statusMsg.setText(fromServerUnicodeDecoded);
            holder.statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            holder.statusMsg.setVisibility(View.GONE);
        }

        // Checking for null feed url
        if (item.getUrl() != null) {
            holder.url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
                    + item.getUrl() + "</a> "));

            // Making url clickable
            holder.url.setMovementMethod(LinkMovementMethod.getInstance());
            holder.url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            holder.url.setVisibility(View.GONE);
        }

        // user profile pic
        //holder.profilePic.setImageUrl(item.getProfilePic(), imageLoader);
        Picasso.with(context).load(item.getProfilePic()).placeholder(R.drawable.edit_profile).transform(new CircleTransform()).fit().into(holder.profilePic);


        // Feed image
        if (item.getImge() != null) {
            holder.feedImageView.setImageUrl(item.getImge(), imageLoader);
            holder.feedImageView.setVisibility(View.VISIBLE);
            holder.feedImageView
                    .setResponseObserver(new TimeLineImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            holder.feedImageView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return timeLineItems.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView name, timestamp, statusMsg, url, txtLike, txtComment, txtCommentLabel;
        ImageView profilePic;
        TimeLineImageView feedImageView;
        ImageView imageLike;

        //Initializing Views
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            statusMsg = (TextView) itemView.findViewById(R.id.txtStatusMsg);
            url = (TextView) itemView.findViewById(R.id.txtUrl);
            profilePic = (ImageView) itemView.findViewById(R.id.profilePic);
            feedImageView = (TimeLineImageView) itemView.findViewById(R.id.feedImage1);
            imageLike = (ImageView) itemView.findViewById(R.id.imgLike);
            txtLike = (TextView) itemView.findViewById(R.id.txtLike);
            txtComment = (TextView) itemView.findViewById(R.id.txtComment);
            txtCommentLabel = (TextView) itemView.findViewById(R.id.txtCommentLabel);
        }
    }

}
