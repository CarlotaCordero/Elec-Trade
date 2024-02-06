package com.acm.elec_trade.Adapter;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acm.elec_trade.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ProductAdapterFB extends FirestoreRecyclerAdapter<ProductFB, ProductAdapterFB.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ProductAdapterFB(@NonNull FirestoreRecyclerOptions<ProductFB> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ProductFB model) {
        holder.name.setText(model.getName());
        holder.desc.setText(model.getDesc());
        holder.price.setText(model.getPrice());
        Glide.with(holder.itemView.getContext())
                .load(model.getImgurl()) // Usa la URL de la imagen almacenada en el modelo
                .centerInside()
                .placeholder(new ColorDrawable(holder.itemView.getResources().getColor(R.color.Goldfinger)))
                .into(holder.imgurl);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card_fb, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc, price;
        ImageView imgurl;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.productName);
            desc = itemView.findViewById(R.id.productDesc);
            price = itemView.findViewById(R.id.productPrice);
            imgurl = itemView.findViewById(R.id.productImage);
        }
    }
}
