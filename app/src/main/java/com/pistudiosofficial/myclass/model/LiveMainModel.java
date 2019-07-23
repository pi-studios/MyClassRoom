package com.pistudiosofficial.myclass.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pistudiosofficial.myclass.objects.PollOptionValueLikeObject;
import com.pistudiosofficial.myclass.objects.PostObject;
import com.pistudiosofficial.myclass.view.HomeView;
import com.pistudiosofficial.myclass.view.MainActivityView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.pistudiosofficial.myclass.Common.CHECK_NEW_COMMENT;
import static com.pistudiosofficial.myclass.Common.CHECK_NEW_COMMENT_POST;
import static com.pistudiosofficial.myclass.Common.CURRENT_CLASS_ID_LIST;
import static com.pistudiosofficial.myclass.Common.LOG;
import static com.pistudiosofficial.myclass.Common.mREF_classList;

public class LiveMainModel {

    MainActivityView mainActivityView;
    HomeView homeView;
    public LiveMainModel(MainActivityView mainActivityView) {
        this.mainActivityView = mainActivityView;
    }

    public LiveMainModel(HomeView homeView) {
        this.homeView = homeView;
    }

    public void performCheckRead(String admin_type, String UID){
        if (admin_type.equals("admin")){
            adminCheck(UID);
        }
        if (admin_type.equals("user")){
            userCheck(UID);
        }
    }
    private void adminCheck(String UID){
            mREF_classList.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean flag;
                    for (String s:CURRENT_CLASS_ID_LIST){
                        flag = false;
                        for (DataSnapshot d:dataSnapshot.child(s).child("admin_index").child(UID).getChildren()){
                                CHECK_NEW_COMMENT.put(d.getKey(),true);
                                flag=true;
                        }
                        if (flag){CHECK_NEW_COMMENT_POST.add(2);}
                        else{CHECK_NEW_COMMENT_POST.add(0);}
                    }
                    mainActivityView.notifNewComment();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }
    private void userCheck(String UID){
        mREF_classList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag;

                for (String s:CURRENT_CLASS_ID_LIST){
                    flag = false;
                    for (DataSnapshot p:dataSnapshot.child(s).child("student_index").child(UID).getChildren()){
                         if (!p.getKey().equals("new_post")){
                            CHECK_NEW_COMMENT.put(p.getKey(),true);
                            flag = true;
                         }
                    }
                    try{
                        if (dataSnapshot.child(s).child("student_index").child(UID).child("new_post")
                                .getValue().toString().equals("true")){
                            CHECK_NEW_COMMENT_POST.add(1);
                        }
                        else {
                            if (flag){CHECK_NEW_COMMENT_POST.add(2);}
                            else {CHECK_NEW_COMMENT_POST.add(0);}
                        }
                    }catch (Exception e){e.printStackTrace();CHECK_NEW_COMMENT_POST.add(1);}
                }
                mainActivityView.notifNewComment();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    // This is for loading post in home screen
    int classCount;
    int counter;
    ArrayList<PostObject> postObjects ;
    HashMap<String, PollOptionValueLikeObject> post_poll_option ;
    ArrayList<String> post_like_list ;
    HashMap<String, ArrayList<String>> post_url_list ;
    ArrayList<String> post_comment_count;
    HashMap<String,String> post_class_id;
    public void performfeedload(ArrayList<String> classID){
        this.classCount = classID.size()-1;
        counter = 0;
        postObjects = new ArrayList<>();
        post_class_id = new HashMap<>();
        post_poll_option = new HashMap<>();
        post_like_list = new ArrayList<>();
        post_url_list = new HashMap<>();
        post_comment_count = new ArrayList<>();
        for (String s: classID){
            performLoadPost(s);
        }
    }

    public void performLoadPost(String classID){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()>0) {
                    for (DataSnapshot s : dataSnapshot.getChildren()){
                        postObjects.add(s.getValue(PostObject.class));
                        post_class_id.put(s.getValue(PostObject.class).getPostID(),classID);
                        likeLoad(s.getValue(PostObject.class),classID);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mREF_classList.child(classID)
                .child("post").orderByKey().limitToLast(2).addListenerForSingleValueEvent(valueEventListener);
    }

    private void likeLoad(PostObject p,String classID){
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(p.getPostID())
                        .child("like").child("like_count").getValue() != null){
                    post_like_list.add(dataSnapshot.child(p.getPostID())
                            .child("like").child("like_count").getValue().toString());
                }
                if (dataSnapshot.child(p.getPostID())
                        .child("like").child("like_count").getValue() == null){
                    post_like_list.add("0");
                }
                if (dataSnapshot.child(p.getPostID())
                        .child("comment").child("comment_count").getValue() != null){
                    post_comment_count.add(dataSnapshot.child(p.getPostID())
                            .child("comment").child("comment_count").getValue().toString());
                }
                if (dataSnapshot.child(p.getPostID())
                        .child("comment").child("comment_count").getValue()==null){
                    post_comment_count.add("0");
                }

                metaDataLoad(p,classID);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mREF_classList.child(classID).child("post").addListenerForSingleValueEvent(valueEventListener1);
    }

    private void metaDataLoad(PostObject p,String classID){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!p.getPostType().equals("admin_poll")){
                    if (dataSnapshot.child(p.getPostID())
                            .child("meta_data").getChildrenCount() != 0){
                        ArrayList<String> url = new ArrayList<>();
                        for (DataSnapshot s : dataSnapshot.child(p.getPostID())
                                .child("meta_data").getChildren()){
                            url.add(s.getValue().toString());
                        }
                        post_url_list.put(p.getPostID(),url);
                    }
                }else {
                    PollOptionValueLikeObject postmetaOBJ = new PollOptionValueLikeObject();
                    for (DataSnapshot s:dataSnapshot.child(p.getPostID()).child("options").getChildren()){
                        if(!s.getKey().equals("poll_clicked_user")) {
                            postmetaOBJ.optionList.add(s.getKey());
                            postmetaOBJ.votesCountList.add(s.getValue().toString());
                        }
                    }
                    post_poll_option.put(p.getPostID(),postmetaOBJ);
                }
                if (counter <classCount){
                    counter++;
                }
                else{
                    homeView.loadFeedSuccess(postObjects,post_poll_option,post_like_list,
                            post_url_list,post_comment_count,post_class_id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mREF_classList.child(classID).child("post").addListenerForSingleValueEvent(valueEventListener);
    }
}
