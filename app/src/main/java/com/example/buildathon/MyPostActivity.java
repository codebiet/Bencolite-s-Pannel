package com.example.buildathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buildathon.Adapters.PostAdapter;
import com.example.buildathon.Model.PostModel;
import com.example.buildathon.Utils.LoadingBar;
import com.example.buildathon.Utils.SessionManagment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.buildathon.Utils.Constants.KEY_MOBILE;
import static com.example.buildathon.Utils.Constants.KEY_PROFILE_IMG;
import static com.example.buildathon.Utils.Constants.POST_INFO;
import static com.example.buildathon.Utils.Constants.USER_REF;

public class MyPostActivity extends AppCompatActivity {

    DatabaseReference postref;
    SessionManagment sessionManagment ;
    LoadingBar loadingBar;
    List<PostModel> modelList;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
    CircleImageView home_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);
        loadingBar = new LoadingBar(this);
        loadingBar.show();
        modelList = new ArrayList<>();
        sessionManagment = new SessionManagment(this);
        home_img = findViewById(R.id.home_img);
        Glide.with(this).load(sessionManagment.getUserDetails().get(KEY_PROFILE_IMG)).into(home_img);
        recyclerView = findViewById(R.id.recyclerHome);
        CardView post = findViewById(R.id.post);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        postAdapter = new PostAdapter(modelList,this);
        recyclerView.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyPostActivity.this, PostActivity.class));
            }
        });
        postref = FirebaseDatabase.getInstance().getReference().child(USER_REF).child(sessionManagment.getUserDetails().get(KEY_MOBILE)).child(POST_INFO);
        getData();
    }
    private void getData() {
        postref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelList.clear();
                for (DataSnapshot snap :snapshot.getChildren()
                ) {
                    PostModel model = snap.getValue(PostModel.class);
                    modelList.add(model);
                    postAdapter.notifyDataSetChanged();
                }
                loadingBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingBar.dismiss();
                Toast.makeText(getApplicationContext(),""+error,Toast.LENGTH_LONG).show();
            }
        });
    }
}