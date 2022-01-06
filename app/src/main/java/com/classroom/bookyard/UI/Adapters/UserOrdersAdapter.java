package com.classroom.bookyard.UI.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.classroom.bookyard.Model.Order;
import com.classroom.bookyard.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;


public class UserOrdersAdapter extends RecyclerView.Adapter<UserOrdersAdapter.ViewHolder> {

    private ArrayList<Order> orders;
    private Context context;
    RecyclerView rc_products;

    View v;
    private FirebaseFirestore db;
    //private RecyclerViewClickListenerProduct mClickListener;

    public UserOrdersAdapter(ArrayList<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
        //this.mClickListener = mClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        db = FirebaseFirestore.getInstance();
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.produit_userorders,
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        Order mOrder;
        TextView ordername, productprice, orderID;


        ViewHolder(View v) {
            super(v);
            ordername = itemView.findViewById(R.id.product_title_cart);
            productprice = itemView.findViewById(R.id.product_price_cart);
            orderID = itemView.findViewById(R.id.id_order);
            rc_products = itemView.findViewById(R.id.rc_products);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false);
            rc_products.setLayoutManager(layoutManager);
            rc_products.setHasFixedSize(true);

        }

        @SuppressLint("SetTextI18n")
        void bindModel(Order order) {

            this.mOrder = order;
            ArrayList<com.classroom.bookyard.Model.Product> mOrderProducts  = mOrder.getProducts();
            ordername.setText(mOrder.getId_order());

            if (mOrderProducts.size() != 0){
                ProductsOrdersAdapter productAdapter = new ProductsOrdersAdapter(mOrderProducts, context);
                rc_products.setAdapter(productAdapter);
                productprice.setText("number of Products: "+mOrderProducts.size());
            }

        }

    }


}
