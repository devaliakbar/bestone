package com.mna.bestone.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mna.bestone.R;
import com.mna.bestone.data.Items;

import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<Items> items;
    private CartAdapterRemoveClickListener cartAdapterRemoveClickListener;

    public interface CartAdapterRemoveClickListener {
        void onItemRemove(String currentItemId);
    }

    public CartAdapter(Context context, CartAdapterRemoveClickListener cartAdapterRemoveClickListener) {
        this.context = context;
        this.cartAdapterRemoveClickListener = cartAdapterRemoveClickListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutIdForListItem = R.layout.cart_item_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new CartAdapter.CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i) {
        Items item = items.get(i);

        cartViewHolder.nameV.setText(item.getName());
        cartViewHolder.catV.setText("Category :" + item.getCatagory());
        cartViewHolder.brandV.setText("Brand :" + item.getBrand());

        String priceInString = "Rs :" + item.getPrice() + "/-";
        cartViewHolder.priceV.setText(priceInString);

        String qtyInString = "Qty :" + item.getQty() + "-Nos/Kg";
        cartViewHolder.qtyV.setText(qtyInString);

        Glide.with(this.context)
                .load("http://bestonesupermarket.com/img/" + item.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(cartViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    public List<Items> swapCursor(List<Items> c) {
        List<Items> temp = items;
        items = c;
        notifyDataSetChanged();
        return temp;
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        TextView nameV, catV, brandV, priceV, qtyV;
        LinearLayout rmvBtn;
        ImageView imageView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_view_id);
            nameV = itemView.findViewById(R.id.name);
            catV = itemView.findViewById(R.id.cat);
            brandV = itemView.findViewById(R.id.brand);
            priceV = itemView.findViewById(R.id.price);
            qtyV = itemView.findViewById(R.id.qty);
            rmvBtn = itemView.findViewById(R.id.rm_but);

            rmvBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    Items curItem = items.get(adapterPosition);
                    String idOfItem = "" + curItem.getId();
                    cartAdapterRemoveClickListener.onItemRemove(idOfItem);
                }
            });
        }
    }
}

