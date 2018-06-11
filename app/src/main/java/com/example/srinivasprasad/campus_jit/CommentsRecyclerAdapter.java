package com.example.srinivasprasad.campus_jit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;

    public CommentsRecyclerAdapter(List<Comments> commentsList){

        this.commentsList = commentsList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        firebaseFirestore=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        String commentMessage = commentsList.get(position).getMessage();
        holder.setComment_message(commentMessage);

        try {
            String cmtTime = commentsList.get(position).getTimestamp().toString();
            holder.setCommentTime(cmtTime);

        } catch (Exception ex) {


            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            holder.setCommentTime(currentDateTimeString);

        }

        String user_id=commentsList.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    holder.setUserImageAndName(task.getResult().getString("name"),
                            task.getResult().getString("image"),
                            task.getResult().getString("thumb_url"));
                }
            }
        });


    }


    @Override
    public int getItemCount() {

        if(commentsList != null) {

            return commentsList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView commentMessage;
        private TextView commentTime;
        private TextView commentUserName;
        private CircleImageView commentUserImage;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setComment_message(String message){

            commentMessage = mView.findViewById(R.id.comment_message);
            commentMessage.setText(message);

        }

        public void setCommentTime(String cmtTime) {

        commentTime=mView.findViewById(R.id.comment_time);
        commentTime.setText(cmtTime);
        }

        public void setUserImageAndName(String name, String image, String thumb_url) {

            commentUserName=mView.findViewById(R.id.comment_username);
            commentUserImage=mView.findViewById(R.id.comment_image);

            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.profile_placeholder);

            commentUserName.setText(name);
            Glide.with(context).applyDefaultRequestOptions(placeholderRequest)
                    .load(image).thumbnail(Glide.with(context).load(thumb_url)).into(commentUserImage);


        }
    }

}
