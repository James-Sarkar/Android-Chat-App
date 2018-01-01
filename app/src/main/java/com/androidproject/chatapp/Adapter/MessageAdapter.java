package com.androidproject.chatapp.Adapter;


import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidproject.chatapp.Model.Message;
import com.androidproject.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by James Sarkar.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private List<Message> messages;

    private FirebaseAuth mAuth;

    private DatabaseReference userReference;

    private final float scale = Resources.getSystem().getDisplayMetrics().density;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();

        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View itemView = inflater.inflate(R.layout.messages_layout, parent, false);

        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        String messageSenderUserId = mAuth.getCurrentUser().getUid();

        Message message = messages.get(position);

        String fromUserId = message.getFrom();

        if (messageSenderUserId.equals(fromUserId)) {
            holder.messageText.setBackgroundResource(R.drawable.message_text_backgorund_2);
            holder.messageText.setTextColor(Color.WHITE);
            holder.messageText.setPadding((int) (20 * scale + 0.5f), (int) (20 * scale + 0.5f),
                    (int) (30 * scale + 0.5f), (int) (20 * scale + 0.5f));

            holder.singleMessageContainer.setGravity(Gravity.RIGHT);

            holder.profilePicture.setVisibility(View.INVISIBLE);
        } else {
            holder.messageText.setBackgroundResource(R.drawable.message_text_background_1);
            holder.messageText.setTextColor(Color.WHITE);
            holder.messageText.setPadding((int) (30 * scale + 0.5f), (int) (20 * scale + 0.5f),
                    (int) (20 * scale + 0.5f), (int) (20 * scale + 0.5f));

            holder.singleMessageContainer.setGravity(Gravity.LEFT);

            userReference.child(fromUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    Picasso.with(null).load(dataSnapshot.child("userThumbnail").getValue().toString())
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_profile)
                            .into(holder.profilePicture, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(null)
                                            .load(dataSnapshot.child("userProfileImage").getValue().toString())
                                            .placeholder(R.drawable.default_profile)
                                            .into(holder.profilePicture);
                                }
                            });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        holder.messageText.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}

class MessageViewHolder extends RecyclerView.ViewHolder {

    TextView messageText;

    LinearLayout singleMessageContainer;

    CircleImageView profilePicture;

    MessageViewHolder(View itemView) {
        super(itemView);

        messageText = (TextView) itemView.findViewById(R.id.message_text);

        singleMessageContainer = (LinearLayout) itemView.findViewById(R.id.single_message_container);

        profilePicture = (CircleImageView) itemView.findViewById(R.id.messages_profile_picture);
    }
}
