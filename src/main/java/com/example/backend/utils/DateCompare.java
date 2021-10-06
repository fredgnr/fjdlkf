package com.example.backend.utils;

import java.util.Calendar;
import java.util.Date;

public class DateCompare {
    public static boolean is1after2(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.after(c2);
    }
}
