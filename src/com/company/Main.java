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

/**
 * Класс использующийся для старта сервера.
 */
public class Main {
    /**
     * Сервер, который будет принимать входящие соединения.
     */
    private static TServer server;
    /**
     * Порт для сервера.
     */
    private static int port;
    /**
     * Логгер для логирования процесса работы сервера.
     */
    private static Logger logger;

    /**
     * Точка входа серверного приложения.
     * @param args параметры используемые при запуске
     */
    public static void main(String[] args) {
        ConfigReader configReader = new ConfigReader();
        LoggerContext context = (LoggerContext)LogManager.getContext(false);
        logger = LogManager.getLogger("MyLogger");
        port = configReader.getPort();
        logger.log(Level.INFO,"Server is running on port "+ port);
	    WebPatternDBHandler webPatternDBHandler = new WebPatternDBHandler(configReader.getUserName(), configReader.getUserPass());
            try{
                WebPatternDB.Processor webPatternDB = new WebPatternDB.Processor(webPatternDBHandler);
                Runnable connectionThread = new Runnable() {
                    @Override
                    public void run() {
                        startServer(webPatternDB);}
                };
                new Thread(connectionThread).start();
                Runtime.getRuntime().addShutdownHook(new Thread(){
                    @Override
                    public void run() {
                        logger.log(Level.INFO,"Closing connection");
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

    /**
     * Запускает сервер.
     * @param processor экземпляр класса унаследованного от сгенерированного thrift класса
     */
    private static void startServer(WebPatternDB.Processor processor) {
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
