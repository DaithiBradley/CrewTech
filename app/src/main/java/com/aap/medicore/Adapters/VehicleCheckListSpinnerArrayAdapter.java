package com.aap.medicore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aap.medicore.Models.Option;
import com.aap.medicore.Models.VehicleChecklistOption;
import com.aap.medicore.R;
import com.aap.medicore.Utils.CustomTextView;

import java.util.List;

public class VehicleCheckListSpinnerArrayAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<VehicleChecklistOption> list;
    private final int mResource;

    public VehicleCheckListSpinnerArrayAdapter(@NonNull Context context, @LayoutRes int resource,
                                               @NonNull List objects) {
        super(context, resource, 0, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        list = objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = mInflater.inflate(mResource, parent, false);

        CustomTextView tvEventName = view.findViewById(R.id.tvEventName);
        tvEventName.setText(list.get(position).getLabel());

        return view;
    }
}