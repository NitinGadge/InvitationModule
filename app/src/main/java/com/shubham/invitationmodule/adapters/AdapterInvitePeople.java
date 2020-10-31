package com.shubham.invitationmodule.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shubham.invitationmodule.R;
import com.shubham.invitationmodule.model.ModelInvitePeople;
import com.shubham.invitationmodule.utils.InvitePeopleListener;

import java.util.ArrayList;

public class AdapterInvitePeople extends RecyclerView.Adapter<AdapterInvitePeople.ViewHolder> {

    private Context context;
    private ArrayList<ModelInvitePeople> modelInvitePeople;

    public AdapterInvitePeople(ArrayList<ModelInvitePeople> modelInvitePeople, Context context) {
        this.context = context;
        this.modelInvitePeople = modelInvitePeople;
    }

    public void setPeoples(ArrayList<ModelInvitePeople> modelInvitePeople) {
        this.modelInvitePeople = new ArrayList<>();
        this.modelInvitePeople = modelInvitePeople;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_invite_people, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(modelInvitePeople.get(position));
    }

    @Override
    public int getItemCount() {
        return modelInvitePeople.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        ImageView ivCheckSelected;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linearLayout);
            ivCheckSelected = itemView.findViewById(R.id.ivCheckSelected);
            tvName = itemView.findViewById(R.id.tvName);
        }

        public void bind(final ModelInvitePeople modelInvitePeople) {
            ivCheckSelected.setVisibility(modelInvitePeople.isChecked() ? View.VISIBLE : View.GONE);
            tvName.setText(modelInvitePeople.getName());

            itemView.setOnClickListener(view -> {
                  modelInvitePeople.setChecked(!modelInvitePeople.isChecked());
                   ivCheckSelected.setVisibility(modelInvitePeople.isChecked() ? View.VISIBLE : View.GONE);
            });
        }
    }

    public ArrayList<ModelInvitePeople> getAll() {
        return modelInvitePeople;
    }

    public ArrayList<ModelInvitePeople> getSelected() {
        ArrayList<ModelInvitePeople> selected = new ArrayList<>();
        for (int i = 0; i < modelInvitePeople.size(); i++) {
            if (modelInvitePeople.get(i).isChecked()) {
                selected.add(modelInvitePeople.get(i));
            }
        }
        return selected;
    }
}
