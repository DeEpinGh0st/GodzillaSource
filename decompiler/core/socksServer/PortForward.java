package core.socksServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import util.Log;

public class PortForward
  implements SocketStatus {
  private InetSocketAddress socketAddress;
  private HttpToSocks httpToSocks;
  private ServerSocket serverSocket;
  private String destHost;
  private String destPort;
  private String errMsg;
  private boolean alive;
  
  public PortForward(InetSocketAddress socketAddress, HttpToSocks httpToSocks, String destHost, String destPort) {
    this.socketAddress = socketAddress;
    this.httpToSocks = httpToSocks;
    this.destHost = destHost;
    this.destPort = destPort;
    this.alive = true;
    try {
      this.serverSocket = new ServerSocket();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }


  
  public String getErrorMessage() {
    return this.errMsg;
  }

  
  public boolean isActive() {
    return this.alive;
  }
  
  public void handle() {
    try {
      while (this.httpToSocks.isAlive() && this.alive) {
        Socket client = this.serverSocket.accept();
        SocksRelayInfo socksRelayInfo = new SocksRelayInfo(this.httpToSocks.socksServerConfig.clientSocketOnceReadSize.get(), this.httpToSocks.socksServerConfig.capacity.get());
        socksRelayInfo.setClient(client);
        socksRelayInfo.setDestHost(this.destHost);
        socksRelayInfo.setDestPort(Short.decode(this.destPort).shortValue());
        if (this.httpToSocks.addRelaySocket(socksRelayInfo)) {
          (new Thread(socksRelayInfo::startConnect)).start();
        }
      } 
    } catch (Exception e) {
      stop();
    } 
    stop();
  }
  
  public boolean start() {
    try {
      this.serverSocket.bind(this.socketAddress);
      (new Thread(this::handle)).start();
    } catch (Exception e) {
      this.errMsg = e.getLocalizedMessage();
      stop();
    } 
    return this.alive;
  }

  
  public boolean stop() {
    if (this.alive) {
      this.alive = false;
      if (this.serverSocket != null) {
        try {
          this.serverSocket.close();
        } catch (IOException e) {
          Log.error(e);
        } 
      }
    } 
    return !this.alive;
  }


  
  public InetSocketAddress getSocketAddress() {
    return this.socketAddress;
  }
  
  public void setSocketAddress(InetSocketAddress socketAddress) {
    this.socketAddress = socketAddress;
  }
  
  public HttpToSocks getHttpToSocks() {
    return this.httpToSocks;
  }
  
  public void setHttpToSocks(HttpToSocks httpToSocks) {
    this.httpToSocks = httpToSocks;
  }
}
