package com.tud.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tud.extensions.EditTextSeparatorFormatWatcher;

public class CheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        TextView itemNumberTextView = findViewById(R.id.itemNumberDisplay);
        TextView totalBasketTextView = findViewById(R.id.totalBasketDisplay);
        EditText creditCardExpirationMonth = findViewById(R.id.cardDateEditText);
        EditText creditCardNumber = findViewById(R.id.cardNumberEditText);
        Intent intent = getIntent();
        double totalBasket = intent.getDoubleExtra(ListActivity.TOTAL_BASKET, 0);
        long itemNumber = intent.getLongExtra(ListActivity.LIST_LENGTH, 0);

        itemNumberTextView.setText(String.valueOf(itemNumber));
        totalBasketTextView.setText(String.format("%s €", totalBasket));
        creditCardNumber.addTextChangedListener(new EditTextSeparatorFormatWatcher('-', 5));
        creditCardExpirationMonth.addTextChangedListener(new EditTextSeparatorFormatWatcher('/', 3));
    }

    public void pay(View view) {
        EditText creditCardExpirationMonth = findViewById(R.id.cardDateEditText);
        EditText creditCardNumber = findViewById(R.id.cardNumberEditText);
        EditText creditCardCVC = findViewById(R.id.cardCVCEditText);
        Context context = getApplicationContext();

        // Check only if all the EditTexts are full
        if (creditCardExpirationMonth.length() == 5
            && creditCardCVC.length() == 3
            && creditCardNumber.length() == 21) {
            Toast.makeText(context, "Payment validated.", Toast.LENGTH_SHORT).show();
            setResult(ListActivity.CLEAR_LIST);
            this.finish();
        } else {
            Toast.makeText(context, "Invalid credit card information.", Toast.LENGTH_SHORT).show();
        }
    }
}

