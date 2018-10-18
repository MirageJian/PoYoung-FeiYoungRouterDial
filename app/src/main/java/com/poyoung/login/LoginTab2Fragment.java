package com.poyoung.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.poyoung.ListRecyclerViewAdapter;
import com.poyoung.R;
import com.poyoung.logout.LogoutActivity;
import com.poyoung.logout.LogoutUrlRecoder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class LoginTab2Fragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private SwipeRefreshLayout mSwipe;
    private ListRecyclerViewAdapter mAdapter;
    private ArrayList<HashMap<String, String>> mListLogoutUrl;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LoginTab2Fragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LoginTab2Fragment newInstance(int columnCount) {
        LoginTab2Fragment fragment = new LoginTab2Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_tab2_list, container, false);
        mSwipe = view.findViewById(R.id.login_fragment_tab2_swipe);
        mSwipe.setProgressBackgroundColorSchemeResource(android.R.color.white);
        mSwipe.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListLogoutUrl.clear();
                mListLogoutUrl.addAll(LogoutUrlRecoder.getAll(getContext()));
                mAdapter.notifyDataSetChanged();
                mSwipe.setRefreshing(false);
            }
        });
        // Set the adapter
        // if (view instanceof RecyclerView) { }
        RecyclerView recyclerView = view.findViewById(R.id.login_fragment_tab2_list);
        Context context = recyclerView.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mListLogoutUrl = LogoutUrlRecoder.getAll(getContext());
        mAdapter = new ListRecyclerViewAdapter(mListLogoutUrl, new OnRecycleViewInteractionListener());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(context).getOrientation()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(HashMap<String, String> item);
    }

    public class OnRecycleViewInteractionListener {
        public void onRecycleItemClick(HashMap<String, String> item) {
            HashMap<String, String> map = LogoutUrlRecoder.getOne(getContext(), item.get("logoutUrl"));
            String result = null;
            if (map != null) {
                result = map.get("logoutUrl");
            }
            Intent intent = new Intent(getActivity(), LogoutActivity.class);
            intent.putExtra("logoutUrl", result);
            startActivity(intent);
        }
        public void onRecycleItemDelete(HashMap<String, String> item) {
            LogoutUrlRecoder.removeOne(getContext(),item.get("logoutUrl"));
            mListLogoutUrl.clear();
            mListLogoutUrl.addAll(LogoutUrlRecoder.getAll(getContext()));
            mAdapter.notifyDataSetChanged();
        }
    }
}
