package com.aap.medicore.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.aap.medicore.Activities.TaskDetails;
import com.aap.medicore.Models.AssignedIncidencesModel;
import com.aap.medicore.Models.TaskList;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomTextView;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class SectionTasksAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Context context;
    private List<AssignedIncidencesModel> assignedIncidencesModels;
    private List<Bitmap> bitmapList;
    private LayoutInflater layoutInflater;
    private String name;
    private String taskLocation;
    private String time;
    private String date;
    public static Bitmap img_bm;
    private SimpleDateFormat simpleDateFormat;


    public SectionTasksAdapter(Context context, List<AssignedIncidencesModel> assignedIncidencesModels, List<Bitmap> bitmapList) {
        this.context = context;
        this.assignedIncidencesModels = assignedIncidencesModels;
        this.bitmapList = bitmapList;
        if (context != null)
            layoutInflater = LayoutInflater.from(context);
        //simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = layoutInflater.inflate(R.layout.layout_date_header, parent, false);
            holder.text = convertView.findViewById(R.id.textView11);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as date
        String headerText = "";
        Date currentDate = new Date();
        String s = simpleDateFormat.format(currentDate);
        try {
            currentDate = simpleDateFormat.parse(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Date jobDate = assignedIncidencesModels.get(position).getObjDate();
        if (jobDate != null) {
            switch (currentDate.compareTo(jobDate)) {
                case 0:
                    headerText = "Today's Calls";
                    break;
                case 1:
                    if (getDifferenceInDates(currentDate, jobDate) == 1)
                        headerText = "Yesterday's Calls";
                    else
                        headerText = "Past Calls";
                    break;
                case -1:
                    if (getDifferenceInDates(jobDate, currentDate) == 1)
                        headerText = "Tomorrow's Calls";
                    else
                        headerText = "Future Calls";
                    break;
            }
            holder.text.setText(headerText);
        }
        else
        {
            Log.d("SectionTasksAdapter","Job date wrong format");
        }
        return convertView;
    }

    private int getDifferenceInDates(Date date1, Date date2) {

        String s1 = simpleDateFormat.format(date1);
        s1 = s1.substring(0, 2);
        int i1 = Integer.parseInt(s1);

        String s2 = simpleDateFormat.format(date2);
        s2 = s2.substring(0, 2);
        int i2 = Integer.parseInt(s2);

        return i1 - i2;
    }

    @Override
    public long getHeaderId(int position) {
        String date = assignedIncidencesModels.get(position).getDate();
        date = date.replace("-", "");
        int i = Integer.parseInt(date);
        return i;
    }

    @Override
    public int getCount() {
        return assignedIncidencesModels.size();
    }

    @Override
    public Object getItem(int position) {
        return assignedIncidencesModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder;

        if (convertView == null) {
            holder = new ItemViewHolder();
            convertView = layoutInflater.inflate(R.layout.layout_row_tasks, parent, false);
            holder.tvHeading = convertView.findViewById(R.id.tvHeading);
            holder.rootLayout = convertView.findViewById(R.id.rootLayout);
            holder.tvFromFacility = convertView.findViewById(R.id.tvFromFacility);
            holder.tvToFacilty = convertView.findViewById(R.id.tvToFacility);
            holder.tvTime = convertView.findViewById(R.id.tvTime);

            convertView.setTag(holder);
        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }

        Gson gson = new Gson();
        String json = assignedIncidencesModels.get(position).getJson();
        final TaskList obj = gson.fromJson(json, TaskList.class);
        name = obj.getName();
        holder.tvHeading.setText(name);
//        holder.tvHeading.setText(response.body().getTaskList().get(position).getName());
        taskLocation = obj.getFromFacitity();
        holder.tvFromFacility.setText(taskLocation);
        if (obj.getToFacility() != null && !obj.getToFacility().isEmpty())
            holder.tvToFacilty.setText(obj.getToFacility());
//        holder.tvDetails.setText(response.body().getTaskList().get(position).getFromFacitity() + " : " + response.body().getTaskList().get(position).getToFacility());
        time = obj.getJobDateTime();
        holder.tvTime.setText(time);
        if (obj.getJobDate() != null) {
            date = (String) obj.getJobDate();
//            date = date.substring(0, 10);
//            holder.tvDate.setText(date);
        }
//        holder.tvTime.setText(response.body().getTaskList().get(position).getJobDateTime());
//        notifyDataSetChanged();
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmapList.size() > 0) {

                    img_bm = bitmapList.get(position);

                } else {
                }


                Intent i = new Intent(context, TaskDetails.class);
                i.putExtra(Constants.task_id, obj.getIncidencId() + "");
                i.putExtra(Constants.task_location, taskLocation);
                i.putExtra(Constants.task_witness, obj.getOrderNo() + "");
                context.startActivity(i);
            }
        });

        return convertView;
    }

    class HeaderViewHolder {
        TextView text;
    }

    class ItemViewHolder {
        TextView tvHeading, tvFromFacility, tvToFacilty, tvTime/*, tvDate*/;
        ConstraintLayout rootLayout;
    }
}
