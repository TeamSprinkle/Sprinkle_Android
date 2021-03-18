package com.example.sprinkle_android.adapter;

import android.Manifest;
import android.provider.CalendarContract;
import android.provider.ContactsContract;

public class Code {

    public class ViewType{
        public static final int LEFT_CONTENT = 0;
        public static final int RIGHT_CONTENT = 1;
    }
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                             // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                    // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,           // 2
            CalendarContract.Calendars.OWNER_ACCOUNT,                   // 3
            CalendarContract.Events.CALENDAR_ID,                        // 4
            CalendarContract.Events._ID,                                // 5
            CalendarContract.Events.TITLE,                              // 6
            CalendarContract.Events.DESCRIPTION,                        // 7
            CalendarContract.Events.DTSTART,                            // 8
            CalendarContract.Events.DTEND,                              // 9
            CalendarContract.Events.EVENT_TIMEZONE                      // 10
    };

    public class PermissionType{
        private static final int PROJECTION_ID_INDEX = 0;
        private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
        private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
        private static final int PROJECTION_CALENDAR_ID = 4;
        private static final int PROJECTION_EVENT_ID = 5;
        private static final int PROJECTION_EVENT_TITLE = 6;
        private static final int PROJECTION_EVENT_DESCRIPTION = 7;
        private static final int PROJECTION_EVENT_DTSTART = 8;
        private static final int PROJECTION_EVENT_DTEND = 9;
        private static final int PROJECTION_EVENT_TIMEZONE = 10;
    }
    public static final String[] PERMISSION_PROJECTION = new String[]{
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_PHONE_STATE
    };

    public static final String[] ADDRESS_PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
    };
    public static final String[] ADDRESS_PHONE_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
}

