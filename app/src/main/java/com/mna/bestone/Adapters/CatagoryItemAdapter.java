package com.mna.bestone.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mna.bestone.R;
import com.mna.bestone.data.CatItem;

import java.util.List;

public class CatagoryItemAdapter extends RecyclerView.Adapter<CatagoryItemAdapter.CatagoryItemViewHolder> {
    CatagoryItemAdapter.OnBottomReachedListener onBottomReachedListener;
    private Context context;
    private List<CatItem> items;
    CatagoryItemAdapterOnClickListener catagoryItemAdapterOnClickListener;

    public interface OnBottomReachedListener {

        void onBottomReached(int position);

    }

    public interface CatagoryItemAdapterOnClickListener {
        void onClick(String currentItem);
    }

    public CatagoryItemAdapter(Context context, CatagoryItemAdapterOnClickListener catagoryItemAdapterOnClickListener) {
        this.context = context;
        this.catagoryItemAdapterOnClickListener = catagoryItemAdapterOnClickListener;
    }

    public void setOnBottomReachedListener(CatagoryItemAdapter.OnBottomReachedListener onBottomReachedListener) {

        this.onBottomReachedListener = onBottomReachedListener;
    }

    @NonNull
    @Override
    public CatagoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutIdForListItem = R.layout.cat_item_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new CatagoryItemAdapter.CatagoryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatagoryItemViewHolder catagoryItemViewHolder, int i) {
        if (i == items.size() - 1) {
            onBottomReachedListener.onBottomReached(i);
        }

        CatItem item = items.get(i);

        catagoryItemViewHolder.nameV.setText(item.getName());
        catagoryItemViewHolder.brandV.setText(item.getBrand());
        String priceString = "PRICE :" + item.getPrice();
        catagoryItemViewHolder.priceV.setText(priceString);

        Glide.with(this.context)
                .load("http://bestonesupermarket.com/img/" + item.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(catagoryItemViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    public List<CatItem> swapCursor(List<CatItem> c) {
        List<CatItem> temp = items;
        items = c;
        notifyDataSetChanged();
        return temp;
    }

    class CatagoryItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameV, brandV, priceV;

        Button addCartBut;
        ImageView imageView;

        public CatagoryItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img_view_id);
            nameV = itemView.findViewById(R.id.name);
            brandV = itemView.findViewById(R.id.brand);
            priceV = itemView.findViewById(R.id.price);

            addCartBut = itemView.findViewById(R.id.add_cart_but);
            addCartBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    CatItem itemForCat = items.get(adapterPosition);
                    String idOfItem = "" + itemForCat.getId();
                    catagoryItemAdapterOnClickListener.onClick(idOfItem);
                }
            });
        }
    }
}
