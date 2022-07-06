package com.reiserx.myapplication24.Adapters.Others;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.myapplication24.Activities.FileViewers.TextViewerActivity;
import com.reiserx.myapplication24.Models.operationsModel;
import com.reiserx.myapplication24.Models.policyModel;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.OperationCustomViewBinding;

import java.util.ArrayList;

public class setupAdapter extends RecyclerView.Adapter<setupAdapter.SingleViewHolder> {

        Context context;
        ArrayList<operationsModel> data;
        ProgressDialog prog;
        FirebaseFirestore firestore;

        public setupAdapter(Context context, ArrayList<operationsModel> data) {
                this.context = context;
                this.data = data;
                prog = new ProgressDialog(context);
                prog.setCancelable(false);
                prog.setMessage("loading...");
                firestore = FirebaseFirestore.getInstance();
        }

        @NonNull
        @Override
        public setupAdapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.operation_custom_view, parent, false);
                return new setupAdapter.SingleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull setupAdapter.SingleViewHolder holder, int position) {
                operationsModel model = data.get(position);

                holder.binding.textView.setText(model.getName());
                holder.binding.getRoot().setOnClickListener(v -> {
                });
                holder.itemView.setOnClickListener(view -> {
                        if (position==2) {
                                prog.show();
                                firestore.collection("Driver").document("Policy").collection("Setup").document("setupData").get().addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                                policyModel policyModel = documentSnapshot.toObject(com.reiserx.myapplication24.Models.policyModel.class);
                                                if (policyModel != null) {
                                                        prog.dismiss();
                                                        Intent intent = new Intent(context, TextViewerActivity.class);
                                                        intent.putExtra("data", policyModel.getData());
                                                        intent.putExtra("timestamp", policyModel.getTimestamp());
                                                        intent.putExtra("requestCode", 2);
                                                        intent.putExtra("name", "Setup");
                                                        context.startActivity(intent);
                                                }
                                        }

                                }).addOnFailureListener(e -> {
                                        prog.dismiss();
                                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                                });
                        } else if (position == 0) {
                                prog.show();
                                FirebaseFirestore.getInstance().collection("Driver").document("Policy").collection("Setup").document("privacyPolicy").get().addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                                policyModel policyModel = documentSnapshot.toObject(com.reiserx.myapplication24.Models.policyModel.class);
                                                if (policyModel != null) {
                                                        prog.dismiss();
                                                        Intent intent = new Intent(context, TextViewerActivity.class);
                                                        intent.putExtra("data", policyModel.getData());
                                                        intent.putExtra("timestamp", policyModel.getTimestamp());
                                                        intent.putExtra("requestCode", 0);
                                                        intent.putExtra("name", "Privacy policy");
                                                        context.startActivity(intent);
                                                }
                                        }
                                }).addOnFailureListener(e -> {
                                        prog.dismiss();
                                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                                });
                        } else if (position == 1) {
                                prog.show();
                                FirebaseFirestore.getInstance().collection("Driver").document("Policy").collection("Setup").document("terms").get().addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                                policyModel policyModel = documentSnapshot.toObject(com.reiserx.myapplication24.Models.policyModel.class);
                                                if (policyModel != null) {
                                                        prog.dismiss();
                                                        Intent intent = new Intent(context, TextViewerActivity.class);
                                                        intent.putExtra("data", policyModel.getData());
                                                        intent.putExtra("timestamp", policyModel.getTimestamp());
                                                        intent.putExtra("requestCode", 1);
                                                        intent.putExtra("name", "Terms of use");
                                                        context.startActivity(intent);
                                                }
                                        }
                                }).addOnFailureListener(e -> {
                                        prog.dismiss();
                                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                                });
                        }
                });
        }

        @Override
        public int getItemCount() {
                return data.size();
        }

        public class SingleViewHolder extends RecyclerView.ViewHolder {

                OperationCustomViewBinding binding;

                public SingleViewHolder(@NonNull View itemView) {
                        super(itemView);
                        binding = OperationCustomViewBinding.bind(itemView);
                }
        }
}