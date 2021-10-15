package com.tud.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tud.database.DatabaseQuerier;
import com.tud.database.models.User;

import java.util.Objects;
import java.util.Optional;

public class RegisterActivity extends AppCompatActivity {

    final private static String invalidRegisterMessage = "Invalid username or password.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String username = intent.getStringExtra(LoginActivity.USERNAME);
        String password = intent.getStringExtra(LoginActivity.PASSWORD);
        setContentView(R.layout.activity_register);

        TextView usernameInput = findViewById(R.id.usernameInput);
        TextView passwordInput = findViewById(R.id.passwordInput);

        usernameInput.setText(username);
        passwordInput.setText(password);
    }

    public void goToLogin(View view) {
        Intent login = new Intent(this, LoginActivity.class);

        startActivity(login);
    }

    public void register(View view) {
        TextView usernameInput = findViewById(R.id.usernameInput);
        TextView passwordInput = findViewById(R.id.passwordInput);

        DatabaseQuerier.registerUserFromCredentials(usernameInput.getText().toString(),
            passwordInput.getText().toString(),
            user -> {
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, user.getUsername() + " has been registered.", Toast.LENGTH_SHORT);

                toast.show();
                this.finish();
            }, () -> {
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, invalidRegisterMessage, Toast.LENGTH_SHORT);

                toast.show();
            });
    }
}