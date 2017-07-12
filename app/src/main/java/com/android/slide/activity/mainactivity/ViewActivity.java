package com.android.slide.activity.mainactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import com.android.slide.widget.LeSlide;
import com.android.slide.widget.LeSlideConfig;
import com.android.slide.widget.LeSlideInterface;


public class ViewActivity extends Activity {

    private static final String TAG = "ViewActivity";
    public static final String IGNORE_STATUS_COLOR_CHANGE = "IgnoreStatusColorChange";
    private LeSlideInterface slideInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        // Build the slidr config
        LeSlideConfig.Builder builder = new LeSlideConfig.Builder();

        Intent intent = getIntent();
        if (!intent.getBooleanExtra(IGNORE_STATUS_COLOR_CHANGE, false)) {
            int primary = getResources().getColor(R.color.accent);
            int secondary = getResources().getColor(R.color.primaryDark);
            builder.primaryColor(primary).secondaryColor(secondary);
        }

        builder.velocityThreshold(2400)
//                .distanceThreshold(.25f)
                .edge(true)
                .touchSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()));

        LeSlideConfig config = builder.build();

        // Attach the Slidr Mechanism to this activity
        slideInterface = LeSlide.attach(this, config);

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ViewActivity.class);
                intent.putExtra(IGNORE_STATUS_COLOR_CHANGE, true);
                startActivity(intent);
//                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        slideInterface.onBackPressed();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        slideInterface.onRestoreInstanceState(savedInstanceState);
    }
}
