package ir.binarybeast.snacky;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import ir.binarybeast.snacky.utils.LayoutHelper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.btn);


        Snacky.createInstance(this, "Error", false, new DesignBuilder.Builder()
                .setSubtitleTextColor(R.color.white)
                .setSubtitleTextSize(12)
                .setTitleTextColor(R.color.white)
                .setTitleTextSize(16)
                .setUndoTitleTextColor(R.color.white)
                .setUndoTextSize(14)
                .setBackgroundRadius(12)
                .build());
        addContentView(Snacky.getInstance("Error"), LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.START, 20, 0, 20, 8));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snacky.getInstance("Error").make("Something went wrong !", "Please try again and send this requet again to server for test", Snacky.LENGTH_LONG, "Done").start();
            }
        });


    }
}