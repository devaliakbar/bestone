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

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private Context context;
    private List<CatItem> items;
    private ItemAdapterOnClickListener itemAdapterOnClickListener;


    public interface ItemAdapterOnClickListener {
        void onItemClick(String currentItem);
    }

    public ItemAdapter(Context context, ItemAdapterOnClickListener itemAdapterOnClickListener) {
        this.context = context;
        this.itemAdapterOnClickListener = itemAdapterOnClickListener;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        int layoutIdForListItem = R.layout.item_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ItemAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        CatItem catItem = items.get(i);

        itemViewHolder.nameV.setText(catItem.getName());
        itemViewHolder.brandV.setText(catItem.getBrand());
        String priceString = "Rs :" + catItem.getPrice();
        itemViewHolder.priceV.setText(priceString);

        Glide.with(this.context)
                .load("http://bestonesupermarket.com/img/" + catItem.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(itemViewHolder.imageView);
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

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameV, brandV, priceV;

        Button addCartBut;
        ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView =itemView.findViewById(R.id.img_view_id);
            nameV = itemView.findViewById(R.id.name);
            brandV = itemView.findViewById(R.id.brand);
            priceV = itemView.findViewById(R.id.price);

            addCartBut = itemView.findViewById(R.id.add_cart_but);
            addCartBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    CatItem curItem = items.get(adapterPosition);
                    String idOfItem = "" + curItem.getId();
                    itemAdapterOnClickListener.onItemClick(idOfItem);
                }
            });
        }
    }
}
