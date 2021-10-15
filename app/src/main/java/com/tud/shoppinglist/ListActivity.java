package com.tud.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        setTitle(username.toUpperCase() + "'s basket");

        this.shoppingListView = findViewById(R.id.shoppingList);
        Optional<Item[]> items = DatabaseQuerier.getShoplistItemsFromUserId(1);

        items.ifPresent(value -> this.items = new ArrayList<>(Arrays.asList(value)));
        this.customAdapter = new CustomAdapter(this.items, this);
        this.shoppingListView.setAdapter(this.customAdapter);
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
                customAdapter.getFilter().filter(newText);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_add_item, null))
                .setTitle("Add item")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TextView itemNameView = ((AlertDialog) dialog).findViewById(R.id.dialogItemName);
                        TextView itemPriceView = ((AlertDialog) dialog).findViewById(R.id.dialogItemPrice);
                        String itemPriceString = itemPriceView.getText().toString();
                        String itemName = itemNameView.getText().toString().trim();

                        if (itemPriceString.isEmpty() || itemName.isEmpty()) {
                            Context context = getApplicationContext();
                            Toast toast = Toast.makeText(context, "Invalid name or price.", Toast.LENGTH_SHORT);

                            toast.show();
                        } else {
                            double itemPrice = Double.parseDouble(itemPriceView.getText().toString());
                            Optional<Item> maybe_item = DatabaseQuerier.createNewItem(itemName, itemPrice);
                            // Add on DB

                            if (maybe_item.isPresent()) {
                                ListActivity.this.items.add(maybe_item.get());
                                ListActivity.this.customAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteItem(View view) {
        // Delete on DB
        // Need to go to the button's parent to get the hidden id TextView
        TextView itemId = ((View) view.getParent()).findViewById(R.id.itemId);

        this.items.removeIf(item -> item.getId() == Integer.parseInt(itemId.getText().toString()));
        // Need to remove the item from the filtered list too notify user of the deletion
        this.customAdapter.filteredItems.removeIf(item -> item.getId() == Integer.parseInt(itemId.getText().toString()));
        this.customAdapter.notifyDataSetChanged();
    }

    public void goToCheckout(View view) {
        double totalBasket = this.items.stream().map(Item::getPrice).mapToDouble(i -> i).sum();

        if (totalBasket > 0) {
            String totalBasketMessage = "Total basket: " + totalBasket + "€";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(totalBasketMessage)
                    .setTitle("Go to checkout ?")
                    .setPositiveButton("Go to checkout", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent checkout = new Intent(ListActivity.this, CheckoutActivity.class);

                            checkout.putExtra("TOTAL_BASKET", totalBasket);
                            checkout.putExtra("LIST_LENGTH", (long) ListActivity.this.items.size());
                            // Empty list
                            startActivity(checkout);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Empty basket.", Toast.LENGTH_SHORT);

            toast.show();
        }
    }

    public class CustomAdapter extends BaseAdapter implements Filterable {
        private final List<Item> items;
        private List<Item> filteredItems;

        public CustomAdapter(List<Item> items, Context context) {
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