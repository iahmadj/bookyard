package com.classroom.app1.cart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.classroom.app1.Model.Product;
import com.classroom.app1.R;

import java.util.ArrayList;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.ViewHolder> {

    private ArrayList<Product> products;
    private Context context;
    //private RecyclerViewClickListenerProduct mClickListener;

    public CartProductAdapter(ArrayList<Product> products, Context context) {
        this.products = products;
        this.context = context;
        //this.mClickListener = mClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.produit_card,
                viewGroup, false);
        return new ViewHolder(v);
    }

    public ArrayList<Product> getItems() {
        return products;
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int position) {
        viewHolder.bindModel(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Product mProduct;
        ImageView productImg;
        TextView productname;
        TextView productprice;


        ViewHolder(View v) {
            super(v);
            productImg = itemView.findViewById(R.id.product_img_cart);
            productname = itemView.findViewById(R.id.product_title_cart);
            productprice = itemView.findViewById(R.id.product_price_cart);

            //v.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void bindModel(Product products) {

            this.mProduct = products;
            productname.setText(mProduct.getfname());
            productprice.setText("Rs. "+mProduct.getPrice().toString() + "/-");


            Glide
                    .with(context)
                    .load(mProduct.getImg_product())
                    .into(productImg);
        }

        //@Override
        /*public void onClick(View view) {
            mClickListener.onClick(view, mProduct);
        }*/
    }
}
