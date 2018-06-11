package com.example.srinivasprasad.campus_jit;

import android.content.Intent;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class HomeScreenActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private String current_user_id;

    private FloatingActionButton AddImageBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;


    private RecyclerView blog_list_view;
    private List<BlogPost> blog_list;

    private BlogRecyclerAdapter blogRecyclerAdapter;


    private DocumentSnapshot lastVisible;
    private  Boolean isFirstPageFirstLoad = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        mainToolbar = findViewById(R.id.main_toolBar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Campus JIT");

        AddImageBtn=findViewById(R.id.add_img);

        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);


        blog_list = new ArrayList<>();
        blog_list_view = findViewById(R.id.blog_list_view);

        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(this));
        blog_list_view.setAdapter(blogRecyclerAdapter);



       AddImageBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent i = new Intent(HomeScreenActivity.this,NewPostActivity.class);
               startActivity(i);
               finish();
           }
       });

        if(mAuth.getCurrentUser() != null) {
            firebaseFirestore.collection("posts").orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    try {



                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {


                                String blogPostId  = doc.getDocument().getId();
                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                                blog_list.add(0,blogPost);

                                /*if(isFirstPageFirstLoad) {
                                    blog_list.add(blogPost);
                                }else {
                                    blog_list.add(0,blogPost);

                                }*/
                                blogRecyclerAdapter.notifyDataSetChanged();
                             //   blog_list_view.scrollToPosition(0);

                            }
                        }
                        //isFirstPageFirstLoad=false;

                    } catch (Exception ex) {

                    }
                }
            });
        }



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser==null){
            Intent i=new Intent(HomeScreenActivity.this,LoginActivity.class);
            startActivity(i);
            finish();

        }else{

            current_user_id=mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        if(!task.getResult().exists()){

                            Intent intent=new Intent(HomeScreenActivity.this,AccountSetupActivity.class);
                            startActivity(intent);
                           // finish();

                        }

                    }else {
                        String er=task.getException().getMessage();
                        Toast.makeText(HomeScreenActivity.this, "Error:"+er, Toast.LENGTH_SHORT).show();

                    }
                }
            });


        }
    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout_btn:
                logOut();
                return true;

            case R.id.action_settings_btn:

                Intent settingsIntent = new Intent(HomeScreenActivity.this, AccountSetupActivity.class);
                startActivity(settingsIntent);
                return true;

            default:
                return false;


        }

    }

    private void logOut() {
       mAuth.signOut();
        sendToLogin();

    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(HomeScreenActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }




}
