package com.denis.control.password;

public enum MinLengths {
    PASSWORD (8),
    UP_CHARS (1),
    LOW_CHARS (1),
    DIGITS (1),
    SPECIALS (1);

    private int minLength;

    MinLengths(int minLength) {
        this.minLength = minLength;
    }

    public int getMinLength() {
        return minLength;
    }
}
