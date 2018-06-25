package mc.nj.proj;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;

import java.util.List;

/**
 * Created by ಆದರ್ಶ್ NJ on 4/5/2017.
 */

public class cardAdapter extends RecyclerView.Adapter<cardAdapter.ViewHolder> {

    List<recentData> datum;
    Context context;

    public cardAdapter(List<recentData> datum, Context con) {
        this.datum = datum;
        this.context = con;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView time;
        TextView desc;
        ImageView act;

        public ViewHolder(View item) {
            super(item);
            cv = (CardView) item.findViewById(R.id.RecentList);
            act = (ImageView) item.findViewById(R.id.actType);
            time = (TextView) item.findViewById(R.id.Datetime);
            desc = (TextView) item.findViewById(R.id.actDesc);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_cards, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String it = datum.get(position).typeURL;
//        Drawable itDraw = holder.get
//                holder.act.setBackground();

        holder.time.setText(datum.get(position).DateTime);
        holder.desc.setText(datum.get(position).Activity);
        String url = datum.get(position).typeURL;
        switch (url) {
            case "food":
                holder.act.setImageResource(R.drawable.food);
                break;
            case "drink":
                holder.act.setImageResource(R.drawable.drink);
                break;
            case "activity":
                holder.act.setImageResource(R.drawable.a1);
                break;
            case "pollen":
                holder.act.setImageResource(R.drawable.allergy);
                break;
            case "cycle":
                holder.act.setImageResource(R.drawable.cycle);
                break;
            case "yoga":
                holder.act.setImageResource(R.drawable.yoga);
                break;
            case "gym":
                holder.act.setImageResource(R.drawable.gym);
                break;

            default:
                holder.act.setImageResource(R.drawable.healthy);
                break;
        }


//        holder.act.setImageDrawable();
    }

    @Override
    public int getItemCount() {
        return datum.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
