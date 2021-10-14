package com.tud.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.badge.BadgeDrawable;
import com.tud.database.DatabaseQuerier;
import com.tud.database.models.Item;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ListActivity extends AppCompatActivity {
    ListView shoppingListView;
    CustomAdapter customAdapter;
    List<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        setContentView(R.layout.activity_list);

        setTitle(username.toUpperCase() + "'S BASKET");
//        TextView activityTitle = findViewById(R.id.activityTitle);
//        String activityTitleString = username + "'s list";
//
//        activityTitle.setText(activityTitleString);

        this.shoppingListView = findViewById(R.id.shoppingList);

        Optional<Item[]> items = DatabaseQuerier.getShoplistItemsFromUserId(1);

        items.ifPresent(value -> this.items = new ArrayList<>(Arrays.asList(value)));
        this.customAdapter = new CustomAdapter(this.items, this);
        this.shoppingListView.setAdapter(this.customAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem menuItem = menu.findItem(R.id.searchView);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.searchView) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void addItem(View v) {
        // Add on DB

        this.items.add(new Item(ThreadLocalRandom.current().nextInt(0, 10000 + 1), "toto", 2));
        this.customAdapter.notifyDataSetChanged();
    }

    public void deleteItem(View view) {
        // Delete on DB
        TextView itemId = ((View)view.getParent()).findViewById(R.id.itemId);

        Log.d("ITEM ID", itemId.getText().toString());
        this.items.removeIf(item -> {
            assert itemId != null;
            return item.getId() == Integer.parseInt(itemId.getText().toString());
        });
        this.customAdapter.notifyDataSetChanged();
    }

    public class CustomAdapter extends BaseAdapter implements Filterable {
        private List<Item> items;
        private List<Item> filteredItems;
        private Context context;

        public CustomAdapter(List<Item> items, Context context) {
            this.items = items;
            this.filteredItems = items;
            this.context = context;
        }

        @Override
        public int getCount() {
            return this.filteredItems.size();
        }

        @Override
        public Object getItem(int position) {
            return this.filteredItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.item, null);
            TextView names = view.findViewById(R.id.itemLabel);
            TextView price = view.findViewById(R.id.itemPrice);
            TextView id = view.findViewById(R.id.itemId);

            names.setText(this.filteredItems.get(position).getName());
            price.setText(String.valueOf(this.filteredItems.get(position).getPrice()));
            id.setText(String.valueOf(this.filteredItems.get(position).getId()));
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(MainActivity.this, ItemsPreviewActivity.class).putExtra("items", itemsModelListFiltered.get(position)));
//                }
//            });
            return view;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();

                    if (constraint == null || constraint.length() == 0) {
                        filterResults.count = items.size();
                        filterResults.values = items;
                    } else {
                        List<Item> resultsModel = new ArrayList<>();
                        String searchStr = constraint.toString().toLowerCase();

                        for (Item item : items) {
                            if (item.getName().toLowerCase().contains(searchStr)) {
                                resultsModel.add(item);
                            }
                            filterResults.count = resultsModel.size();
                            filterResults.values = resultsModel;
                        }
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredItems = (List<Item>) results.values;
                    notifyDataSetChanged();
                }
            };
        }
    }

}