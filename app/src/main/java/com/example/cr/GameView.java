package com.example.cr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.Random;


public class GameView extends View{

    private boolean mGameOver = false;
    private static final int MAP_SIZE = 20;
    private static final int START_X = 10;
    private static final int START_Y = 10;

    private static final Point[][] mPoint = new Point[MAP_SIZE][MAP_SIZE];
    private final LinkedList<Point> Snake = new LinkedList<>();

    private Direction direction;

    private int boxSize;
    private int boxPadding;
    private final Paint paint = new Paint();


    public GameView(Context context){
        super(context);
    }

    public GameView(Context context, @Nullable AttributeSet attr){
        super(context, attr);
    }

    public GameView(Context context, @Nullable AttributeSet attr,int defStyleAttr){
        super(context,attr,defStyleAttr);
    }

    public void newGame() {
        mGameOver= false;
        direction = Direction.RIGHT;
        mapInit();
        //TODO
//        updateScore();
    }

    public void init(){
        boxSize = getContext().getResources().getDimensionPixelSize(R.dimen.game_size)/MAP_SIZE;
        boxPadding = boxSize / 10;
    }

    private void mapInit(){
        for(int i = 0; i < MAP_SIZE; ++i){
            for(int j = 0 ; j < MAP_SIZE; ++j){
                mPoint[i][j] = new Point(j,i);
            }
        }
        Snake.clear();
        for(int i = 0 ; i < 3 ; ++i){
            Point point = getPoint(START_X+i,START_Y);
            point.type = PointType.SNAKE;
            Snake.addFirst(point);
        }
        spawnApple();
    }

    private void spawnApple(){
        Random random = new Random();
        while(true){
           Point point = getPoint(random.nextInt(MAP_SIZE),random.nextInt(MAP_SIZE));
           if(point.type == PointType.EMPTY){
               point.type = PointType.APPLE;
               break;
           }
        }
    }

    private Point getPoint(int x, int y){
        return mPoint[y][x];
    }

    private Point getNext(Point point) {
        int x = point.x;
        int y = point.y;

        switch (direction) {
            case UP:
                y = y == 0 ? MAP_SIZE - 1 : y - 1;
                break;
            case DOWN:
                y = y == MAP_SIZE - 1 ? 0 : y + 1;
                break;
            case LEFT:
                x = x == 0 ? MAP_SIZE - 1 : x - 1;
                break;
            case RIGHT:
                x = x == MAP_SIZE - 1 ? 0 : x + 1;
                break;
        }
        return getPoint(x, y);
    }

    public void next(){
        Point first = Snake.getFirst();
        Point next = getNext(first);

        switch (next.type) {
            case EMPTY:
                next.type = PointType.SNAKE;
                Snake.addFirst(next);
                Snake.getLast().type = PointType.EMPTY;
                Snake.removeLast();
                break;
            case APPLE:
                next.type = PointType.SNAKE;
                Snake.addFirst(next);
                spawnApple();

                //TODO
                //updateScore();

                break;
            case SNAKE:
                mGameOver = true;
                break;
        }
    }

    public void setDirection(Direction dir) {
        if ((dir == Direction.LEFT || dir == Direction.RIGHT) &&
                (direction == Direction.LEFT || direction == Direction.RIGHT)) {
            return;
        }
        if ((dir == Direction.UP || dir == Direction.DOWN) &&
                (direction == Direction.UP || direction== Direction.DOWN)) {
            return;
        }
        direction = dir;
    }

    public boolean isGameOver(){
       return mGameOver;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        for(int y = 0; y < MAP_SIZE; ++y){
            for(int x = 0; x < MAP_SIZE; ++x){

                int left = boxSize * x;
                int right = left + boxSize;
                int top = boxSize * y;
                int bott = top + boxSize;

                switch (getPoint(x,y).type){
                    case APPLE:
                        paint.setColor(Color.parseColor("#306850"));
                        break;
                    case SNAKE:
                        paint.setColor(Color.parseColor("#e0f8cf"));
                        canvas.drawRect(left,top,right,bott,paint);
                        paint.setColor(Color.parseColor("#071821"));
                        left += boxPadding;
                        right -= boxPadding;
                        top += boxPadding;
                        bott -= boxPadding;
                        break;
                    case EMPTY:
                        paint.setColor(Color.parseColor("#e0f8cf"));
                }
                canvas.drawRect(left,top,right,bott,paint);
            }
        }

    }

}