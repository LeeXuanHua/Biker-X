package com.example.bikerx.ui.chat;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bikerx.R;

import java.util.ArrayList;

/**
 * A fragment class that inherits the built-in Fragment class to support user interaction relating to Message objects.
 * This class mainly offers functionalities for the Chat/Forum component of the application.
 *
 * @author Xuan Hua
 * @version 1.0, 25/03/2022
 * @since 17.0.2
 *
 */
public class MessageFragment extends Fragment {
    /**
     * The view model of this MessageFragment object.
     * @see MessageViewModel
     */
    private MessageViewModel mViewModel;
    /**
     * The recycler view of this MessageFragment object.
     * @see RecyclerView
     */
    private RecyclerView mRecyclerView;
    /**
     * The inflated view of this MessageFragment object.
     * @see View
     */
    private View view;
    /**
     * The message adapter of this MessageFragment object
     * @see MessageAdapter
     */
    private MessageAdapter mAdapter;
    /**
     * The thread id of this MessageFragment object
     * Obtained from the arguments passed in by the navigation action
     */
    private String threadId;
    /**
     * The thread name of this MessageFragment object
     * Obtained from the arguments passed in by the navigation action
     */
    private String forumHeading;
    /**
     * The TextView to send message for this MessageFragment object
     */
    private TextView messageTextbox;

    /**
     * Starts a new instance of the MessageFragment class
     * @return a new instance of MessageFragment class
     */
    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    /**
     * Overrides the original onCreate to assign the arguments obtained from the navigation to MessageFragment attributes
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        threadId = getArguments().getString("threadId");
        forumHeading = getArguments().getString("threadName");
    }

    /**
     * Overrides the original onCreateView to assign the view model and view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        view = inflater.inflate(R.layout.message_fragment, container, false);
        return view;
    }

    /**
     * Overrides the original onViewCreated to set up the forum title, message recycler view and to bind the buttons
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setForumTitle(view, forumHeading);
        displayMessageList(view);
        bindButtons(view);
    }

    /**
     * Set text for the forum title, obtained from MessageFragment attribute
     * @param view
     * @param forumHeading current MessageFragment attribute
     */
    private void setForumTitle(View view, String forumHeading) {
        TextView messageForumHeading = view.findViewById(R.id.messageForumHeading);
        messageForumHeading.setText(forumHeading);
    }

    /**
     * Calls the fetchMessages and getMessages method from the view model, and set up a recycler view for Message objects
     * Recycler view layout is defined to set stack from end
     * Displays an error message if there is no data fetched (TO-DO)
     * @param view
     */
    private void displayMessageList(View view) {
        mViewModel.fetchMessages(threadId);
//        if (mViewModel.mutableMessageArrayList.getValue().isEmpty()) {
//            Toast.makeText(getActivity().getApplicationContext(), "Error Getting Data", Toast.LENGTH_LONG).show();
//        }
        mViewModel.getMessages().observe(this, new Observer<ArrayList<Message>>() {
            @Override
            public void onChanged(ArrayList<Message> messageArray) {
                mRecyclerView = view.findViewById(R.id.messageRecycleView);
                mRecyclerView.setHasFixedSize(true);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
//                mLayoutManager.setReverseLayout(true);
                mLayoutManager.setStackFromEnd(true);
                mRecyclerView.setLayoutManager(mLayoutManager);

                mAdapter = new MessageAdapter(messageArray);
                mRecyclerView.setAdapter(mAdapter);
            };
        });
    }

    /**
     * Binds the buttons and views during onViewCreated
     * @param view
     */
    private void bindButtons(View view) {
        messageTextbox = view.findViewById(R.id.sendMessageTextbox);

        view.findViewById(R.id.sendMessageButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String messageContent = messageTextbox.getText().toString();
                if (!messageContent.replace(" ","").replace("\n","").equals("")) {
                    mViewModel.sendMessage(getActivity(), threadId, messageContent, mAdapter);
                }
                messageTextbox.setText("");
            }
        });
    }

    /**
     * Overrides the original onDestroyView
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
