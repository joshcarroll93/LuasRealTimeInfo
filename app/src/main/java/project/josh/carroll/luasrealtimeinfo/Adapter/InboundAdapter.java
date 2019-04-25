package project.josh.carroll.luasrealtimeinfo.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import java.util.List;

import project.josh.carroll.luasrealtimeinfo.Data.Tram;
import project.josh.carroll.luasrealtimeinfo.R;

public class InboundAdapter extends RecyclerView.Adapter<InboundAdapter.ViewHolder> {

    private List<Tram> trams;

    public InboundAdapter(List<Tram> trams){
        this.trams = trams;
    }

    public void updateTrams(List<Tram> updatedTrams){

        if(trams != null){
            trams.clear();
            trams.addAll(updatedTrams);
        }
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_real_time_item , parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.destination.setText(trams.get(i).getDestination());

        if(!trams.get(i).getDestination().equalsIgnoreCase("No trams forecast")){
            if(trams.get(i).getDueTime().equalsIgnoreCase("Due"))
                viewHolder.dueTime.setText(trams.get(i).getDueTime());
            else if(trams.get(i).getDueTime().equalsIgnoreCase("1"))
                viewHolder.dueTime.setText(trams.get(i).getDueTime().concat(" min"));
            else
                viewHolder.dueTime.setText(trams.get(i).getDueTime().concat(" mins"));
        }
    }

    @Override
    public int getItemCount() {
        return trams.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView destination;
        private TextView dueTime;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            destination = itemView.findViewById(R.id.text_view_destination);
            dueTime = itemView.findViewById(R.id.text_view_due_time);
        }
    }
}
