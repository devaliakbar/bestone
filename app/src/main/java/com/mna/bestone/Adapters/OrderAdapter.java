package com.mna.bestone.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mna.bestone.R;
import com.mna.bestone.data.CatagoryList;
import com.mna.bestone.data.Items;
import com.mna.bestone.data.Order;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context mContext;
    private List<Order> orders;
    private OrderRemoveOnClickListener orderRemoveOnClickListener;

    public OrderAdapter(Context context, OrderRemoveOnClickListener orderRemoveOnClickListener) {
        this.mContext = context;
        this.orderRemoveOnClickListener = orderRemoveOnClickListener;
    }


    public interface OrderRemoveOnClickListener {
        void onRemove(String currentRaw);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutIdForListItem = R.layout.order_view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new OrderAdapter.OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, int i) {
        Order order = orders.get(i);

        String priceString = "Rs :" + order.getPrice();
        orderViewHolder.totalV.setText(priceString);

        orderViewHolder.dateV.setText(order.getDate());


        //HIDING DELETE BUTTON FOR DELEVERED ITEM
        if (!order.getStatus().matches("ORDER")) {
            orderViewHolder.cancel.setVisibility(View.GONE);
            orderViewHolder.totItem.setVisibility(View.VISIBLE);
            orderViewHolder.totItem.setText(order.getNoItem() + " Items");
        } else {
            orderViewHolder.statusV.setText("Not Delivered");
            orderViewHolder.totItem.setVisibility(View.GONE);
            orderViewHolder.cancel.setVisibility(View.VISIBLE);
            orderViewHolder.statusV.setTextColor(mContext.getResources().getColor(R.color.nom_color));
        }

        if (order.getStatus().matches("CANCEL")) {
            orderViewHolder.statusV.setText("Order Canceled");
            orderViewHolder.statusV.setTextColor(mContext.getResources().getColor(R.color.red));
        }

        if (order.getStatus().matches("DONE")) {
            orderViewHolder.statusV.setText("Order Delivered");
            orderViewHolder.statusV.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }


        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        orderViewHolder.recyclerView.setLayoutManager(layoutManager);

        orderViewHolder.recyclerView.setHasFixedSize(true);
        OrderItemAdapter itemAdapter = new OrderItemAdapter(mContext);
        orderViewHolder.recyclerView.setAdapter(itemAdapter);

        List<Items> returnList = order.getItemList();
        itemAdapter.swapCursor(returnList);
    }

    @Override
    public int getItemCount() {
        if (orders == null) return 0;
        return orders.size();
    }

    public List<Order> swapCursor(List<Order> c) {
        List<Order> temp = orders;
        orders = c;
        notifyDataSetChanged();
        return temp;
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView dateV, statusV, totalV, totItem;
        Button cancel;

        RecyclerView recyclerView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            dateV = itemView.findViewById(R.id.date);
            statusV = itemView.findViewById(R.id.status);
            totalV = itemView.findViewById(R.id.total);

            totItem = itemView.findViewById(R.id.tot_item);

            recyclerView = itemView.findViewById(R.id.order_item_recycler);

            cancel = itemView.findViewById(R.id.clr_but);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    Order ordr = orders.get(adapterPosition);
                    String idOfOrder = "" + ordr.getId();
                    orderRemoveOnClickListener.onRemove(idOfOrder);
                }
            });
        }
    }
}
