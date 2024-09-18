package com.mmry.entry;

import java.io.Serializable;
import java.util.List;

public class Msg implements Serializable {
    private String code;
    public String content;
    public List<String> drivers;
    public List<String> folderNames;
    private List<FileEntity> data;

    public List<String> getFolderNames() {
        return folderNames;
    }

    public void setFolderNames(List<String> folderNames) {
        this.folderNames = folderNames;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<String> drivers) {
        this.drivers = drivers;
    }

    public List<FileEntity> getData() {
        return data;
    }

    public void setData(List<FileEntity> data) {
        this.data = data;
    }
}
