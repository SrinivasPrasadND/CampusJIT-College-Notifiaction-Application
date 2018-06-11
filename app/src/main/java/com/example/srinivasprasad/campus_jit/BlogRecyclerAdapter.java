package com.example.srinivasprasad.campus_jit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list;
    public Context context;

    public BlogRecyclerAdapter(List<BlogPost> blog_list){

        this.blog_list = blog_list;


    }

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String Curent_user_id;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context=parent.getContext();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String user_id = blog_list.get(position).getUser_id();
        Curent_user_id=firebaseAuth.getCurrentUser().getUid();

        if (user_id.equals(Curent_user_id)){
            holder.postDeleteBtn.setVisibility(View.VISIBLE);
        }

        final String blogPostId = blog_list.get(position).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);

        final String image_url = blog_list.get(position).getImage_url();
        final String thumb_url = blog_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url,thumb_url);




        //UserName and Image


        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    holder.setImgUserName(task.getResult().get("name").toString(),task.getResult().get("image").toString(),task.getResult().get("thumb_url").toString());

                }else {
                    String Er=task.getException().getMessage();
                    Toast.makeText(context, "Error:"+Er, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Time
        try {
            String millisecond = blog_list.get(position).getTimestamp().toString();
            //  String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(millisecond);
        }catch (Exception ex){
            String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
            holder.setTime(currentDateTimeString);
        }
        //getLikeCounts
        firebaseFirestore.collection("posts/"+blogPostId+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                try {
                    if (!documentSnapshots.isEmpty()) {

                        int count = documentSnapshots.size();
                        holder.updateLikesCount(count);
                    } else {
                        holder.updateLikesCount(0);
                    }
                }catch (Exception ex){

                }
            }
        });

        //get Comments Count
        firebaseFirestore.collection("posts/"+blogPostId+"/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                try {
                    if (!documentSnapshots.isEmpty()) {

                        int count = documentSnapshots.size();
                        holder.updateCommentsCount(count);
                    } else {
                        holder.updateCommentsCount(0);
                    }
                }catch (Exception ex){

                }
            }
        });

        //getLikes
        firebaseFirestore.collection("posts/"+blogPostId+"/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                try{
                if (documentSnapshot.exists()){
                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_red));
                }else {
                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));
                }
                }catch (Exception ex){

                }
            }
        });


        //Like Feature
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("posts/"+blogPostId+"/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        try {
                            if (!task.getResult().exists()) {

                                Map<String, Object> likesMap = new HashMap<>();
                                likesMap.put("timestamp", FieldValue.serverTimestamp());
                                firebaseFirestore.collection("posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);
                            } else {
                                firebaseFirestore.collection("posts/" + blogPostId + "/Likes").document(currentUserId).delete();
                            }
                        }catch (Exception ex){

                        }
                    }
                });

            }
        });

        holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("blog_post_id", blogPostId);
                commentIntent.putExtra("blog_image_url",image_url);
                commentIntent.putExtra("blog_thumb_url",thumb_url);
                context.startActivity(commentIntent);

            }
        });

        holder.postDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deleteIntent = new Intent(context,PostDeleteActivity.class);
                deleteIntent.putExtra("blog_post_id", blogPostId);
                deleteIntent.putExtra("blog_image_url",image_url);
                deleteIntent.putExtra("blog_thumb_url",thumb_url);
                context.startActivity(deleteIntent);
            }
        });


        holder.blogUserimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(context,UserProfileActivity.class);
                profileIntent.putExtra("User_id",user_id);
                context.startActivity(profileIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView descView;
        private ImageView blogImageView;
        private TextView blogDate;
        private TextView  blogUsername;
        private ImageView blogUserimg;

        private ImageView blogLikeBtn;
        private TextView blogLikeCount;
        private ImageView postDeleteBtn;
        private ImageView blogCommentBtn;
        private TextView blogCommentCount;



        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogCommentBtn = mView.findViewById(R.id.comment_icon);
            postDeleteBtn=mView.findViewById(R.id.delete_image_btn);

            blogUserimg = mView.findViewById(R.id.blog_user_image);
        }

        public void setDescText(String descText){

            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(descText);

        }

        public void setBlogImage(String downloadUri,String thumb_img){

            blogImageView=mView.findViewById(R.id.blog_image);
            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.image_placeholder);
            Glide.with(context).applyDefaultRequestOptions(placeholderRequest).load(downloadUri)
                    .thumbnail(Glide.with(context).load(thumb_img)).into(blogImageView);
        }

        public void setTime(String date){

            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);

        }

        public void setImgUserName(String name, String image,String image_thumb) {


            blogUsername = mView.findViewById(R.id.blog_user_name);
            blogUsername.setText(name);


            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.profile_placeholder);

            try {
                Glide.with(context).applyDefaultRequestOptions(placeholderRequest).load(image)
                        .thumbnail(Glide.with(context).load(image_thumb)).into(blogUserimg);
            }catch (Exception e){

            }

        }

        public void updateLikesCount(int count){

            blogLikeCount= mView.findViewById(R.id.blog_like_count);
            blogLikeCount.setText(count+" Likes");
        }

        public void updateCommentsCount(int count) {

            blogCommentCount = mView.findViewById(R.id.comments_count);
            blogCommentCount.setText(count+" Comments");

        }
    }


}
