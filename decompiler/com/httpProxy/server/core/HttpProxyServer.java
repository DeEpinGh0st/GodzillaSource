package com.httpProxy.server.core;

import com.httpProxy.server.CertPool;
import com.httpProxy.server.request.HttpRequest;
import com.httpProxy.server.response.HttpResponse;
import com.httpProxy.server.response.HttpResponseStatus;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ssl.SSLSocket;








public class HttpProxyServer
{
  private int listenPort;
  private int backlog;
  private InetAddress bindAddr;
  private boolean nextSocket = true;
  private CertPool certPool;
  private ServerSocket serverSocket;
  private HttpProxyHandle proxyHandle;
  
  public HttpProxyServer(int listenPort, int backlog, InetAddress bindAddr, CertPool certPool, HttpProxyHandle proxyHandle) {
    this.listenPort = listenPort;
    this.backlog = backlog;
    this.bindAddr = bindAddr;
    this.certPool = certPool;
    this.proxyHandle = proxyHandle;
  }


  
  public boolean startup() {
    try {
      this.serverSocket = new ServerSocket(this.listenPort, this.backlog, this.bindAddr);
    
    }
    catch (Exception e) {
      e.printStackTrace();
      return false;
    } 
    
    return acceptService(this.serverSocket);
  }
  public void shutdown() {
    try {
      this.serverSocket.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public void handler(Socket socket, HttpRequest httpRequest) throws Exception {
    try {
      if (this.proxyHandle != null) {
        this.proxyHandle.handler(socket, httpRequest);
      } else {
        HttpResponse httpResponse = new HttpResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, null, "No Input HttpProxyHandle");
        socket.getOutputStream().write(httpResponse.encode());
      } 
    } catch (Exception e) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(byteArrayOutputStream);
      e.printStackTrace(printStream);
      printStream.flush();
      printStream.close();
      HttpResponse httpResponse = new HttpResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, null, byteArrayOutputStream.toByteArray());
      byteArrayOutputStream.close();
      socket.getOutputStream().write(httpResponse.encode());
    } 

    
    closeSocket(socket);
  }
  
  private boolean acceptService(ServerSocket sslServerSocket) {
    (new Thread(() -> {
          while (this.nextSocket) {
            Socket sslSocket;
            
            try {
              sslSocket = sslServerSocket.accept();
            } catch (IOException e) {
              return;
            } 























            
            (new Thread(())).start();
          } 
        })).start();
    return true;
  }
  protected void closeSocket(Socket socket) {
    if (socket == null && !socket.isClosed()) {
      return;
    }
    try {
      if (!socket.isClosed()) {
        socket.close();
      }
    } catch (IOException iOException) {}
  }


  
  public HttpProxyHandle getProxyHandle() {
    return this.proxyHandle;
  }
  
  public void setProxyHandle(HttpProxyHandle proxyHandle) {
    this.proxyHandle = proxyHandle;
  }
  
  public boolean isNextSocket() {
    return this.nextSocket;
  }
  
  public void setNextSocket(boolean nextSocket) {
    this.nextSocket = nextSocket;
  }
}
