package com.tud.extensions;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

public class EditTextSeparatorFormatWatcher implements TextWatcher {
    private final char space;
    private final int groupSize;

    public EditTextSeparatorFormatWatcher(char spacer, int groupSize) {
        this.space = spacer;
        this.groupSize = groupSize;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Remove spacing char
        if (s.length() > 0 && (s.length() % this.groupSize) == 0) {
            final char c = s.charAt(s.length() - 1);
            if (space == c) {
                s.delete(s.length() - 1, s.length());
            }
        }
        // Insert char where needed.
        if (s.length() > 0 && (s.length() % this.groupSize) == 0) {
            char c = s.charAt(s.length() - 1);
            // Only if its a digit where there should be a space we insert a space
            if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                s.insert(s.length() - 1, String.valueOf(space));
            }
        }
    }
}