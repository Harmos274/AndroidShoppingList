package com.tud.database;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.tud.database.models.Item;
import com.tud.database.models.User;

import java.util.concurrent.ThreadLocalRandom;

class FieldValidator {
    static public boolean validUsername(String username) {
        return username.length() >= 6;
    }

    static public boolean validPassword(String password) {
        return password.length() >= 6 && !password.contains(" ");
    }
}

public class DatabaseQuerier {
    public interface SuccessCallback<T> {
        void call(T success);
    }

    public interface FailureCallback {
        void call();
    }

    private static final String DOMAIN = "@shoplist.com";
    private static final FirebaseAuth authDb = FirebaseAuth.getInstance();

    static public void getUserFromCredentials(@NonNull String username, @NonNull String password,
                                              SuccessCallback<User> onSuccess,
                                              FailureCallback onFailure) {
        if (FieldValidator.validUsername(username) && FieldValidator.validPassword(password)) {
            authDb.signInWithEmailAndPassword(username + DOMAIN, password)
                .addOnSuccessListener(runnable -> onSuccess.call(new User(username, password)))
                .addOnFailureListener(runnable -> onFailure.call());
        } else {
            onFailure.call();
        }
    }

    static public void registerUserFromCredentials(@NonNull String username, @NonNull String password,
                                                   SuccessCallback<User> onSuccess,
                                                   FailureCallback onFailure) {
        if (FieldValidator.validUsername(username) && FieldValidator.validPassword(password)) {
            authDb.createUserWithEmailAndPassword(username + DOMAIN, password)
                .addOnSuccessListener(runnable -> onSuccess.call(new User(username, password)))
                .addOnFailureListener(runnable -> onFailure.call());
        } else {
            onFailure.call();
        }
    }

    static public Item createNewItem(String name, double price) {
        return new Item(ThreadLocalRandom.current().nextInt(0, 10000 + 1), name, price);
    }
}
