package com.company;

import com.company.thrift.WebPatternDB;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import java.io.File;


public class Main {

    private static TServer server;
    private static int port;
    private static Logger logger;

    public static void main(String[] args) {
        LoggerContext context = (LoggerContext)LogManager.getContext(false);
        context.setConfigLocation(new File("C:\\Users\\ZloiY\\IdeaProjects\\Laba2_Server\\src\\log4j2.xml").toURI());
        logger = LogManager.getLogger();
        ConfigReader configReader = new ConfigReader();
        port = configReader.getPort();
        logger.log(Level.INFO,"Server is running on port "+ port);
	    WebPatternDBHandler webPatternDBHandler = new WebPatternDBHandler(configReader.getUserName(), configReader.getUserPass());
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
                        logger.log(Level.INFO,"Stopping server.");
                        Configurator.shutdown(context);
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
            logger.log(Level.INFO,"Awaiting client");
            server.serve();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
