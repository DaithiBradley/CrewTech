package com.aap.medicore.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.Fragments.InboxMessageDetailsFragment;
import com.aap.medicore.Models.InboxMessage;
import com.aap.medicore.R;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.InboxMessageRepo;

import java.util.Collections;
import java.util.List;

public class InboxMessageRecyclerViewAdapter extends RecyclerView.Adapter<InboxMessageRecyclerViewAdapter.ViewHolder> {

    private List<InboxMessage> list;
    private FragmentManager fragmentManager;
    private CustomTextView emptyTV;
    private Context context;
    private DeleteMessageCallback deleteMessageCallback;
    private InboxMessageRepo inboxMessageRepo;

    public interface DeleteMessageCallback{
        void delete();
    }

    public InboxMessageRecyclerViewAdapter(Context context, FragmentManager fragmentManager, CustomTextView emptyTV,DeleteMessageCallback deleteMessageCallback) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.emptyTV = emptyTV;
        this.deleteMessageCallback=deleteMessageCallback;
        this.inboxMessageRepo=InboxMessageRepo.getInstance(context);
    }

    @NonNull
    @Override
    public InboxMessageRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inbox_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxMessageRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<InboxMessage> messageList) {
        list = messageList;
        emptyTV.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
        Collections.sort(list, (m1, m2) -> {
            if (m1 != null && m2 != null) {
                if (m1.equals(m2))
                    return 1;
                else if (m2.isOpened && m1.isOpened)
                    return 0;
                else if (m2.isOpened)
                    return -1;
                if (m1.isOpened)
                    return 1;
            }
            return 0;
        });
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView messageStatus;
        public ImageView removeMessage;
        public TextView title;
        public TextView content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.message_title);
            content = itemView.findViewById(R.id.message_content);
            messageStatus = itemView.findViewById(R.id.message_status_icon);
            removeMessage = itemView.findViewById(R.id.remove_message);
        }

        public void bind(InboxMessage value) {
            Drawable status = ContextCompat.getDrawable(context, value.isOpened ? R.drawable.ic_email_read : R.drawable.ic_email_unread);
            int color = ContextCompat.getColor(context, value.isOpened ? R.color.screenBackground : R.color.colorWhite);
            itemView.setBackgroundColor(color);
            itemView.setAlpha(value.isOpened ? 0.7f : 1f);
            messageStatus.setImageDrawable(status);
            title.setText(value.title);
            content.setText(Html.fromHtml(value.content).toString());

            removeMessage.setOnClickListener(view -> {
                InboxMessage tmp = list.get(getAdapterPosition());
                list.remove(tmp);
                inboxMessageRepo.removeInboxMessage(tmp);
                emptyTV.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
                if(deleteMessageCallback!=null)
                    deleteMessageCallback.delete();
                notifyDataSetChanged();
            });

            View.OnClickListener listener = view -> {
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.container, new InboxMessageDetailsFragment(list.get(getAdapterPosition())), "MESSAGE_DETAILS_FRAGMENT")
                        .addToBackStack(null)
                        .commit();
            };
            messageStatus.setOnClickListener(listener);
            title.setOnClickListener(listener);
            content.setOnClickListener(listener);
        }
    }
}
