package com.tud.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tud.database.DatabaseQuerier;
import com.tud.database.models.Item;

import java.util.Optional;


public class ListActivity extends AppCompatActivity {
    private void addItemToShopList(Item item) {
        LinearLayout listLayout = findViewById(R.id.scrollViewLinearLayout);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.item, null);
        TextView itemLabel = rowView.findViewById(R.id.itemLabel);
        TextView itemPrice = rowView.findViewById(R.id.itemPrice);

        itemLabel.setText(item.getName());
        itemPrice.setText(String.valueOf(item.getPrice()));
        listLayout.addView(rowView, listLayout.getChildCount() - 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        setContentView(R.layout.activity_list);

        TextView activityTitle = findViewById(R.id.activityTitle);
        String activityTitleString = username + "'s list";

        activityTitle.setText(activityTitleString);

        Optional<Item[]> items = DatabaseQuerier.getShoplistItemsFromUserId(1);

        if (items.isPresent()) {
            for (Item item : items.get()) {
                this.addItemToShopList(item);
            }
        }
    }

    public void onAddField(View v) {
        // Add on DB
        LinearLayout listLayout = findViewById(R.id.scrollViewLinearLayout);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.item, null);

        listLayout.addView(rowView, listLayout.getChildCount() - 1);
    }

    public void onDelete(View v) {
        // Delete on DB
        LinearLayout listLayout = findViewById(R.id.scrollViewLinearLayout);

        listLayout.removeView((View) v.getParent());
    }
}