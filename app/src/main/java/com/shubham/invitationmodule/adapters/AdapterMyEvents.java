package com.shubham.invitationmodule.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shubham.invitationmodule.R;
import com.shubham.invitationmodule.model.ModelMyEvents;
import com.shubham.invitationmodule.network.Api;

import java.util.ArrayList;
import java.util.List;

public class AdapterMyEvents extends RecyclerView.Adapter<AdapterMyEvents.ViewHolder> implements Filterable {

    private List<ModelMyEvents> modelMyEventsList;
    private List<ModelMyEvents> modelMyEventsListFiltered;
    private Context context;

    public AdapterMyEvents(List<ModelMyEvents> modelMyEventsList, Context context) {
        this.modelMyEventsList = modelMyEventsList;
        this.context = context;
        modelMyEventsListFiltered = new ArrayList<>(modelMyEventsList);
    }

    @NonNull
    @Override
    public AdapterMyEvents.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_events, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterMyEvents.ViewHolder holder, int position) {

        final ModelMyEvents modelMyEvents = modelMyEventsList.get(position);

        Glide.with(context).load(Api.serverAddress + modelMyEvents.getEventPoster()).into(holder.ivEventPoster);

        holder.tvEventTitle.setText(modelMyEvents.getEventTitle());
        holder.eventDate.setText(modelMyEvents.getEventDate());
        holder.eventMonth.setText(modelMyEvents.getEventDate());
        holder.eventTimeAndPlace.setText(modelMyEvents.getEventTime() + "\n" + modelMyEvents.getEventAddress());
        holder.attending.setText(modelMyEvents.getAttending());
        holder.notAttending.setText(modelMyEvents.getNotAttending());
        holder.maybe.setText(modelMyEvents.getMaybe());

        String date = modelMyEvents.getEventDate();
        String[] separated = date.split("/");
        holder.eventDate.setText(separated[0]);
        holder.eventMonth.setText(separated[1]);

        if (holder.eventMonth.getText().equals("1")) {
            holder.eventMonth.setText(R.string.january);
        }
        if (holder.eventMonth.getText().equals("2")) {
            holder.eventMonth.setText(R.string.february);
        }
        if (holder.eventMonth.getText().equals("3")) {
            holder.eventMonth.setText(R.string.march);
        }
        if (holder.eventMonth.getText().equals("4")) {
            holder.eventMonth.setText(R.string.april);
        }
        if (holder.eventMonth.getText().equals("5")) {
            holder.eventMonth.setText(R.string.may);
        }
        if (holder.eventMonth.getText().equals("6")) {
            holder.eventMonth.setText(R.string.june);
        }
        if (holder.eventMonth.getText().equals("7")) {
            holder.eventMonth.setText(R.string.july);
        }
        if (holder.eventMonth.getText().equals("8")) {
            holder.eventMonth.setText(R.string.august);
        }
        if (holder.eventMonth.getText().equals("9")) {
            holder.eventMonth.setText(R.string.september);
        }
        if (holder.eventMonth.getText().equals("10")) {
            holder.eventMonth.setText(R.string.october);
        }
        if (holder.eventMonth.getText().equals("11")) {
            holder.eventMonth.setText(R.string.november);
        }
        if (holder.eventMonth.getText().equals("12")) {
            holder.eventMonth.setText(R.string.december);
        }
    }

    @Override
    public int getItemCount() {
        return modelMyEventsList.size();
    }

    @Override
    public Filter getFilter() {
        return eventFilter;
    }

    private Filter eventFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ModelMyEvents> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(modelMyEventsListFiltered);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ModelMyEvents item : modelMyEventsListFiltered) {
                    if (item.getEventTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            modelMyEventsList.clear();
            modelMyEventsList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView ivEventPoster;
        TextView tvEventTitle, eventDate, eventMonth, eventTimeAndPlace, attending, notAttending, maybe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivEventPoster = itemView.findViewById(R.id.ivEventPoster);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventMonth = itemView.findViewById(R.id.eventMonth);
            eventTimeAndPlace = itemView.findViewById(R.id.eventTimeAndPlace);
            attending = itemView.findViewById(R.id.attending);
            notAttending = itemView.findViewById(R.id.notAttending);
            maybe = itemView.findViewById(R.id.maybe);
        }
    }
}
