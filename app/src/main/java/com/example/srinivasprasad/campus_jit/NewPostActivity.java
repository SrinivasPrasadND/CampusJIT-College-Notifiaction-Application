package com.example.srinivasprasad.campus_jit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {

    private Toolbar newPostToolbar;
    private EditText postDesc;
    private ImageView postImg;
    private Button postBtn;
    private ProgressBar postProgress;
    private TextView uploadText;

    private Uri postImageUri = null;

    private String current_user_id;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private Bitmap compressedImageFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        newPostToolbar= findViewById(R.id.new_posttoolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Post");

        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        postDesc = findViewById(R.id.post_decs);
        postImg= findViewById(R.id.post_new_img);
        postBtn= findViewById(R.id.post_btn);
        postProgress= findViewById(R.id.post_progress);
        uploadText=findViewById(R.id.upload_text);


        current_user_id=mAuth.getCurrentUser().getUid();

        postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(4, 3)
                        .start(NewPostActivity.this);

            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                final String desc=postDesc.getText().toString();



                if(!TextUtils.isEmpty(desc) && postImageUri!=null){

                    postProgress.setVisibility(View.VISIBLE);
                    uploadText.setVisibility(View.VISIBLE);

                    postBtn.setEnabled(false);
                    postImg.setEnabled(false);
                    postDesc.setEnabled(false);

                    final String randomName= UUID.randomUUID().toString();

                    StorageReference filePath= storageReference.child("post_images").child(randomName+".jpg");
                    filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                            final String downloadUri= task.getResult().getDownloadUrl().toString();

                            if(task.isSuccessful()){

                                File newImageFile =new File(postImageUri.getPath());

                                try {
                                    compressedImageFile = new Compressor(NewPostActivity.this)
                                            .setMaxHeight(100)
                                            .setMaxWidth(100)
                                            .setQuality(2)
                                            .compressToBitmap(newImageFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                                byte[] thumbData = byteArrayOutputStream.toByteArray();

                                UploadTask uploadTask=  storageReference.child("post_images/thumbs").child(randomName+".jpg").putBytes(thumbData);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        String downloadThumbUri = taskSnapshot.getDownloadUrl().toString();

                                        Map<String, Object> postMap= new HashMap<>();
                                        postMap.put("image_url",downloadUri);
                                        postMap.put("thumb",downloadThumbUri);
                                        postMap.put("desc",desc);
                                        postMap.put("user_id",current_user_id);
                                        postMap.put("timestamp", FieldValue.serverTimestamp());

                                        firebaseFirestore.collection("posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                                if(task.isSuccessful()){

                                                    postBtn.setEnabled(true);
                                                    postImg.setEnabled(true);
                                                    postDesc.setEnabled(true);

                                                   // Toast.makeText(NewPostActivity.this, "Post added", Toast.LENGTH_SHORT).show();
                                                   Intent i = new Intent(NewPostActivity.this,HomeScreenActivity.class);
                                                   startActivity(i);
                                                   finish();


                                                }else{

                                                    postBtn.setEnabled(true);
                                                    postImg.setEnabled(true);
                                                    postDesc.setEnabled(true);
                                                    String er=task.getException().getMessage();
                                                    Toast.makeText(NewPostActivity.this, "Error:"+er, Toast.LENGTH_SHORT).show();

                                                }
                                                postProgress.setVisibility(View.INVISIBLE);
                                                uploadText.setVisibility(View.INVISIBLE);

                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@android.support.annotation.NonNull Exception e) {
                                        postBtn.setEnabled(true);
                                        postImg.setEnabled(true);
                                        postDesc.setEnabled(true);

                                        String er=task.getException().getMessage();
                                        Toast.makeText(NewPostActivity.this, "Error:"+er, Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }else{
                                postBtn.setEnabled(true);
                                postImg.setEnabled(true);
                                postDesc.setEnabled(true);
                                postProgress.setVisibility(View.INVISIBLE);
                                uploadText.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i= new Intent(NewPostActivity.this,HomeScreenActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri=result.getUri();

                postImg.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }


}

