package com.jzb.pluginmoudle.main.bean;

/**
 * create：2022/7/5 10:46
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class ModuleBean {
    private String name;
    private String apkName;
    private String version;
    private String downloadURL;
    private String packageID;
    public ModuleBean(String name,String apkName,String version,String downloadURL,String packageID){
        setName(name);
        setApkName(apkName);
        setVersion(version);
        setDownloadURL(downloadURL);
        setPackageID(packageID);
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }

    public String getPackageID() {
        return packageID;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public String getApkName() {
        return apkName;
    }
}
