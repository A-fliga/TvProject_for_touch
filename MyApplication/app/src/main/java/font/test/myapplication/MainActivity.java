package font.test.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private MarqueeView marqueeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        marqueeView = (MarqueeView) findViewById(R.id.marquee_view);
        View myview= LayoutInflater.from(this).inflate(R.layout.text_layout,null);
        marqueeView.addView(myview);
        marqueeView.setScrollSpeed(8);
        marqueeView.setScrollDirection(MarqueeView.RIGHT_TO_LEFT);
        marqueeView.setViewMargin(0);
        marqueeView.startScroll();
    }
}
