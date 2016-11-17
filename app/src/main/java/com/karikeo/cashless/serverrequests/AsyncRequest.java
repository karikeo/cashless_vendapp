package com.karikeo.cashless.serverrequests;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.karikeo.cashless.Constants;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.Map;

abstract class AsyncRequest {
    private final String TAG = this.getClass().getSimpleName();

    protected final String SOAP_ACTION;
    protected final String METHOD_NAME;

    protected static final String NAMESPACE = "http://badplanet.ddns.net/";
    protected static final String URL = "http://badplanet.ddns.net:11307/WebService.asmx";

    static final String STATUS = "status";


    protected OnAsyncServerRequest listener;


    protected AsyncRequest(String method_name){
        SOAP_ACTION = new StringBuilder(NAMESPACE).append(method_name).toString();
        METHOD_NAME = method_name;
    }

    public void execute(){
        AsyncTaskRequest request = new AsyncTaskRequest();
        request.execute();
    }

    private SoapObject response;

    private class AsyncTaskRequest extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            //Call abstract function. Here user must setup additional parameters required for request.
            fillWithUserParams(request);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setAddAdornments(false);
            envelope.dotNet = true;
            envelope.skipNullProperties = true;
            envelope.implicitTypes = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            try {
                if (Constants.DEBUG != 0){
                    androidHttpTransport.debug = true;
                }

                androidHttpTransport.call(SOAP_ACTION, envelope);

                if (Constants.DEBUG != 0) {
                    Log.d(TAG, new StringBuilder("request: ").append(androidHttpTransport.requestDump).toString());
                    Log.d(TAG, new StringBuilder("response: ").append(androidHttpTransport.responseDump).toString());
                }

                response = (SoapObject) envelope.getResponse();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            Bundle b = new Bundle();
            getAllProperties(response, b);
            AsyncRequest.this.onPostExecute(b);
        }
    }

    private void getAllProperties(SoapObject ob, Bundle b) {

        if (ob.toString().indexOf("Inserted to database") != -1) {
            return;
        }

        try {
            ob = (SoapObject) ob.getProperty("diffgram");
            ob = (SoapObject) ob.getProperty("NewDataSet");
            ob = (SoapObject) ob.getProperty("Table");

            for (String s : PropertyFields.ALL_PROPERTIES) {
                if (ob.hasProperty(s)) {
                    b.putString(s, ob.getPrimitivePropertyAsString(s));
                }
            }
        }catch (RuntimeException e){
            Log.d(TAG, "Wrong message from server");
        }
    }


    private void fillWithUserParams(SoapObject request) {
        Map<String, String> map = new HashMap<>();

        onExecute(map);

        for (String key : map.keySet()){
            request.addProperty(key, map.get(key));
        }
    }

    protected abstract void onExecute(Map<String, String> key_value);

    /*In case of error response null*/
    protected abstract void onPostExecute(Bundle bundle);
}
