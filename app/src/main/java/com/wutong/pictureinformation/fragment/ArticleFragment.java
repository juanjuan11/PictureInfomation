package com.wutong.pictureinformation.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.wutong.pictureinformation.ContentActivity;
import com.wutong.pictureinformation.R;
import com.wutong.pictureinformation.adapter.ArticleListViewAdapter;
import com.wutong.pictureinformation.entity.Article;
import com.wutong.pictureinformation.util.HttpUtil;
import com.wutong.pictureinformation.util.ParseJsonWithJsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ArticleFragment extends Fragment{

    private ListView listView;
    private View view;
    private ArticleListViewAdapter articleListViewAdapter;
    private List<Article> articleList=new ArrayList<>();
    private String url="https://gank.io/api/v2/data/category/GanHuo/type/Android/page/1/count/10";

    private  final static int GET_SUCCESS=1;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what==1){
                articleListViewAdapter.notifyDataSetChanged();
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_article,container,false);//加载布局
        initListView();
        return view;
    }

    private void initListView() {
        queryFromServer(url);
        listView=view.findViewById(R.id.listView);
        articleListViewAdapter=new ArticleListViewAdapter(getContext(),R.layout.article_list_item,articleList);
        listView.setAdapter(articleListViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent=new Intent();
                intent.putExtra("article", articleList.get(position));
//                Toast.makeText(getContext(),"您点击了"+articleList.get(position),Toast.LENGTH_SHORT).show();
                intent.setClass(getContext(),ContentActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }



    private void queryFromServer(String url){//结果回调在Callback()中
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData=response.body().string();
                articleList.clear();
                List<Article> articles=new ArrayList<>();
                articles.clear();
                articles=ParseJsonWithJsonObject.getArticles(responseData);//将服务器获取的数据解析
                if (articles!=null){
                    articleList.addAll(articles);
                    handler.sendEmptyMessage(GET_SUCCESS);
                }
            }
        });
    }
}
