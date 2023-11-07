package com.example.m_hike.hike;

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
import com.example.m_hike.model.Hike;

import java.util.ArrayList;
import java.util.List;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.ViewHolder> implements Filterable {
    public interface CustomListeners {
        void onItemClick(Hike hike);

        void editItem(Hike hike);

        void deleteItem(Hike hike);
    }

    private final Context context;
    private final int layout;
    private final List<Hike> lstHike;
    private final List<Hike> filteredHikeList;
    private CustomListeners customListeners;
    private String searchCriteria = "Name";

    public HikeAdapter(Context context, int layout, List<Hike> lstHike) {
        this.context = context;
        this.layout = layout;
        this.lstHike = lstHike;
        this.filteredHikeList = new ArrayList<>(lstHike);
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
        Hike hike = filteredHikeList.get(position);
        holder.bind(hike);
    }

    @Override
    public int getItemCount() {
        return filteredHikeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvHikeDate;
        private final TextView tvHikeName;
        private final TextView tvHikeLocation;
        private final TextView tvHikeLength;
        private final ImageButton btnOverFlowHike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHikeDate = itemView.findViewById(R.id.tvHikeDate);
            tvHikeName = itemView.findViewById(R.id.tvHikeName);
            tvHikeLocation = itemView.findViewById(R.id.tvHikeLocation);
            tvHikeLength = itemView.findViewById(R.id.tvHikeLength);
            btnOverFlowHike = itemView.findViewById(R.id.btnOverFlowHike);
        }

        public String limitString(String string, int maxLength) {
            if (string.length() > maxLength) {
                string = string.substring(0, maxLength) + "...";
            }
            return string;
        }

        @SuppressLint("SetTextI18n")
        public void bind(final Hike hike) {
            tvHikeDate.setText(hike.getDate());
            tvHikeName.setText(limitString(hike.getName(), 15));
            tvHikeLocation.setText(limitString(hike.getLocation(), 15));
            tvHikeLength.setText(hike.getLength().toString() + " km");

            itemView.setOnClickListener(view -> {
                if (customListeners != null) {
                    customListeners.onItemClick(hike);
                }
            });

            btnOverFlowHike.setOnClickListener(view -> showPopupMenu(view, hike));
        }

        @SuppressLint("RestrictedApi")
        private void showPopupMenu(View view, Hike hike) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.item_overflow, popupMenu.getMenu());

            if (popupMenu.getMenu() instanceof MenuBuilder) {
                MenuBuilder m = (MenuBuilder) popupMenu.getMenu();
                m.setOptionalIconsVisible(true);
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_overflow_edit) {
                    customListeners.editItem(hike);
                    return true;
                } else if (itemId == R.id.action_overflow_delete) {
                    customListeners.deleteItem(hike);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        }
    }

    public void setSearchCriteria(String criteria) {
        this.searchCriteria = criteria;
        getFilter().filter("");
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Hike> filteredResults = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredResults.addAll(lstHike);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Hike item : lstHike) {
                        if (searchCriteria.equals("Name") && item.getName().toLowerCase().contains(filterPattern)) {
                            filteredResults.add(item);
                        } else if (searchCriteria.equals("Location") && item.getLocation().toLowerCase().contains(filterPattern)) {
                            filteredResults.add(item);
                        } else if (searchCriteria.equals("Length") && item.getLength().toString().contains(filterPattern)) {
                            filteredResults.add(item);
                        } else if (searchCriteria.equals("Date") && item.getDate().toLowerCase().contains(filterPattern)) {
                            filteredResults.add(item);
                        } else if (searchCriteria.equals("Filter")) {
                            String[] filter = filterPattern.split(",");
                            String name = filter[0].equals("empty") ? "" : filter[0].trim();
                            String location = filter[1].equals("empty") ? "" : filter[1].trim();
                            String length = filter[2].equals("empty") ? "" : filter[2].trim();
                            String date = filter[3].equals("empty") ? "" : filter[3].trim();

                            if (item.getName().toLowerCase().contains(name.toLowerCase()) &&
                                    item.getLocation().toLowerCase().contains(location.toLowerCase()) &&
                                    item.getLength().toString().contains(length) &&
                                    item.getDate().toLowerCase().contains(date.toLowerCase())) {
                                filteredResults.add(item);
                            }
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
                filteredHikeList.clear();
                filteredHikeList.addAll((List<Hike>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItem(Hike newItem) {
        filteredHikeList.add(0, newItem);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateItem(Hike updatedItem) {
        for (Hike hike : filteredHikeList) {
            if (hike.getId() == updatedItem.getId()) {
                filteredHikeList.set(filteredHikeList.indexOf(hike), updatedItem);
                notifyDataSetChanged();
                break;
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteItem(Hike deletedItem) {
        for (Hike hike : filteredHikeList) {
            if (hike.getId() == deletedItem.getId()) {
                filteredHikeList.remove(hike);
                notifyDataSetChanged();
                break;
            }
        }
    }
}
