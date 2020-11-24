package com.aap.medicore.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.Activities.EquipmentCheckList;
import com.aap.medicore.Models.AdminFormModel;
import com.aap.medicore.Models.AdminForms;
import com.aap.medicore.R;

import java.util.List;

import retrofit2.Response;

public class NewAdapterAdmin extends RecyclerView.Adapter<NewAdapterAdmin.ViewHolder> {

    private List<AdminFormModel> mData;
    private LayoutInflater mInflater;
    Context context;

    // data is passed into the constructor
    public NewAdapterAdmin(Context context, List<AdminFormModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;

    }

    // inflates the row layout from xml when needed
    @Override
    public NewAdapterAdmin.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adminform_row, parent, false);
        return new NewAdapterAdmin.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(NewAdapterAdmin.ViewHolder holder, int position) {
        AdminFormModel formModel = mData.get(position);
        holder.myTextView.setText(formModel.getTitle());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, EquipmentCheckList.class);
                i.putExtra("formId", formModel.getId());
                context.startActivity(i);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }



    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        RelativeLayout layout;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
            layout = itemView.findViewById(R.id.layout);

        }

    }

}
