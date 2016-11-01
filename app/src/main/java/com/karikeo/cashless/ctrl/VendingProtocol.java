package com.karikeo.cashless.ctrl;

/*Protocol wrapper for the Vending device*/
public class VendingProtocol {
    private static final String TAG = "com.karikeo.cashless.ctrl.VendingProtocol";

    private static final String COMMAND_BALANCE = "BALANCE=";
    private static final String COMMAND_CANCEL = "CANCEL";

    public interface onDataIncome{
        void onData(byte[] buf);
    }

    public String sendBalance(final int balance){
        return sendBalance(Integer.toString(balance));
    }

    public String sendBalance(final String balance){
        return new String(COMMAND_BALANCE + balance);
    }

    public String sendCancel(){
        return new String(COMMAND_CANCEL);
    }

}
