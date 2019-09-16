package com.example.wuziqi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.wuziqi.game.BaseComputerAi;
import com.example.wuziqi.game.Point;

import java.util.ArrayList;
import java.util.List;

public class wuziqiPanel extends View {

    private int mPanelWidth;
    private static int MAX_LINE = 15;  //行数
    private int MAX_COUNT_IN_LINE =5 ; //最大相连棋子数
    private float mLineHeight;

    private float pieceLineHeight = 0.75f;//棋子大小

    private Paint paint = new Paint();

    //棋子图片
    private Bitmap whiteChess;
    private Bitmap blackChess;

    private boolean isBlack = true; //玩家是否为黑棋
    private List<Point>  mWhiteArray = new ArrayList<>();
    private List<Point>  mBlackArray = new ArrayList<>();
    private BaseComputerAi aiplayer = new BaseComputerAi();

    //private boolean isWin = false;//是否已获胜
    private boolean isAiplayer = false;
    private boolean isGameOver =false;
    private boolean isWhiteWin ;

    private static final String TAG = "wuziqiPanel";

    public wuziqiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x44ff0000);

        initPaint();
    }
    public void start(){
        mWhiteArray.clear();
        mBlackArray.clear();
        aiplayer.getMyPoints().clear();
        if(isAiplayer)
        {
            mWhiteArray = aiplayer.getMyPoints();
        }
        isGameOver =false;
        isWhiteWin = false;
        invalidate();//重绘

    }

    /*
     * 初始化绘图
     * */
    private void initPaint() {
        paint.setColor(0x88000000);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);

        Resources resources = getResources();
        blackChess = BitmapFactory.decodeResource(resources, R.drawable.stone_b1);//获取图片
        whiteChess = BitmapFactory.decodeResource(resources, R.drawable.stone_w2);
    }


    /*
     * 测量自定义View大小
     * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthModel = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightModel = MeasureSpec.getMode(heightMeasureSpec);

        int size = 0;
        if (widthModel == MeasureSpec.UNSPECIFIED) {
            size = heightSize;
        } else if (heightModel == MeasureSpec.UNSPECIFIED) {
            size = widthSize;
        } else {
            size = Math.min(widthSize, heightSize);
        }
        setMeasuredDimension(size, size);
        Log.i(TAG,"onMeasure");
    }

    /*
    * 宽高改变时回调
    * */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth / MAX_LINE;//线宽

        int pieceWidth = (int) (mLineHeight * pieceLineHeight);//棋子大小

        whiteChess = Bitmap.createScaledBitmap(whiteChess, pieceWidth, pieceWidth, false);
        blackChess = Bitmap.createScaledBitmap(blackChess, pieceWidth, pieceWidth, false);

        Log.i(TAG,"onSizeChanged");
    }
    /*
    * 重写绘制函数
    * */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawChessBoard(canvas);//画棋盘

        drawPieces(canvas);//画棋子
        checkGameOver();

        Log.i(TAG,"onDraw");
    }

    private void checkGameOver() {

        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);

        if(blackWin||whiteWin)
        {
            isGameOver=true;
            isWhiteWin = whiteWin;
            String text =isWhiteWin?"白棋胜利":"黑棋胜利";
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
            alert(text);
        }
    }

    private boolean checkFiveInLine(List<Point> points) {
        for(Point p :points)
        {
            int x = p.x;
            int y = p.y;
            boolean win = checkHorizontal(x,y,points);
            if(win)return true;
            win = checkVertical(x,y,points);
            if(win)return true;
            win = checkLeftDiagonal(x,y,points);
            if(win)return true;
            win = checkRightDiagonal(x,y,points);
            if(win)return true;
        }
        return false;
    }

/*
* 横向检查棋子
* */
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        for(int i=1;i<MAX_COUNT_IN_LINE;i++)
        {
            if(points.contains(new Point(x-i,y)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE)return true;
        for(int i=1;i<MAX_COUNT_IN_LINE;i++)
        {
            if(points.contains(new Point(x+i,y)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE)return true;

        return false;
    }
    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        for(int i=1;i<MAX_COUNT_IN_LINE;i++)
        {
            if(points.contains(new Point(x,y-i)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE)return true;
        for(int i=1;i<MAX_COUNT_IN_LINE;i++)
        {
            if(points.contains(new Point(x,y+i)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE)return true;

        return false;
    }
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        for(int i=1;i<MAX_COUNT_IN_LINE;i++)
        {
            if(points.contains(new Point(x-i,y+i)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE)return true;
        for(int i=1;i<MAX_COUNT_IN_LINE;i++)
        {
            if(points.contains(new Point(x+i,y-i)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE)return true;

        return false;
    }
    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        for(int i=1;i<MAX_COUNT_IN_LINE;i++)
        {
            if(points.contains(new Point(x-i,y-i)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE)return true;
        for(int i=1;i<MAX_COUNT_IN_LINE;i++)
        {
            if(points.contains(new Point(x+i,y+i)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE)return true;

        return false;
    }

    private void drawPieces(Canvas canvas) {
        for(int i=0;i<mWhiteArray.size();i++)
        {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(whiteChess,
                    (whitePoint.x+(1-pieceLineHeight)/2)*mLineHeight,
                    (whitePoint.y+(1-pieceLineHeight)/2)*mLineHeight,
                    null);

        }
        for(int i=0;i<mBlackArray.size();i++)
        {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(blackChess,
                    (blackPoint.x+(1-pieceLineHeight)/2)*mLineHeight,
                    (blackPoint.y+(1-pieceLineHeight)/2)*mLineHeight,
                    null);

        }
    }

    /*
*  划线
* */
    private void drawChessBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;

        for (int i = 0; i < MAX_LINE; i++) {
            float startX = lineHeight / 2;
            float endX = w - lineHeight / 2;

            float y = (float) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, paint);
            canvas.drawLine(y, startX, y, endX, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isGameOver)return  false;
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP) {

            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getVaildPoint(x, y);
            if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
                return false;
            }
            if(!isAiplayer)
            {
                if (isBlack) {
                    mBlackArray.add(p);
                } else {
                    mWhiteArray.add(p);
                }
                invalidate();
                isBlack = !isBlack;
                return true;
            }
            else{
                mBlackArray.add(p);
                mWhiteArray.add(aiplayer.run(mBlackArray,null));

                //aiplayer.allFreePoints.remove(temp);

                invalidate();
                return true;
            }

        }
        else if (action == MotionEvent.ACTION_DOWN) {

            return true;
        }

        return super.onTouchEvent(event);
    }

    private Point getVaildPoint(int x, int y) {
        return new Point((int)(x/mLineHeight),(int)(y/mLineHeight));
    }

    private void alert(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("提示");
        builder.setMessage(msg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("重新开局", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isGameOver = false;
                isWhiteWin =false;
                dialogInterface.dismiss();
                start();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY= "instance_black_array";

    @Override
    protected Parcelable onSaveInstanceState() {

        //Bundle bundle =new Bundle();
       // bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        //bundle.putBoolean(INSTANCE_GAME_OVER,isGameOver);
        //bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        return super.onSaveInstanceState();
    }
    public void changeMode()
    {
        isAiplayer = !isAiplayer;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }
}
