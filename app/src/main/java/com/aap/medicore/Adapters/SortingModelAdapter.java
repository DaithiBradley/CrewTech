package com.aap.medicore.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.Models.SortingModel;
import com.aap.medicore.R;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class SortingModelAdapter extends RecyclerView.Adapter<SortingModelAdapter.MyViewHolder> {

    private List<SortingModel> sortingModelList;
    private Context context;
    private List<Bitmap> bitmapList;

    public SortingModelAdapter(List<SortingModel> sortingModelList, Context context, List<Bitmap> bitmapList) {
        this.sortingModelList = sortingModelList;
        this.context = context;
        this.bitmapList = bitmapList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_sorting_model_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SortingModel model = sortingModelList.get(position);
       // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        holder.tvDate.setText(simpleDateFormat.format(model.getDate()));
        AdapterTasksList adapterTasksList = new AdapterTasksList(model.getAssignedIncidencesModels(),context,bitmapList);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        holder.rvTasks.setLayoutManager(manager);
        holder.rvTasks.setAdapter(adapterTasksList);

    }

    @Override
    public int getItemCount() {
        return sortingModelList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tvDate;
        private RecyclerView rvTasks;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            rvTasks = itemView.findViewById(R.id.rvTasks);

        }
    }
}
