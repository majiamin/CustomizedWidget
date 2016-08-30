package com.qianyue.customizedwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 画线指向属性
 * Created by qianyue on 2016/8/23.
 */
public class InfoView extends View {

    private static final String TAG = InfoView.class.getSimpleName();
    private Paint linePaint;// 直线的画笔
    private Paint textPaint;// 写字的笔
    private String[] infos;// 要显示的内容数组
    private int LINE_OFFSET = 40;// 线段相对圆的偏移
    private int LINE_WAVE = 20;// 线段波动范围
    private int DEGREE_OFFSET = 180; // 初始偏移角度
    private int DEGREE_WAVE = 30; //角度波动范围
    private int diameter; // 图片直径
    private int maxTranslationY;// 最大的线长
    int a = 0;

    private String center_word = "身份证号:130324XXXXXXXXXXXX";// 放在中间的词,可空,此处填写身份证
    private Paint wordPaint;// 写中间的词的画笔
    private Paint areaPaint;// 画下边的透明区域的笔

    private List<Float> degrees;// 所有线的角度集合
    private List<Integer> translationYs;// 所有translationY集合


    private Paint bitmapPaint;
    private Paint headPaint;
    private Bitmap bitmap;
    private Bitmap bmp;
    private float wordWidth;
    private float wordHeight;

    public InfoView(Context context) {
        this(context, null);
    }

    public InfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

        LINE_OFFSET = dip2px(context, 10);
        LINE_WAVE = dip2px(context, 20);

        int textSize = 0;
        diameter = 0;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InfoView);
        int imgeId = a.getResourceId(R.styleable.InfoView_image_src, -1);
        textSize = a.getDimensionPixelSize(R.styleable.InfoView_text_size, dip2px(context,15));
        diameter = a.getDimensionPixelSize(R.styleable.InfoView_image_radius, dip2px(context,150))*2;
        DEGREE_OFFSET = a.getInt(R.styleable.InfoView_degree_offset, DEGREE_OFFSET);
        DEGREE_WAVE = a.getInt(R.styleable.InfoView_degree_wave, DEGREE_WAVE);
        LINE_OFFSET = a.getDimensionPixelSize(R.styleable.InfoView_line_offset, LINE_OFFSET);
        LINE_WAVE = a.getDimensionPixelSize(R.styleable.InfoView_line_wave, LINE_WAVE);
        int textColor = a.getColor(R.styleable.InfoView_text_color, Color.BLUE);
        int lineColor = a.getColor(R.styleable.InfoView_line_color, Color.BLUE);
        int lineWidth = a.getDimensionPixelSize(R.styleable.InfoView_line_width, dip2px(context,1));
        int wordColor = a.getColor(R.styleable.InfoView_word_color, Color.BLUE);
        int wordSize = a.getDimensionPixelSize(R.styleable.InfoView_word_size,dip2px(context,15));

        wordPaint.setColor(wordColor);
        wordPaint.setTextSize(wordSize);


        linePaint.setStrokeWidth(lineWidth);
        linePaint.setColor(lineColor);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        bitmap = BitmapFactory.decodeResource(getResources(), imgeId);
        a.recycle();

        if (bitmap == null) {
            return;
        }
        bitmap = scaleImage(bitmap);
        bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);


        setInfos(new String[]{"小明", "24岁", "178cm", "北京", "男"});
//        setCenter_word("khj    ");
    }

    public int getLINE_OFFSET() {
        return LINE_OFFSET;
    }

    public void setLINE_OFFSET(int LINE_OFFSET) {
        this.LINE_OFFSET = LINE_OFFSET;
    }

    public int getLINE_WAVE() {
        return LINE_WAVE;
    }

    public void setLINE_WAVE(int LINE_WAVE) {
        this.LINE_WAVE = LINE_WAVE;
    }

    public int getDEGREE_OFFSET() {
        return DEGREE_OFFSET;
    }

    public void setDEGREE_OFFSET(int DEGREE_OFFSET) {
        this.DEGREE_OFFSET = DEGREE_OFFSET;
    }

    public int getDEGREE_WAVE() {
        return DEGREE_WAVE;
    }

    public void setDEGREE_WAVE(int DEGREE_WAVE) {
        this.DEGREE_WAVE = DEGREE_WAVE;
    }

    /**
     * 获取画线的画笔
     *
     * @return
     */
    public Paint getLinePaint() {
        return this.linePaint;
    }

    /**
     * 获取写字的画笔
     *
     * @return
     */
    public Paint getTextPaint() {
        return textPaint;
    }

    /**
     * 获取画bitmap的画笔
     *
     * @return
     */
    public Paint getBitmapPaint() {
        return bitmapPaint;
    }

    /**
     * 获取画圆形头像的画笔
     *
     * @return
     */
    public Paint getHeadPaint() {
        return headPaint;
    }


    /**
     * 这是初始化工作
     */
    private void init() {

        degrees = new ArrayList<>();
        translationYs = new ArrayList<>();


        linePaint = new Paint();
        linePaint.setStrokeWidth(3f);
        linePaint.setColor(Color.YELLOW);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setStrokeWidth(3f);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);


        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setStrokeWidth(3);
        bitmapPaint.setColor(Color.BLUE);

        headPaint = new Paint();
        headPaint.setAntiAlias(true);
        headPaint.setStrokeWidth(3);
        headPaint.setColor(Color.BLUE);

        wordPaint = new Paint();
        wordPaint.setColor(Color.WHITE);
        wordPaint.setTextSize(30);
        wordPaint.setAntiAlias(true);



        areaPaint = new Paint();
        areaPaint.setStrokeWidth(5f);
        areaPaint.setColor(Color.rgb(0x1b, 0x22, 0x34));
        areaPaint.setAlpha(120);
        areaPaint.setStyle(Paint.Style.FILL);
        areaPaint.setAntiAlias(true);


    }

    public void setCenter_word(String center_word) {
        if (center_word != null) {
            this.center_word = center_word;
            invalidate();
        }
    }

    /**
     * 设置标签内容
     *
     * @param infos
     */
    public void setInfos(String[] infos) {
        this.infos = infos;
        resetOffset(infos);
        invalidate();
    }

    /**
     * 重置相应的偏移参数
     *
     * @param infos
     */
    private void resetOffset(String[] infos) {
        a = 0;
        Log.i(TAG, "===resetOffset: " + diameter);
        if (infos != null && degrees.size() != infos.length) {
            degrees.clear();
            translationYs.clear();
            // 然后根据infos的数量算出画布要旋转多少角度画线段
            float degree = 360f / infos.length;
            for (int i = 0; i < infos.length; i++) {
                int translationY = new Random().nextInt(LINE_WAVE) + LINE_OFFSET + diameter / 2;
                maxTranslationY = Math.max(maxTranslationY, translationY);
                translationYs.add(translationY);
                float degTmp = (float) (degree * i + (Math.random() * DEGREE_WAVE)) + DEGREE_OFFSET;
                degrees.add(degTmp);
            }
        }
    }


    /**
     * 设置标签内容
     *
     * @param infos
     */
    public void setInfos(List<String> infos) {
        this.infos = null;
        if (infos != null) {
            this.infos = new String[infos.size()];
            this.infos = infos.toArray(this.infos);
        }
        resetOffset(this.infos);
        invalidate();
    }

    /**
     * 设置图片
     *
     * @param imageRes 图片id
     */
    public void setImageRes(int imageRes) {
        bitmap = BitmapFactory.decodeResource(getResources(), imageRes);
        if (bitmap == null) {
            return;
        }
        bitmap = scaleImage(bitmap);
        bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        invalidate();
    }


    /**
     * 设置图片
     *
     * @param bitmap 图片bitmap对象
     */
    public void setImage(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        this.bitmap = scaleImage(bitmap);
        bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (center_word!=null) {

            // View的高度和宽度
            int measuredHeight = getMeasuredHeight();
            int measuredWidth = getMeasuredWidth();

            // 计算文字的宽度和高度
            Paint.FontMetrics fm = wordPaint.getFontMetrics();
            wordWidth = wordPaint.measureText(center_word);
            wordHeight = fm.descent + 8f;
            // 控制斜线的角度
            int deltaX = 15;
            // 算出下边两点的坐标


            // 开始画线
            Path path = new Path();
            path.moveTo(0, 0);
            path.lineTo(0, measuredHeight - wordHeight);
            path.lineTo((measuredWidth - wordWidth - 2 * deltaX) / 2, measuredHeight - wordHeight);
            path.lineTo((measuredWidth - wordWidth - 2 * deltaX) / 2 + deltaX, measuredHeight);
            path.lineTo((measuredWidth - wordWidth - 2 * deltaX) / 2 + deltaX + wordWidth,
                    measuredHeight);
            path.lineTo((measuredWidth - wordWidth - 2 * deltaX) / 2 + deltaX + wordWidth +
                    deltaX, measuredHeight - wordHeight);
            path.lineTo(measuredWidth, measuredHeight - wordHeight);
            path.lineTo(measuredWidth, 0);
            canvas.drawPath(path, linePaint);
            path.close();
            canvas.drawPath(path, areaPaint);
            canvas.drawText(center_word,(measuredWidth- wordWidth)/2,measuredHeight- wordHeight +5.5f,wordPaint);
        }


        // 首先找到中心点
        float centralX = getMeasuredWidth() / 2f;
        float centralY = getMeasuredHeight() / 2f;

        canvas.save();
        canvas.translate(centralX, centralY);


        if (infos != null && infos.length > 0) {

            for (int i = 0; i < infos.length; i++) {
                canvas.save();// 保存一下当前的状态
                float degTmp = degrees.get(i);
                canvas.rotate(degTmp);

                int translationY = Math.min(a, translationYs.get(i));

                canvas.drawLine(0, 0, 0, translationY, linePaint);

                // 画完线之后再移动到线头上
                canvas.translate(0, translationY);
                canvas.rotate(-degTmp);

                // 计算文字的宽度和高度
                Paint.FontMetrics fm = textPaint.getFontMetrics();
                float textWidth = textPaint.measureText(infos[i]);
                float textHeight = fm.descent + 0.5f;

                float tempTranslationY = 0;
                float tempTranslationX = 0;
                if ((degTmp >= 0 && degTmp <= 90) || (degTmp > 360 && degTmp < 360 +
                        DEGREE_OFFSET + DEGREE_WAVE)
                        || (degTmp > 90 && degTmp <= 180)) {
                    tempTranslationY = -textHeight;
                    tempTranslationX = -textWidth;
                    canvas.drawLine(0, 0, -textWidth, 0, linePaint);
                } else if ((degTmp > 180 && degTmp <= 270) || (degTmp > 270 && degTmp <= 360)) {
                    tempTranslationY = -textHeight;
                    tempTranslationX = 0;
                    canvas.drawLine(0, 0, textWidth, 0, linePaint);
                }

                canvas.drawText(infos[i], tempTranslationX, tempTranslationY, textPaint);

                canvas.restore();


            }

        }


        if (bitmap != null) {
            // 这里千万别忘了把画笔模式还原
            bitmapPaint.setXfermode(null);
            Canvas mCanvas = new Canvas(bmp);
            mCanvas.drawCircle(this.bitmap.getWidth() / 2, this.bitmap.getHeight() / 2, this
                    .bitmap.getWidth() / 2, bitmapPaint);

            bitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            mCanvas.drawBitmap(this.bitmap, 0, 0, bitmapPaint);
            canvas.drawBitmap(bmp, -bmp.getWidth() / 2f, -bmp.getHeight() / 2f, headPaint);
        }
        canvas.restore();


        a += 15;
        if (a <= maxTranslationY) {
            postInvalidateDelayed(10);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthMode) {
            case MeasureSpec.AT_MOST:// 对应wrap_content
                widthSize = (int) (wordHeight+diameter + (textPaint.getTextSize() + LINE_OFFSET + LINE_WAVE)
                        * 2);
                break;
            case MeasureSpec.EXACTLY:// 对应指定值
                break;
            default:
                break;
        }

        switch (heightMode) {
            case MeasureSpec.AT_MOST:// 对应wrap_content
                heightSize = (int) (diameter + (textPaint.getTextSize() + LINE_OFFSET +
                        LINE_WAVE) * 2);
                break;
            case MeasureSpec.EXACTLY:// 对应指定值
                break;
            default:
                break;
        }

        setMeasuredDimension(widthSize, heightSize);

    }

    /**
     * 对图片进行一个缩放
     *
     * @param bitmap
     * @return
     */
    private Bitmap scaleImage(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(diameter / (float) bitmap.getWidth(), diameter / (float) bitmap
                .getHeight()); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight
                (), matrix, true);
        return resizeBmp;
    }

    /**
     * 单位转换
     *
     * @param context
     * @param dipValue
     * @return
     */
    private int dip2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
