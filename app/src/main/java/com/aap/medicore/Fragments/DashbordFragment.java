package com.aap.medicore.Fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aap.medicore.Activities.Login;
import com.aap.medicore.Activities.StateActivity;
import com.aap.medicore.Adapters.SectionTasksAdapter;
import com.aap.medicore.BaseClasses.BaseActivity;
import com.aap.medicore.BaseClasses.BaseFragment;
import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.AssignedIncidencesModel;
import com.aap.medicore.Models.QueueModel;
import com.aap.medicore.Models.SortingModel;
import com.aap.medicore.Models.TasksListResponse;
import com.aap.medicore.Models.VehicleResponse;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.CircularTextView;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.DateComparator;
import com.aap.medicore.Utils.SessionTimeoutDialog;
import com.aap.medicore.Utils.TinyDB;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class DashbordFragment extends BaseFragment {

    private OnFragmentInteractionListener mListener;
    private StickyListHeadersListView rvVisits;
    private View view;
    private LinearLayoutManager manager;
    //    private AdapterTasksList adapter;
//    private SortingModelAdapter adapter;
    private SectionTasksAdapter adapter;
    private TextView tvBedsCount, tvEngineHorsepower, tvColor, tvRegistrationNo, tvStatus, tvState, tvVehicleErrorMessage, tvNoCalls;
    //    private LinearLayout llVRegistration, llVName;
    private TinyDB tinyDB;
    private LinearLayout /*llLogout,*/ llState;
    private ConstraintLayout llNoVehicleData, llVehicleDetails;
    private SwipeRefreshLayout pullToRefresh;
    private ImageView ivStatus, ivLogout/*, ivState*/;
    //    private ImageView profileImage;
    private DatabaseHandler databaseHandler;
    private Response<TasksListResponse> responselist;
    public static List<Bitmap> bitmapArray = new ArrayList<Bitmap>();
    private CircularTextView tvCallsCount;
    private int numDates = 1;

    Handler h = new Handler();
    int delay = 5 * 1000; //1 second=1000 milisecond, 15*1000=15seconds
    Runnable runnable;
    private ArrayList<AssignedIncidencesModel> assignedIncidencesModels, assignedIncidencesModels2;
    private List<SortingModel> sortingModelList;
    private Set<Date> dateList;


    public DashbordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_frag_dashboard, container, false);
        init(view);
        clickListeners();

        tvCallsCount.setStrokeWidth(1);
        tvCallsCount.setStrokeColor("#79C730");
        tvCallsCount.setSolidColor("#FFFFFF");
        sortingModelList = new ArrayList<>();
        assignedIncidencesModels2 = new ArrayList<>();
        dateList = new HashSet<>();
        if (isConnected(getActivity())) {
            fetchVehicleData();
            hitTasksList();
        } else {
//            adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(), bitmapArray/*, responselist*/);
//            tvCallsCount.setText(databaseHandler.getAllIncidences().size() + "");
//            rvVisits.setLayoutManager(manager);
//            rvVisits.setAdapter(adapter);
//            if (!(tinyDB.getString(Constants.vehicle_details).isEmpty())) {
//                llNoVehicleData.setVisibility(View.GONE);
//                llVehicleDetails.setVisibility(View.VISIBLE);
//
//                Gson gson = new Gson();
//                String json = tinyDB.getString(Constants.vehicle_details);
//
//                final VehicleDetail obj = gson.fromJson(json, VehicleDetail.class);
//
//                tvBedsCount.setText(obj.getBedSpace() + "");
//                tvColor.setText(obj.getVehicleName());
//                tvEngineHorsepower.setText(obj.getEnginHouspower() + "");
//                tvRegistrationNo.setText(obj.getRegistrationno());
//
//                llVehicleDetails.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent i = new Intent(getActivity(), VehicleDetails.class);
//                        i.putExtra(Constants.user_id, obj.getUserId());
//                        startActivity(i);
//                    }
//                });
//
//
//            } else {
//
//                llNoVehicleData.setVisibility(View.VISIBLE);
//                llVehicleDetails.setVisibility(View.GONE);
//
//            }
        }

        return view;
    }

    private void init(View v) {
        tinyDB = new TinyDB(getActivity());
        databaseHandler = new DatabaseHandler(getActivity());

//        profileImage = v.findViewById(R.id.profileImage);

        if (isConnected(getActivity()))
            if (!(tinyDB.getString(Constants.profile_image).isEmpty()))
//                Glide.with(getActivity()).load(tinyDB.getString(Constants.profile_image)).into(profileImage);

                rvVisits = v.findViewById(R.id.rvVisits);

        manager = new LinearLayoutManager(getActivity());
        tvCallsCount = v.findViewById(R.id.tvCallsCount);
        ivLogout = v.findViewById(R.id.ivLogout);

//        tvBedsCount = v.findViewById(R.id.tvBedsCount);
//        tvEngineHorsepower = v.findViewById(R.id.tvEngineHorsepower);
        tvColor = v.findViewById(R.id.tvColor);
        tvRegistrationNo = v.findViewById(R.id.tvRegistrationNo);

        llVehicleDetails = v.findViewById(R.id.llVehicleDetails);
        pullToRefresh = v.findViewById(R.id.pullToRefresh);

        ivStatus = v.findViewById(R.id.ivStatus);
        tvStatus = v.findViewById(R.id.tvStatus);

//        ivState = v.findViewById(R.id.ivState);
        tvState = v.findViewById(R.id.tvState);

        if (!(tinyDB.getString(Constants.StateTitle).isEmpty())) {
            tvState.setText(tinyDB.getString(Constants.StateTitle));
        } else {
            tinyDB.putString(Constants.StateTitle, "Available");
            tinyDB.putString(Constants.StateDescription, "Currently we are available and done with assigned incidents.");
            tinyDB.putInt(Constants.StatePosition, 4);
            tvState.setText(tinyDB.getString(Constants.StateTitle));
        }
        llState = v.findViewById(R.id.llState);

        llNoVehicleData = v.findViewById(R.id.llNoVehicleData);
//        llVRegistration = v.findViewById(R.id.llVRegistration);
//        llVName = v.findViewById(R.id.llVName);
        tvVehicleErrorMessage = v.findViewById(R.id.tvVehicleErrorMessage);
        tvNoCalls = v.findViewById(R.id.tvNoCalls);

    }

    private void clickListeners() {

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutConfirmDialogBox();
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

//                hitPullToRefreshTasksList(pullToRefresh);
//                pullToRefresh.setRefreshing(true);
                hitTasksList();
                fetchVehicleData();
            }
        });

//        ivState.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(), StateActivity.class);
//                startActivity(i);
//            }
//        });

        llState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), StateActivity.class);
                startActivity(i);
            }
        });


    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
//        h.removeCallbacks(runnable); //stop handler when activity not visible
        mListener = null;
        super.onDetach();

    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }


    public void hitTasksList() {
//        adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(), bitmapArray/*, response*/);
//        tvCallsCount.setText(databaseHandler.getAllIncidences().size() + "");
//        rvVisits.setLayoutManager(manager);
//        rvVisits.setAdapter(adapter);
//        if (databaseHandler.getAllIncidences().size() > 0) {
//            rvVisits.setVisibility(View.VISIBLE);
//            tvNoCalls.setVisibility(View.GONE);
//        } else {
//            rvVisits.setVisibility(View.GONE);
//            tvNoCalls.setVisibility(View.VISIBLE);
//        }
//        Snackbar.make(requireActivity().findViewById(android.R.id.content),
//                "Hit Task perform", Snackbar.LENGTH_LONG).show();
        retrofit2.Call<TasksListResponse> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().hitTasksList(tinyDB.getString(Constants.token), tinyDB.getString(Constants.user_id));
//        call = RetrofitClass.getInstance().getWebRequestsInstance().hitTasksList(tinyDB.getString(Constants.user_id));

        call.enqueue(new Callback<TasksListResponse>() {
            @Override
            public void onResponse(retrofit2.Call<TasksListResponse> call, final Response<TasksListResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {

                        databaseHandler.deleteAssignedIncidencesTable();
//                        databaseHandler.getAllIncidences().clear();
                        responselist = response;
//                        bitmapArray.clear();

                        for (int i = 0; i < response.body().getTaskList().size(); i++) {

                            AssignedIncidencesModel model = new AssignedIncidencesModel();
                            model.setId(response.body().getTaskList().get(i).getIncidencId());
                            Gson gson = new Gson();
                            String json = gson.toJson(response.body().getTaskList().get(i));
                            model.setJson(json);
                            model.setDate(response.body().getTaskList().get(i).getJobDate());
                            model.setJobDateTime(response.body().getTaskList().get(i).getJobDateTime());
                            QueueModel queueIncidenceStateOnIncidenceID = databaseHandler.getQueueIncidenceStateOnIncidenceID(model.getId() + "");
                            if (queueIncidenceStateOnIncidenceID.getId().isEmpty())
                                databaseHandler.addIncidences(model);
                            Log.d("SIZE ARRAY", "Array size is: " + databaseHandler.getAllIncidences());
                            Log.d("SIZE ARRAY", "server Array size is: " + response.body().getTaskList().size());
                        }

                        assignedIncidencesModels = databaseHandler.getAllIncidences();

                        if (assignedIncidencesModels.size() > 0) {
                            rvVisits.setVisibility(View.VISIBLE);
                            tvNoCalls.setVisibility(View.GONE);
                            assignedIncidencesModels2.clear();
                            sortingModelList.clear();
                            dateList.clear();
//                            sortList();
//                            adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(), bitmapArray/*, response*/);
                            Collections.sort(assignedIncidencesModels, new DateComparator());
//                            adapter = new SortingModelAdapter(sortingModelList, getContext(),bitmapArray);
                            tvCallsCount.setText(Integer.toString(assignedIncidencesModels.size()));
//                            rvVisits.setLayoutManager(manager);
                            adapter = new SectionTasksAdapter(getContext(), assignedIncidencesModels, bitmapArray);
                            rvVisits.setAdapter(adapter);
                            rvVisits.getViewTreeObserver()
                                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {
                                            //At this point the layout is complete and the
                                            //dimensions of recyclerView and any child views are known.
                                            //Remove listener after changed RecyclerView's height to prevent infinite
                                            pullToRefresh.setRefreshing(false);
                                            rvVisits.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                        }
                                    });

                        } else {
                            rvVisits.setVisibility(View.GONE);
                            tvNoCalls.setVisibility(View.VISIBLE);
                            tvCallsCount.setText("0");
                            pullToRefresh.setRefreshing(false);

                        }


//                        if (adapter != null) {
//                            adapter.setItems(response.body().getTaskList());
//                            adapter.notifyDataSetChanged();
//                        } else {
//                            adapter = new AdapterTasksList(response.body().getTaskList(), getActivity());
//                            rvVisits.setLayoutManager(manager);
//                            rvVisits.setAdapter(adapter);
////                        tvCallsCount.setVisibility(View.VISIBLE);
//                            tvCallsCount.setText(response.body().getTaskList().size() + "");
//
//                        }
                    } else if (response.body().getStatus() == 404) {

//                        databaseHandler.deleteAssignedIncidencesTable();
//                        adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(), bitmapArray/*, response*/);
//                        tvCallsCount.setText(databaseHandler.getAllIncidences().size() + "");
//                        rvVisits.setLayoutManager(manager);
//                        rvVisits.setAdapter(adapter);
//                        if (databaseHandler.getAllIncidences().size() > 0) {
//                            rvVisits.setVisibility(View.VISIBLE);
//                            tvNoCalls.setVisibility(View.GONE);
//                        } else {
                        rvVisits.setVisibility(View.GONE);
                        tvNoCalls.setVisibility(View.VISIBLE);
                        tvCallsCount.setText("0");
                        pullToRefresh.setRefreshing(false);

//                        }

                    } else if (response.code() == 401) {
                        if (getActivity() != null)
                            new SessionTimeoutDialog((BaseActivity) getActivity()).getDialog().show();
                        pullToRefresh.setRefreshing(false);

                    }
                }
//                pullToRefresh.setRefreshing(false);

            }

            @Override
            public void onFailure(retrofit2.Call<TasksListResponse> call, Throwable t) {
                t.printStackTrace();
                assignedIncidencesModels = databaseHandler.getAllIncidences();

                if (assignedIncidencesModels.size() > 0) {
                    rvVisits.setVisibility(View.VISIBLE);
                    tvNoCalls.setVisibility(View.GONE);
                    assignedIncidencesModels2.clear();
                    sortingModelList.clear();
                    dateList.clear();
//                            sortList();
//                            adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(), bitmapArray/*, response*/);
                    Collections.sort(assignedIncidencesModels, new DateComparator());
//                            adapter = new SortingModelAdapter(sortingModelList, getContext(),bitmapArray);
                    tvCallsCount.setText(Integer.toString(assignedIncidencesModels.size()));
//                            rvVisits.setLayoutManager(manager);
                    adapter = new SectionTasksAdapter(getContext(), assignedIncidencesModels, bitmapArray);
                    rvVisits.setAdapter(adapter);
                    rvVisits.getViewTreeObserver()
                            .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    //At this point the layout is complete and the
                                    //dimensions of recyclerView and any child views are known.
                                    //Remove listener after changed RecyclerView's height to prevent infinite
                                    pullToRefresh.setRefreshing(false);
                                    rvVisits.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                            });

                } else {
                    rvVisits.setVisibility(View.GONE);
                    tvNoCalls.setVisibility(View.VISIBLE);
                    tvCallsCount.setText("0");
                    pullToRefresh.setRefreshing(false);
                }
//                pullToRefresh.setRefreshing(false);

            }
        });
    }

    private void sortList() {

        AssignedIncidencesModel model = assignedIncidencesModels.get(0);
        assignedIncidencesModels2.add(model);

        for (int i = 1; i < assignedIncidencesModels.size() - 1; i++) {
            if (assignedIncidencesModels.get(i).getObjDate().compareTo(model.getObjDate()) == 0)
                assignedIncidencesModels2.add(assignedIncidencesModels.get(i));
            else
                dateList.add(assignedIncidencesModels.get(i).getObjDate());
        }

        SortingModel sortingModel = new SortingModel();
        sortingModel.setDate(model.getObjDate());
        sortingModel.setAssignedIncidencesModels(assignedIncidencesModels2);
        sortingModelList.add(sortingModel);

        for (Date date : dateList) {

            SortingModel sortingModel1 = new SortingModel();
            List<AssignedIncidencesModel> assignedIncidencesModels3 = new ArrayList<>();

            for (AssignedIncidencesModel assignedIncidencesModel : assignedIncidencesModels) {
                if (date.compareTo(assignedIncidencesModel.getObjDate()) == 0)
                    assignedIncidencesModels3.add(assignedIncidencesModel);
            }
            sortingModel1.setDate(date);
            sortingModel1.setAssignedIncidencesModels(assignedIncidencesModels3);
            sortingModelList.add(sortingModel1);
        }

    }
    //    private class ConvertUrlToBitmap extends AsyncTask<String, Long, Bitmap> {
//        @Override
//        protected Bitmap doInBackground(String... params) {
//
//            try {
//                URL url = new URL(params[0]);
//                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                bitmapArray.add(bitmap);
//                Log.e("biTMAPARRAY","BITMAP ARRAY SIZE "+bitmapArray.size());
//                return bitmap;
//            } catch (Exception e) {
//                Log.e("", e.toString());
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(final Bitmap image) {
//            super.onPostExecute(image);
//
//
//
//
//
//        }
//    }

    public void hitPullToRefreshTasksList(final SwipeRefreshLayout pullToRefresh) {
//        adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(), bitmapArray/*, response*/);
        tvCallsCount.setText(databaseHandler.getAllIncidences().size() + "");
//        rvVisits.setLayoutManager(manager);
        rvVisits.setAdapter(adapter);
        if (databaseHandler.getAllIncidences().size() > 0) {
            rvVisits.setVisibility(View.VISIBLE);
            tvNoCalls.setVisibility(View.GONE);
        } else {
            rvVisits.setVisibility(View.GONE);
            tvNoCalls.setVisibility(View.VISIBLE);
        }

        retrofit2.Call<TasksListResponse> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().hitTasksList(tinyDB.getString(Constants.token), tinyDB.getString(Constants.user_id));
//        call = RetrofitClass.getInstance().getWebRequestsInstance().hitTasksList(tinyDB.getString(Constants.user_id));
        call.enqueue(new Callback<TasksListResponse>() {
            @Override
            public void onResponse(retrofit2.Call<TasksListResponse> call, final Response<TasksListResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        databaseHandler.deleteAssignedIncidencesTable();

                        for (int i = 0; i < response.body().getTaskList().size(); i++) {

                            AssignedIncidencesModel model = new AssignedIncidencesModel();
                            model.setId(response.body().getTaskList().get(i).getIncidencId());
                            Gson gson = new Gson();
                            String json = gson.toJson(response.body().getTaskList().get(i));
                            model.setJson(json);
                            QueueModel queueIncidenceStateOnIncidenceID = databaseHandler.getQueueIncidenceStateOnIncidenceID(model.getId() + "");
                            if (queueIncidenceStateOnIncidenceID.getId().isEmpty())
                                databaseHandler.addIncidences(model);
                        }

//                        adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(), bitmapArray/*, response*/);
                        tvCallsCount.setText(databaseHandler.getAllIncidences().size() + "");
//                        rvVisits.setLayoutManager(manager);
                        rvVisits.setAdapter(adapter);
                        if (databaseHandler.getAllIncidences().size() > 0) {
                            rvVisits.setVisibility(View.VISIBLE);
                            tvNoCalls.setVisibility(View.GONE);
                        } else {
                            rvVisits.setVisibility(View.GONE);
                            tvNoCalls.setVisibility(View.VISIBLE);
                        }
                        pullToRefresh.setRefreshing(false);

                    } else if (response.body().getStatus() == 404) {
                        databaseHandler.deleteAssignedIncidencesTable();
//                        adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(), bitmapArray/*, response*/);
//                        tvCallsCount.setText(databaseHandler.getAllIncidences().size() + "");
//                        rvVisits.setLayoutManager(manager);
                        rvVisits.setAdapter(adapter);
                        if (databaseHandler.getAllIncidences().size() > 0) {
                            rvVisits.setVisibility(View.VISIBLE);
                            tvNoCalls.setVisibility(View.GONE);
                        } else {
                            rvVisits.setVisibility(View.GONE);
                            tvNoCalls.setVisibility(View.VISIBLE);
                        }
                        pullToRefresh.setRefreshing(false);
                    }
                } else if (response.code() == 401) {
                    if (getActivity() != null)
                        new SessionTimeoutDialog((BaseActivity) getActivity()).getDialog().show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<TasksListResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void hitRefreshTasksList() {
        retrofit2.Call<TasksListResponse> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().hitTasksList(tinyDB.getString(Constants.token), tinyDB.getString(Constants.user_id));

//        call = RetrofitClass.getInstance().getWebRequestsInstance().hitTasksList(tinyDB.getString(Constants.user_id));
        call.enqueue(new Callback<TasksListResponse>() {
            @Override
            public void onResponse(retrofit2.Call<TasksListResponse> call, final Response<TasksListResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {

                        databaseHandler.deleteAssignedIncidencesTable();

                        for (int i = 0; i < response.body().getTaskList().size(); i++) {

                            AssignedIncidencesModel model = new AssignedIncidencesModel();
                            model.setId(response.body().getTaskList().get(i).getIncidencId());
                            Gson gson = new Gson();
                            String json = gson.toJson(response.body().getTaskList().get(i));
                            model.setJson(json);

                            databaseHandler.addIncidences(model);

                        }

//                        adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(), bitmapArray/*, response*/);
//                        rvVisits.setLayoutManager(manager);
                        rvVisits.setAdapter(adapter);
                    } else if (response.code() == 401) {
                        if (getActivity() != null)
                            new SessionTimeoutDialog((BaseActivity) getActivity()).getDialog().show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<TasksListResponse> call, Throwable t) {
                t.printStackTrace();
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void logoutConfirmDialogBox() {
        final Dialog dialog = new Dialog(getActivity());
        final AppCompatButton btnNo, btnYes;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);
        btnYes =  dialog.findViewById(R.id.btnYes);
        btnNo =  dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                logoutCrew(tinyDB.getString(Constants.user_id),dialog);
            }
        });
        dialog.show();
    }

    private void cancelDialog(Dialog dialog) {

        dialog.cancel();
        Toast.makeText(DashbordFragment.this.getActivity(), "Please try again after some time", Toast.LENGTH_SHORT).show();
    }

    private void deleteFCM_Token(Dialog dialog) {
        Call<ResponseBody> call = RetrofitClass.getInstance().getWebRequestsInstance().sendFCMTokenToServer(tinyDB.getString(Constants.token), "\"\"", "ANDROID");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful())
                    removeLocalUserData(dialog);
                else
                    cancelDialog(dialog);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                cancelDialog(dialog);
            }
        });
    }

    private void logoutCrew(String userId, Dialog dialog){
        Call<ResponseBody> call = RetrofitClass.getInstance().getWebRequestsInstance().crewLogout(tinyDB.getString(Constants.token),userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful())
                    deleteFCM_Token(dialog);
                else
                    cancelDialog(dialog);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                cancelDialog(dialog);
            }
        });
    }

    private void removeLocalUserData(Dialog dialog) {

        mLastClickTime = SystemClock.elapsedRealtime();

        tinyDB.putBoolean(Constants.LoggedIn, false);
        tinyDB.remove(Constants.user_id);
        tinyDB.remove(Constants.email);
        tinyDB.remove(Constants.first_name);
        tinyDB.remove(Constants.last_name);
        tinyDB.remove(Constants.username);
        tinyDB.remove(Constants.StateTitle);
//        tinyDB.remove(Constants.tokenFCM);

        Intent i = new Intent(getActivity(), Login.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        dialog.hide();
        getActivity().finishAffinity();
    }

    public void fetchVehicleData() {

        retrofit2.Call<VehicleResponse> call;
        call = RetrofitClass.getInstance().getWebRequestsInstance().getVehicleDetail(tinyDB.getString(Constants.token), tinyDB.getString(Constants.user_id));
        call.enqueue(new Callback<VehicleResponse>() {
            @Override
            public void onResponse(Call<VehicleResponse> call, final Response<VehicleResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {


                        Gson gson = new Gson();
                        String json = gson.toJson(response.body().getVehicleDetail());
                        tinyDB.putString(Constants.vehicle_details, json);

                        llNoVehicleData.setVisibility(View.GONE);
                        llVehicleDetails.setVisibility(View.VISIBLE);

//                        tvBedsCount.setText(response.body().getVehicleDetail().getBedSpace() + "");
                        tvColor.setText(response.body().getVehicleDetail().getVehicleName());
//                        tvEngineHorsepower.setText(response.body().getVehicleDetail().getEnginHouspower() + "");
                        tvRegistrationNo.setText(response.body().getVehicleDetail().getRegistrationno());

                        tinyDB.putInt("vehicle_id", response.body().getVehicleDetail().getVehicleId());

//                        llVehicleDetails.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent i = new Intent(getActivity(), VehicleDetails.class);
//                                i.putExtra(Constants.user_id, response.body().getVehicleDetail().getUserId());
//                                startActivity(i);
//                            }
//                        });

                    } else if (response.code() == 401) {
                        if (getActivity() != null) {
                            new SessionTimeoutDialog((BaseActivity) getActivity()).getDialog().show();
                            getActivity().finish();
                        }
                    } else {
//                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).getDialog();
                        llNoVehicleData.setVisibility(View.VISIBLE);
                        llVehicleDetails.setVisibility(View.GONE);
                        tvVehicleErrorMessage.setText("Vehicle is not assigned, Please contact Admin to assign Vehicle!");
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<VehicleResponse> call, Throwable t) {
//                Toast.makeText(getActivity(), "Can't connect to Server.", Toast.LENGTH_SHORT).getDialog();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onResume() {
        hitTasksList();
//        h.postDelayed(runnable = new Runnable() {
//            public void run() {
//                if (getActivity() != null) {
//                    if (isConnected(getActivity())) {
//                        fetchVehicleData();
//                        hitTasksList();
//                    } else {
//                        adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(), bitmapArray, responselist);
//                        tvCallsCount.setText(databaseHandler.getAllIncidences().size() + "");
//                        rvVisits.setLayoutManager(manager);
//                        rvVisits.setAdapter(adapter);
//                        if (databaseHandler.getAllIncidences().size() > 0) {
//                            rvVisits.setVisibility(View.VISIBLE);
//                            tvNoCalls.setVisibility(View.GONE);
//                        } else {
//                            rvVisits.setVisibility(View.GONE);
//                            tvNoCalls.setVisibility(View.VISIBLE);
//                        }
//                        if (!(tinyDB.getString(Constants.vehicle_details).isEmpty())) {
//                            llNoVehicleData.setVisibility(View.GONE);
//                            llVehicleDetails.setVisibility(View.VISIBLE);
//
//                            Gson gson = new Gson();
//                            String json = tinyDB.getString(Constants.vehicle_details);
//
//                            final VehicleDetail obj = gson.fromJson(json, VehicleDetail.class);
//
////                            tvBedsCount.setText(obj.getBedSpace() + "");
//                            tvColor.setText(obj.getVehicleName());
////                            tvEngineHorsepower.setText(obj.getEnginHouspower() + "");
//                            tvRegistrationNo.setText(obj.getRegistrationno());
//
//                            llVehicleDetails.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                                        return;
//                                    }
//                                    mLastClickTime = SystemClock.elapsedRealtime();
//                                    Intent i = new Intent(getActivity(), VehicleDetails.class);
//                                    i.putExtra(Constants.user_id, obj.getUserId());
//                                    startActivity(i);
//                                }
//                            });
//
//                        } else {
//
//                            llNoVehicleData.setVisibility(View.VISIBLE);
//                            llVehicleDetails.setVisibility(View.VISIBLE);
//
//                        }
//                    }
//                    h.postDelayed(runnable, delay);
//                }
//            }
//        }, delay);


        getActivity().registerReceiver(broadcastActionBroadcastReceiver, new IntentFilter(
                Constants.BROADCAST_ACTION));
        getActivity().registerReceiver(notificationEventBroadcastReceiver, new IntentFilter(
                Constants.NOTIFICATION_MESSAGE_EVENT));

        if (!(tinyDB.getString(Constants.StateTitle).isEmpty())) {
            tvState.setText(tinyDB.getString(Constants.StateTitle));
        } else {
            tinyDB.putString(Constants.StateTitle, "Available");
            tinyDB.putString(Constants.StateDescription, "Currently we are available and done with assigned incidents.");
            tinyDB.putInt(Constants.StatePosition, 4);
            tvState.setText(tinyDB.getString(Constants.StateTitle));
        }

        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastActionBroadcastReceiver);
        getActivity().unregisterReceiver(notificationEventBroadcastReceiver);
    }

    private BroadcastReceiver broadcastActionBroadcastReceiver = new BroadcastReceiver() {
        Boolean status = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            status = intent.getBooleanExtra(Constants.Status, false);

            if (status) {
                ivStatus.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_status_online_new));
                tvStatus.setText("Online");
//                tvStatus.setTextColor(getActivity().getResources().getColor(R.color.green));
            } else {
                ivStatus.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_status_offline_new));
                tvStatus.setText("Offline");
//                tvStatus.setTextColor(getActivity().getResources().getColor(R.color.colorGray));
            }
            Log.e("Status", intent.getBooleanExtra(Constants.Status, false) + "");

        }
    };

    private BroadcastReceiver notificationEventBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hitTasksList();
        }
    };
}
