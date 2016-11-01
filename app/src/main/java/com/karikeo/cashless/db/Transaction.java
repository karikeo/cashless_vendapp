package com.karikeo.cashless.db;


public class Transaction {
    private long id;
    private String type;
    private String macAddr;
    private String date;
    private String balanceDelta;

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

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
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
}
