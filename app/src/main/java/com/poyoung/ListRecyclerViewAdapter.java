package com.poyoung;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.poyoung.login.LoginTab2Fragment.OnRecycleViewInteractionListener;
import com.poyoung.login.LoginTab2Fragment.OnListFragmentInteractionListener;
import com.poyoung.dummy.DummyContent.DummyItem;
import com.poyoung.logout.LogoutUrlRecoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<HashMap<String, String>> mValues;
    private final OnRecycleViewInteractionListener mListener;

    public ListRecyclerViewAdapter(ArrayList<HashMap<String, String>> items, OnRecycleViewInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_login_tab2_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        String clientip = null;
        Matcher matcher = Pattern.compile("wlanuserip=([^&]+)").matcher(mValues.get(position).get("logoutUrl"));
        if (matcher.find()) {
            clientip = matcher.group(1);
        }
        holder.mIdView.setText(clientip);
        holder.mContentView.setText(mValues.get(position).get("date"));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onRecycleItemClick(holder.mItem);
                }
            }
        });
        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onRecycleItemDelete(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final Button mButton;
        public HashMap<String, String> mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.login_fragment_tab2_item_number);
            mContentView = view.findViewById(R.id.login_fragment_tab2_content);
            mButton = view.findViewById(R.id.login_fragment_tab2_delete_item);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

}
