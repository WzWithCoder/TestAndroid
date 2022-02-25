/*
    The Android Not Open Source Project
    Copyright (c) 2014-9-2 wangzheng <iswangzheng@gmail.com>

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    @author wangzheng  DateTime 2014-9-2
 */

package com.example.wangzheng.widget;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wangzheng.R;
import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.widget.arrow.ArrowView;
import com.example.wangzheng.widget.round.RoundLinearLayout;

import static android.widget.RelativeLayout.ALIGN_PARENT_LEFT;
import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;
import static android.widget.RelativeLayout.ALIGN_PARENT_TOP;
import static android.widget.RelativeLayout.BELOW;


//文案提示
public class SimpleTextTip extends FrameLayout {
    private ArrowView   mArrowView;
    private RoundLinearLayout mCardView;
    private TextView    mTipView;

    private PopupWindow mPopupWindow = null;
    private View        mAnchor      = null;

    public SimpleTextTip(@NonNull Context context) {
        this(context, null);
    }

    public SimpleTextTip(@NonNull Context context,
                         @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleTextTip(@NonNull Context context,
                         @Nullable AttributeSet attrs,
                         int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.simple_text_tip_layout, this);

        mArrowView = findViewById(R.id.arrowView);
        mCardView = findViewById(R.id.cardView);
        mTipView = findViewById(R.id.tipView);
    }

    public static void show(View anchorView, CharSequence text) {
        SimpleTextTip textTip = new SimpleTextTip(anchorView.getContext());
        textTip.apply(anchorView, text);
    }


    private void apply(View anchor, CharSequence text) {
        mAnchor = anchor;
        mTipView.setText(text);
        makeChildMeasure();
        inflatePopupWindow();
        showPopupWindow(anchor);
    }

    private void showPopupWindow(View anchor) {
        if (mArrowView.getOrientation() == ArrowView.About.UP) {
            mPopupWindow.showAsDropDown(anchor);
        } else {
            registerForScrollChanged(anchor);
            mPopupWindow.showAtLocation(anchor,
                    Gravity.NO_GRAVITY, 0, 0);
            post(()-> updateWindowLocation());
        }
    }

    private void updateWindowLocation() {
        int[] location = new int[2];
        mAnchor.getLocationOnScreen(location);
        mPopupWindow.update(0, location[1] - getHeight(),
                mPopupWindow.getWidth(),
                mPopupWindow.getHeight());
    }

    private void makeChildMeasure() {
        //屏幕大小
        Point screenSize = new Point();
        CommonKit.getScreenSize(screenSize);

        //获取锚点在屏幕中的位置，包括状态栏
        int[] anchorSize = new int[2];
        mAnchor.getLocationOnScreen(anchorSize);
        //锚点大小
        int anchorWidth = mAnchor.getWidth();
        int anchorHeight = mAnchor.getHeight();
        //箭头方向
        mArrowView.setOrientation(screenSize.y / 2 > anchorSize[1] + anchorHeight / 2
                ? ArrowView.About.UP : ArrowView.About.DOWN);
        //箭头大小
        ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams)
                        mArrowView.getLayoutParams();
        int arrowWidth = layoutParams.width;

        //提示内容对齐方式
        RelativeLayout.LayoutParams cardParams =
                (RelativeLayout.LayoutParams) mCardView.getLayoutParams();
        if (screenSize.x / 2 < anchorSize[0] + anchorWidth / 2) {//锚点位于屏幕右边
            //提示内容右对齐
            cardParams.addRule(ALIGN_PARENT_RIGHT);
        } else { //提示内容左对齐
            cardParams.addRule(ALIGN_PARENT_LEFT);
        }

        //布局定位
        RelativeLayout.LayoutParams arrowParams =
                (RelativeLayout.LayoutParams) mArrowView.getLayoutParams();
        arrowParams.leftMargin = anchorSize[0] + (anchorWidth - arrowWidth) / 2;
        if (mArrowView.getOrientation() == ArrowView.About.DOWN) {//内容位于箭头上面
            cardParams.addRule(ALIGN_PARENT_TOP);
            arrowParams.addRule(BELOW, R.id.cardView);
        } else {//内容位于箭头下面
            arrowParams.addRule(ALIGN_PARENT_TOP);
            cardParams.addRule(BELOW, R.id.arrowView);
        }
    }


    private final ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener =
            new ViewTreeObserver.OnScrollChangedListener() {
                public void onScrollChanged() {
                    updateWindowLocation();
                }
            };

    private void unregisterForScrollChanged() {
        ViewTreeObserver vto = mAnchor.getViewTreeObserver();
        vto.removeOnScrollChangedListener(mOnScrollChangedListener);
    }

    private void registerForScrollChanged(View anchor) {
        unregisterForScrollChanged();
        ViewTreeObserver vto = anchor.getViewTreeObserver();
        if (vto != null) {
            vto.addOnScrollChangedListener(mOnScrollChangedListener);
        }
    }

    private void inflatePopupWindow() {
        mPopupWindow = new PopupWindow(
                this,
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setTouchInterceptor(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP ||
                        event.getAction() == MotionEvent.ACTION_CANCEL) {
                    mPopupWindow.dismiss();
                }
                return false;
            }
        });
    }
}


