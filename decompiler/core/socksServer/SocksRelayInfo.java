package core.socksServer;

import core.EasyI18N;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;
import util.Log;
import util.http.Parameter;





public class SocksRelayInfo
  implements SocketStatus
{
  private int packetMaxSize = 51200;
  
  private byte version;
  
  private byte command;
  
  private short destPort;
  private String destHost;
  private String bindHost;
  private short bindPort;
  private String userId;
  private Socket client;
  private boolean alive;
  private final LinkedBlockingDeque<Parameter> readtaskList;
  private final LinkedBlockingDeque<Parameter> writetaskList;
  private String socketId;
  private boolean isBind;
  public byte[] connectSuccessMessage;
  private String errMsg;
  
  public SocksRelayInfo(int packetMaxSize, int capacity) {
    this.packetMaxSize = packetMaxSize;
    this.alive = true;
    this.userId = "null";
    this.readtaskList = new LinkedBlockingDeque<>(capacity);
    this.writetaskList = new LinkedBlockingDeque<>();
    this.socketId = UUID.randomUUID().toString().replace("-", "");
  }
  
  public byte getVersion() {
    return this.version;
  }
  
  public void setVersion(byte version) {
    this.version = version;
  }
  
  public byte getCommand() {
    return this.command;
  }
  
  public void setCommand(byte command) {
    this.command = command;
  }
  
  public short getDestPort() {
    return this.destPort;
  }
  
  public void setDestPort(short destPort) {
    this.destPort = destPort;
  }
  
  public String getDestHost() {
    return this.destHost;
  }
  
  public void setDestHost(String destHost) {
    this.destHost = destHost;
  }
  
  public String getBindHost() {
    return this.bindHost;
  }
  
  public void setBindHost(String bindHost) {
    this.bindHost = bindHost;
  }
  
  public short getBindPort() {
    return this.bindPort;
  }
  
  public void setBindPort(short bindPort) {
    this.bindPort = bindPort;
  }
  
  public String getUSERID() {
    return this.userId;
  }
  
  public void setUSERID(String USERID) {
    this.userId = USERID;
  }

  
  public Socket getRelaySocket() {
    return this.client;
  }
  public Socket getSocket() {
    return this.client;
  }
  
  public Socket getClient() {
    return this.client;
  }
  
  public void setClient(Socket client) {
    this.client = client;
  }
  
  public boolean isAlive() {
    return this.alive;
  }
  
  public int getReadTaskSize() {
    return this.readtaskList.size();
  }
  
  public void setAlive(boolean alive) {
    this.alive = alive;
  }
  
  public Parameter ReadTaskData(boolean remove) {
    try {
      if (remove) {
        return this.readtaskList.poll();
      }
      return this.readtaskList.getFirst();
    }
    catch (Exception e) {
      return null;
    } 
  }
  
  public void pushReadTask(Parameter reqParameter) {
    try {
      this.readtaskList.put(reqParameter);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }

  
  public void pushWriteTask(Parameter resParameter) {
    if (!this.alive && resParameter.getParameterByteArray("type")[0] == 3) {
      return;
    }
    
    this.writetaskList.add(resParameter);
  }
  
  public boolean startBind(String bindHost, String bindPort) {
    Thread.currentThread().setName(Thread.currentThread().getStackTrace()[1].getMethodName());
    this.isBind = true;
    Parameter reqParameter = HttpToSocks.createParameter((byte)6);
    reqParameter.add("socketId", getSocketId());
    reqParameter.add("host", bindHost);
    reqParameter.add("port", bindPort);
    pushReadTask(reqParameter);
    try {
      Parameter resParameter = this.writetaskList.take();
      byte type = resParameter.getParameterByteArray("type")[0];
      switch (type) {
        case 5:
          Log.log("创建套接字绑定成功", new Object[0]);
          return true;
      } 
      this.errMsg = resParameter.getParameterString("errMsg");
      Log.error(String.format(EasyI18N.getI18nString("创建套接字绑定失败 destHost:%s destPort:%s errmsg:%s"), new Object[] { this.destHost, Short.valueOf(this.destPort), this.errMsg }));
      close();
      return false;
    }
    catch (InterruptedException e) {
      e.printStackTrace();
      
      close();
      return false;
    } 
  }
  public void bindSocketServerOpenSocket() {
    Socket socket = new Socket();
    try {
      socket.connect(new InetSocketAddress(getDestHost(), getDestPort()));
      Parameter reqParameter = HttpToSocks.createParameter((byte)5);
      reqParameter.add("socketId", getSocketId());
      setClient(socket);
      pushReadTask(reqParameter);
      start();
    } catch (Exception e) {
      close();
      Log.error(e);
      Log.error(String.format(EasyI18N.getI18nString("连接socket失败 socketId:%s domain:%s port:%s"), new Object[] { getSocketId(), getDestHost(), Short.valueOf(getDestPort()) }));
    } 
  }

  
  public void startConnect() {
    Thread.currentThread().setName(Thread.currentThread().getStackTrace()[1].getMethodName());
    Parameter openSocketReqParameter = HttpToSocks.createParameter((byte)1);
    openSocketReqParameter.add("host", getDestHost());
    openSocketReqParameter.add("port", String.valueOf(getDestPort()));
    openSocketReqParameter.add("socketId", getSocketId());
    pushReadTask(openSocketReqParameter);
    try {
      Parameter resParameter = this.writetaskList.take();
      byte type = resParameter.getParameterByteArray("type")[0];
      switch (type) {
        case 5:
          if (this.connectSuccessMessage != null) {
            pushWriteTask((new Parameter()).addParameterByteArray("type", new byte[] { 3 }).addParameterByteArray("data", this.connectSuccessMessage));
            this.connectSuccessMessage = null;
          } 
          start();
          return;
      } 
      this.errMsg = resParameter.getParameterString("errMsg");
      Log.error(String.format(EasyI18N.getI18nString("创建套接字失败 destHost:%s destPort:%s errmsg:%s"), new Object[] { this.destHost, Short.valueOf(this.destPort), this.errMsg }));
      close();
      
      return;
    } catch (InterruptedException e) {
      e.printStackTrace();
    } 
  }

  
  public String toString() {
    return "SocksRelayInfo{Version=" + this.version + ", Command=" + this.command + ", DestPort=" + this.destPort + ", DestHost=" + this.destHost + ", USERID='" + this.userId + '\'' + '}';
  }








  
  public String getErrorMessage() {
    return this.errMsg;
  }

  
  public boolean isActive() {
    return this.alive;
  }
  
  public boolean start() {
    (new Thread(this::_startRead)).start();
    (new Thread(this::_startWrite)).start();
    return true;
  }

  
  public boolean stop() {
    close();
    return !this.alive;
  }

  
  public int getPacketMaxSize() {
    return this.packetMaxSize;
  }
  
  public void setPacketMaxSize(int packetMaxSize) {
    this.packetMaxSize = packetMaxSize;
  }
  
  private void _startRead() {
    Thread.currentThread().setName(hashCode() + Thread.currentThread().getStackTrace()[1].getMethodName());
    byte[] buff = new byte[this.packetMaxSize];
    try {
      InputStream inputStream = getRelaySocket().getInputStream();
      while (this.alive && !this.client.isClosed()) {
        try {
          int readNum = inputStream.read(buff);
          if (readNum > 0) {
            Parameter reqParameter = new Parameter();
            reqParameter.add("type", new byte[] { 3 });
            reqParameter.add("socketId", getSocketId().getBytes());
            reqParameter.add("data", Arrays.copyOfRange(buff, 0, readNum));
            pushReadTask(reqParameter);
            continue;
          } 
          close();
          
          return;
        } catch (IOException e) {
          if (SocketTimeoutException.class.isAssignableFrom(e.getClass())) {
            continue;
          }
          close();
          return;
        } 
      } 
    } catch (IOException e) {
      e.printStackTrace();
    } 
    close();
  }
  
  private void _startWrite() {
    Thread.currentThread().setName(hashCode() + Thread.currentThread().getStackTrace()[1].getMethodName());
    try {
      OutputStream outputStream = getRelaySocket().getOutputStream();
      while (this.alive && !this.client.isClosed()) {
        try {
          Parameter resParameter = this.writetaskList.take();
          byte[] typeArr = resParameter.getParameterByteArray("type");
          byte[] data = resParameter.getParameterByteArray("data");
          if (typeArr != null && typeArr.length > 0) {
            switch (typeArr[0]) {
              case 3:
                if (!getRelaySocket().isClosed()) {
                  outputStream.write(data);
                  continue;
                } 
                return;
              
              case 2:
                close();
                return;
            } 


          
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        } 
      } 
    } catch (IOException e) {
      e.printStackTrace();
    } 
    close();
  }

  
  public void tryWrite(byte[] data) {
    if (this.alive) {
      try {
        getRelaySocket().getOutputStream().write(data);
      } catch (Exception e) {
        close();
      } 
    }
  }
  
  public synchronized void close() {
    if (!this.alive) {
      return;
    }
    try {
      if (this.client != null && !this.client.isClosed()) {
        this.client.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } 
    this.alive = false;
    Parameter reqParameter = this.readtaskList.peekLast();
    if (reqParameter != null) {
      if (reqParameter.getParameterByteArray("type")[0] != 2) {
        reqParameter = new Parameter();
        reqParameter.add("type", new byte[] { 2 });
        reqParameter.add("socketId", getSocketId().getBytes());
        pushReadTask(reqParameter);
        this.writetaskList.add(reqParameter);
      } 
    } else {
      reqParameter = new Parameter();
      reqParameter.add("type", new byte[] { 2 });
      reqParameter.add("socketId", getSocketId().getBytes());
      pushReadTask(reqParameter);
      this.writetaskList.add(reqParameter);
    } 
  }

  
  public String getSocketId() {
    return this.socketId;
  }
  
  public void setSocketId(String socketId) {
    this.socketId = socketId;
  }
}
