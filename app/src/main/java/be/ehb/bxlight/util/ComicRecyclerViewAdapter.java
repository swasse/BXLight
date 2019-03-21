package be.ehb.bxlight.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import be.ehb.bxlight.R;
import be.ehb.bxlight.model.entities.ComicPOI;

public class ComicRecyclerViewAdapter extends RecyclerView.Adapter<ComicRecyclerViewAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivBackground;
        final TextView tvName;
        ComicPOI mItem;

        ViewHolder(View view) {
            super(view);
            ivBackground = view.findViewById(R.id.iv_row_image);
            tvName = view.findViewById(R.id.tv_row_name);
        }
    }

    private final List<ComicPOI> mValues;

    public ComicRecyclerViewAdapter(List<ComicPOI> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comic_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        ComicPOI item = mValues.get(position);
        holder.mItem = item;

        holder.tvName.setText(item.getCharacterName());

        try {
            FileInputStream fis = holder.itemView.getContext().openFileInput(item.getImage());
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            holder.ivBackground.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


}
