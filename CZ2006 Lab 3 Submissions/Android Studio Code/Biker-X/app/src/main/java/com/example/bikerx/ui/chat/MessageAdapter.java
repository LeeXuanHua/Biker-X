package com.example.bikerx.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikerx.MainActivity;
import com.example.bikerx.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A recyclerview adapter class that inherits the built-in RecyclerView.Adapter class (of type MessageAdapter.ViewHolder) to support recyclerview operations relating to Message objects.
 * This class mainly offers functionalities for the Chat/Forum component of the application.
 *
 * @author Xuan Hua
 * @version 1.0, 25/03/2022
 * @since 17.0.2
 *
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    /**
     * The message array list of this MessageAdapter object.
     */
    private ArrayList<Message> messageMutableList;
    /**
     * The context of this MessageAdapter object.
     */
    private Context mContext;

    /**
     * Constructs a new MessageAdapter object with the specified message array list.
     * @param messageMutableList the message array list of this MessageAdapter object
     */
    public MessageAdapter(ArrayList<Message> messageMutableList) {
        this.messageMutableList = messageMutableList;
    }

    /**
     * Overrides the original onCreateViewHolder to assign the view and context
     * @param parent
     * @param viewType
     * @return the viewholder for the view of this ForumThreadAdapter object
     */
    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_message_row_item, parent, false);
        mContext = view.getContext();
        return new ViewHolder(view);
    }

    /**
     * Overrides the original onBindViewHolder to assign the data from the message array list to the recyclerview rows
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String userId = messageMutableList.get(position).getUserId();
        String name = messageMutableList.get(position).getUserName();
        String messageID = messageMutableList.get(position).getMessageId();
        String content = messageMutableList.get(position).getMessageContent();

        Date timestamp = messageMutableList.get(position).getTime().toDate();
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext.getApplicationContext());
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(mContext.getApplicationContext());
        String time = timeFormat.format(timestamp);
        String date = dateFormat.format(timestamp);
        String previousDate = null;
        if (position>=1) {
            Date previousTimestamp = messageMutableList.get(position-1).getTime().toDate();
            previousDate = dateFormat.format(previousTimestamp);
        }

        holder.setData(position, ((MainActivity) mContext).getUserId(), userId, name, messageID, date, time, content, previousDate);
    }

    /**
     * Overrides the original getItemCount to obtain the size of the message array list
     * @return
     */
    @Override
    public int getItemCount() {return messageMutableList.size();}

    /**
     * A ViewHolder class that inherits the built-in RecyclerView.ViewHolder.
     * This class sets up and displays the data obtained to the User Interface.
     * Date will be displayed only for messages from different dates.
     * Messages sent by the current user will be colour-coded and have its right constraint defined
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView messageName;
        private TextView messageContent;
        private TextView messageTime;
        private TextView messageDate;
        private ConstraintLayout messageConstraint;
        private ConstraintLayout messageDateConstraint;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            messageName = itemView.findViewById(R.id.messageName);
            messageContent = itemView.findViewById(R.id.messageContent);
            messageTime = itemView.findViewById(R.id.messageTime);
            messageDate = itemView.findViewById(R.id.messageDate);
            messageConstraint = itemView.findViewById(R.id.messageRow);
            messageDateConstraint = itemView.findViewById(R.id.messageDateRow);
        }

        public void setData(int position, String currentUserId, String userId, String name, String messageID, String date, String time, String content, String previousDate){
            messageName.setText(name);
            messageContent.setText(content);
            messageTime.setText(time);
            messageDate.setText(date);
            if (date.equals(previousDate)) {
                messageDateConstraint.setVisibility(View.GONE);
            } else {
                messageDateConstraint.setVisibility(View.VISIBLE);
            }
            if(userId.equals(currentUserId)) {
                final ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) messageConstraint.getLayoutParams();
                layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET;
                layoutParams.rightToRight = R.id.messageRowItemFrame;
                layoutParams.setMarginEnd(20);
                messageConstraint.setLayoutParams(layoutParams);

                messageConstraint.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.recycler_view_message_row_item_user_color));
            } else {
                final ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) messageConstraint.getLayoutParams();
                layoutParams.rightToRight = ConstraintLayout.LayoutParams.UNSET;
                layoutParams.startToStart = R.id.messageRowItemFrame;
                layoutParams.setMarginStart(10);
                messageConstraint.setLayoutParams(layoutParams);

                messageConstraint.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.recycler_view_message_row_item_color));
            }
        }
    }
}