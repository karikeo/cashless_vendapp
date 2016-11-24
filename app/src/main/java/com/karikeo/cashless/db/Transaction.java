package com.karikeo.cashless.db;


public class Transaction {
    public enum TYPE {
        BALANCE,
        CANCEL,
        TIMEOUT,
        COMPLETE,
        FAIL,
        UNKNOWN;

        @Override
        public String toString() {
            return super.toString();
        }

        public static TYPE fromString(String type){
            switch (type){
                case "BALANCE":
                    return BALANCE;
                case "CANCEL":
                    return CANCEL;
                case "TIMEOUT":
                    return TIMEOUT;
                case "COMPLETE":
                    return COMPLETE;
                case "FAIL":
                    return FAIL;
                default:
                    return UNKNOWN;
            }
        }
    }
    private long id;
    private String macAddress;
    private String date;
    private String balanceDelta;
    private String email;
    private TYPE type;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public TYPE getType() {
        return type;
    }
    public void setType(TYPE type) {
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
}
