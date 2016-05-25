package com.programmer.zombie.worldmood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TagsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        ListView tags = (ListView) findViewById(R.id.tags);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, LoginActivity.tags);
        tags.setAdapter(adapter);
        tags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TagsActivity.this, MainActivity.class);
                intent.putExtra("Tag", LoginActivity.tags.get(position));
                startActivity(intent);
            }
        });
    }

    public void newTweetAction(View view) {
        Intent intent = new Intent(TagsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
