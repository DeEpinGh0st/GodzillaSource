package org.apache.log4j.varia;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import org.apache.log4j.helpers.LogLog;




































































































































class HUPNode
  implements Runnable
{
  Socket socket;
  DataInputStream dis;
  DataOutputStream dos;
  ExternallyRolledFileAppender er;
  
  public HUPNode(Socket socket, ExternallyRolledFileAppender er) {
    this.socket = socket;
    this.er = er;
    try {
      this.dis = new DataInputStream(socket.getInputStream());
      this.dos = new DataOutputStream(socket.getOutputStream());
    } catch (InterruptedIOException e) {
      Thread.currentThread().interrupt();
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (RuntimeException e) {
      e.printStackTrace();
    } 
  }
  
  public void run() {
    try {
      String line = this.dis.readUTF();
      LogLog.debug("Got external roll over signal.");
      if ("RollOver".equals(line)) {
        synchronized (this.er) {
          this.er.rollOver();
        } 
        this.dos.writeUTF("OK");
      } else {
        
        this.dos.writeUTF("Expecting [RollOver] string.");
      } 
      this.dos.close();
    } catch (InterruptedIOException e) {
      Thread.currentThread().interrupt();
      LogLog.error("Unexpected exception. Exiting HUPNode.", e);
    } catch (IOException e) {
      LogLog.error("Unexpected exception. Exiting HUPNode.", e);
    } catch (RuntimeException e) {
      LogLog.error("Unexpected exception. Exiting HUPNode.", e);
    } 
  }
}
