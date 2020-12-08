package com.example.buildathon.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buildathon.ChatActivity;
import com.example.buildathon.Fragments.EducationFragment;
import com.example.buildathon.Fragments.ExperienceFragment;
import com.example.buildathon.Fragments.ProfileFragment;
import com.example.buildathon.Fragments.ProjectFragment;
import com.example.buildathon.MainActivity;
import com.example.buildathon.Model.AccomplishmentModel;
import com.example.buildathon.Model.EducationModel;
import com.example.buildathon.Model.ExperienceModel;
import com.example.buildathon.Model.RoomModel;
import com.example.buildathon.R;
import com.example.buildathon.Utils.SessionManagment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.List;

import static com.example.buildathon.Utils.Constants.KEY_MOBILE;
import static com.example.buildathon.Utils.Constants.USER_REF;


public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.MyViewHolder> {

    private List<ExperienceModel> modelList;
    private Context context;
    DatabaseReference reference;
    SessionManagment sessionManagment;
    String id;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name , desc , date , creator ;
        ImageView edit,delete;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.txt_name);
            desc = view.findViewById(R.id.txt_desc);
            date = view.findViewById(R.id.txt_startdate);
            creator = view.findViewById(R.id.txt_creator);
            edit = view.findViewById(R.id.editEducation);
            delete = view.findViewById(R.id.deleteEducation);
        }
    }

    public ExperienceAdapter(List<ExperienceModel> modelList , Context context, String id) {
        this.modelList = modelList;
        this.context = context;
        this.id=id;
    }

    @Override
    public ExperienceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_project, parent, false);

        return new ExperienceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExperienceAdapter.MyViewHolder holder, int position) {
        ExperienceModel mList = modelList.get(position);
        holder.name.setText(mList.getTitle());
        holder.desc.setText(mList.getCompany_name());
        holder.date.setText(mList.getStart_date());
        holder.creator.setText(mList.getLocation());
        if (id!=null)
        {
            holder.edit.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        }
        sessionManagment = new SessionManagment(context);
        reference = FirebaseDatabase.getInstance().getReference().child(USER_REF).child(sessionManagment.getUserDetails().get(KEY_MOBILE)).child("experience");
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ExperienceFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("model", (Serializable) modelList);
                bundle.putInt("pos",position);
                bundle.putString("type","e");
                ((MainActivity)context).loadFragment(fragment,bundle);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure want to delete?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        reference.child(String.valueOf(position)).removeValue();
                        modelList.remove(position);
                        reference.setValue(modelList).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        notifyDataSetChanged();
                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

}


