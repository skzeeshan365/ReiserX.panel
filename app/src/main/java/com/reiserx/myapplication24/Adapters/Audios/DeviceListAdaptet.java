package com.reiserx.myapplication24.Adapters.Audios;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.reiserx.myapplication24.Activities.Operations.operation;
import com.reiserx.myapplication24.Models.Users;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.SingleListBinding;

import java.util.ArrayList;

public class DeviceListAdaptet extends RecyclerView.Adapter<DeviceListAdaptet.SingleViewHolder> {
    Context context;
    ArrayList<Users> data;

    public DeviceListAdaptet(Context context, ArrayList<Users> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public DeviceListAdaptet.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_list, parent, false);
        return new DeviceListAdaptet.SingleViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DeviceListAdaptet.SingleViewHolder holder, int position) {
        Users users = data.get(position);

        holder.binding.userID.setText("UserID: ".concat(users.getUid()));
        holder.binding.code.setText("Code: ".concat(String.valueOf(users.getTimestamp())));
        holder.binding.nameText.setText(users.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, operation.class);
            intent.putExtra("UserID", users.getUid());
            intent.putExtra("name", users.getName());
            context.startActivity(intent);
        });

        SharedPreferences save = context.getSharedPreferences("Admins", MODE_PRIVATE);


        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        holder.itemView.setOnLongClickListener(v -> {

            FirebaseDatabase mdb = FirebaseDatabase.getInstance();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();


            dialog.setTitle("Remove ".concat(users.getName()));
            dialog.setMessage("Are you sure you want to remove this user ");
            dialog.setNegativeButton("CANCEL", null);
            dialog.setPositiveButton("REMOVE", (dialog1, which) -> {
                if(save.getString("Admin", "").equals("Admin")) {
                    mdb.getReference("Userdata").child(users.getUid())
                            .removeValue()
                            .addOnSuccessListener(unused -> {
                            });
                } else if (save.getString("Admin", "").equals("User")) {
                    mdb.getReference("Administration").child("TargetDevices").child(FirebaseAuth.getInstance().getUid()).child(users.getUid())
                            .removeValue();
                }
                mdb.getReference("Main").child(users.getUid())
                        .removeValue();
                storage.getReference("Main").child(users.getUid()).delete();
                firestore.collection("Main").document(users.getUid()).delete();

            });
            dialog.show();

            return false;
        });
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SingleViewHolder extends RecyclerView.ViewHolder {

        SingleListBinding binding;

        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SingleListBinding.bind(itemView);
        }
    }
}
