package com.example.nitishbhaskar.cherrypick;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class MyFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Product,MyFirebaseRecyclerAdapter.ProductViewHolder> {

    private Context mContext ;
    static IClickListener myClickListener;
    private int position;


    public MyFirebaseRecyclerAdapter(Class<Product> modelClass, int modelLayout,
                                     Class<ProductViewHolder> holder, Query ref, Context context) {
        super(modelClass, modelLayout, holder, ref);
        this.mContext = context;
    }

    @Override
    protected void populateViewHolder(ProductViewHolder productViewHolder, Product product, int i) {

        productViewHolder.productName.setText(product.getProductName());
        productViewHolder.productDescription.setText(product.getDescription());
        productViewHolder.productPrice.setText("Price: $"+product.getPrice());
        productViewHolder.productDatePostedOn.setText("Date: "+product.getDatePostedOn());
        if(product.getImage() != null)
            Picasso.with(mContext).load(product.getImage()).into(productViewHolder.productImage);
        productViewHolder.productName.setTransitionName((String)product.getProductName());
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

    public void notification(Context mContext, HashMap<String, ?> currentProduct){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})
                        .setContentTitle((String) currentProduct.get("productName") + " @ $"+(String) currentProduct.get("price"))
                        .setContentText((String) currentProduct.get("description"));
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(10, mBuilder.build());
    }

}
