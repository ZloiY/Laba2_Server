package com.company;

import com.company.thrift.WebPatternDB;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class Main {

    private static TServer server;
    private static LogThread logThread;

    public static void main(String[] args) {
	    WebPatternDBHandler webPatternDBHandler = new WebPatternDBHandler(logThread);
	    logThread = new LogThread();
	    logThread.start();
        try{
            WebPatternDB.Processor webPatternDB = new WebPatternDB.Processor(webPatternDBHandler);
            Runnable connectionThread = new Runnable() {
                @Override
                public void run() {getClient(webPatternDB);}
            };
            new Thread(connectionThread).start();
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {

                    webPatternDBHandler.closeConnection();
                    logThread.log("Stopping server.");
                    logThread.closeThread();
                    if (server.isServing()||server!=null)
                    server.stop();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void getClient(WebPatternDB.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(1488);
            server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            logThread.log("Server is running");
            server.serve();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
