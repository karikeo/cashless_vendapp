package com.karikeo.cashless.ctrl;

import android.util.Log;

/*Protocol wrapper for the Vending device*/
public class VendingProtocol implements IMessageListener{
    private static final String TAG = "com.karikeo.cashless.ctrl.VendingProtocol";

    private static final String COMMAND_BALANCE = "BALANCE=";
    private static final String COMMAND_CANCEL = "CANCEL";

    private static final String MESSAGE_DIVIDER = ";";

    private static final String TYPE_VEND = "VEND";
    private static final String TYPE_STATUS_OK = "OK";


    public void onMessage(String message) {
        String parts[] = message.split(MESSAGE_DIVIDER);

        if (parts.length < 3){
            Log.d(TAG, "Unknown message:" + message);
            return;
        }

        if (!parts[0].equals(TYPE_VEND) || !parts[1].equals(TYPE_STATUS_OK)){
            Log.d(TAG, "Something wrong:" + message);
        }
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
