package com.tud.database;

import androidx.annotation.NonNull;

import com.tud.database.models.Item;
import com.tud.database.models.User;

import java.util.Optional;

class FieldValidator {
    static public boolean validUsername(String username) {
        return username.length() >= 6;
    }

    static public boolean validPassword(String password) {
        return password.length() >= 6 && !password.contains(" ");
    }
}

public class DatabaseQuerier {
    static public Optional<User> getUserFromCredentials(@NonNull String username, @NonNull String password) {
        // Placeholder
        if (FieldValidator.validUsername(username) && FieldValidator.validPassword(password)) {
            return Optional.of(new User(username, password));
        } else {
            return Optional.empty();
        }
    }

    static public Optional<User> registerUserFromCredentials(@NonNull String username, @NonNull String password) {
        // Placeholder
        if (FieldValidator.validUsername(username) && FieldValidator.validPassword(password)) {
            return Optional.of(new User(username, password));
        } else {
            return Optional.empty();
        }
    }

    static public Optional<Item[]> getShoplistItemsFromUserId(int user_id) {
        // Placeholder
        if (user_id != 0) {
            return Optional.of(new Item[]{new Item("Tomatoes", 3.5)});
        } else {
            return Optional.empty();
        }
    }
}
