package com.aap.medicore.Adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.Models.ChecklistFormOption;
import com.aap.medicore.Models.Option;
import com.aap.medicore.Models.Option2;
import com.aap.medicore.R;

import java.util.List;

public class EquipmentAccessoriesAdapter extends RecyclerView.Adapter<EquipmentAccessoriesAdapter.MyViewHolder> {

    private List<Option2> accessoriesModelList;
    private List<Drawable> images;
    private RecyclerView recyclerView;
    private OnAdminItemClickedListener listener;
    private OnClinicalItemClickedListener listener1;
    private List<Option> clinicalModelList;
    boolean flag;

    public EquipmentAccessoriesAdapter(List<Option2> accessoriesModelList, List<Drawable> images, OnAdminItemClickedListener listener) {
        this.accessoriesModelList = accessoriesModelList;
        this.listener = listener;
        this.images = images;
        flag = true;
//        for (int i=4; i< accessoriesModelList.size()+4;i++){
//            switch (i%4){
//                case 0:
//                    accessoriesModelList.get(i-4).setImageResource(images.get(0));
//                    break;
//                case 1:
//                    accessoriesModelList.get(i-4).setImageResource(images.get(1));
//                    break;
//                case 2:
//                    accessoriesModelList.get(i-4).setImageResource(images.get(2));
//                    break;
//                case 3:
//                    accessoriesModelList.get(i-4).setImageResource(images.get(3));
//                    break;
//            }
//        }
    }

    public EquipmentAccessoriesAdapter(List<Option> clinicalModelList, OnClinicalItemClickedListener listener) {
        this.clinicalModelList = clinicalModelList;
        this.listener1 = listener;
        flag = false;

//        for (int i=4; i< accessoriesModelList.size()+4;i++){
//            switch (i%4){
//                case 0:
//                    accessoriesModelList.get(i-4).setImageResource(images.get(0));
//                    break;
//                case 1:
//                    accessoriesModelList.get(i-4).setImageResource(images.get(1));
//                    break;
//                case 2:
//                    accessoriesModelList.get(i-4).setImageResource(images.get(2));
//                    break;
//                case 3:
//                    accessoriesModelList.get(i-4).setImageResource(images.get(3));
//                    break;
//            }
//        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.equipment_accessory_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        holder.ivAccessory.setImageDrawable(accessoriesModelList.get(position).getImageResource());
        if (flag) {
            holder.tvAccessoryName.setText(accessoriesModelList.get(position).getOption());
            if (accessoriesModelList.get(position).isSelected()) {
                holder.ivCheck.setVisibility(View.VISIBLE);
            } else {
                holder.ivCheck.setVisibility(View.GONE);
            }
            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!accessoriesModelList.get(position).isSelected()) {
//                    holder.ivCheck.setVisibility(View.VISIBLE);
                        accessoriesModelList.get(position).setSelected(true);

                    } else {
//                    holder.ivCheck.setVisibility(View.GONE);
                        accessoriesModelList.get(position).setSelected(false);
                    }

                    listener.onAdminItemClicked(recyclerView.getId(), accessoriesModelList);

                    notifyDataSetChanged();
                }
            });
        } else {
            holder.tvAccessoryName.setText(clinicalModelList.get(position).getLabel());
            if (clinicalModelList.get(position).isSelected()) {
                holder.ivCheck.setVisibility(View.VISIBLE);
            } else {
                holder.ivCheck.setVisibility(View.GONE);
            }
            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!clinicalModelList.get(position).isSelected()) {
//                    holder.ivCheck.setVisibility(View.VISIBLE);
                        clinicalModelList.get(position).setSelected(true);

                    } else {
//                    holder.ivCheck.setVisibility(View.GONE);
                        clinicalModelList.get(position).setSelected(false);
                    }

                    listener1.onClinicalItemClicked(recyclerView.getId(), clinicalModelList);

                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (flag)
            return accessoriesModelList.size();

        return clinicalModelList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        //        ImageView ivAccessory;
        TextView tvAccessoryName;
        ImageView ivCheck;
        ConstraintLayout rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
//            this.ivAccessory = itemView.findViewById(R.id.ivAccessory);
            ivCheck = itemView.findViewById(R.id.ivCheck);
            tvAccessoryName = itemView.findViewById(R.id.tvAccessoryName);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }

    public interface OnAdminItemClickedListener {
        void onAdminItemClicked(int recyclerViewId, List<Option2> list);
    }

    public interface OnClinicalItemClickedListener {
        void onClinicalItemClicked(int recyclerViewId, List<Option> list);
    }
}
