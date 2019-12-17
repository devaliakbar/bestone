package com.mna.bestone;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Update extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Button updateBut = findViewById(R.id.update_but);
        updateBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                String URL = "https://play.google.com/store/apps/details?id=com.mna.bestone";
                browserIntent.setData(Uri.parse(URL));
                startActivity(browserIntent);
            }
        });
    }
}
