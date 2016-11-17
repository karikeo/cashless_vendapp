package com.karikeo.cashless.db;


public class Transaction {
    private long id;
    private String type;
    private String macAddress;
    private String date;
    private String balanceDelta;
    private boolean status;
    private String email;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getMacAddress() {
        return macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getBalanceDelta() {
        return balanceDelta;
    }
    public void setBalanceDelta(String balanceDelta) {
        this.balanceDelta = balanceDelta;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(boolean ok){status = ok;}
    public boolean getStatus(){return status;}
}
