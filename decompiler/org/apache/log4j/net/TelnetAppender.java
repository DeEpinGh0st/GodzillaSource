package org.apache.log4j.net;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;














































public class TelnetAppender
  extends AppenderSkeleton
{
  private SocketHandler sh;
  private int port = 23;



  
  public boolean requiresLayout() {
    return true;
  }


  
  public void activateOptions() {
    try {
      this.sh = new SocketHandler(this.port);
      this.sh.start();
    }
    catch (InterruptedIOException e) {
      Thread.currentThread().interrupt();
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (RuntimeException e) {
      e.printStackTrace();
    } 
    super.activateOptions();
  }

  
  public int getPort() {
    return this.port;
  }

  
  public void setPort(int port) {
    this.port = port;
  }


  
  public void close() {
    if (this.sh != null) {
      this.sh.close();
      try {
        this.sh.join();
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      } 
    } 
  }


  
  protected void append(LoggingEvent event) {
    if (this.sh != null) {
      this.sh.send(this.layout.format(event));
      if (this.layout.ignoresThrowable()) {
        String[] s = event.getThrowableStrRep();
        if (s != null) {
          StringBuffer buf = new StringBuffer();
          for (int i = 0; i < s.length; i++) {
            buf.append(s[i]);
            buf.append("\r\n");
          } 
          this.sh.send(buf.toString());
        } 
      } 
    } 
  }




  
  protected class SocketHandler
    extends Thread
  {
    private Vector writers = new Vector();
    private Vector connections = new Vector();
    private ServerSocket serverSocket;
    private int MAX_CONNECTIONS = 20; private final TelnetAppender this$0;
    
    public void finalize() {
      close();
    }




    
    public void close() {
      synchronized (this) {
        for (Enumeration e = this.connections.elements(); e.hasMoreElements();) {
          
          try { ((Socket)e.nextElement()).close(); }
          catch (InterruptedIOException ex)
          { Thread.currentThread().interrupt(); }
          catch (IOException ex) {  }
          catch (RuntimeException ex) {}
        } 
      } 


      
      try { this.serverSocket.close(); }
      catch (InterruptedIOException ex)
      { Thread.currentThread().interrupt(); }
      catch (IOException ex) {  }
      catch (RuntimeException ex) {}
    }


    
    public synchronized void send(String message) {
      Iterator ce = this.connections.iterator();
      for (Iterator e = this.writers.iterator(); e.hasNext(); ) {
        ce.next();
        PrintWriter writer = e.next();
        writer.print(message);
        if (writer.checkError()) {
          ce.remove();
          e.remove();
        } 
      } 
    }



    
    public void run() {
      while (true)
      { if (!this.serverSocket.isClosed()) {
          try {
            Socket newClient = this.serverSocket.accept();
            PrintWriter pw = new PrintWriter(newClient.getOutputStream());
            if (this.connections.size() < this.MAX_CONNECTIONS) {
              synchronized (this) {
                this.connections.addElement(newClient);
                this.writers.addElement(pw);
                pw.print("TelnetAppender v1.0 (" + this.connections.size() + " active connections)\r\n\r\n");
                
                pw.flush();
              }  continue;
            } 
            pw.print("Too many connections.\r\n");
            pw.flush();
            newClient.close();
            continue;
          } catch (Exception e) {
            if (e instanceof InterruptedIOException || e instanceof InterruptedException) {
              Thread.currentThread().interrupt();
            }
            if (!this.serverSocket.isClosed()) {
              LogLog.error("Encountered error while in SocketHandler loop.", e);
            }
          } 
        } else {
          break;
        } 
        try {
          this.serverSocket.close();
        } catch (InterruptedIOException ex) {
          Thread.currentThread().interrupt();
        } catch (IOException ex) {} return; }  try { this.serverSocket.close(); } catch (InterruptedIOException interruptedIOException) { Thread.currentThread().interrupt(); } catch (IOException iOException) {}
    }

    
    public SocketHandler(int port) throws IOException {
      this.serverSocket = new ServerSocket(port);
      setName("TelnetAppender-" + getName() + "-" + port);
    }
  }
}
