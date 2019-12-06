package com.example.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ProductViewHolder>{

    Context mCtx;
    List<NameList> nameList;

    public InventoryAdapter(Context mCtx, List<NameList> nameList) {
        this.mCtx = mCtx;
        this.nameList = nameList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.product_layout,
                parent, false);
        ProductViewHolder productViewHolder = new ProductViewHolder(view);
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        NameList product = nameList.get(position);

        holder.textViewTitle.setText(product.getName());
        holder.textViewShortDesc.setText(product.getDescription());
        holder.textviewPrice.setText(String.valueOf(product.getMeasure_unit()));
       // holder.textViewRating.setText(String.valueOf(product.getUpload_path()));

//        StorageReference storageReference = product.getUpload_path();

//        Glide.with(mCtx).load(product.getUpload_path()).into(holder.imageView);



    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

       public ImageView imageView;
        TextView textViewTitle, textViewShortDesc, textViewRating, textviewPrice;

        public ProductViewHolder(View itemView) {
            super(itemView);

//            imageView = itemView.findViewById(R.id.imageView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textviewPrice = itemView.findViewById(R.id.textViewPrice);


        }
    }




}
