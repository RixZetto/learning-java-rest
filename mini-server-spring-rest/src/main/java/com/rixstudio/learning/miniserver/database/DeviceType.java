package com.rixstudio.learning.miniserver.database;

public enum DeviceType {
    ANDROID, IOS

    ;

    public static int toInt(DeviceType type) {
        if (type == null)
            return 0;
        if (type == DeviceType.ANDROID)
            return 1;
        return 2;
    }

    public static DeviceType fromInt(Integer value) {
        if (value == null)
            return null;
        if (value == 0)
            return ANDROID;
        return IOS;
    }

}
