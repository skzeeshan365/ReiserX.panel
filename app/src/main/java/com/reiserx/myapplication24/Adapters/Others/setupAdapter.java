package com.reiserx.myapplication24.Adapters.Others;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.myapplication24.Models.operationsModel;
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
                        if (position==3) {
                                context.startActivity(new Intent(context, OssLicensesMenuActivity.class));
                        } else if (position == 1) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse("http://reiserx.herokuapp.com/privacypolicy/"));
                                context.startActivity(i);
                        } else if (position == 2) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse("http://reiserx.herokuapp.com/terms%20of%20use/"));
                                context.startActivity(i);
                        } else if (position == 0) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse("http://reiserx.herokuapp.com/Documentation/"));
                                context.startActivity(i);
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