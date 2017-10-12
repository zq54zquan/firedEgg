package act.firegg.top.firegg;

import android.support.v4.app.INotificationSideChannel;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by zhou on 2017/10/12.
 */

public class EggElemet {
    private ArrayList<String> thumb;
    private ArrayList<String> hd;

    public ArrayList<String> getThumb() {
        return thumb;
    }

    public void setThumb(ArrayList<String> thumb) {
        this.thumb = thumb;
    }

    public ArrayList<String> getHd() {
        return hd;
    }

    public void setHd(ArrayList<String> hd) {
        this.hd = hd;
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    public int getCtime() {
        return ctime;
    }

    public void setCtime(int ctime) {
        this.ctime = ctime;
    }

    public ArrayList<Integer> getThumb_width() {
        return thumb_width;
    }

    public void setThumb_width(ArrayList<Integer> thumb_width) {
        this.thumb_width = thumb_width;
    }

    public ArrayList<Integer> getThumb_height() {
        return thumb_height;
    }

    public void setThumb_height(ArrayList<Integer> thumb_height) {
        this.thumb_height = thumb_height;
    }

    public ArrayList<Integer> getHd_width() {
        return hd_width;
    }

    public void setHd_width(ArrayList<Integer> hd_width) {
        this.hd_width = hd_width;
    }

    public ArrayList<Integer> getHd_height() {
        return hd_height;
    }

    public void setHd_height(ArrayList<Integer> hd_height) {
        this.hd_height = hd_height;
    }

    private ArrayList<Integer> thumb_width;
    private ArrayList<Integer> thumb_height;
    private ArrayList<Integer> hd_width;
    private ArrayList<Integer> hd_height;
    private int support;
    private int ctime;

}
