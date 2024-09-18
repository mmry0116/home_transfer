package com.mmry.entry;

import java.time.LocalDateTime;

/**
 * @author : 明明如月
 * @date : 2024/4/6 18:15
 */
public class FileEntity {
    private String name;
    private String path;//文件路径 不包含最后名字
    private String type;
    private String size;
    private byte sortPriority;
    private String creationTime;
    private String lastAccessTime;
    private String lastModifiedTime;

    public String getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(String lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public byte getSortPriority() {
        return sortPriority;
    }

    public void setSortPriority(byte sortPriority) {
        this.sortPriority = sortPriority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


}
