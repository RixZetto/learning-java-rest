package com.rixstudio.learning.miniserver.controller;

import java.util.List;

import org.json.JSONArray;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rixstudio.learning.miniserver.database.Database;
import com.rixstudio.learning.miniserver.database.Device;
import com.rixstudio.learning.miniserver.database.DeviceType;
import com.rixstudio.learning.miniserver.exception.DatabaseException;

@RestController
public class AppleRestController {

    @RequestMapping("/ping")
    public String ping(@RequestParam(value = "m", defaultValue = "no message") String message) {
        return "pong: " + message;
    }

    @RequestMapping(path = "/apns/register")
    public void registerDevice(@RequestParam(value = "token") String deviceToken) {
        try {
            Database.instance.registerDevice(deviceToken, DeviceType.IOS);
            List<Device> devices = Database.instance.listDevices(DeviceType.IOS);
            if (devices != null) {
                for (Device device : devices) {
                    System.out.println(" - " + device.getToken());
                }
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(path = "/apns/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String listDevices() {
        try {
            List<Device> devices = Database.instance.listDevices(null);
            if (devices != null) {
                for (Device device : devices) {
                    System.out.println(" - " + device.getToken());
                }
            }
            JSONArray array = new JSONArray(devices);
            return array.toString();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
