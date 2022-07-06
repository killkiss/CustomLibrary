package com.fileopen.jzb;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * create：2022/5/7 10:14
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class VerifyDevice {

    public static boolean isSuccess = false;

    /**
     * 模拟器验证结果
     *
     * @return
     */
    public static boolean verify(Context context) {
        if (notHasLightSensorManager(context)) {
            isSuccess = true;
        } else if (ifFeatures()) {
            isSuccess = true;
        } else if (checkIsNotRealPhone()) {
            isSuccess = true;
        }
        return isSuccess;
    }

    /**
     * 依据是否存在 光传感器 来判断是否为模拟器
     *
     * @param context
     * @return
     */
    private static boolean notHasLightSensorManager(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (sensor == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据部分特征参数设备信息来判断是否为模拟器
     *
     * @return
     */
    private static boolean ifFeatures() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    /*
     *根据CPU是否为电脑来判断是否为模拟器
     *返回:true 为模拟器
     */
    private static boolean checkIsNotRealPhone() {
        String cpuInfo = readCpuInfo();
        if ((cpuInfo.contains("intel") || cpuInfo.contains("amd"))) {
            return true;
        }
        return false;
    }

    /**
     * 根据 CPU 是否为电脑来判断是否为模拟器
     *
     * @return
     */
    private static String readCpuInfo() {
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            Process process = processBuilder.start();
            StringBuffer stringBuffer = new StringBuffer();
            String readLine = "";
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            while ((readLine = responseReader.readLine()) != null) {
                stringBuffer.append(readLine);
            }
            responseReader.close();
            result = stringBuffer.toString().toLowerCase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
