package org.marelias.contacts.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.underscore.U;

import java.util.ArrayList;
import java.util.List;

import org.marelias.contacts.R;

public class GroupsSelectionAdapter extends RecyclerView.Adapter<GroupsSelectionAdapter.GroupViewHolder> {
    
    private List<String> allGroups;
    private List<String> selectedGroups;
    private OnGroupSelectionListener listener;

    public interface OnGroupSelectionListener {
        void onGroupSelectionChanged(List<String> selectedGroups);
    }

    public GroupsSelectionAdapter(List<String> allGroups, List<String> selectedGroups, OnGroupSelectionListener listener) {
        this.allGroups = allGroups;
        this.selectedGroups = selectedGroups != null ? new ArrayList<>(selectedGroups) : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_checkbox, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        String groupName = allGroups.get(position);
        holder.groupName.setText(groupName);
        holder.checkBox.setChecked(selectedGroups.contains(groupName));
        
        holder.itemView.setOnClickListener(v -> {
            holder.checkBox.toggle();
            updateSelection(groupName, holder.checkBox.isChecked());
        });
        
        holder.checkBox.setOnClickListener(v -> {
            updateSelection(groupName, holder.checkBox.isChecked());
        });
    }

    private void updateSelection(String groupName, boolean isSelected) {
        if (isSelected && !selectedGroups.contains(groupName)) {
            selectedGroups.add(groupName);
        } else if (!isSelected && selectedGroups.contains(groupName)) {
            selectedGroups.remove(groupName);
        }
        
        if (listener != null) {
            listener.onGroupSelectionChanged(selectedGroups);
        }
    }

    @Override
    public int getItemCount() {
        return allGroups.size();
    }

    public List<String> getSelectedGroups() {
        return selectedGroups;
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView groupName;

        GroupViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.group_checkbox);
            groupName = itemView.findViewById(R.id.group_name);
        }
    }
} 