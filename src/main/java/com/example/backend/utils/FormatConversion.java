package com.example.backend.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatConversion {
    public static String DateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (date != null) {
            return formatter.format(date);
        } else return "";
    }

}
