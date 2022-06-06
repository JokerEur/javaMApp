package com.example.cr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private static final int FPS = 60;
    private static final int SPEED = 30;

    private static final int STATUS_PAUSED = 1;
    private static final int STATUS_START = 2;
    private static final int STATUS_OVER = 3;
    private static final int STATUS_PLAYING = 4;

    private GameView mGameView;

    private final Handler mHandler = new Handler();
    private TextView GameStatusText;
    private Button GameBtn;

    private final AtomicInteger mGameStatus = new AtomicInteger(STATUS_START);

    private void setGameStatus(int gameStatus) {
        int prevStatus = mGameStatus.get();
        GameStatusText.setVisibility(View.VISIBLE);
        mGameStatus.set(gameStatus);
        switch (gameStatus) {
            case STATUS_OVER:
                GameStatusText.setText("GAME OVER");
                break;
            case STATUS_START:
                mGameView.newGame();
                GameStatusText.setText("START GAME");
                break;

            case STATUS_PAUSED:
                GameStatusText.setText("GAME PAUSED");
                break;

            case STATUS_PLAYING:
                if (prevStatus == STATUS_OVER) {
                    mGameView.newGame();
                }
                start();
                GameStatusText.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGameView = findViewById(R.id.gameView);
        GameStatusText = findViewById(R.id.gameStatus);
        GameBtn = findViewById(R.id.A_Btn);
        mGameView.init();

        findViewById(R.id.upBtn).setOnClickListener(v -> mGameView.setDirection(Direction.UP));
        findViewById(R.id.downBtn).setOnClickListener(v -> mGameView.setDirection(Direction.DOWN));
        findViewById(R.id.rightBtn).setOnClickListener(v -> mGameView.setDirection(Direction.RIGHT));
        findViewById(R.id.leftBtn).setOnClickListener(v -> mGameView.setDirection(Direction.LEFT));

        start();


        GameBtn.setOnClickListener(v -> {
            if (mGameStatus.get() == STATUS_PLAYING) {
                setGameStatus(STATUS_PAUSED);
            } else {
                setGameStatus(STATUS_PLAYING);
            }
        });

        setGameStatus(STATUS_START);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGameStatus.get() == STATUS_PLAYING) {
            setGameStatus(STATUS_PAUSED);
        }
    }

    private void start(){
       final int delay = 1000/FPS;

       new Thread(() -> {
           int count = 0;
           while(!mGameView.isGameOver() && mGameStatus.get() != STATUS_PAUSED){
                try{
                    Thread.sleep(delay);
                    if(count % SPEED == 0){
                        mGameView.next();
                        mHandler.post(mGameView::invalidate);
                    }
                    ++count;
                }catch (InterruptedException ignore){

                }
               if (mGameView.isGameOver()) {
                   mHandler.post(() -> setGameStatus(STATUS_OVER));
               }
           }
       }).start();
    }
}