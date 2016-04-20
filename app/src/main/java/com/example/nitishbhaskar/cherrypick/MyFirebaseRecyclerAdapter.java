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
        //movieViewHolder.nameTV.setText(movie.getName());
        /*Picasso.with(mContext).load(movie.getUrl()).into(movieViewHolder.icon);
        movieViewHolder.title.setText(movie.getName());
        movieViewHolder.description.setText(movie.getDescription());
//            checkBox.setChecked((Boolean) movie.get("selection"));
        Float rat = (movie.getRating());*/
        /*if(rat < 7){
            movieViewHolder.mSimpleRatingView.setIconColor(0xffff0000);
            movieViewHolder.mSimpleRatingView.setSelectedRating(SimpleRatingView.Rating.NEGATIVE);
        }
        else if (rat > 7 && rat < 8){
            movieViewHolder.mSimpleRatingView.setIconColor(0xff0000ff);
            movieViewHolder.mSimpleRatingView.setSelectedRating(SimpleRatingView.Rating.NEUTRAL);
        }
        else{
            movieViewHolder.mSimpleRatingView.setIconColor(0xff00ff00);
            movieViewHolder.mSimpleRatingView.setSelectedRating(SimpleRatingView.Rating.POSITIVE);
        }*/

       // movieViewHolder.rating.setText(String.valueOf(rat));
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

        public ProductViewHolder(View v) {
            super(v);

        productName = (TextView) itemView.findViewById(R.id.productTitle);
        productPrice = (TextView) itemView.findViewById(R.id.productPrice);
        productDatePostedOn = (TextView) itemView.findViewById(R.id.productDatePostedOn);
        productDescription = (TextView) itemView.findViewById(R.id.productDescription);


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
