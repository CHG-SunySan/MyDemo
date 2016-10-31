package view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.TintTypedArray;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import tests.sunysan.com.headtoolbar.R;

/**
 * Created by SunySan on 2016/10/30.
 */
public class HeadToolBar extends Toolbar {
    public static int TOOLBAR_HEIGHT = 0;
    private PopupWindow popWind;

    private Context mContext;

    private TextView mTitleTextView;
    private TextView mSubtitleTextView;
    private LinearLayout rightLay;

    private CharSequence mTitleText;
    private CharSequence mSubtitleText;

    private int mTitleTextColor;
    private int mSubtitleTextColor;

    private int mTitleTextAppearance;
    private int mSubtitleTextAppearance;

    private@Nullable AttributeSet attrs;
    private int defStyleAttr;



    public HeadToolBar(Context context) {
        super(context);
        mContext = context;
        saveRawData(null, R.attr.toolbarStyle);
//        resolveAttribute(context, null,R.attr.toolbarStyle);
    }

    public HeadToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        saveRawData(attrs, R.attr.toolbarStyle);
//        resolveAttribute(context,attrs, R.attr.toolbarStyle);
        initView();
    }

    public HeadToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        saveRawData(attrs, R.attr.toolbarStyle);
//        resolveAttribute(context,attrs,defStyleAttr);
        initView();
    }

    /**
     * 重新设置标题的样式
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void reSetTitleStyle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                R.styleable.Toolbar, defStyleAttr, 0);
        final int titleTextAppearance = a.getResourceId(R.styleable.Toolbar_titleTextAppearance, 0);
        if (titleTextAppearance != 0) {
            setTitleTextAppearance(context, titleTextAppearance);
        }
        if (mTitleTextColor != 0) {
            setTitleTextColor(mTitleTextColor);
        }
        a.recycle();
        post(new Runnable() {
            @Override
            public void run() {
                if (getLayoutParams() instanceof LayoutParams) {
                    ((LayoutParams) getLayoutParams()).gravity = Gravity.CENTER;
                }
            }
        });
    }


    /**
     * 重新设置标题的样式
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void reSetSubTitleStyle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                R.styleable.Toolbar, defStyleAttr, 0);
        final int titleTextAppearance = a.getResourceId(R.styleable.Toolbar_subtitleTextAppearance, 0);
        if (titleTextAppearance != 0) {
            setSubtitleTextAppearance(context, titleTextAppearance);
        }
        if (mSubtitleTextColor != 0) {
            setTitleTextColor(mSubtitleTextColor);
        }
        a.recycle();
        post(new Runnable() {
            @Override
            public void run() {
                if (getLayoutParams() instanceof LayoutParams) {
                    ((LayoutParams) getLayoutParams()).gravity = Gravity.CENTER;
                }
            }
        });

    }

    private void initView() {
        //默认导航图标
//        setNavigationIcon(R.drawable.head_back_sub_line);
        //默认标题
//        setTitle(R.string.app_name);
        //默认标题颜色
        setTitleColor(R.color.toolbar_white);
        //默认背景颜色
//        setBgColor(R.color.theme);
        //先不要调用该方法
        reSetHeadToolBarHeight(52);

        setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "返回", Toast.LENGTH_LONG).show();
                scanForActivity(mContext).finish();
            }
        });

        post(new Runnable() {
            @Override
            public void run() {
                TOOLBAR_HEIGHT = getHeight();
            }
        });
    }


    /**
     * 设置右边图标或文字并实现监听
     * 【备注：text和icon 只能二选一，text为空时传“”，icon为空时传null】
     * 如果需要给按钮设置id的话，到时候再设置。
     *
     * @param text
     * @param icon
     * @param btnClick
     */
    public void setRight1(@Nullable Integer text, @Nullable Integer icon, View.OnClickListener btnClick) {
        rightLay = new LinearLayout(mContext);
        LinearLayout.LayoutParams paramLay = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT
        );
        paramLay.weight = 1;
        paramLay.setMargins(0, 0, 10, 0);
        rightLay.setLayoutParams(paramLay);
        rightLay.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        rightLay.setOrientation(LinearLayout.HORIZONTAL);

        initRight(text, icon, btnClick);
        addRightView(rightLay);

    }

    /**
     * 设置右边的第二个图标或文字
     *
     * @param text
     * @param icon
     * @param btnClick
     */
    public void setRight2(@Nullable Integer text, @Nullable Integer icon, View.OnClickListener btnClick) {
        initRight(text, icon, btnClick);
    }

    /**
     * 设置右边的第三个图标或文字
     *
     * @param text
     * @param icon
     * @param btnClick
     */
    public void setRight3(@Nullable Integer text, @Nullable Integer icon, View.OnClickListener btnClick) {
        initRight(text, icon, btnClick);
    }

    private void initRight(@Nullable Integer text, @Nullable Integer icon, View.OnClickListener btnClick) {
        ImageView imageView = null;
        TextView textView = null;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.RIGHT;
        params.setMargins(0, 0, 10, 0);

        if (icon == null && text != null) {
            textView = new TextView(mContext);
            textView.setSingleLine();
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            textView.setTextSize(16);
            textView.setLayoutParams(params);
            textView.setText(text);
            textView.setId(text);
            textView.setOnClickListener(btnClick);
        } else if (icon != null && text == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(params);
            imageView.setImageResource(icon);
            imageView.setId(icon);
            imageView.setOnClickListener(btnClick);
        }

        if (rightLay != null) {
            if (textView != null)
                rightLay.addView(textView);
            if (imageView != null)
                rightLay.addView(imageView);
        }

    }


    //设置toolbar背景颜色
    public void setBgColor(int color) {
        super.setBackgroundColor(getResources().getColor(color));
    }

    //设置标题的字体颜色
    public void setTitleColor(int color) {
        mTitleTextColor = color;
        setTitleTextColor(getResources().getColor(color));
    }

    //设置副标题的字体颜色
    public void setSubtitleTitleColor(int color) {
        mSubtitleTextColor = color;
        setSubtitleTextColor(getResources().getColor(color));
    }


    @Override
    public CharSequence getTitle() {
        return mTitleText;
    }

    /**
     * 利用反射来设置返回建的大小
     *
     * @param height
     */
    public void reSetHeadToolBarHeight(int height) {
        try {
            Field f = Toolbar.class.getDeclaredField("mNavButtonView");
            f.setAccessible(true);
            ImageButton mNavButtonView = (ImageButton) f.get(this);
            if (mNavButtonView != null) {
                Toolbar.LayoutParams params = (LayoutParams) mNavButtonView.getLayoutParams();
                params.gravity = Gravity.CENTER_VERTICAL;
                params.height += dip2px(height);
                params.width += dip2px(height);
                mNavButtonView.setLayoutParams(params);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置中间标题
     * 参考 http://www.jianshu.com/p/621225a55561
     * @param title
     */
    public void setCenterTitle(CharSequence title) {
        reSetTitleStyle(mContext, attrs, defStyleAttr);
        setTitle("");
        if (!TextUtils.isEmpty(title)) {
            if (mTitleTextView == null) {
                final Context context = getContext();
                mTitleTextView = new TextView(context);
                mTitleTextView.setSingleLine();
                mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                if (mTitleTextAppearance != 0) {
                    mTitleTextView.setTextAppearance(context, mTitleTextAppearance);
                }
                if (mTitleTextColor != 0) {
                    mTitleTextView.setTextColor(mTitleTextColor);
                }
            }
            if (mTitleTextView.getParent() != this) {
                addCenterView(mTitleTextView);
            }
        } else if (mTitleTextView != null && mTitleTextView.getParent() == this) {// 当title为空时，remove
            removeView(mTitleTextView);
        }
        if (mTitleTextView != null) {
            mTitleTextView.setText(title);
        }
        mTitleText = title;
    }

    /**
     * 设置中间标题 (目前还不能设置在中间)
     * @param subtitle
     */
    public void setCenterSubtitle(CharSequence subtitle) {
        reSetSubTitleStyle(mContext, attrs, defStyleAttr);
//        setSubtitle("");
        if (!TextUtils.isEmpty(subtitle)) {
            if (mSubtitleTextView == null) {
                final Context context = getContext();
                mSubtitleTextView = new TextView(context);
                mSubtitleTextView.setSingleLine();
                mSubtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                if (mSubtitleTextAppearance != 0) {
                    mSubtitleTextView.setTextAppearance(context, mSubtitleTextAppearance);
                }
                if (mSubtitleTextColor != 0) {
                    mSubtitleTextView.setTextColor(mSubtitleTextColor);
                }
            }
            if (mSubtitleTextView.getParent() != this) {
                addCenterView(mSubtitleTextView);
            }
        } else if (mSubtitleTextView != null && mSubtitleTextView.getParent() == this) {
            removeView(mSubtitleTextView);
//            mHiddenViews.remove(mSubtitleTextView);
        }
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setText(subtitle);
        }
        mSubtitleText = subtitle;
    }

    /**
     * 提供一个获取右边对应控件的view
     * 设置字体颜色
     *
     * @param position
     * @param resource
     * @return
     */
    public View setRightTextColor(int position, int resource) {
        TextView view = (TextView) rightLay.getChildAt(position);
        view.setTextColor(getResources().getColor(resource));
        return view;
    }

    /**
     * 设置控件图标
     *
     * @param position
     * @param resource
     * @return
     */
    public View setRightImage(int position, int resource) {
        TextView view = (TextView) rightLay.getChildAt(position);
        view.setBackgroundResource(resource);
        return view;
    }

    /**
     * 对右边控件的简单操作
     *
     * @param position
     * @return
     */
    public View setRightImage(int position) {
        TextView view = (TextView) rightLay.getChildAt(position);
        return view;
    }


    /**
     * 隐藏左边按钮
     */
    public void setNavigationIconGone() {
        setNavigationIcon(null);
    }

    /**
     * 替换右边menu三个点的图标
     * @param icon
     */
    public void setOverflowIconChange(@Nullable Integer icon){
        setOverflowIcon(mContext.getResources().getDrawable(icon));
    }


    /**
     * 设置中间标题显示位置（中间）
     * @param v
     */
    private void addCenterView(View v) {
        final ViewGroup.LayoutParams vlp = v.getLayoutParams();
        final LayoutParams lp;
        if (vlp == null) {
            lp = generateDefaultLayoutParams();
        } else if (!checkLayoutParams(vlp)) {
            lp = generateLayoutParams(vlp);
        } else {
            lp = (LayoutParams) vlp;
        }
        addView(v, lp);
    }

    /**
     * 设置右边图标靠右，解决靠右时与设置中间标题的冲突
     * @param v
     */
    private void addRightView(View v) {
        final ViewGroup.LayoutParams vlp = v.getLayoutParams();
        final LayoutParams lp;
        if (vlp == null) {
            lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.RIGHT;
        } else if (!checkLayoutParams(vlp)) {
            if (vlp instanceof LayoutParams) {
                lp = new LayoutParams((LayoutParams) vlp);
            } else if (vlp instanceof ActionBar.LayoutParams) {
                lp = new LayoutParams((ActionBar.LayoutParams) vlp);
            } else if (vlp instanceof MarginLayoutParams) {
                lp = new LayoutParams((MarginLayoutParams) vlp);
            } else {
                lp = new LayoutParams(vlp);
            }
            lp.gravity = Gravity.RIGHT;
        } else {
            lp = (LayoutParams) vlp;
        }
        addView(v, lp);
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        LayoutParams lp = new LayoutParams(getContext(), attrs);
        lp.gravity = Gravity.CENTER;
        return lp;
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        LayoutParams lp;
        if (p instanceof LayoutParams) {
            lp = new LayoutParams((LayoutParams) p);
        } else if (p instanceof ActionBar.LayoutParams) {
            lp = new LayoutParams((ActionBar.LayoutParams) p);
        } else if (p instanceof MarginLayoutParams) {
            lp = new LayoutParams((MarginLayoutParams) p);
        } else {
            lp = new LayoutParams(p);
        }
        lp.gravity = Gravity.CENTER;
        return lp;
    }


    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        return lp;
    }

    @Override
    public void setTitleTextAppearance(Context context, @StyleRes int resId) {
        mTitleTextAppearance = resId;
        if (mTitleTextView != null) {
            mTitleTextView.setTextAppearance(context, resId);
        }
    }

    @Override
    public void setSubtitleTextAppearance(Context context, @StyleRes int resId) {
        mSubtitleTextAppearance = resId;
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setTextAppearance(context, resId);
        }
    }

    /**
     * 重写设置标题的颜色
     * @param color
     */
    @Override
    public void setTitleTextColor(@ColorInt int color) {
        mTitleTextColor = color;
        if (mTitleTextView != null) {
            mTitleTextView.setTextColor(color);
        }
    }

    /**
     * 重写设置副标题的颜色
     * @param color
     */
    @Override
    public void setSubtitleTextColor(@ColorInt int color) {
        mSubtitleTextColor = color;
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setTextColor(color);
        }
    }


    /**
     * menu 的一个pop显示
     * @param m
     */
    public void popShow(Context m,int layout) {
        /**
         * 定位PopupWindow，让它恰好显示在Action Bar的下方。 通过设置Gravity，确定PopupWindow的大致位置。
         * 首先获得状态栏的高度，再获取Action bar的高度，这两者相加设置y方向的offset样PopupWindow就显示在action
         * bar的下方了。 通过dp计算出px，就可以在不同密度屏幕统一X方向的offset.但是要注意不要让背景阴影大于所设置的offset，
         * 否则阴影的宽度为offset.
         */
        // 获取状态栏高度
        Rect frame = new Rect();
        ((Activity)m).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        //        状态栏高度：frame.top
        int xOffset = frame.top + HeadToolBar.TOOLBAR_HEIGHT;//减去阴影宽度，适配UI.
        int yOffset = dip2px(3f); //设置x方向offset为5dp
//        View parentView = LayoutInflater.from(mContext).inflate(manyLay,null);
        View popView =  LayoutInflater.from(mContext).inflate(
                layout, null);
         popWind = new PopupWindow(popView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);//popView即popupWindow的布局，ture设置focusAble.

        //必须设置BackgroundDrawable后setOutsideTouchable(true)才会有效。这里在XML中定义背景，所以这里设置为null;
        popWind.setBackgroundDrawable(new BitmapDrawable(getResources(),
                (Bitmap) null));
        popWind.setOutsideTouchable(true); //点击外部关闭。
        popWind.setAnimationStyle(android.R.style.Animation_Dialog);    //设置一个动画。
        //设置Gravity，让它显示在右上角。
        popWind.showAtLocation((View) getParent(), Gravity.RIGHT | Gravity.TOP,
                yOffset, xOffset);

        if (toolBarPopInterface != null){
            toolBarPopInterface.toolBarInitView(popView);
        }
    }


    public void dimissPop(){
        if (popWind != null && popWind.isShowing()){
            popWind.dismiss();
        }
    }


    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue scale
     *                 （DisplayMetrics类中属性density）
     * @return
     */
    private int dip2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * 保存原有数据
     */
    private void saveRawData(@Nullable AttributeSet attrs, int defStyleAttr) {
        this.attrs = attrs;
        this.defStyleAttr = defStyleAttr;
    }


    /**
     * http://blog.csdn.net/u013062469/article/details/46981195
     * 参考自上面网站，在于解决直接使用context后的
     * android.view.ContextThemeWrapper cannot be cast to android.app.Activity
     * 的错误
     *
     * @param cont
     * @return
     */
    private static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }


    public interface ToolBarPopInterface{
        void toolBarInitView(View v);
    }

    private ToolBarPopInterface toolBarPopInterface;

    public void setToolBarPopInterface(ToolBarPopInterface toolBarPopInterface) {
        this.toolBarPopInterface = toolBarPopInterface;
    }

}
