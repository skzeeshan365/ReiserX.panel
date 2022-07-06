package com.reiserx.myapplication24.Adapters.Contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reiserx.myapplication24.Classes.copyToClipboard;
import com.reiserx.myapplication24.Models.contacts_lists;
import com.reiserx.myapplication24.R;
import com.reiserx.myapplication24.databinding.ContactListBinding;

import java.util.ArrayList;
import java.util.List;

public class contactAdapter extends RecyclerView.Adapter<contactAdapter.SingleViewHolder> {
    Context context;
    ArrayList<contacts_lists> data;

    public contactAdapter(Context context, ArrayList<contacts_lists> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public contactAdapter.SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_list, parent, false);
        return new contactAdapter.SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull contactAdapter.SingleViewHolder holder, int position) {
        contacts_lists contacts = data.get(position);

        holder.binding.name.setText(contacts.getName());
        holder.binding.number.setText(contacts.phoneNumber);

        holder.binding.getRoot().setOnClickListener(v -> {
        });
        holder.binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                copyToClipboard copyToClipboard = new copyToClipboard();
                copyToClipboard.copy(contacts.phoneNumber, context);
                return false;
            }
        });
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setFilter(List<contacts_lists> FilteredDataList) {
        data = (ArrayList<contacts_lists>) FilteredDataList;
        notifyDataSetChanged();
    }

    public class SingleViewHolder extends RecyclerView.ViewHolder {

        ContactListBinding binding;

        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ContactListBinding.bind(itemView);
        }
    }
}
