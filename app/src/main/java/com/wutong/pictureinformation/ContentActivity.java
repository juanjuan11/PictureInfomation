package com.wutong.pictureinformation;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wutong.pictureinformation.entity.Article;
import com.wutong.pictureinformation.util.HttpUtil;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ContentActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvContentTitle,tvContentAuthor,tvContentInfo;
    private ImageView ivCollect;
    private Article article;
    private String responseData;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what==1){
                tvContentInfo.setText(Html.fromHtml(responseData));
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        initView();
        initData();
    }

    private void initData() {
        //获取intent的数据
        Log.d("nn","initData");
        article = getIntent().getParcelableExtra("article");
        //进行数据获取
        tvContentTitle.setText(article.getTitle());
        tvContentAuthor.setText(article.getAuthor());
        String url=article.getContent();
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                responseData=response.body().string();
//                tvContentAuthor.setText(Html.fromHtml(responseData));
                Log.d( "onResponse: ",responseData);
                if (responseData!=null){
                    Message message=new Message();
                    message.what=1;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void initView() {
        tvContentTitle=findViewById(R.id.tv_content_title);
        tvContentAuthor=findViewById(R.id.tv_content_author);
        tvContentInfo=findViewById(R.id.tv_content_info);
        ivCollect=findViewById(R.id.iv_img_collect);
        ivCollect.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_img_collect:
                Toast.makeText(this,"恭喜您，收藏成功",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
