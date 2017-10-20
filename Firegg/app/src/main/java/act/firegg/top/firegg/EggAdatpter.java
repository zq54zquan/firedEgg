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
import android.widget.LinearLayout;

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
        LinearLayout eggContainer;
        public ViewHolder(View view) {
            super(view);
            eggContainer = (LinearLayout) view.findViewById(R.id.image_container);
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

        int count = holder.eggContainer.getChildCount();
        for (int i = 0; i < count ; ++i) {
            View v = holder.eggContainer.getChildAt(i);
            if (v instanceof ImageView) {
                holder.eggContainer.removeView(v);
            }
        }

        count = egg.getThumb().size();
        int allheight = 0;
        for (int i  = 0 ; i < count ; ++i) {
            String thumb = egg.getThumb().get(i);
            int w = egg.getThumb_width().get(i);
            int h = egg.getThumb_height().get(i);
            ImageView imageView = new ImageView(mContext);
            int width = screenWidth;
            int height = (int) ((float) screenWidth/w * h);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width,height);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.eggContainer.addView(imageView);
            if (thumb.endsWith("gif")) {
                Glide.with(mContext).load(thumb).asGif().into(imageView);
            } else {
                Glide.with(mContext).load(thumb).into(imageView);
            }
            allheight += height;
        }
        ViewGroup.LayoutParams params = holder.eggContainer.getLayoutParams();
        params.height = allheight;
        holder.eggContainer.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return eggElemets.size();
    }
}
