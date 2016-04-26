package com.example.nitishbhaskar.cherrypick;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.squareup.picasso.Picasso;


public class MyFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Product,MyFirebaseRecyclerAdapter.ProductViewHolder> {

    private Context mContext ;
    static IClickListener myClickListener;


    public MyFirebaseRecyclerAdapter(Class<Product> modelClass, int modelLayout,
                                     Class<ProductViewHolder> holder, Query ref, Context context) {
        super(modelClass,modelLayout,holder,ref);
        this.mContext = context;
    }

    @Override
    protected void populateViewHolder(ProductViewHolder productViewHolder, Product product, int i) {

        productViewHolder.productName.setText(product.getProductName());
        productViewHolder.productDescription.setText(product.getDescription());
        productViewHolder.productPrice.setText("Price: $"+product.getPrice());
        productViewHolder.productDatePostedOn.setText("Date: "+product.getDatePostedOn());
        //TODO: Populate viewHolder by setting the movie attributes to cardview fields
        if(product.getImage() != null)
            Picasso.with(mContext).load(product.getImage()).into(productViewHolder.productImage);
    }

    public void setOnItemClickListener(final IClickListener myItemClickListener){
        this.myClickListener = myItemClickListener;
    }



    //TODO: Populate ViewHolder and add listeners.
    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        public TextView productName;
        public TextView productPrice;
        public TextView productDatePostedOn;
        public TextView productId;
        public TextView productDescription;
        public TextView productLocation;
        public ImageView productImage;

        public ProductViewHolder(View v) {
            super(v);

        productName = (TextView) itemView.findViewById(R.id.productTitle);
        productPrice = (TextView) itemView.findViewById(R.id.productPrice);
        productDatePostedOn = (TextView) itemView.findViewById(R.id.productDatePostedOn);
        productDescription = (TextView) itemView.findViewById(R.id.productDescription);
            productImage = (ImageView) itemView.findViewById(R.id.productIcon);

       /* cardMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myClickListener != null) {
                    myClickListener.cardMenuClickListener(v, getPosition());
                }
            }
        });*/

        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (myClickListener != null) {
                    myClickListener.viewClickListener(itemView, getPosition());
                }
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (myClickListener != null) {
                    myClickListener.viewLongClickListener(itemView, getPosition());
                }
                return true;
            }
        });

        }

    }

}
