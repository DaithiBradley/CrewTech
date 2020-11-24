package com.aap.medicore.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aap.medicore.Activities.Home;
import com.aap.medicore.Adapters.InboxMessageRecyclerViewAdapter;
import com.aap.medicore.BaseClasses.BaseFragment;
import com.aap.medicore.Models.InboxMessage;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.InboxMessageRepo;
import com.aap.medicore.Utils.TinyDB;

import java.util.List;
import java.util.Objects;


public class InboxListFragment extends BaseFragment implements InboxMessageRecyclerViewAdapter.DeleteMessageCallback {

    private InboxMessageRecyclerViewAdapter emailAdapter;
    private View viewLayout;
    private RecyclerView inboxRecycler;
    private CustomTextView emptyTV;
    private SwipeRefreshLayout pullToRefresh;
    private TinyDB tinyDB;

    public InboxListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tinyDB=new TinyDB(getContext().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewLayout = inflater.inflate(R.layout.inbox_list_fragment, container, false);
        initIds();
        return viewLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailAdapter = new InboxMessageRecyclerViewAdapter(getContext(), Objects.requireNonNull(getParentFragment()).getChildFragmentManager(), emptyTV, this::delete);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        inboxRecycler.addItemDecoration(divider);
        inboxRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        inboxRecycler.setAdapter(emailAdapter);
        emailAdapter.setData(getData());

        requireActivity().registerReceiver(receiver, new IntentFilter(Constants.INBOX_MESSAGE_EVENT));
        openMessageDetails();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireActivity().unregisterReceiver(receiver);
    }

    private void initIds() {
        inboxRecycler = viewLayout.findViewById(R.id.inbox_recycler);
        emptyTV = viewLayout.findViewById(R.id.tvNoMesssages);
        pullToRefresh = viewLayout.findViewById(R.id.pullToRefresh);

        pullToRefresh.setOnRefreshListener(onRefresh);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNewInbox();
        emailAdapter.setData(getData());
    }

    private List<InboxMessage> getData() {
        return InboxMessageRepo.getInstance(getContext()).getInboxMessages();
    }

    private SwipeRefreshLayout.OnRefreshListener onRefresh = () -> {
        checkNewInbox();
        emailAdapter.setData(getData());
        pullToRefresh.setRefreshing(false);
    };

    private void checkNewInbox() {
        ((Home) Objects.requireNonNull(Objects.requireNonNull(getParentFragment()).getActivity())).checkForNewMessages();
    }

    @Override
    public void delete() {
        checkNewInbox();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null && intent.getAction().equalsIgnoreCase(Constants.INBOX_MESSAGE_EVENT)) {
                checkNewInbox();
                emailAdapter.setData(getData());
            }
        }
    };

    private void openMessageDetails()
    {
        if(tinyDB.getBoolean(Constants.OPEN_INBOX_MESSAGE_ACTION))
        {
            String id=tinyDB.getString(Constants.INBOX_MESSAGE_ID);
            if(id!=null) {
                InboxMessage msg=InboxMessageRepo.getInstance(getContext()).getInboxMessage(id);
                getParentFragment().getChildFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.container, new InboxMessageDetailsFragment(msg), "MESSAGE_DETAILS_FRAGMENT")
                        .addToBackStack(null)
                        .commit();
                tinyDB.remove(Constants.INBOX_MESSAGE_ID);
            }
            tinyDB.remove(Constants.OPEN_INBOX_MESSAGE_ACTION);
        }


    }
}
