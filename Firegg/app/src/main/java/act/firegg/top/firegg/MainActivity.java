package act.firegg.top.firegg;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private ArrayList<EggElemet> eggList= new ArrayList<EggElemet>();
    private EggAdatpter adatpter = new EggAdatpter(eggList);
    private SwipeRefreshLayout swipeRefreshLayout;
    private int page= 0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.eggs_recycle_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adatpter);
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.rfr);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                MainActivity.this.loadEggs(0,10);
            }
        });
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                MainActivity.this.loadEggs(MainActivity.this.page,10);
            }
        });
        this.loadEggs(0,10);
    }

    private void loadEggs(final int page , int size) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://www.firegg.top:8099/api/ox?page="+page+"&size="+size)
                .build();
        try {
                client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (page == 0 ){
                        MainActivity.this.eggList.clear();
                    }
                    String responseData = response.body().string();
                    parseJSON(responseData);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseJSON(String str) {
        try {
            JSONObject jsonobj = new JSONObject(str);
            if (jsonobj.getInt("code") == 200) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<EggElemet>>() {
                }.getType();
                List<EggElemet> list = gson.fromJson(jsonobj.getJSONArray("data").toString(), type);
                eggList.addAll(list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adatpter.notifyDataSetChanged();
                        MainActivity.this.swipeRefreshLayout.setRefreshing(false);
                    }
                });
                ++page;
            }
        }catch (JSONException e) {
            e.printStackTrace();

        }

    }
}
