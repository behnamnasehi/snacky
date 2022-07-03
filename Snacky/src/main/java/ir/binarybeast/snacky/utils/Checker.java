package ir.binarybeast.snacky.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Checker {

    @NonNull
    public static <T> T checkNotNull(@NonNull T reference, @NonNull Object errorMessage) {
        if (reference == null) {
            NullPointerException reference1 = new NullPointerException(String.valueOf(errorMessage));
            throw reference1;
        } else {
            return reference;
        }
    }


    @NonNull
    public static String checkNotEmpty(@Nullable String string) {
        if (TextUtils.isEmpty(string)) {
            IllegalArgumentException string1 = new IllegalArgumentException("Given String is empty or null");
            throw string1;
        } else {
            return string;
        }
    }

    @NonNull
    public static String checkNotEmpty(@Nullable String string, @NonNull Object errorMessage) {
        if (TextUtils.isEmpty(string)) {
            IllegalArgumentException string1 = new IllegalArgumentException(String.valueOf(errorMessage));
            throw string1;
        } else {
            return string;
        }
    }

    public static boolean checkIsNullOrEmpty(@Nullable String string) {
        return string == null || TextUtils.isEmpty(string);
    }

    public static boolean checkIsZero(int number) {
        return number == 0;
    }

    public static void checkState(boolean expression, @NonNull Object errorMessage) {
        if (!expression) {
            IllegalStateException expression1 = new IllegalStateException(String.valueOf(errorMessage));
            throw expression1;
        }
    }

    public static boolean isNumeric(String str) {
        try {

            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static <T> T checkNotNull(@Nullable T reference) {
        if (reference == null) {
            NullPointerException reference1 = new NullPointerException("null reference");
            throw reference1;
        } else {
            return reference;
        }
    }

}
