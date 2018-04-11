package com.fatwong.encore.ui.fragment.discover;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.ChartRecyclerAdapter;
import com.fatwong.encore.bean.Chart;
import com.fatwong.encore.interfaces.APIService;
import com.fatwong.encore.ui.activity.discover.ChartDetailActivity;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChartFragment extends Fragment {

    @BindView(R.id.chart_recycler)
    RecyclerView chartRecycler;
    Unbinder unbinder;

    private ChartRecyclerAdapter chartRecyclerAdapter;

    public ChartFragment() {
        // Required empty public constructor
    }

    public static ChartFragment newInstance() {
        ChartFragment fragment = new ChartFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        chartRecyclerAdapter = new ChartRecyclerAdapter(getActivity());
        chartRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        chartRecycler.setAdapter(chartRecyclerAdapter);
        chartRecyclerAdapter.setOnChartClickListener(new ChartRecyclerAdapter.OnChartClickListener() {
            @Override
            public void onItemClick(View view, int type) {
                ChartDetailActivity.open(getActivity(), String.valueOf(type));
            }
        });
        return view;
    }

    private void initData() {
        OkGo.<String>get(APIService.BASE_PARAMETERS_URL + "cainiaomusic/ranking_last")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Gson gson = new Gson();
                        Chart chart = gson.fromJson(response.body(), Chart.class);
                        chartRecyclerAdapter.setData(chart.getContent());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
