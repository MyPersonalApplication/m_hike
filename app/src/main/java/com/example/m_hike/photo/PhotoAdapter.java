package com.example.m_hike.photo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.R;
import com.example.m_hike.model.Photo;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> implements Filterable {
    public interface CustomListeners {
        void onItemClick(Photo photo);

        void deleteItem(Photo photo);
    }

    private final Context context;
    private final int layout;
    private final List<Photo> lstPhoto;
    private final List<Photo> filteredPhotoList;
    private CustomListeners customListeners;

    public PhotoAdapter(Context context, int layout, List<Photo> lstPhoto) {
        this.context = context;
        this.layout = layout;
        this.lstPhoto = lstPhoto;
        this.filteredPhotoList = new ArrayList<>(lstPhoto);
    }

    public void setCustomListeners(CustomListeners customListeners) {
        this.customListeners = customListeners;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = filteredPhotoList.get(position);
        holder.bind(photo);
    }

    @Override
    public int getItemCount() {
        return filteredPhotoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgViewPhoto;
        private final TextView tvPhotoDate;
        private final TextView tvPhotoTitle;
        private final ImageButton ibDeletePhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgViewPhoto = itemView.findViewById(R.id.imageViewPhoto);
            tvPhotoDate = itemView.findViewById(R.id.tvPhotoDate);
            tvPhotoTitle = itemView.findViewById(R.id.tvPhotoTitle);
            ibDeletePhoto = itemView.findViewById(R.id.ibDeletePhoto);
        }

        public String limitString(String string, int maxLength) {
            if (string.length() > maxLength) {
                string = string.substring(0, maxLength) + "...";
            }
            return string;
        }

        @SuppressLint("SetTextI18n")
        public void bind(final Photo photo) {
            byte[] imgPhoto = photo.getImageUrl();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgPhoto, 0, imgPhoto.length);
            imgViewPhoto.setImageBitmap(bitmap);

            tvPhotoDate.setText(photo.getTimestamp());
            tvPhotoTitle.setText(limitString(photo.getTitle(), 20));

            itemView.setOnClickListener(view -> {
                if (customListeners != null) {
                    customListeners.onItemClick(photo);
                }
            });

            ibDeletePhoto.setOnClickListener(view -> {
                if (customListeners != null) {
                    customListeners.deleteItem(photo);
                }
            });
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Photo> filteredResults = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredResults.addAll(lstPhoto);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Photo item : lstPhoto) {
                        if (item.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredResults.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredResults;
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredPhotoList.clear();
                filteredPhotoList.addAll((List<Photo>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItem(Photo newItem) {
        filteredPhotoList.add(0, newItem);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateItem(Photo updatedItem) {
        for (Photo photo : filteredPhotoList) {
            if (photo.getId() == updatedItem.getId()) {
                filteredPhotoList.set(filteredPhotoList.indexOf(photo), updatedItem);
                notifyDataSetChanged();
                break;
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteItem(Photo deletedItem) {
        for (Photo observation : filteredPhotoList) {
            if (observation.getId() == deletedItem.getId()) {
                filteredPhotoList.remove(observation);
                notifyDataSetChanged();
                break;
            }
        }
    }
}
