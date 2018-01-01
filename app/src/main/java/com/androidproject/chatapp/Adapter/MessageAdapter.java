package com.androidproject.chatapp.Adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidproject.chatapp.Model.Message;
import com.androidproject.chatapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by James Sarkar.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View itemView = inflater.inflate(R.layout.messages_layout, parent, false);

        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.messageText.setText(message.getMessage());

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}

class MessageViewHolder extends RecyclerView.ViewHolder {

    TextView messageText;

    CircleImageView userProfilePicture;

    MessageViewHolder(View itemView) {
        super(itemView);

        messageText = (TextView) itemView.findViewById(R.id.message_text);
        userProfilePicture = (CircleImageView) itemView.findViewById(R.id.messages_profile_picture);
    }
}
