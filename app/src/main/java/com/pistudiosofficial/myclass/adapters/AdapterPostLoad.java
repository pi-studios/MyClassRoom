package com.pistudiosofficial.myclass.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pistudiosofficial.myclass.Common;
import com.pistudiosofficial.myclass.PhotoFullPopupWindow;
import com.pistudiosofficial.myclass.R;
import com.pistudiosofficial.myclass.activities.CommentActivity;
import com.pistudiosofficial.myclass.activities.FeedbackActivity;
import com.pistudiosofficial.myclass.model.PostInteractionModel;
import com.pistudiosofficial.myclass.objects.PollOptionValueLikeObject;
import com.pistudiosofficial.myclass.objects.PostObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.pistudiosofficial.myclass.Common.CHECK_NEW_COMMENT;
import static com.pistudiosofficial.myclass.Common.COMMENT_LOAD_POST_OBJECT;
import static com.pistudiosofficial.myclass.Common.CURRENT_CLASS_ID_LIST;
import static com.pistudiosofficial.myclass.Common.CURRENT_INDEX;

import static com.pistudiosofficial.myclass.Common.CURRENT_USER;
import static com.pistudiosofficial.myclass.Common.mREF_COMMENT_LOAD;
import static com.pistudiosofficial.myclass.Common.mREF_classList;

public class AdapterPostLoad extends RecyclerView.Adapter<AdapterPostLoad.MyViewHolder> {

    ArrayList<PostObject> postObjectArrayList;
    HashMap<String, PollOptionValueLikeObject> post_poll_option;
    ArrayList<String> post_like_list;
    HashMap<String, ArrayList<String>> post_url_list;
    ArrayList<String> post_id;
    ArrayList<String> comment_count;
    PostInteractionModel model;
    Context context;
    int k;
    ArrayList<String> url;

    public AdapterPostLoad(ArrayList<PostObject> postObjectArrayList,HashMap<String,
                            PollOptionValueLikeObject> post_poll_option,
                            ArrayList<String> post_like_list,
                            HashMap<String, ArrayList<String>> post_url_list,
                            ArrayList<String> comment_count,
                            Context context) {
        this.postObjectArrayList = postObjectArrayList;
        this.post_like_list = post_like_list;
        this.post_poll_option = post_poll_option;
        this.post_url_list = post_url_list;
        this.context = context;
        this.comment_count = comment_count;
        post_id = new ArrayList<>();
        model = new PostInteractionModel();
        for (PostObject s : postObjectArrayList){
            post_id.add(s.getPostID());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.post_row,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        if (postObjectArrayList!=null) {
            myViewHolder.tv_title.setText(postObjectArrayList.get(i).getCreatorName());
            myViewHolder.tv_creatIon_time.setText(postObjectArrayList.get(i).getCreationDate());
            myViewHolder.tv_post_content.setText(postObjectArrayList.get(i).getBody());
            if (postObjectArrayList.get(i).getPostType().equals("admin_feedback")&&CURRENT_USER.AdminLevel.equals("user")){
                myViewHolder.tv_post_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, FeedbackActivity.class);
                        intent.putExtra("class_id",CURRENT_CLASS_ID_LIST.get(CURRENT_INDEX));
                        context.startActivity(intent);
                    }
                });
            }
            if (post_like_list != null) {
                myViewHolder.bt_like.setText("Like:" + post_like_list.get(i));
            }
            if (comment_count != null) {
                myViewHolder.bt_comment.setText("Comment:" + comment_count.get(i));
            }
            myViewHolder.bt_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button bt = (Button) view;
                    bt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_black_24dp, 0, 0, 0);
                    model.likeClicked(post_id.get(i), Common.CURRENT_CLASS_ID_LIST.get(Common.CURRENT_INDEX));
                }
            });
            if (postObjectArrayList.get(i).getPostType().equals("admin_poll")) {
                PollOptionValueLikeObject obj = post_poll_option.get(post_id.get(i));
                myViewHolder.listView.removeAllViews();
                for (int j = 0; j < obj.optionList.size(); j++) {
                    Button tv = new Button(context);
                    tv.setId(j);
                    if (Common.CURRENT_USER.AdminLevel.equals("admin")) {
                        tv.setText(obj.optionList.get(j) + " : " + obj.votesCountList.get(j));
                    } else {
                        tv.setText(obj.optionList.get(j));
                    }
                    tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    tv.setTextSize(16);
                    tv.setPadding(5, 3, 0, 3);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String option = tv.getText().toString();
                            if (Common.CURRENT_USER.AdminLevel.equals("user")) {
                                model.pollClicked(option, Common.CURRENT_CLASS_ID_LIST.get(Common.CURRENT_INDEX),
                                        post_id.get(i));
                            }
                        }
                    });
                    myViewHolder.listView.addView(tv);
                }
            } else {
                if (post_url_list.containsKey(post_id.get(i))) {
                    url = post_url_list.get(post_id.get(i));
                    for (k = 0; k < url.size(); k++) {
                        ImageView img = new ImageView(context);
                        img.setId(k);
                        Glide.with(context).load(url.get(k)).apply(new RequestOptions().override(480, 250)).into(img);
                        img.setLayoutParams(new ViewGroup.LayoutParams(300,
                                200));
                        myViewHolder.listView.setOrientation(LinearLayout.HORIZONTAL);
                        img.setPadding(5, 3, 0, 3);
                        img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ImageView imgView = (ImageView) view;
                                String url = post_url_list
                                        .get(post_id.get(i)).get(imgView.getId());
                                new PhotoFullPopupWindow(context, R.layout.popup_photo_full, view, url, null);

                            }
                        });
                        myViewHolder.listView.addView(img);
                    }
                }
            }
            myViewHolder.bt_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mREF_COMMENT_LOAD = mREF_classList.child(CURRENT_CLASS_ID_LIST.get(CURRENT_INDEX))
                            .child("post").child(post_id.get(i)).child("comment");
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("post_id", post_id.get(i));
                    COMMENT_LOAD_POST_OBJECT = postObjectArrayList.get(i);
                    context.startActivity(intent);
                }
            });
            if (postObjectArrayList.get(i).getCreatorProPickLink() != null && context != null) {
                Glide.with(context).load(postObjectArrayList.get(i).getCreatorProPickLink()).into(myViewHolder.img_post_icon);
            }
            try {
                if (CHECK_NEW_COMMENT.get(post_id.get(i))) {
                    myViewHolder.bt_comment
                            .setCompoundDrawablesWithIntrinsicBounds(R.drawable.chat_green, 0, 0, 0);
                }
            }catch (Exception e){e.printStackTrace();}
        }// Need To add share
    }

    @Override
    public int getItemCount() {
        return postObjectArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_title,tv_creatIon_time,tv_post_content;
        Button bt_like, bt_comment, bt_share;
        ImageView img_post_icon;
        LinearLayout listView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title_post_row);
            tv_creatIon_time = itemView.findViewById(R.id.tv_creation_time_post_row);
            tv_post_content = itemView.findViewById(R.id.tv_post_content_post_row);
            bt_like = itemView.findViewById(R.id.bt_like_post_row);
            bt_comment = itemView.findViewById(R.id.bt_comment_post_row);
            bt_share = itemView.findViewById(R.id.bt_share_post_row);
            listView = itemView.findViewById(R.id.linearLayout_post_row);
            img_post_icon = itemView.findViewById(R.id.img_post_icon_post_row);
        }
    }

}
