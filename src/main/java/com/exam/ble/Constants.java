package com.exam.ble;

import java.util.UUID;

public class Constants {
    // used to identify adding bluetooth names
    public final static int REQUEST_ENABLE_BT = 3054;
    // used to request fine location permission
    public final static int REQUEST_FINE_LOCATION = 3055;
    // used to request coarse location permission
    public final static int REQUEST_COARSE_LOCATION = 3056;
    // scan period in milliseconds
    public final static int SCAN_PERIOD = 5000;

    public static String SERVICE_STRING = "00001101-0000-1000-8000-00805f9b34fb";
    public static final UUID SERVICE_UUID = UUID.fromString(SERVICE_STRING);
    //public static String SERVICE_STRING = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
    //public static final UUID SERVICE_UUID = UUID.fromString(SERVICE_STRING);

    public static String SEND_CHARACTERISTIC_UUID = "00001142-0000-1000-8000-00805f9b34fb";
    //public static String SEND_CHARACTERISTIC_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26fa";

    //public static String CONFIG_UUID = "00001142-0000-1000-8000-00805f9b34fb";
    public static String CONFIG_UUID = "00002902-0000-1000-8000-00805f9b34fb";
}
