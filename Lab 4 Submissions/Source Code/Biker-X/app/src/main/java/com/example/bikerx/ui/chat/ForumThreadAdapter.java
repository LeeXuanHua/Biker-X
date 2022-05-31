package com.example.bikerx.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikerx.R;

import java.util.ArrayList;

/**
 * A recyclerview adapter class that inherits the built-in RecyclerView.Adapter class (of type ForumThreadAdapter.ViewHolder) to support recyclerview operations relating to ForumThread objects.
 * This class mainly offers functionalities for the Chat/Forum component of the application.
 *
 * @author Xuan Hua
 * @version 1.0, 25/03/2022
 * @since 17.0.2
 *
 */
public class ForumThreadAdapter extends RecyclerView.Adapter<ForumThreadAdapter.ViewHolder>{
    /**
     * The forum thread array list of this ForumThreadAdapter object.
     */
    private ArrayList<ForumThread> forumThreadMutableList;
    /**
     * The context of this ForumThreadAdapter object.
     */
    private Context cContext;
    /**
     * The fragment communication interface used to connect this adapter to the chat fragment.
     */
    private FragmentCommunication cCommunicator;

    /**
     * Constructs a new ForumThreadAdapter object with the specified forum thread array list and fragment communication interface.
     * @param forumThreadMutableList the forum thread array list of this ForumThreadAdapter object
     * @param cCommunicator the fragment communication interface of this ForumThreadAdapter object
     */
    public ForumThreadAdapter(ArrayList<ForumThread> forumThreadMutableList, FragmentCommunication cCommunicator) {
        this.forumThreadMutableList = forumThreadMutableList;
        this.cCommunicator = cCommunicator;
    }

    /**
     * Overrides the original onCreateViewHolder to assign the view and context
     * @param parent
     * @param viewType
     * @return the viewholder for the view and fragment communication interface of this ForumThreadAdapter object
     */
    @NonNull
    @Override
    public ForumThreadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_forum_row_item, parent, false);
        cContext = view.getContext();
        return new ViewHolder(view, cCommunicator);
    }

    /**
     * Overrides the original onBindViewHolder to assign the data from the forum thread array list to the recyclerview rows
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String threadId = forumThreadMutableList.get(position).getThreadId();
        String threadName = forumThreadMutableList.get(position).getThreadName();
        ArrayList<Message> messageArrayList = forumThreadMutableList.get(position).getMessageArrayList();

        holder.setData(position, threadId, threadName, messageArrayList);
    }

    /**
     * Overrides the original getItemCount to obtain the size of the forum thread array list
     * @return the length of the array list for forum thread, specified as integer
     */
    @Override
    public int getItemCount() {return forumThreadMutableList.size();}

    /**
     * A ViewHolder class that inherits the built-in RecyclerView.ViewHolder and implements the View.OnClickListerner interface
     * This class sets up and displays the data obtained to the User Interface
     * Alternating forum threads are set with alternating background colours
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView forumThreadName;
        private TextView forumThreadDescription;
        private FrameLayout forumRow;
        private Button forumButton;
        private FrameLayout forumThreadRowFrame;
        private FragmentCommunication cCommunication;
        private String threadId;
        private String threadName;

        public ViewHolder(@NonNull View itemView, FragmentCommunication Communicator){
            super(itemView);
            forumThreadName = itemView.findViewById(R.id.forumThreadName);
            forumThreadDescription = itemView.findViewById(R.id.forumThreadDescription);
            forumRow = itemView.findViewById(R.id.forumThreadRowItemFrame);
            forumButton = itemView.findViewById(R.id.forumThreadButton);
            forumButton.setOnClickListener(this);
            forumThreadRowFrame = itemView.findViewById(R.id.forumThreadRowItemFrame);
            forumThreadRowFrame.setOnClickListener(this);
            cCommunication = Communicator;
        }

        public void setData(int position, String threadId, String threadName, ArrayList<Message> messageArrayList){
            this.threadId = threadId;
            this.threadName = threadName;

            forumThreadName.setText(threadName);
            forumThreadDescription.setText(messageArrayList.get(0).getMessageContent());
            if(position%2==0) {
                forumRow.setBackgroundResource(R.color.recycler_view_forum_row_item_forumThreadRowItemFrame_color_odd);
            } else {
                forumRow.setBackgroundResource(R.color.recycler_view_forum_row_item_forumThreadRowItemFrame_color_even);
            }
        }

        @Override
        public void onClick(View view) {
            cCommunication.onForumClick(this.threadId, this.threadName);
        }
    }
}