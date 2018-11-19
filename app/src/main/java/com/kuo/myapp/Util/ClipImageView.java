package com.kuo.myapp.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;


public class ClipImageView extends android.support.v7.widget.AppCompatImageView{
    private static final String TAG = ClipImageView.class.getSimpleName();

    public ClipImageView(Context context) {
        super(context);
    }

    public ClipImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ClipImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private Path pathCircle = new Path();

    /**
     * Viewのサイズ確保
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        // 丸いパス
        pathCircle.addCircle(w/2, h/2, h/2, Path.Direction.CW);
    }

    /**
     * 描画処理
     */
    protected void onDraw(Canvas canvas)
    {
        // パスに沿って切り取り
        canvas.clipPath(pathCircle);

        super.onDraw(canvas);
    }
}
