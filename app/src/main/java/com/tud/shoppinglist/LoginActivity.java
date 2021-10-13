package com.tud.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tud.database.DatabaseQuerier;
import com.tud.database.models.User;

import java.util.Optional;

public class LoginActivity extends AppCompatActivity {

    final private static String invalidCredentialMessage = "Invalid username or password.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void goToRegister(View view) {
        Intent register = new Intent(this, RegisterActivity.class);

        startActivity(register);
    }

    public void login(View view) {
        TextView usernameInput = findViewById(R.id.usernameInput);
        TextView passwordInput = findViewById(R.id.passwordInput);
        Optional<User> maybeUser = DatabaseQuerier.validateCredential(usernameInput.getText().toString(), passwordInput.getText().toString());

        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
        } else {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, invalidCredentialMessage, Toast.LENGTH_SHORT);

            toast.show();
        }
    }
}