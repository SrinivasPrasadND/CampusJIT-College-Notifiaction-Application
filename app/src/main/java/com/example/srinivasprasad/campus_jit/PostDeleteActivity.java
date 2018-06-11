package com.example.srinivasprasad.campus_jit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostDeleteActivity extends AppCompatActivity {

    private Toolbar deleteToolabr;
    private ProgressBar deleteProgressBar;
    private TextView displayDleteing;

    private TextView DeleteBtn;
    private TextView CancelBtn;

    private String blog_post_id;
    private String BlogImageUrl;
    private String BlogThumbUrl;

    private ImageView DeleteImage;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_delete);

        deleteToolabr=findViewById(R.id.delete_toolbar);
        setSupportActionBar(deleteToolabr);
        getSupportActionBar().setTitle("Delete Post");

        firebaseFirestore=FirebaseFirestore.getInstance();

        DeleteImage=findViewById(R.id.delete_post_image);
        DeleteBtn=findViewById(R.id.delete_btn);
        CancelBtn=findViewById(R.id.cancel_btn);

        deleteProgressBar=findViewById(R.id.progress_delete);
        displayDleteing=findViewById(R.id.removing_text);
        deleteProgressBar.setVisibility(View.INVISIBLE);
        displayDleteing.setVisibility(View.INVISIBLE);


        blog_post_id = getIntent().getStringExtra("blog_post_id");
        BlogImageUrl = getIntent().getStringExtra("blog_image_url");
        BlogThumbUrl = getIntent().getStringExtra("blog_thumb_url");

        RequestOptions placeholderRequest = new RequestOptions();
        placeholderRequest.placeholder(R.drawable.image_placeholder);
        Glide.with(this).applyDefaultRequestOptions(placeholderRequest).load(BlogImageUrl).thumbnail(Glide.with(this).load(BlogThumbUrl)).into(DeleteImage);

        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goToHome();
            }
        });

        DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteProgressBar.setVisibility(View.VISIBLE);
                displayDleteing.setVisibility(View.VISIBLE);
                firebaseFirestore.collection("posts").document(blog_post_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            deleteProgressBar.setVisibility(View.INVISIBLE);
                            displayDleteing.setVisibility(View.INVISIBLE);

                            Toast.makeText(PostDeleteActivity.this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                            goToHome();
                        }
                        else{
                            deleteProgressBar.setVisibility(View.INVISIBLE);
                            displayDleteing.setVisibility(View.INVISIBLE);

                            Toast.makeText(PostDeleteActivity.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    public void goToHome(){
        Intent i = new Intent(PostDeleteActivity.this,HomeScreenActivity.class);
        startActivity(i);
        finish();
    }
}
