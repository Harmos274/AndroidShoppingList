package com.tud.shoppinglist;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.tud.database.DatabaseQuerier;
import com.tud.database.models.Item;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    ListView shoppingListView;
    ItemListAdapter itemListAdapter;
    List<Item> items = new ArrayList<>();
    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == CLEAR_LIST) {
                ListActivity.this.items.clear();
                ListActivity.this.itemListAdapter.notifyDataSetChanged();
            }
        });

    public static int CLEAR_LIST = 123;
    public static String TOTAL_BASKET = "TOTAL_BASKET";
    public static String LIST_LENGTH = "LIST_LENGTH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String username = intent.getStringExtra(LoginActivity.USERNAME);
        setContentView(R.layout.activity_list);
        setTitle(username + "'s basket");

        this.shoppingListView = findViewById(R.id.shoppingList);
        this.itemListAdapter = new ItemListAdapter(this.items);
        this.shoppingListView.setAdapter(this.itemListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        inflater.inflate(R.menu.option_menu, menu);
        MenuItem searchMenu = menu.findItem(R.id.searchView);
        SearchView searchView = (SearchView) searchMenu.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemListAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.optionMenu) {
            this.finish();
            return true;
        } else if (id == R.id.searchView) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addItem(View v) {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog addItemDialog = new AlertDialog.Builder(this)
            .setView(inflater.inflate(R.layout.dialog_add_item, null))
            .setTitle("Add item")
            .setPositiveButton("Add", (dialog, id) -> {
                TextView itemNameView = ((AlertDialog) dialog).findViewById(R.id.dialogItemName);
                TextView itemPriceView = ((AlertDialog) dialog).findViewById(R.id.dialogItemPrice);
                String itemPriceString = itemPriceView.getText().toString();
                String itemName = itemNameView.getText().toString().trim();

                if (itemPriceString.isEmpty() || itemName.isEmpty() || this.items.stream().anyMatch(l -> l.getName().equals(itemName))) {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "Invalid name or price. Your item could already be in the list.", Toast.LENGTH_SHORT);

                    toast.show();
                } else {
                    double itemPrice = Double.parseDouble(itemPriceView.getText().toString());
                    Item item = DatabaseQuerier.createNewItem(itemName, itemPrice);

                    ListActivity.this.items.add(item);
                    ListActivity.this.itemListAdapter.notifyDataSetChanged();
                }
            }).create();

        addItemDialog.show();
    }

    public void deleteItem(View view) {
        // Need to go to the button's parent to get the hidden id TextView
        TextView itemId = ((View) view.getParent()).findViewById(R.id.itemId);

        this.items.removeIf(item -> item.getId() == Integer.parseInt(itemId.getText().toString()));
        // Need to remove the item from the filtered list too notify user of the deletion
        this.itemListAdapter.filteredItems.removeIf(item -> item.getId() == Integer.parseInt(itemId.getText().toString()));
        this.itemListAdapter.notifyDataSetChanged();
    }

    public void goToCheckout(View view) {
        double totalBasket = this.items.stream().map(Item::getPrice).mapToDouble(i -> i).sum();

        if (totalBasket > 0) {
            String totalBasketMessage = "Total basket: " + totalBasket + "€";
            AlertDialog basketDialog = new AlertDialog.Builder(this)
                .setMessage(totalBasketMessage)
                .setTitle("Go to checkout ?")
                .setPositiveButton("Go to checkout", (dialog, id) -> {
                    Intent checkout = new Intent(ListActivity.this, CheckoutActivity.class);

                    checkout.putExtra(TOTAL_BASKET, totalBasket);
                    checkout.putExtra(LIST_LENGTH, (long) ListActivity.this.items.size());
                    activityResultLaunch.launch(checkout);
                }).create();

            basketDialog.show();
        } else {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Empty basket.", Toast.LENGTH_SHORT);

            toast.show();
        }
    }

    public class ItemListAdapter extends BaseAdapter implements Filterable {
        private final List<Item> items;
        private List<Item> filteredItems;

        public ItemListAdapter(List<Item> items) {
            this.items = items;
            this.filteredItems = items;
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