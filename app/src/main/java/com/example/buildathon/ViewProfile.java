package com.example.buildathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.example.buildathon.Adapters.EducationAdapter;
import com.example.buildathon.Adapters.ExperienceAdapter;
import com.example.buildathon.Adapters.ProjectAdapter;
import com.example.buildathon.Adapters.SkillAdapter;
import com.example.buildathon.Fragments.AboutFragment;
import com.example.buildathon.Fragments.EducationFragment;
import com.example.buildathon.Fragments.ExperienceFragment;
import com.example.buildathon.Fragments.ProjectFragment;
import com.example.buildathon.Fragments.SkillFragment;
import com.example.buildathon.Model.AccomplishmentModel;
import com.example.buildathon.Model.EducationModel;
import com.example.buildathon.Model.ExperienceModel;
import com.example.buildathon.Model.SkillModel;
import com.example.buildathon.Model.UserModel;
import com.example.buildathon.Utils.LoadingBar;
import com.example.buildathon.Utils.SessionManagment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.buildathon.Utils.Constants.KEY_DESCRIPTION;
import static com.example.buildathon.Utils.Constants.KEY_LOCATION;
import static com.example.buildathon.Utils.Constants.KEY_MOBILE;
import static com.example.buildathon.Utils.Constants.KEY_NAME;
import static com.example.buildathon.Utils.Constants.KEY_PROFILE_IMG;
import static com.example.buildathon.Utils.Constants.KEY_TITLE;
import static com.example.buildathon.Utils.Constants.USER_REF;

public class ViewProfile extends AppCompatActivity {

    SessionManagment sessionManagment;
    String name , title , description , location;
    TextView txtname , txttitle  , txtloc , txtToolTitle;
    ReadMoreTextView txtdesc;
    ImageView editAbout,edit_education,edit_accomplish,edit_experience,edit_skill,edit_img;
    List<EducationModel> education;
    LoadingBar loadingBar;
    List<AccomplishmentModel> project;
    List<ExperienceModel> experience;
    List<SkillModel> skill;
    ExperienceAdapter experienceAdapter;
    DatabaseReference userref;
    UserModel model;
    RecyclerView recyclerEducation,recyclerProject,recyclerExperience,recyclerSkill;
    EducationAdapter educationAdapter;
    SkillAdapter skillAdapter;
    ProjectAdapter projectAdapter;
    TextView txtexp , txtEdu , txtPro,txtSkill;
    private Uri mImageUri;
    StorageReference mStorageRef;
    CircleImageView profileimg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        model =(UserModel) getIntent().getSerializableExtra("model");
        txtToolTitle=findViewById(R.id.txtTitle);
        txtToolTitle.setText(model.getName());
        sessionManagment = new SessionManagment(this);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        loadingBar = new LoadingBar(this);
        loadingBar.show();
        userref = FirebaseDatabase.getInstance().getReference().child(USER_REF).child(sessionManagment.getUserDetails().get(KEY_MOBILE));
        txtname = findViewById(R.id.txt_name);
        txttitle = findViewById(R.id.txt_title);
        txtdesc = (ReadMoreTextView)findViewById(R.id.txt_about);
        profileimg = findViewById(R.id.profile_img);
        recyclerEducation = findViewById(R.id.recyclerEducation);
        recyclerProject = findViewById(R.id.recyclerAccomplish);
        recyclerSkill = findViewById(R.id.recyclerSkill);
        recyclerExperience = findViewById(R.id.recyclerExperience);
        txtloc = findViewById(R.id.txt_location);
        education = new ArrayList<>();
        skill = new ArrayList<>();
        project = new ArrayList<>();
        experience = new ArrayList<>();
        updateAbout();
        getData();


    }

    private void getEducation() {
        List<EducationModel> educationList = model.getEducation();
        recyclerEducation.setLayoutManager(new GridLayoutManager(ViewProfile.this,1));
        educationAdapter= new EducationAdapter(educationList,ViewProfile.this,model.getId());
        recyclerEducation.setAdapter(educationAdapter);
        educationAdapter.notifyDataSetChanged();

    }
    private void getProjects() {
        List<AccomplishmentModel> projectList = model.getProject();
        recyclerProject.setLayoutManager(new GridLayoutManager(ViewProfile.this,1));
        projectAdapter= new ProjectAdapter(projectList,ViewProfile.this,model.getId());
        recyclerProject.setAdapter(projectAdapter);
        projectAdapter.notifyDataSetChanged();

    }


    private void getData() {
        education = model.getEducation();
        project = model.getProject();
        experience = model.getExperience();
        skill = model.getSkill();
        Log.e("yess",model.toString());
        if (education!=null)
        {
            getEducation();
        }
        if (project!=null)
        {
            getProjects();
        }
        if (experience!=null)
        {
            getExperience();
        }

        if (skill!=null)
        {
            getSkill();
        }
        if (model.getImg_url()!=null)
        {
            Glide.with(ViewProfile.this).load(model.getImg_url()).into(profileimg);
        }
        loadingBar.dismiss();
    }

    private void getSkill(){
        List<SkillModel> skillList = model.getSkill();
        recyclerSkill.setLayoutManager(new GridLayoutManager(ViewProfile.this,1));
        skillAdapter= new SkillAdapter(skillList,ViewProfile.this,model.getId());
        recyclerSkill.setAdapter(skillAdapter);
        skillAdapter.notifyDataSetChanged();
    }

    private void getExperience() {
        List<ExperienceModel> experienceList = model.getExperience();
        recyclerExperience.setLayoutManager(new GridLayoutManager(ViewProfile.this,1));
        experienceAdapter= new ExperienceAdapter(experienceList,ViewProfile.this,model.getId());
        recyclerExperience.setAdapter(experienceAdapter);
        experienceAdapter.notifyDataSetChanged();
    }

    private void updateAbout() {
        title = model.getTitle();
        description = model.getDescription();
        location = model.getLocation();
        if (title==null||title.equalsIgnoreCase(""))
            txttitle.setVisibility(View.GONE);
        if (description==null||description.equalsIgnoreCase(""))
            txtdesc.setVisibility(View.GONE);
        if (location==null||location.equalsIgnoreCase(""))
            txtloc.setVisibility(View.GONE);
        txtname.setText(model.getName());
        txttitle.setText(title);
        txtloc.setText(location);
        txtdesc.setText(description);
    }
}