package org.apache.log4j.chainsaw;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;






















class LoggingReceiver
  extends Thread
{
  private static final Logger LOG = Logger.getLogger(LoggingReceiver.class);

  
  private MyTableModel mModel;

  
  private ServerSocket mSvrSock;

  
  private class Slurper
    implements Runnable
  {
    private final Socket mClient;
    
    private final LoggingReceiver this$0;

    
    Slurper(Socket aClient) {
      this.mClient = aClient;
    }

    
    public void run() {
      LoggingReceiver.LOG.debug("Starting to get data");
      try {
        ObjectInputStream ois = new ObjectInputStream(this.mClient.getInputStream());
        
        while (true) {
          LoggingEvent event = (LoggingEvent)ois.readObject();
          LoggingReceiver.this.mModel.addEvent(new EventDetails(event));
        } 
      } catch (EOFException e) {
        LoggingReceiver.LOG.info("Reached EOF, closing connection");
      } catch (SocketException e) {
        LoggingReceiver.LOG.info("Caught SocketException, closing connection");
      } catch (IOException e) {
        LoggingReceiver.LOG.warn("Got IOException, closing connection", e);
      } catch (ClassNotFoundException e) {
        LoggingReceiver.LOG.warn("Got ClassNotFoundException, closing connection", e);
      } 
      
      try {
        this.mClient.close();
      } catch (IOException e) {
        LoggingReceiver.LOG.warn("Error closing connection", e);
      } 
    }
  }













  
  LoggingReceiver(MyTableModel aModel, int aPort) throws IOException {
    setDaemon(true);
    this.mModel = aModel;
    this.mSvrSock = new ServerSocket(aPort);
  }

  
  public void run() {
    LOG.info("Thread started");
    try {
      while (true) {
        LOG.debug("Waiting for a connection");
        Socket client = this.mSvrSock.accept();
        LOG.debug("Got a connection from " + client.getInetAddress().getHostName());
        
        Thread t = new Thread(new Slurper(client));
        t.setDaemon(true);
        t.start();
      } 
    } catch (IOException e) {
      LOG.error("Error in accepting connections, stopping.", e);
      return;
    } 
  }
}
