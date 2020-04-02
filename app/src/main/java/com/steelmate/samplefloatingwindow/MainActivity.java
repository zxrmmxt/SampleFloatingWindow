package com.steelmate.samplefloatingwindow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (MyFloatingWindow.hasPermission()) {
//            finish();
            MyApp.sMyApp.tpmsFloatingWindowModel.showFloatingWindowButton();
        } else {
            MyFloatingWindow.requestPermission(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        finish();
        if (MyFloatingWindow.onActivityResult(requestCode, resultCode, data)) {
            MyApp.sMyApp.tpmsFloatingWindowModel.showFloatingWindowButton();
        }
    }

    public void alarm(View view) {
        MyApp.sMyApp.tpmsFloatingWindowModel.showAlarm(new String[]{"1.5", "1.5", "1.5", "1.5"},
                                                       new String[]{"32", "32", "32", "32"},
                                                       new boolean[]{true, false, true, false},
                                                       new boolean[]{false, true, false, true}
                                                      );
    }

    public void normal(View view) {
        MyApp.sMyApp.tpmsFloatingWindowModel.showAlarm(new String[]{"1.5", "1.5", "1.5", "1.5"},
                                                       new String[]{"32", "32", "32", "32"},
                                                       new boolean[]{false, false, false, false},
                                                       new boolean[]{false, false, false, false}
                                                      );
    }
}
