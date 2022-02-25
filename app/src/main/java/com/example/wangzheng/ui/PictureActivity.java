package com.example.wangzheng.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.wangzheng.R;
import com.example.wangzheng.album.Builder;
import com.example.wangzheng.common.BasicAdapter;
import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.common.CornersTransform;
import com.example.wangzheng.model.PictureEntity;
import com.example.wangzheng.uiHandleBridger.Callable;
import com.example.wangzheng.uiHandleBridger.ICallable;
import com.example.wangzheng.widget.SquareImageView;
import com.example.wangzheng.widget.zoom_imageview.ZoomImageView;
import com.wz.arouter.BindViews;
import com.wz.arouter.annotation.InjectView;
import com.wz.arouter.annotation.Route;

@Route("PictureActivity")
public class PictureActivity extends Activity {
    @InjectView(R.id.animImageView)
    public ZoomImageView animImageView;

    @InjectView(R.id.imageView)
    public ImageView imageView;
    @InjectView(R.id.gridView)
    public GridView gridView;

    private GestureDetector mDetector;
    private int[] drawableIds = {R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4
            , R.drawable.img5, R.drawable.img8, R.drawable.img9, R.drawable.img6, R.drawable.img7};

    private ICallable<Integer, String> mCallable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_picture_layout);
        BindViews.inject(this);
        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                distanceX = e2.getX() - e1.getX();
                distanceY = e2.getY() - e1.getY();
                imageView.offsetLeftAndRight((int) distanceX);
                imageView.offsetTopAndBottom((int) distanceY);
                return true;
            }
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Builder.newBuilder()
                        .addDrawableId(getResources(), R.drawable.timg)
                        .rect(CommonKit.getRectOnScreen(imageView))
                        .fullScreen(true)
                        .build(PictureActivity.this);
                return true;
            }
        });
        imageView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return true;
            }
        });

        Glide.with(this)
                .load(R.drawable.timg)
                .into(imageView);

        gridView.setAdapter(new BasicAdapter<PictureEntity>(this) {
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);

            {
                for (int id : drawableIds) {
                    PictureEntity picture = new PictureEntity(id);
                    picture.aspectRatio = PictureEntity.aspectRatio(getResources(), id);
                    mList.add(picture);
                }
            }

            public View getView(final int position, View convertView, ViewGroup parent) {
                final SquareImageView imageView = new SquareImageView(mContext);
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                final PictureEntity data = getItem(position);
                Glide.with(mContext)
                        .load(data.drawableId)
                        .transform(new CenterCrop(mContext), new CornersTransform(mContext, 8))
                        .into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        callRemoteHandle(position);
                        Builder.newBuilder()
                                .pictures(getList())
                                .rect(CommonKit.getRectOnScreen(imageView))
                                .index(position)
                                .fullScreen(true)
                                .numColumns(gridView.getNumColumns())
                                .hSpacing(gridView.getHorizontalSpacing())
                                .vSpacing(gridView.getVerticalSpacing())
                                .build(PictureActivity.this);
                    }
                });
                return imageView;
            }
        });


        Intent intent = getIntent();
        mCallable = Callable.with(intent);
    }

    private void callRemoteHandle(int index) {
        mCallable.call(index);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
