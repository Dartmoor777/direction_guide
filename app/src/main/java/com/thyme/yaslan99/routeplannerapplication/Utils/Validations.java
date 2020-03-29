package com.thyme.yaslan99.routeplannerapplication.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yaroslava Landyga
 */

public class Validations {
    private Pattern mPattern;
    private Matcher mMatcher;

    public boolean isValidEmail(String text) {

        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        mPattern = Pattern.compile(EMAIL_PATTERN);
        mMatcher = mPattern.matcher(text);
        return mMatcher.matches();
    }

    public boolean isEmpty(String text) {
        return text.isEmpty();
    }
}
