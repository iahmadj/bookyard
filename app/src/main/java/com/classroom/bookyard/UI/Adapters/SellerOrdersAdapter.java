package com.classroom.bookyard.UI.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.classroom.bookyard.Model.Order;
import com.classroom.bookyard.R;
import com.classroom.bookyard.UI.ClickListeners.RecyclerViewClickListenerOrder;
import com.classroom.bookyard.UI.Seller.SellersOrders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SellerOrdersAdapter extends RecyclerView.Adapter<SellerOrdersAdapter.ViewHolder> {

    private static final String TAG = "data";
    private ArrayList<Order> orders;
    private Context context;
    TextView textView;
    View v;
    private FirebaseFirestore db;
    private RecyclerViewClickListenerOrder mClickListener;
    private Order mProduct;
    String or,address;


    public SellerOrdersAdapter(ArrayList<Order> orders, Context context, RecyclerViewClickListenerOrder mClickListener) {
        this.orders = orders;
        this.context = context;
        this.mClickListener = mClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        db = FirebaseFirestore.getInstance();
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.seller_product_orders,
                viewGroup, false);

        return new ViewHolder(v);
    }

    public ArrayList<Order> getItems() {
        return orders;
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int position) {
        viewHolder.bindModel(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Order mOrder;
        TextView ordername, productprice, orderID, caddress;
        RecyclerView rc_products;
        Button check, cancel;


        ViewHolder(View v) {
            super(v);
            ordername = itemView.findViewById(R.id.product_title_cart);
            productprice = itemView.findViewById(R.id.product_price_cart);
            orderID = itemView.findViewById(R.id.id_order);
            rc_products = itemView.findViewById(R.id.rc_products);
            caddress = itemView.findViewById(R.id.caddress);

            //Buttons
            check = itemView.findViewById(R.id.check);
            cancel = itemView.findViewById(R.id.cancel);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false);

            rc_products.setLayoutManager(layoutManager);
            rc_products.setHasFixedSize(true);

            v.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void bindModel(Order order) {

            this.mOrder = order;
            or = mOrder.getId_order();


            ArrayList mOrderProducts = mOrder.getProducts();
            ordername.setText(mOrder.getId_order());
            caddress.setText(mOrder.getAddress());

            if (mOrderProducts.size() != 0) {
                ProductsOrdersAdapter productAdapter = new ProductsOrdersAdapter(mOrderProducts, context);
                rc_products.setAdapter(productAdapter);
                productprice.setText("number of Products: " + mOrderProducts.size());
            }

            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateState(1, mOrder.getId_order());
                    ((Activity)context).finish();
                    context.startActivity(new Intent(context, SellersOrders.class));
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateState(2, mOrder.getId_order());
                    ((Activity)context).finish();
                    context.startActivity(new Intent(context, SellersOrders.class));
                }
            });

        }


        @Override
        public void onClick(View view) {
            Toast.makeText(context.getApplicationContext(), "Please Dont Click me",Toast.LENGTH_SHORT);
        }
    }


    public void updateState(int i, String s) {

        Map<String, Object> data = new HashMap<>();
        data.put("status", i);

        db.collection("orders").document(s)
                .set(data, SetOptions.merge());

    }
}

