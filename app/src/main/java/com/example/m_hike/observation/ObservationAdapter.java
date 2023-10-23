package com.example.m_hike.observation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.R;
import com.example.m_hike.model.Observation;

import java.util.ArrayList;
import java.util.List;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.ViewHolder> implements Filterable {
    public interface CustomListeners {
        void onItemClick(Observation observation);
        void editItem(Observation observation);
        void deleteItem(Observation observation);
    }

    private final Context context;
    private final int layout;
    private final List<Observation> lstObservation;
    private final List<Observation> filteredObservationList;
    private CustomListeners customListeners;

    public ObservationAdapter(Context context, int layout, List<Observation> lstObservation) {
        this.context = context;
        this.layout = layout;
        this.lstObservation = lstObservation;
        this.filteredObservationList = new ArrayList<>(lstObservation);
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
        Observation observation = filteredObservationList.get(position);
        holder.bind(observation);
    }

    @Override
    public int getItemCount() {
        return filteredObservationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvObservationDate;
        private final TextView tvObservationName;
        private final TextView tvObservationComment;
        private final TextView tvObservationPhoto;
        private final ImageButton btnOverFlowObservation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvObservationDate = itemView.findViewById(R.id.tvObservationDate);
            tvObservationName = itemView.findViewById(R.id.tvObservationName);
            tvObservationComment = itemView.findViewById(R.id.tvObservationComment);
            tvObservationPhoto = itemView.findViewById(R.id.tvObservationPhoto);
            btnOverFlowObservation = itemView.findViewById(R.id.btnOverFlowObservation);
        }

        public String limitString(String string, int maxLength) {
            if (string.length() > maxLength) {
                string = string.substring(0, maxLength) + "...";
            }
            return string;
        }

        @SuppressLint("SetTextI18n")
        public void bind(final Observation observation) {
            tvObservationDate.setText(observation.getTime());
            tvObservationName.setText(limitString(observation.getName(), 15));
            tvObservationComment.setText(limitString(observation.getAdditionalComment(), 15));
            tvObservationPhoto.setText(3 + " photos");

            itemView.setOnClickListener(view -> {
                if (customListeners != null) {
                    customListeners.onItemClick(observation);
                }
            });

            btnOverFlowObservation.setOnClickListener(view -> showPopupMenu(view, observation));
        }

        @SuppressLint("RestrictedApi")
        private void showPopupMenu(View view, Observation observation) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.item_overflow, popupMenu.getMenu());

            if (popupMenu.getMenu() instanceof MenuBuilder) {
                MenuBuilder m = (MenuBuilder) popupMenu.getMenu();
                m.setOptionalIconsVisible(true);
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_overflow_edit) {
                    customListeners.editItem(observation);
                    return true;
                } else if (itemId == R.id.action_overflow_delete) {
                    customListeners.deleteItem(observation);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Observation> filteredResults = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredResults.addAll(lstObservation);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Observation item : lstObservation) {
                        if (item.getName().toLowerCase().contains(filterPattern)) {
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
                filteredObservationList.clear();
                filteredObservationList.addAll((List<Observation>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItem(Observation newItem) {
        filteredObservationList.add(0, newItem);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateItem(Observation updatedItem) {
        for (Observation observation : filteredObservationList) {
            if (observation.getId() == updatedItem.getId()) {
                filteredObservationList.set(filteredObservationList.indexOf(observation), updatedItem);
                notifyDataSetChanged();
                break;
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteItem(Observation deletedItem) {
        for (Observation observation : filteredObservationList) {
            if (observation.getId() == deletedItem.getId()) {
                filteredObservationList.remove(observation);
                notifyDataSetChanged();
                break;
            }
        }
    }
}
