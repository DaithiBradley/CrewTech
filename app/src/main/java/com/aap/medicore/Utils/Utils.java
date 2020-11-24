package com.aap.medicore.Utils;

public class Utils {
    public static String tryParseImagePathWithError(String val) {
        String result;
        if (val.toCharArray()[0] == '/')
            val = val.replaceFirst("/", "");
        if (val.contains("/https%3A/crewtech.org"))
            val = val.replaceFirst("/https%3A/crewtech.org", "");
        result = Constants.IMAGE_IP + val;
        return result;
    }
}
