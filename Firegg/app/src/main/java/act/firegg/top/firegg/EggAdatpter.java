package act.firegg.top.firegg;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by zhou on 2017/10/12.
 */

public class EggAdatpter extends RecyclerView.Adapter<EggAdatpter.ViewHolder> {
    private List<EggElemet> eggElemets;
    private Context mContext;
    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView eggImage;
        public ViewHolder(View view) {
            super(view);
            cardView = (CardView)view;
            eggImage = (ImageView) view.findViewById(R.id.egg_img);
        }
    }

    public EggAdatpter(List<EggElemet> eggsList) {
        eggElemets = eggsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.egg_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EggElemet egg = eggElemets.get(position);
        Glide.with(mContext).load(egg.getThumb()).into(holder.eggImage);
    }

    @Override
    public int getItemCount() {
        return eggElemets.size();
    }
}
