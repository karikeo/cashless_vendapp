package com.karikeo.cashless;


import android.app.Application;

import com.karikeo.cashless.bt.CommInterface;
import com.karikeo.cashless.db.TransactionDataSource;
import com.karikeo.cashless.protocol.CoderDecoderInterface;
import com.karikeo.cashless.protocol.CommandInterface;
import com.karikeo.cashless.serverrequests.BalanceUpdater;

public class CashlessApplication extends Application{

    private CommInterface commInterface;
    private CoderDecoderInterface codeInterface;
    private CommandInterface command;
    private TransactionDataSource dbAccess;
    private BalanceUpdater bUpdater;


    public CommInterface getCommInterface(){
        return commInterface;
    }
    public void  setCommInterface(CommInterface comm){
        commInterface = comm;
    }

    public CoderDecoderInterface getCoderDecoderInterface() {return codeInterface;}
    public void setCoderDecoderInterface(CoderDecoderInterface codeInterface){this.codeInterface = codeInterface;}

    public CommandInterface getCommandInterface(){return command;}
    public void setCommandInterface(CommandInterface comm){command = comm;}

    public TransactionDataSource getDbAccess() {return dbAccess;}
    public void setTransactionAccess(TransactionDataSource db){ dbAccess = db;}

    public BalanceUpdater getBalanceUpdater(){return bUpdater;}
    public void setBalanceUpdater(BalanceUpdater b){bUpdater = b;}


    @Override
    public void onTerminate() {
        super.onTerminate();

        if (commInterface != null){
            if (commInterface.isConnected())
                commInterface.closeConnection();
        }
    }
}
