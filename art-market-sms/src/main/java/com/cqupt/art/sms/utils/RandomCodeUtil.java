package com.cqupt.art.sms.utils;

import java.text.DecimalFormat;
import java.util.Random;

public class RandomCodeUtil {
    public static final Random random = new Random();
    private static final DecimalFormat sixdf = new DecimalFormat("000000");

    public static String getSixBitRandom() {
        return sixdf.format(random.nextInt(1000000));
    }
}
