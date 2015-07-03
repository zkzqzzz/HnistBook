package cn.hnist.lib.android.hnistbook.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import cn.hnist.lib.android.hnistbook.R;
import cn.hnist.lib.android.hnistbook.api.Api;
import cn.hnist.lib.android.hnistbook.bean.Book;
import cn.hnist.lib.android.hnistbook.bean.Constant;
import cn.hnist.lib.android.hnistbook.bean.JSONRequest;
import cn.hnist.lib.android.hnistbook.ui.widget.SlidingActivity;
import cn.hnist.lib.android.hnistbook.util.BlurUtils;
import cn.hnist.lib.android.hnistbook.util.NetWorkUtils;

/**
 * Created by lujun on 2015/3/18.
 */
public class BookDetailActivity extends SlidingActivity {

    private Toolbar mToolBar;
    private ImageView ivBookImg;
    private TextView tvBookTitle, tvBookAuthor, tvBookPublisher, tvBookPubdate, tvBookPages,
            tvBookPrice, tvBookIsbn, tvBookSummary, tvBookTags;
    private LinearLayout llProgressBar, llContent;
    private Bundle mBundle;
    private RequestQueue mQueue;
    private SwipeRefreshLayout srlBookDetail;
    private String isbn = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_view);
        mToolBar = (Toolbar) findViewById(R.id.book_detail_toolbar);
        ivBookImg = (ImageView) findViewById(R.id.iv_bda_book_img);
        tvBookTitle = (TextView) findViewById(R.id.tv_bda_book_title);
        tvBookAuthor = (TextView) findViewById(R.id.tv_bda_book_author);
        tvBookPublisher = (TextView) findViewById(R.id.tv_bda_book_publisher);
        tvBookPubdate = (TextView) findViewById(R.id.tv_bda_book_pubdate);
        tvBookPages = (TextView) findViewById(R.id.tv_bda_book_pages);
        tvBookPrice = (TextView) findViewById(R.id.tv_bda_book_price);
        tvBookIsbn = (TextView) findViewById(R.id.tv_bda_book_isbn);
        tvBookSummary = (TextView) findViewById(R.id.tv_bda_book_summary);
        tvBookTags = (TextView) findViewById(R.id.tv_bda_book_tags);
        llContent = (LinearLayout) findViewById(R.id.ll_bda_content);
        llProgressBar = (LinearLayout) findViewById(R.id.ll_progressBar_bda_view);
        srlBookDetail =(SwipeRefreshLayout) findViewById(R.id.srl_bookdetail);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        srlBookDetail.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (TextUtils.isEmpty(isbn)){
                    Toast.makeText(BookDetailActivity.this,
                            getResources().getString(R.string.msg_intent_extras_null),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                searchBook(isbn);
            }
        });
        if ((mBundle = getIntent().getExtras()) != null){
            String title = mBundle.getString(Constant.BOOK.title.toString());
            String isbn10 = mBundle.getString(Constant.BOOK.isbn10.toString());
            String isbn13 = mBundle.getString(Constant.BOOK.isbn13.toString());
            setTitle((title == null || title.equals("")) ? "" : "《" + title + "》");
            if (isbn13 == null || isbn13.equals("")){
                if (isbn10 == null || isbn10.equals("")){
                    Toast.makeText(this, getResources().getString(R.string.msg_intent_extras_null),
                            Toast.LENGTH_SHORT).show();
                }else {
                    isbn = isbn10;
                    searchBook(isbn10);
                }
            }else {
                isbn = isbn13;
                searchBook(isbn13);
            }
        }else{
            Toast.makeText(this, getResources().getString(R.string.msg_intent_extras_null) + getIntent().getExtras(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void searchBook(String isbn){
        if (NetWorkUtils.getNetWorkType(this) == NetWorkUtils.NETWORK_TYPE_DISCONNECT){
            Toast .makeText(this, getResources().getString(R.string.msg_no_internet),
                    Toast.LENGTH_SHORT).show();
            onLoadComplete();
            return;
        }
        mQueue = Volley.newRequestQueue(this);
        JSONRequest<Book> jsonRequest = new JSONRequest<Book>(
                Api.GET_ISBNBOOK_URL + isbn,
                Book.class,
                new Response.Listener<Book>() {
                    @Override
                    public void onResponse(Book book) {
                        setData(book);
                        onLoadComplete();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast .makeText(BookDetailActivity.this, volleyError.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        onLoadComplete();
                        /*Toast .makeText(getActivity(),
                                    getResources().getString(R.string.msg_find_error),
                                    Toast.LENGTH_SHORT).show();*/
                    }
                });
        mQueue.add(jsonRequest);
    }

    private void setData(Book book){
        if (book == null){
            Toast .makeText(BookDetailActivity.this, getResources().getString(R.string.msg_no_find),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isEmpty(book.getImages().getMedium())){
            Glide.with(this).load(book.getImages().getMedium()).into(ivBookImg);
            /*ImageLoader.getInstance().loadImage(book.getImages().getLarge(),
                    new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            BlurUtils.blur(loadedImage, ivBookImg, 1.5f,1.1f);
                        }
                    });*/
        }
        if (TextUtils.isEmpty(getTitle())){ setTitle(book.getTitle()); }
        tvBookTitle.setText(book.getTitle());
        String author = "";
        for (int j = 0; j < book.getAuthor().length; j++){
            author += book.getAuthor()[j] + "、";
        }
        if (author.length() > 0){ author = author.substring(0, author.length() - 1); }
        tvBookAuthor.setText(author);
        tvBookPublisher.setText(book.getPublisher());
        tvBookPubdate.setText(book.getPubdate());
        tvBookPages.setText(book.getPages() + getString(R.string.tv_unit_page));
        tvBookPrice.setText(book.getPrice());
        tvBookIsbn.setText(TextUtils.isEmpty(book.getIsbn13()) ? book.getIsbn10() : book.getIsbn13());
        String tags = "";
        for (Book.Tag tag : book.getTags()){
            tags += tag.getName() + "、";
        }
        if (tags.length() > 0){ tags = tags.substring(0, tags.length() - 1); }
        tvBookTags.setText(tags);
        tvBookSummary.setText(book.getSummary());
    }

    private void onLoadComplete(){
        llProgressBar.setVisibility(View.GONE);
        llContent.setVisibility(View.VISIBLE);
        srlBookDetail.setRefreshing(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
