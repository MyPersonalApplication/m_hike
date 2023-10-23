package com.example.m_hike.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m_hike.R;
import com.example.m_hike.model.User;

import java.util.List;

public class LoginAdapter extends RecyclerView.Adapter<LoginAdapter.ViewHolder> {
    public interface CustomListeners {
        void onItemClick(User user);
        void editItem(User user);
        void deleteItem(User user);
    }

    private final Context context;
    private final int layout;
    private final List<User> lstUser;
    private CustomListeners customListeners;

    public LoginAdapter(Context context, int layout, List<User> lstPeople) {
        this.context = context;
        this.layout = layout;
        this.lstUser = lstPeople;
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
        User user = lstUser.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return lstUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgViewAvatar;
        private final TextView tvUsername;
        private final TextView tvFullName;
        private final TextView tvCreated;
        private final ImageButton btnOverFlowUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvCreated = itemView.findViewById(R.id.tvCreated);
            btnOverFlowUser = itemView.findViewById(R.id.btnOverFlowUser);
        }

        public void bind(final User user) {
            byte[] imgAvatar = user.getAvatar();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgAvatar, 0, imgAvatar.length);
            imgViewAvatar.setImageBitmap(bitmap);

            tvUsername.setText(user.getUsername());
            tvFullName.setText(user.getFullName());
            tvCreated.setText(user.getCreated());

            itemView.setOnClickListener(view -> {
                if (customListeners != null) {
                    customListeners.onItemClick(user);
                }
            });

            btnOverFlowUser.setOnClickListener(view -> showPopupMenu(view, user));
        }

        @SuppressLint("RestrictedApi")
        private void showPopupMenu(View view, User user) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.item_overflow, popupMenu.getMenu());

            if (popupMenu.getMenu() instanceof MenuBuilder) {
                MenuBuilder m = (MenuBuilder) popupMenu.getMenu();
                m.setOptionalIconsVisible(true);
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_overflow_edit) {
                    customListeners.editItem(user);
                    return true;
                } else if (itemId == R.id.action_overflow_delete) {
                    customListeners.deleteItem(user);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        }
    }
}
