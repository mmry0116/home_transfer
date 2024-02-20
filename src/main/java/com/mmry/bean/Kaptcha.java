package com.mmry.bean;

/**
 * @author : 明明如月
 * @date : 2024/2/19 9:55
 */
public class Kaptcha {
    private String uuid;
    private String kaptcha;

    @Override
    public String toString() {
        return "Kaptcha{" +
                "uuid='" + uuid + '\'' +
                ", kaptcha='" + kaptcha + '\'' +
                '}';
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getKaptcha() {
        return kaptcha;
    }

    public void setKaptcha(String kaptcha) {
        this.kaptcha = kaptcha;
    }
}
