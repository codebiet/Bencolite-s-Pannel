package com.example.buildathon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buildathon.Model.PostModel;
import com.example.buildathon.Utils.LoadingBar;
import com.example.buildathon.Utils.SessionManagment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.buildathon.Utils.Constants.KEY_MOBILE;
import static com.example.buildathon.Utils.Constants.KEY_NAME;
import static com.example.buildathon.Utils.Constants.KEY_PROFILE_IMG;
import static com.example.buildathon.Utils.Constants.POST_INFO;
import static com.example.buildathon.Utils.Constants.USER_REF;

public class PostActivity extends AppCompatActivity {

    DatabaseReference postref , userref;
    LoadingBar loadingBar;
    SessionManagment sessionManagment;
    TextView btnPost , username;
    EditText etPost;
    ImageView pickImage , imgPost;
    CircleImageView userimg;
    private static final int PICK_IMAGE_REQUEST =1;
    private Uri mImageUri;
    StorageReference mStorageRef;
    private StorageTask mUploadTask;
    String posttext;
    PostModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        sessionManagment = new SessionManagment(this);
        loadingBar = new LoadingBar(this);
        postref = FirebaseDatabase.getInstance().getReference().child(POST_INFO);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        userref = FirebaseDatabase.getInstance().getReference().child(USER_REF).child(sessionManagment.getUserDetails().get(KEY_MOBILE)).child(POST_INFO);

        userimg = findViewById(R.id.userimg);
        btnPost = findViewById(R.id.btnPost);
        username = findViewById(R.id.username);
        etPost = findViewById(R.id.post_text);
        imgPost = findViewById(R.id.imagepost);
        pickImage = findViewById(R.id.insertImage);
        Glide.with(this).load(sessionManagment.getUserDetails().get(KEY_PROFILE_IMG)).into(userimg);
        username.setText(sessionManagment.getUserDetails().get(KEY_NAME));

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        etPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()<1&&mImageUri==null)
                {
                    btnPost.setEnabled(false);
                    btnPost.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
                else
                {
                    btnPost.setEnabled(true);
                    btnPost.setTextColor(getResources().getColor(R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPost.getText().toString().trim().length()<1&&mImageUri==null)
                {
                    btnPost.setEnabled(false);
                    return;
                }
                if (btnPost.isEnabled())
                {
                    uploadFile();
                }
            }
        });

    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri){
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK
                && data!= null && data.getData()!=null){
            mImageUri=data.getData();
            imgPost.setVisibility(View.VISIBLE);
            btnPost.setEnabled(true);
            Glide.with(this).load(mImageUri).into(imgPost);
        }
    }
    private void uploadFile(){
        loadingBar.show();
        if(mImageUri!=null){
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            mUploadTask=fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> urlTask= taskSnapshot.getStorage().getDownloadUrl();
                            while(!urlTask.isSuccessful());
                            posttext = etPost.getText().toString().trim();
                            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                            Uri downloadUrl=urlTask.getResult();
                            String postId = postref.push().getKey();
                            String username = sessionManagment.getUserDetails().get(KEY_NAME);
                            String usernumber = sessionManagment.getUserDetails().get(KEY_MOBILE);
                            model = new PostModel(username,usernumber,posttext,downloadUrl.toString(),currentDate,currentTime,postId);
                            userref.child(postId).setValue(model);
                            postref.child(postId).setValue(model);
                            Toast.makeText(PostActivity.this,"Uploaded Successfully",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostActivity.this , e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>(){
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot){

                        }
                    });
        }
        else if (etPost.getText().toString().trim().length()>0)
        {
            posttext = etPost.getText().toString().trim();
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            String postId = postref.push().getKey();
            String username = sessionManagment.getUserDetails().get(KEY_NAME);
            String usernumber = sessionManagment.getUserDetails().get(KEY_MOBILE);
            model = new PostModel(username,usernumber,posttext,"".toString(),currentDate,currentTime,postId);
            userref.child(postId).setValue(model);
            postref.child(postId).setValue(model);
            Toast.makeText(PostActivity.this,"Uploaded Successfully",Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        else{
            loadingBar.dismiss();
            Toast.makeText(PostActivity.this,"Type Something or add image",Toast.LENGTH_SHORT).show();
        }
    }
}