package com.mmry.bean;


import java.time.LocalDateTime;

public class PersistentLogins {
    private String userName ;
    private String series ;
    private String token;
    private LocalDateTime lastUsed;

    public PersistentLogins() {
    }

    public PersistentLogins(String userName, String series, String token, LocalDateTime lastUsed) {
        this.userName = userName;
        this.series = series;
        this.token = token;
        this.lastUsed = lastUsed;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }
}
