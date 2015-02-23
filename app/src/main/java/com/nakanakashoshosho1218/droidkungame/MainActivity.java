package com.nakanakashoshosho1218.droidkungame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MainActivity extends Activity {

    final String[] RAINBOW_VALUES = {
            "dd000b",
            "ee6d00",
            "ffeb00",
            "61a911",
            "",
            "006f28",
            "007669",
            "0079e0",
            "003d99",
            "101258",
            "64034e"
    };
    final String[] RAINBOW_STRS = {
            "G", "A", "M", "E", " ", "F", "I", "N", "I", "S", "H"
    };


    TextView scoreText;
    TextView timeLabel;
    TextView timeText;
    TextView gameStartLabel;
    TextView gameOverLabel;
    TextView comboText;
    TextView finalScoreText;
    TextView bestScoreLabel;
    TextView bestScoreText;

    Button shareTwitterButton;

    ImageView[] imageView = new ImageView[25];

    int score;
    int rnd;

    int width;
    int height;

    float positionX;
    float positionY;

    float textWidth;
    float textHeight;

    int color;
    int baseColor;

    float baseHue;
    float baseSaturation;
    float baseValue;

    float[] colorValue = {0.7f, 0.75f, 0.8f, 0.85f, 0.9f, 0.95f, 0.98f};

    boolean isMissTouch;
    int countTimes;
    int combo;

    CountDownTimer mCountDownTimer;
    long time = 1000;

    private AnimationSet mImageAnimation;
    private AnimationSet mComboTextAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isMissTouch = false;
        countTimes = 0;

        scoreText = (TextView) findViewById(R.id.textView_score);
        timeLabel = (TextView) findViewById(R.id.label_time);
        timeText = (TextView) findViewById(R.id.textView_time);
        gameStartLabel = (TextView) findViewById(R.id.label_gameStart);
        gameOverLabel = (TextView) findViewById(R.id.label_gameOver);
        comboText = (TextView) findViewById(R.id.textView_combo);
        finalScoreText = (TextView) findViewById(R.id.textView_FinalScore);
        bestScoreLabel = (TextView) findViewById(R.id.label_bestScore);
        bestScoreText = (TextView) findViewById(R.id.textView_bestScore);

        shareTwitterButton = (Button) findViewById(R.id.button_shareTwitter);

        //TextViewのフォント(DIN Condensed Bold)
        Typeface typeface = Typeface.createFromAsset(getAssets(), "DIN Condensed Bold.ttf");
        scoreText.setTypeface(typeface);
        timeLabel.setTypeface(typeface);
        timeText.setTypeface(typeface);
        gameStartLabel.setTypeface(typeface);
        gameOverLabel.setTypeface(typeface);
        comboText.setTypeface(typeface);
        finalScoreText.setTypeface(typeface);
        bestScoreLabel.setTypeface(typeface);
        bestScoreText.setTypeface(typeface);

        //ImageViewの関連付け
        for (int i = 0; i < imageView.length; i++) {
            String ivId = "imageView" + (i + 1);
            int resID = getResources().getIdentifier(ivId, "id", "com.nakayamashohei.droidkungame");

            imageView[i] = (ImageView) findViewById(resID);
            imageView[i].setImageResource(R.drawable.droid);
            imageView[i].setTag(new Integer(i));

            imageView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = ((Integer) v.getTag()).intValue();

                    if (index == rnd) {
                        imageView[index].startAnimation(mImageAnimation);
                        imageView[index].setBackgroundColor(Color.parseColor("#E91E63"));
                        imageView[index].setEnabled(false);

                        Rect rect = new Rect();
                        imageView[index].getGlobalVisibleRect(rect);

                        positionX = (rect.left + rect.right) / 2;
                        positionY = rect.top;

                        score++;
                        scoreText.setText(String.valueOf(score));

                        if (isMissTouch) {
                            isMissTouch = false;
                            Log.d("MainActivity", "isMissTouch");
                        }

                        countTimes++;
                        combo++;

                        if (combo >= 2) {
                            comboText.setVisibility(View.VISIBLE);
                            comboText.setText(combo + " COMBO");
                            //textWidthはTextViewの幅
                            textWidth = comboText.getTextSize() * comboText.length();
                            textHeight = comboText.getHeight();


                            //ComboTextの長さを取得
                            Paint p = comboText.getPaint();
                            p.setTextSize(comboText.getTextSize());
                            textWidth = (int) p.measureText((String) comboText.getText());
                            Log.e("onClick", "TextView Width = " + textWidth);

                            //widthはImageViewの幅
                            //positionXがImageViewのX座標
                            comboText.setTranslationX(positionX - textWidth / 2);
                            comboText.setTranslationY(positionY);

                            mComboTextAnimation = new AnimationSet(true);
                            ScaleAnimation comboTextStartAnim = new ScaleAnimation(1.0f, 1.6f, 1.0f, 1.6f, positionX, positionY);
                            comboTextStartAnim.setDuration(500);
                            mComboTextAnimation.addAnimation(comboTextStartAnim);
                            ScaleAnimation comboTextEndAnim = new ScaleAnimation(1.6f, 1.0f, 1.6f, 1.0f, positionX, positionY);
                            comboTextEndAnim.setDuration(300);
                            mComboTextAnimation.addAnimation(comboTextEndAnim);
                            mComboTextAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });
                            comboText.startAnimation(mComboTextAnimation);
                            comboText.setVisibility(View.INVISIBLE);
                        }

                        if (countTimes >= 3) {
                            Log.d("MainActivity", "countTimes" + countTimes);

                            time = time + 3000;
                            mCountDownTimer.cancel();
                            timer();
                            countTimes = 0;
                        }
                    } else {
                        isMissTouch = true;
                        countTimes = 0;
                        combo = 0;
                        comboText.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

        //スタート画面のタッチのイベントを全部吸収
        findViewById(R.id.translucentLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //スコア
        score = 0;
        scoreText.setText(String.valueOf(score));

        random();
        question();

        //画面サイズの取得
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        width = size.x / 5;
        height = width;

        Log.e("MainActivity", "Width : " + width + "Height : " + height);

        //タップ時のアニメーション
        mImageAnimation = new AnimationSet(true);
        ScaleAnimation imageStartAnim = new ScaleAnimation(1.0f, 0.6f, 1.0f, 0.6f, width / 2, height / 2);
        imageStartAnim.setDuration(500);
        mImageAnimation.addAnimation(imageStartAnim);
        ScaleAnimation imageEndAnim = new ScaleAnimation(0.6f, 1.0f, 0.6f, 1.0f, width / 2, height / 2);
        imageEndAnim.setDuration(300);
        mImageAnimation.addAnimation(imageEndAnim);
        mImageAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                question();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        useScaledImage();
    }

    //タイマー
    public void timer() {

        mCountDownTimer = new CountDownTimer(time, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                StringBuilder builder = new StringBuilder();
                //second
                builder.append(String.format("%1$02d", (int) (millisUntilFinished / 1000)));
                builder.append("'");
                //milli second
                builder.append(String.format("%1$02d", (int) ((millisUntilFinished % 1000) / 10)));

                time = millisUntilFinished;

                timeText.setText(builder.toString());

            }

            @Override
            public void onFinish() {
                time = 0;

                timeText.setText("00'00");

                //Finish時
                findViewById(R.id.translucentLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.finishLayout).setVisibility(View.VISIBLE);
                finalScoreText.setText(String.valueOf(score));

                SharedPreferences mHighScorePref = getSharedPreferences("TapDroid", Context.MODE_PRIVATE);
                int highScore = mHighScorePref.getInt("BEST SCORE", 0);
                if (highScore < score) {
                    SharedPreferences.Editor editor = mHighScorePref.edit();
                    editor.putInt("BEST SCORE", score);
                    editor.commit();
                    String finishText = getGameOverHtml();
                    gameOverLabel.setText(Html.fromHtml(finishText));
                    bestScoreText.setText(String.valueOf(score));
                } else {
                    String finishText = "GAME FINISH";
                    gameOverLabel.setText(finishText);
                    bestScoreText.setText(String.valueOf(highScore));
                }

                findViewById(R.id.translucentLayout).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        //タッチのイベントを全部吸収
                        return true;
                    }
                });
            }
        }.start();
    }

    //問題の表示
    public void question() {
        random();
        for (int i = 0; i < imageView.length; i++) {

            imageView[i].setBackgroundColor(baseColor);
            imageView[i].setEnabled(true);
        }
        imageView[rnd].setBackgroundColor(color);
    }

    //色の調整
    public void random() {
        rnd = (int) (Math.random() * 25);

        baseHue = (float) (Math.random() * 349) + 1;
        baseSaturation = (float) (Math.random() * 0.5f) + 0.5f;

        if (score <= 5) {
            baseValue = colorValue[0];
        } else if (score <= 10) {
            baseValue = colorValue[1];
        } else if (score <= 20) {
            baseValue = colorValue[2];
        } else if (score <= 35) {
            baseValue = colorValue[3];
        } else if (score <= 55) {
            baseValue = colorValue[4];
        } else if (score <= 80) {
            baseValue = colorValue[5];
        } else {
            baseValue = colorValue[6];
        }

        float[] baseHsv = {baseHue, baseSaturation, baseValue};
        float[] hsv = {baseHue, baseSaturation, 1f};

        baseColor = Color.HSVToColor(baseHsv);
        color = Color.HSVToColor(hsv);
    }

    //Startボタンが押された時の処理
    public void start(View v) {
        findViewById(R.id.translucentLayout).setVisibility(View.GONE);
        findViewById(R.id.startLayout).setVisibility(View.GONE);
        timer();
    }

    //リスタート時の処理
    public void restart(View v) {
        findViewById(R.id.translucentLayout).setVisibility(View.GONE);
        findViewById(R.id.finishLayout).setVisibility(View.GONE);
        score = 0;
        scoreText.setText(String.valueOf(score));
        combo = 0;
        time = 15000;
        random();
        question();
        timer();
    }

    //ShareTwitterボタンの処理
    public void shareTwitter(View v) {
        String url = "http://twitter.com/share?text=MyScore " + score + " %23TapDroid";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void useScaledImage() {
        //Twitterのアイコンのサイズの設定
        Resources res = getResources();
        BitmapDrawable twitterBitmapDrawable = (BitmapDrawable) res.getDrawable(R.drawable.ic_twitter);
        Bitmap twitterBitmap = Bitmap.createScaledBitmap(twitterBitmapDrawable.getBitmap(),
                (int) (twitterBitmapDrawable.getIntrinsicHeight() * 0.5),
                (int) (twitterBitmapDrawable.getIntrinsicWidth() * 0.5),
                false);

        Drawable drawable = new BitmapDrawable(getResources(), twitterBitmap);

        shareTwitterButton.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    public String getGameOverHtml() {
        String html = "";

        for (int i = 0; i < RAINBOW_STRS.length; i++) {
            html +=
                    "<font color=#" + RAINBOW_VALUES[i] + ">" + RAINBOW_STRS[i] + "</font>";
        }

        return html;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Tracker t = ((GoogleAnalyticsTracker) getApplication()).getTracker(GoogleAnalyticsTracker.TrackerName.APP_TRACKER);
        t.setScreenName("MainActivity");
        t.send(new HitBuilders.AppViewBuilder().build());
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onRestart() {
        super.onStart();
        Tracker t = ((GoogleAnalyticsTracker) getApplication()).getTracker(GoogleAnalyticsTracker.TrackerName.APP_TRACKER);
        t.setScreenName("MainActivity");
        t.send(new HitBuilders.AppViewBuilder().build());
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

}
