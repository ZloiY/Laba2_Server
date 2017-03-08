package com.company;

import com.company.thrift.InvalidRequest;
import com.company.thrift.Operation;
import com.company.thrift.WebPatternDB;
import com.company.thrift.WorkWithClient;
import javafx.scene.image.Image;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import java.io.*;
import java.nio.ByteBuffer;

public class Main {

    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[10]);
	    WebPatternDBHandler webPatternDBHandler = new WebPatternDBHandler();
	    try {
            InputStream inputStream = new FileInputStream("C:\\Users\\ZloiY\\IdeaProjects\\Laba2_Server\\MVC.png");
            byteBuffer.clear();
            byteBuffer = ByteBuffer.allocate(inputStream.read());
        }catch (IOException e){
	        e.printStackTrace();
        }
        WorkWithClient workWithClient1 = new WorkWithClient(1, "MVC", "example", byteBuffer);
        WorkWithClient workWithClient2 = new WorkWithClient(1, "MVC", "new example", byteBuffer);
      /*  try {
            webPatternDBHandler.workWithRequest(1, Operation.INSERT, workWithClient1, workWithClient2);
            webPatternDBHandler.workWithRequest(1, Operation.EDIT, workWithClient1, workWithClient2);
        }catch (InvalidRequest request){
            request.printStackTrace();
        }*/
        webPatternDBHandler.closeConnection();
        try{
            WebPatternDB.Processor webPatternDB = new WebPatternDB.Processor(webPatternDBHandler);
            Runnable connectionThread = new Runnable() {
                @Override
                public void run() {getClient(webPatternDB);}
            };
            new Thread(connectionThread).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void getClient(WebPatternDB.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(1488);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            server.serve();
            System.out.println("server is running");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
