package com.pistudiosofficial.myclass.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pistudiosofficial.myclass.R;
import com.pistudiosofficial.myclass.adapters.AdapterChatList;
import com.pistudiosofficial.myclass.objects.ChatListMasterObject;
import com.pistudiosofficial.myclass.view.ChatListView;

import static com.pistudiosofficial.myclass.Common.CHAT_MASTER_OBJECT;

public class ChatListDialogFragment extends DialogFragment implements ChatListView {

    RecyclerView recyclerView;
    LinearLayoutManager llm;
    ChatListMasterObject chatListMasterObject;
    AdapterChatList adapterChatList;
    public ChatListDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ChatListDialogFragment newInstance(String title) {
        ChatListDialogFragment frag = new ChatListDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.chatListMasterObject = CHAT_MASTER_OBJECT;
        View view = inflater.inflate(R.layout.activity_chat_list, container);
        adapterChatList = new AdapterChatList(chatListMasterObject.chatHashMap
                ,chatListMasterObject.userObjects,chatListMasterObject.chatIndex,
                chatListMasterObject.chatCounts,getContext());
        recyclerView = view.findViewById(R.id.recyclerView_chatList);
        llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapterChatList);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        getDialog().setTitle("Chat List");

    }
    @Override
    public void ChatLoaded(ChatListMasterObject chatListMasterObject) {
        adapterChatList.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (metrics.heightPixels * 0.30));// here i have fragment height 30% of window's height you can set it as per your requirement
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.Animation_WindowSlideUpDown;

    }
}