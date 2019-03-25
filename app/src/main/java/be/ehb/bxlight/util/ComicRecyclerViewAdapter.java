package be.ehb.bxlight.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import be.ehb.bxlight.DetailActivity;
import be.ehb.bxlight.R;
import be.ehb.bxlight.model.entities.ComicPOI;

public class ComicRecyclerViewAdapter extends RecyclerView.Adapter<ComicRecyclerViewAdapter.ViewHolder> implements Filterable {

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivBackground;
        final TextView tvName;
        ComicPOI mItem;

        ViewHolder(final View view) {
            super(view);
            ivBackground = view.findViewById(R.id.iv_row_image);
            tvName = view.findViewById(R.id.tv_row_name);
            ivBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(view.getContext(), DetailActivity.class);
                    i.putExtra("item", mItem);
                    view.getContext().startActivity(i);
                }
            });
        }
    }

    private final List<ComicPOI> mValues;
    private List<ComicPOI> mValuesFiltered;

    public ComicRecyclerViewAdapter(List<ComicPOI> items) {
        mValues = items;
        mValuesFiltered = items;
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
        ComicPOI item = mValuesFiltered.get(position);
        holder.mItem = item;
        holder.tvName.setText(String.format("%s\nBy %s", item.getCharacterName(), item.getAuthor()));

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
        return mValuesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mValuesFiltered = mValues;
                } else {
                    List<ComicPOI> filteredList = new ArrayList<>();
                    for (ComicPOI row : mValues) {
                        if (row.getCharacterName().toLowerCase().contains(charString.toLowerCase())
                        || row.getAuthor().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    mValuesFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mValuesFiltered;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mValuesFiltered = (ArrayList<ComicPOI>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
