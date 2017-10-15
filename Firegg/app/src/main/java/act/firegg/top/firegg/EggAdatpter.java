package act.firegg.top.firegg;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by zhou on 2017/10/12.
 */

public class EggAdatpter extends RecyclerView.Adapter<EggAdatpter.ViewHolder> {
    private List<EggElemet> eggElemets;
    private Context mContext;
    private int screenWidth = 0;
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
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
            Point p = new Point();
            wm.getDefaultDisplay().getSize(p);
            screenWidth = p.x;

        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.egg_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EggElemet egg = eggElemets.get(position);
        ViewGroup.LayoutParams layout = holder.eggImage.getLayoutParams();
        layout.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layout.width = screenWidth;
        holder.eggImage.setLayoutParams(layout);
        holder.eggImage.setMaxHeight(screenWidth*100);
        holder.eggImage.setMaxWidth(screenWidth);
        if (egg.getThumb().get(0).endsWith("gif")) {
            Glide.with(mContext).load(egg.getThumb().get(0)).asGif().into(holder.eggImage);
        }else {
            Glide.with(mContext).load(egg.getThumb().get(0)).into(holder.eggImage);
        }
    }

    @Override
    public int getItemCount() {
        return eggElemets.size();
    }
}
