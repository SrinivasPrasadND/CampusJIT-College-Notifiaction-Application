package com.example.srinivasprasad.campus_jit;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private String user_id;
    private Toolbar profileToolbar;
    private CircleImageView pUserImg;

    private TextView UserPostTxt;
    private TextView pUserName,pUserSem,pUserBranch;
    private FirebaseFirestore firebaseFirestore;

    private RecyclerView pBlog_list_view;
    private List<BlogPost> pBlog_list;

    private ProfileRecyclerAdapter profileRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profileToolbar=findViewById(R.id.profile_toolbar);
        setSupportActionBar(profileToolbar);
        getSupportActionBar().setTitle("User Profile");

        firebaseFirestore=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        user_id=getIntent().getStringExtra("User_id");
        pUserImg=findViewById(R.id.profile_user_img);
        pUserName=findViewById(R.id.profile_user_name);
        pUserSem=findViewById(R.id.profile_user_sem);
        pUserBranch=findViewById(R.id.profile_user_branch);
        UserPostTxt=findViewById(R.id.user_post_text);

        pBlog_list=new ArrayList<>();
        pBlog_list_view=findViewById(R.id.profile_post_list);

        profileRecyclerAdapter=new ProfileRecyclerAdapter(pBlog_list);
        pBlog_list_view.setLayoutManager(new LinearLayoutManager(this));
        pBlog_list_view.setAdapter(profileRecyclerAdapter);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    String name=task.getResult().getString("name");
                    String sem=task.getResult().getString("sem");
                    String branch=task.getResult().getString("branch");
                    String image=task.getResult().getString("image");
                    String thumb=task.getResult().getString("thumb_url");
                    setUserDetails(name,sem,branch,image,thumb);

                    UserPostTxt.setText(name+"'s Posts");


                }
            }
        });

        firebaseFirestore.collection("posts").whereEqualTo("user_id",user_id).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent( QuerySnapshot DocumentSnapshots,FirebaseFirestoreException e) {

                try{
                for (DocumentChange doc : DocumentSnapshots.getDocumentChanges()){

                    if (doc.getType() == DocumentChange.Type.ADDED){

                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                        pBlog_list.add(blogPost);
                        profileRecyclerAdapter.notifyDataSetChanged();
                    }

                }
                }catch (Exception ex){
                    //Toast.makeText(UserProfileActivity.this, ""+ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();





    }

    private void setUserDetails(String name, String sem, String branch, String image, String thumb) {


        RequestOptions placeholderRequest = new RequestOptions();
        placeholderRequest.placeholder(R.drawable.profile_placeholder);

        try {
            Glide.with(this).applyDefaultRequestOptions(placeholderRequest).load(image).thumbnail(Glide.with(this).load(thumb)).into(pUserImg);
        }catch (Exception e){

        }
        pUserName.setText(name);
        pUserSem.setText(sem);
        pUserBranch.setText(branch);
    }
}
