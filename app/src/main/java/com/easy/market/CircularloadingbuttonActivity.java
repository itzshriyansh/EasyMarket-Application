package com.easy.market;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ekalips.fancybuttonproj.FancyButton;

public class CircularloadingbuttonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circularloadingbutton);
        FancyButton button1 = (FancyButton) findViewById(R.id.btn2);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof  FancyButton)
                {
                    if (((FancyButton)view).isExpanded())
                        ((FancyButton)view).collapse();
                    else
                        ((FancyButton)view).expand();
                }

            }
        };
        button1.setOnClickListener(listener);
    }
}
