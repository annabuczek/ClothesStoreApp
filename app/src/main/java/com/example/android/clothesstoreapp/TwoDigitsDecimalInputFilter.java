package com.example.android.clothesstoreapp;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class implementing InputFilter interface.
 * Use to filter user input in order to match defined pattern.
 */


public class TwoDigitsDecimalInputFilter implements InputFilter {

    /**
     * Regex Pattern to be applied on user input in order to filter it
     */
    private Pattern mPattern;

    /**
     * Public constructor
     *
     * @param digitsBeforeZero number of digits before .
     * @param digitsAfterZero  number of digits after .
     */
    public TwoDigitsDecimalInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        String pattern = "\\d{0," + digitsBeforeZero + "}+(\\.\\d{0," + digitsAfterZero + "})?";
        mPattern = Pattern.compile(pattern);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        // CharSequence match is user input we need to be checking
        CharSequence match = TextUtils.concat(dest.subSequence(0, dstart), source.subSequence(start, end), dest.subSequence(dend, dest.length()));
        // Matcher object check if our Pattern equals user input
        Matcher matcher = mPattern.matcher(match);
        if (!matcher.matches()) {
            return "";
        }
        return null;
    }
}
