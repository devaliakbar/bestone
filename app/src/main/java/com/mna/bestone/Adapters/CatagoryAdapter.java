package com.mna.bestone.Adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mna.bestone.Catagory;
import com.mna.bestone.Network.VolleyRequest;
import com.mna.bestone.R;
import com.mna.bestone.data.CatItem;
import com.mna.bestone.data.CatagoryList;
import com.mna.bestone.data.JsonExtract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CatagoryAdapter extends RecyclerView.Adapter<CatagoryAdapter.CatagoryViewHolder> {

    OnBottomReachedListener onBottomReachedListener;
    private Context mContext;
    private List<CatagoryList> catagories = new ArrayList<>();
    private CatagoryAdapterOnClickListener mClickHandler;
    private ItemAdapter.ItemAdapterOnClickListener itemAdapterOnClickListener;

    public CatagoryAdapter(Context context, CatagoryAdapterOnClickListener catagoryAdapterOnClickListener, ItemAdapter.ItemAdapterOnClickListener itemAdapterOnClickListener) {
        mContext = context;
        mClickHandler = catagoryAdapterOnClickListener;
        this.itemAdapterOnClickListener = itemAdapterOnClickListener;
    }

    public interface OnBottomReachedListener {
        void onBottomReached(int position);
    }

    public interface CatagoryAdapterOnClickListener {
        void onClick(String currentRaw);
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {

        this.onBottomReachedListener = onBottomReachedListener;
    }

    @NonNull
    @Override
    public CatagoryAdapter.CatagoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutIdForListItem = R.layout.catagory_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new CatagoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CatagoryViewHolder holder, int position) {
        if (position == catagories.size() - 1) {

            onBottomReachedListener.onBottomReached(position);

        }
        final CatagoryList catagoryList = catagories.get(position);

        holder.catagoryNameText.setText(catagoryList.getCatagory());

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);

        holder.recyclerView.setHasFixedSize(true);
        ItemAdapter itemAdapter = new ItemAdapter(mContext, itemAdapterOnClickListener);
        holder.recyclerView.setAdapter(itemAdapter);

        itemAdapter.swapCursor(catagoryList.getItems());
    }

    @Override
    public int getItemCount() {
        if (catagories == null) return 0;
        return catagories.size();
    }


    public List<CatagoryList> swapCursor(List<CatagoryList> c) {
        List<CatagoryList> temp = catagories;
        catagories = c;
        notifyDataSetChanged();
        return temp;
    }

    class CatagoryViewHolder extends RecyclerView.ViewHolder {
        TextView catagoryNameText;

        RecyclerView recyclerView;

        Button viewCatButton;

        public CatagoryViewHolder(@NonNull View itemView) {
            super(itemView);
            catagoryNameText = itemView.findViewById(R.id.cat_id);
            recyclerView = itemView.findViewById(R.id.item_recycler);
            viewCatButton = itemView.findViewById(R.id.view_cat_but);
            viewCatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    CatagoryList cat = catagories.get(adapterPosition);
                    mClickHandler.onClick(cat.getCatagory());
                }
            });
        }
    }

}
