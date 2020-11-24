package com.aap.medicore.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aap.medicore.BaseClasses.BaseFragment;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;

import java.util.Objects;


public class InboxFragment extends BaseFragment {
    private View viewLayout;
    private InboxListFragment inboxListFragment;

    public InboxFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewLayout = inflater.inflate(R.layout.inbox_fragment, container, false);
        return viewLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            inboxListFragment=new InboxListFragment();
            getChildFragmentManager().beginTransaction().add(R.id.container, inboxListFragment).addToBackStack("INBOX_LIST").commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getChildFragmentManager() != null &&
                getChildFragmentManager().findFragmentByTag("MESSAGE_DETAILS_FRAGMENT") != null &&
                Objects.requireNonNull(getChildFragmentManager().findFragmentByTag("MESSAGE_DETAILS_FRAGMENT")).isVisible())
            if (getChildFragmentManager().getBackStackEntryCount() >= 1) {
                getChildFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
    }

/*    @Override
    public void onDestroyView() {
        while (getChildFragmentManager().getBackStackEntryCount() != 0) {
            getChildFragmentManager().popBackStack();
        }
        super.onDestroyView();
    }*/
}