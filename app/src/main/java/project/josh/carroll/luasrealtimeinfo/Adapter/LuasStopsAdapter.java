package project.josh.carroll.luasrealtimeinfo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import java.util.ArrayList;

import project.josh.carroll.luasrealtimeinfo.Activity.RealTimeInfo;
import project.josh.carroll.luasrealtimeinfo.Data.LuasStop;
import project.josh.carroll.luasrealtimeinfo.R;

public class LuasStopsAdapter extends RecyclerView.Adapter<LuasStopsAdapter.ViewHolder> {

    private String TAG = getClass().getSimpleName();
    private static ArrayList<ArrayList<LuasStop>> luasLines;
    private ArrayList<LuasStop> currentChoice;
    private Context context;
    private int line;


    public LuasStopsAdapter(Context context, ArrayList<LuasStop> stops){
        currentChoice = stops;
        luasLines = new ArrayList<>();
        this.context = context;
    }

    public void setLuasLine(int type) {

        if(currentChoice != null){

            line = type;

            currentChoice.clear();

            if(type == 0){
                currentChoice.addAll(luasLines.get(0));
            }
            else if( type == 1){
                currentChoice.addAll(luasLines.get(1));
            }

            for (int i = 0; i < currentChoice.size(); i++){
                Log.d(TAG, currentChoice.get(i).getPronunciation());
            }

            notifyDataSetChanged();
        }
    }

    public void setLists(ArrayList<ArrayList<LuasStop>> stops){

        luasLines.add(stops.get(0));
        luasLines.add(stops.get(1));

        notifyDataSetChanged();
    }

    public static ArrayList<ArrayList<LuasStop>> getLuasLines(){
        return luasLines;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_luas_stop_item , parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final int position = i;
         viewHolder.stopName.setText(currentChoice.get(i).getPronunciation());

         switch (line){

             case 0:
                 viewHolder.view.setBackgroundColor(context.getResources().getColor(R.color.red));
                 break;

             case 1:
                 viewHolder.view.setBackgroundColor(context.getResources().getColor(R.color.green));
                 break;
         }
         viewHolder.stopName.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 Intent intent = new Intent(context, RealTimeInfo.class);
                 intent.putExtra("stop", currentChoice.get(position).getAbbreviation());
                 intent.putExtra("stop name", currentChoice.get(position).getPronunciation());
                 intent.putExtra("latitude", currentChoice.get(position).getLatitude());
                 intent.putExtra("longitude", currentChoice.get(position).getLongitude());
                 context.startActivity(intent);
             }
         });
    }

    @Override
    public int getItemCount() {
        return currentChoice.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

         private TextView stopName;
         private View view;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            stopName = itemView.findViewById(R.id.text_view_stop_name);
            view = itemView.findViewById(R.id.luas_stop_view);
        }
    }
}
