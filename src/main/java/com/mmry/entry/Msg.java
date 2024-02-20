package com.mmry.entry;

import java.io.Serializable;

public class Msg implements Serializable {
    private String code;
    private String content;

    public Msg(String code, String content) {
        this.code = code;
        this.content = content;
    }

    public Msg() {
    }

    @Override
    public String toString() {
        return "Msg{" +
                "code='" + code + '\'' +
                ", content='" + content + '\'' +
                '}';
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
}
