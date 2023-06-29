package com.sfdex.transgenic;

import com.google.gson.Gson;
import com.sfdex.transgenic.model.Model;

import java.io.BufferedReader;
import java.io.FileReader;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DeviceModel implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("DeviceModel handleLoadPackage:" + lpparam.packageName);
        hookSystemPropertiesGet(lpparam);
    }

    private void hookSystemPropertiesGet(XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        XC_MethodHook.Unhook unhook = XposedHelpers.findAndHookMethod(Class.forName("android.os.SystemProperties"), "get", "java.lang.String", "java.lang.String", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                try {
                    setSystemProperties();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        boolean hookRet = unhook != null/* && unhook.getHookedMethod() != null && unhook.getHookedMethod().getName() != null*/;
        XposedBridge.log("hookSystemPropertiesGet DeviceModel:" + lpparam.packageName + "hook ret: " + (hookRet ? "success" : "failed"));
        if (hookRet) {
            setSystemProperties();
        }
    }

    private void setSystemProperties() throws Exception {
        Class<?> clz = Class.forName("android.os.Build");
        Model model;
        try (BufferedReader bfr = new BufferedReader(new FileReader("/data/local/tmp/genetically-modified-phone.json"))) {
            String modelInfoStr = bfr.readLine();
            XposedBridge.log("DeviceModel: modelInfoStr: " + modelInfoStr);
            model = new Gson().fromJson(modelInfoStr, Model.class);
        } catch (Exception e) {
            XposedBridge.log("DeviceModel: parse cached device info error: " + e);
            e.printStackTrace();
            return;
        }

        XposedHelpers.setStaticObjectField(clz, "MANUFACTURER", model.getManufacturer());
        XposedHelpers.setStaticObjectField(clz, "BRAND", model.getBrand());
        XposedHelpers.setStaticObjectField(clz, "MODEL", model.getModel());
        XposedHelpers.setStaticObjectField(clz, "DEVICE", model.getDevice());
        XposedHelpers.setStaticObjectField(clz, "BOARD", model.getBoard());
        XposedHelpers.setStaticObjectField(clz, "PRODUCT", model.getProduct());
    }
}
