package com.company;

import com.company.thrift.WebPatternDB;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class Main {

    private static TServer server;
    private static LogThread logThread;
    private static int port;

    public static void main(String[] args) {
        logThread = new LogThread();
        ConfigReader configReader = new ConfigReader();
        port = configReader.getPort();
        logThread.log("Server is running on port "+ port);
	    WebPatternDBHandler webPatternDBHandler = new WebPatternDBHandler(logThread, configReader.getUserName(), configReader.getUserPass());
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
            TServerTransport serverTransport = new TServerSocket(port);
            server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
            logThread.log("Awaiting client");
            server.serve();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
