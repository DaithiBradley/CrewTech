package com.aap.medicore.Fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aap.medicore.Activities.Home;
import com.aap.medicore.BaseClasses.BaseFragment;
import com.aap.medicore.Models.InboxMessage;
import com.aap.medicore.R;
import com.aap.medicore.Utils.InboxMessageRepo;

import java.util.Objects;

public class InboxMessageDetailsFragment extends BaseFragment {
    private View viewLayout;
    private TextView title;
    private TextView content;
    private ImageView removeMessage;
    private ImageView backButton;
    private InboxMessage value;
    private InboxMessageRepo inboxMessageRepo;

    public InboxMessageDetailsFragment(InboxMessage value) {
        this.value = value;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewLayout = inflater.inflate(R.layout.inbox_message_detail_fragment, container, false);
        initIds();
        initListeners();

        setMessageOpen();
        return viewLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title.setText(value.title);
        content.setText(Html.fromHtml(value.content));
    }

    private void initIds() {
        title = viewLayout.findViewById(R.id.title);
        content = viewLayout.findViewById(R.id.content);
        removeMessage = viewLayout.findViewById(R.id.ivRemove);
        backButton = viewLayout.findViewById(R.id.ivBack);

        inboxMessageRepo=InboxMessageRepo.getInstance(getContext());
    }

    private void initListeners() {
        backButton.setOnClickListener(onBack);
        removeMessage.setOnClickListener(onRemove);
    }

    private View.OnClickListener onBack=view -> {
        navigateBack();
    };

    private View.OnClickListener onRemove=view -> {
        inboxMessageRepo.removeInboxMessage(value);
        navigateBack();
    };

    private void setMessageOpen() {
        if (!value.isOpened) {
            inboxMessageRepo.setOpened(value);
            checkNewInbox();
        }
    }

    private void checkNewInbox()
    {
        ((Home) Objects.requireNonNull(Objects.requireNonNull(getParentFragment()).getActivity())).checkForNewMessages();
    }

    private void navigateBack()
    {
        Objects.requireNonNull(Objects.requireNonNull(getParentFragment()).getChildFragmentManager()).popBackStack();
    }
}
