package com.tud.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.tud.database.models.User;

import java.util.Optional;

public class DatabaseQuerier {
    static public Optional<User> validateCredential(@NonNull String username, @NonNull String password) {
        // Placeholder
        if (username.equals("error")) {
            return Optional.empty();
        } else {
            return Optional.of(new User(username, password));
        }
    }
}
