package com.mna.bestone.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mna.bestone.R;
import com.mna.bestone.data.Items;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemHolder> {
    private Context context;
    private List<Items> items;
    public OrderItemAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public OrderItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutIdForListItem = R.layout.order_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new OrderItemAdapter.OrderItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemHolder orderItemHolder, int i) {
        Items item = items.get(i);

        orderItemHolder.nameV.setText(item.getName());
        orderItemHolder.catV.setText("Category :" + item.getCatagory());
        orderItemHolder.brandV.setText("Brand :" + item.getBrand());

        String priceInString = "Rs :" + item.getPrice() + "/-";
        orderItemHolder.priceV.setText(priceInString);

        String qtyInString = "Qty :" + item.getQty() + "-Nos/Kg";
        orderItemHolder.qtyV.setText(qtyInString);

        Glide.with(this.context)
                .load("http://bestonesupermarket.com/img/" + item.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(orderItemHolder.imageView);
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

    class OrderItemHolder extends RecyclerView.ViewHolder {
        TextView nameV, catV, brandV, priceV, qtyV;
        ImageView imageView;
        public OrderItemHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_items);
            nameV = itemView.findViewById(R.id.name);
            catV = itemView.findViewById(R.id.cat);
            brandV = itemView.findViewById(R.id.brand);
            priceV = itemView.findViewById(R.id.price);
            qtyV = itemView.findViewById(R.id.qty);
        }
    }
}
