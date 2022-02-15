package core.socksServer;

import com.httpProxy.server.request.HttpRequest;
import com.httpProxy.server.response.HttpResponse;
import core.EasyI18N;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import util.Log;
import util.ParameterInputStream;
import util.ParameterOutputStream;
import util.functions;
import util.http.Parameter;





public class HttpToSocks
{
  Map<String, SocksRelayInfo> session = new ConcurrentHashMap<>(50);
  
  String[] keys;
  int keyIndex;
  byte[] sessionKey;
  long accessNum = 1L;
  
  SocksServer socksServer;
  
  boolean alive;
  String sessionId;
  long summaryUploadBytes;
  long summaryDownloadBytes;
  long tmpUploadBytes;
  long requestSuccessNum;
  long requestFailureNum;
  long startSocksTime;
  SocksServerConfig socksServerConfig;
  
  public HttpToSocks(SocksServerConfig socksServerConfig) {
    this.socksServerConfig = socksServerConfig;
    this.alive = false;
  }

  
  public static void main(String[] args) {
    SocksServerConfig socksServerConfig = new SocksServerConfig("127.0.0.1", 1088);
    
    socksServerConfig.clientSocketOnceReadSize.set(10);
    socksServerConfig.clientPacketSize.set(10);
    socksServerConfig.remoteKey = "admin";
    socksServerConfig.remoteProxyUrl = "http://127.0.0.1:8088/";
    socksServerConfig.requestErrRetry.set(50);
    socksServerConfig.requestHandle = new SimpleHttpRequestHandle();
    socksServerConfig.serverPacketSize = 10;
    socksServerConfig.serverSocketOnceReadSize = 10;
    socksServerConfig.capacity.set(5);

    
    HttpToSocks handleSocks = new HttpToSocks(socksServerConfig);
    System.out.println(handleSocks.generateSessionId());
    System.out.println(handleSocks.testConnect());
    System.out.println(handleSocks.testConnect());
    System.out.println(handleSocks.testConnect());
  }
  
  public boolean start() throws Exception {
    if (this.alive) {
      Log.error("服务已开启");
      return false;
    } 
    this.socksServer = new SocksServer();
    this.startSocksTime = System.currentTimeMillis();
    this.socksServer.start(this.socksServerConfig.listenAddress);
    (new Thread(this::startSocksRelay)).start();
    (new Thread(() -> {
          this.startSocksTime = System.currentTimeMillis();




          
          try {
            while (this.alive) {
              Socket client = this.socksServer.accept();



              
              (new Thread(())).start();
            } 
          } catch (Exception e) {
            e.printStackTrace();
            reset();
          } 
        })).start();
    this.alive = true;
    return true;
  }

  
  public void reset() {
    this.startSocksTime = 0L;
    this.summaryUploadBytes = 0L;
    this.summaryDownloadBytes = 0L;
    this.requestSuccessNum = 0L;
    this.requestFailureNum = 0L;
    this.keys = null;
    this.keyIndex = 0;
    this.accessNum = 1L;
    this.session = new ConcurrentHashMap<>(50);
    if (!this.alive) {
      return;
    }
    if (this.alive) {
      try {
        ArrayList<Parameter> packets = new ArrayList();
        packets.add(createParameter((byte)7));
        SendRequest(packets);
      } catch (Throwable e) {
        e.printStackTrace();
      } 
    }
    close();
    this.alive = false;
    System.gc();
    System.gc();
    System.gc();
  }

  
  public long getAccessNum() {
    return this.accessNum;
  }
  public long addAccessNum() {
    return this.accessNum++;
  }

  
  public Map<String, SocksRelayInfo> getSession() {
    return this.session;
  }
  
  public long getSummaryUploadBytes() {
    return this.summaryUploadBytes;
  }
  
  public long getSummaryDownloadBytes() {
    return this.summaryDownloadBytes;
  }
  
  public long getRequestSuccessNum() {
    return this.requestSuccessNum;
  }
  
  public long getRequestFailureNum() {
    return this.requestFailureNum;
  }
  
  public long getStartSocksTime() {
    return this.startSocksTime;
  }
  
  public String getSessionId() {
    return this.sessionId;
  }
  
  public SocksServerConfig getSocksServerConfig() {
    return this.socksServerConfig;
  }
  
  public boolean isAlive() {
    return this.alive;
  }
  
  public boolean addRelaySocket(SocksRelayInfo socksRelayInfo) {
    if (this.alive) {
      this.session.put(socksRelayInfo.getSocketId(), socksRelayInfo);
      return true;
    } 
    return false;
  }
  public SocksRelayInfo addBindMirror(String bindHost, String bindPort, String destHost, String destPort) throws UnsupportedOperationException {
    SocksRelayInfo socksRelayInfo = new SocksRelayInfo(this.socksServerConfig.clientSocketOnceReadSize.get(), 1024);
    if (this.alive) {
      socksRelayInfo.setDestHost(destHost);
      socksRelayInfo.setDestPort(Short.parseShort(destPort));
      socksRelayInfo.setBindHost(bindHost);
      socksRelayInfo.setBindPort(Short.parseShort(destPort));
      addRelaySocket(socksRelayInfo);
      boolean ok = socksRelayInfo.startBind(bindHost, bindPort);
      if (!ok) {
        socksRelayInfo.close();
        Log.log(EasyI18N.getI18nString("mirrorSocket 启动失败可能是地址/端口已被占用 errMsg:%s"), new Object[] { socksRelayInfo.getErrorMessage() });
      } 
    } else {
      Log.error(EasyI18N.getI18nString("HttpToSocks未启动"));
      new UnsupportedOperationException(EasyI18N.getI18nString("HttpToSocks未启动"));
    } 
    return socksRelayInfo;
  }
  public int deleteDeadSocket() {
    int deleteNum = 0;
    this.session.keySet().iterator().forEachRemaining(key -> {
          SocksRelayInfo socksRelayInfo = this.session.get(key);
          
          if (!socksRelayInfo.isAlive() && socksRelayInfo.getReadTaskSize() == 0) {
            this.session.remove(key);
            Log.log(String.format("free socket socketId:%s", new Object[] { socksRelayInfo.getSocketId() }), new Object[0]);
          } 
        });
    return deleteNum;
  }
  public void close() {
    try {
      this.alive = false;
      if (this.socksServer != null) {
        deleteDeadSocket();
        if (this.socksServer != null) {
          this.socksServer.close();
        }
        this.session.values().forEach(socksRelayInfo -> {
              try {
                if (socksRelayInfo.isAlive()) {
                  socksRelayInfo.close();
                }
              } catch (Exception e) {
                e.printStackTrace();
              } 
            });
      } 
      Log.log("HttpToSocks->close", new Object[0]);
    } catch (Exception e) {
      Log.error(e);
    } 
    this.session.clear();
    System.gc();
  }
  
  public void closeSocket(String socketId) {
    try {
      SocksRelayInfo socksRelayInfo = this.session.get(socketId);
      if (socksRelayInfo != null) {
        socksRelayInfo.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }

  
  public boolean testConnect() {
    try {
      ArrayList<Parameter> messageList = new ArrayList();
      
      messageList.add(createParameter((byte)8));
      
      Parameter res = SendRequest(messageList, this.socksServerConfig.requestErrRetry.get()).readParameter();
      if (res != null) {
        byte[] typeArr = res.getParameterByteArray("type");
        if (typeArr != null && typeArr.length > 0 && 
          typeArr[0] == 5) {
          return true;
        }
      }
    
    } catch (Exception e) {
      Log.error(e);
    } 
    return false;
  }
  
  public synchronized void handleMessage(ParameterInputStream parameterInputStream) {
    String socketId = null;
    SocksRelayInfo _socksRelayInfo = null;
    Parameter resParameter = null;
    while ((resParameter = parameterInputStream.readParameter()) != null) {
      socketId = resParameter.getParameterString("socketId");
      byte[] typeArray = resParameter.getParameterByteArray("type");
      
      if (typeArray != null && typeArray.length > 0) {
        
        if (!this.session.containsKey(socketId) && typeArray[0] != 2) {
          SocksRelayInfo socksRelayInfo = new SocksRelayInfo(0, 5);
          socksRelayInfo.setSocketId(socketId);
          addRelaySocket(socksRelayInfo);
          socksRelayInfo.close();
          continue;
        } 
        _socksRelayInfo = this.session.get(socketId);
        try {
          SocksRelayInfo mirrorRelayInfo;
          switch (typeArray[0]) {
            case 2:
              _socksRelayInfo = this.session.remove(socketId);
              if (_socksRelayInfo != null) {
                _socksRelayInfo.pushWriteTask(resParameter);
                _socksRelayInfo.close();
              } 
            
            case 3:
              if (_socksRelayInfo.isAlive()) {
                _socksRelayInfo.pushWriteTask(resParameter);
                this.summaryDownloadBytes += (resParameter.getParameterByteArray("data")).length;
              } 
            
            case 5:
              _socksRelayInfo.pushWriteTask(resParameter);
            
            case 1:
              mirrorRelayInfo = this.session.get(socketId);
              if (mirrorRelayInfo != null) {
                SocksRelayInfo socksRelayInfo = new SocksRelayInfo(this.socksServerConfig.clientSocketOnceReadSize.get(), this.socksServerConfig.capacity.get());
                socksRelayInfo.setSocketId(resParameter.getParameterString("destSocketId"));
                socksRelayInfo.setDestHost(mirrorRelayInfo.getDestHost());
                socksRelayInfo.setDestPort(mirrorRelayInfo.getDestPort());
                addRelaySocket(socksRelayInfo);
                (new Thread(socksRelayInfo::bindSocketServerOpenSocket)).start();
              } 
          } 


        
        } catch (Exception e) {
          e.printStackTrace();
        } 
      } 
    } 
  }

  
  public void startSocksRelay() {
    Thread.currentThread().setName(Thread.currentThread().getStackTrace()[1].getMethodName());
    while (this.alive) {
      if (this.session.size() == 0) {
        try {
          Thread.sleep(50L);
        } catch (InterruptedException e) {
          e.printStackTrace();
        } 
        continue;
      } 
      deleteDeadSocket();
      try {
        Thread.sleep(this.socksServerConfig.requestDelay.get());
      } catch (InterruptedException e) {
        e.printStackTrace();
      } 
      ArrayList<Parameter> packets = getAllChanData();
      
      ParameterInputStream parameterInputStream = SendRequest(packets);
      
      if (parameterInputStream == null) {
        break;
      }
      this.summaryUploadBytes += this.tmpUploadBytes;
      handleMessage(parameterInputStream);
    } 

    
    reset();
  }
  
  public ArrayList<Parameter> getAllChanData() {
    this.tmpUploadBytes = 0L;
    int dataLength = 0;
    ArrayList<Parameter> packets = new ArrayList<>();
    for (int forNum = 0; forNum < 3; forNum++) {
      while (dataLength < this.socksServerConfig.clientPacketSize.get() && this.session.size() > 0 && this.keyIndex < this.session.size()) {
        if (this.keyIndex == 0) {
          this.keys = (String[])this.session.keySet().toArray((Object[])new String[0]);
        }
        if (this.session.size() > 0) {
          String key = this.keys[this.keyIndex];
          SocksRelayInfo socket = this.session.get(key);
          if (socket != null) {
            Parameter resParameter = socket.ReadTaskData(true);
            if (resParameter != null) {
              dataLength += resParameter.len();
              packets.add(resParameter);
              
              if (resParameter.getParameterByteArray("type")[0] == 3) {
                this.tmpUploadBytes += (resParameter.getParameterByteArray("data")).length;
              }
            } 
            if (this.keyIndex + 1 >= this.keys.length) {
              this.keyIndex = 0;
              break;
            } 
            this.keyIndex++;
          } 
        } 
      } 
    } 
    
    if (packets.isEmpty()) {
      packets.add(createParameter((byte)4));
    }
    return packets;
  }
  public String generateSessionId() throws UnsupportedOperationException {
    this.sessionKey = getSessionKey(this.socksServerConfig.remoteKey);
    try {
      Parameter request = createParameter((byte)0).addParameterByteArray("accessNum", ByteBuffer.allocate(8).putLong(0L).array());
      
      request.add("serverMaxResponseSize", ByteBuffer.allocate(8).putLong(this.socksServerConfig.serverPacketSize).array());
      request.add("serverPacketMaxSize", ByteBuffer.allocate(8).putLong(this.socksServerConfig.serverSocketOnceReadSize).array());
      request.add("serverCapacity", ByteBuffer.allocate(8).putLong(this.socksServerConfig.capacity.get()).array());
      
      HttpResponse response = SendRequestPostRaw(this.socksServerConfig.remoteProxyUrl, encryptRequestData(request.serialize(), this.sessionKey), this.socksServerConfig.requestHandle);
      Parameter parameter = Parameter.unSerialize(Dcrypt(response.getResponseData(), this.sessionKey));
      if (parameter.getParameterByteArray("type")[0] == 5) {
        this.sessionId = parameter.getParameterString("sessionId");
        return this.sessionId;
      } 
    } catch (Exception e) {
      throw new UnsupportedOperationException(e);
    } 
    return null;
  }
  public static Parameter createParameter(byte type) {
    Parameter ret = new Parameter();
    ret.add("type", new byte[] { type });
    return ret;
  }
  
  public ParameterInputStream SendRequest(ArrayList<Parameter> packets) {
    return SendRequest(packets, this.socksServerConfig.requestErrRetry.get());
  }
  
  public ParameterInputStream SendRequest(ArrayList<Parameter> packets, long maxErrNum) {
    Parameter headerParameter = new Parameter();
    ParameterInputStream ret = null;
    HttpResponse response = null;
    packets.add(0, headerParameter);
    long currentErrNum = 0L;
    while (currentErrNum < maxErrNum) {
      try {
        byte[] currentTimeByteArr = ByteBuffer.allocate(8).putLong(System.currentTimeMillis()).array();
        headerParameter.add("accessTime", currentTimeByteArr);
        headerParameter.add("accessNum", ByteBuffer.allocate(8).putLong(getAccessNum()).array());
        headerParameter.add("sessionId", this.sessionId.getBytes());
        ParameterOutputStream parameterOutputStream = new ParameterOutputStream();
        for (int i = 0; i < packets.size(); i++) {
          parameterOutputStream.writeParameter(packets.get(i));
        }
        addAccessNum();
        byte[] reqData = encryptRequestData(parameterOutputStream.getBuffer(), this.sessionKey);
        response = SendRequestPostRaw(this.socksServerConfig.remoteProxyUrl, reqData, this.socksServerConfig.requestHandle);
        if (response != null && (response.getResponseData()).length > 0) {
          ret = ParameterInputStream.asParameterInputStream(Dcrypt(response.getResponseData(), this.sessionKey));
          this.requestSuccessNum++;
          break;
        } 
        this.requestFailureNum++;
      }
      catch (Exception e) {
        this.requestFailureNum++;
        Log.error(e);
        try {
          Thread.sleep(this.socksServerConfig.requestErrDelay.get());
        } catch (InterruptedException interruptedException) {
          interruptedException.printStackTrace();
        } 
      } 
      currentErrNum++;
    } 
    if (ret == null && this.alive) {
      Log.error(String.format(EasyI18N.getI18nString("最大错误尝试次数:%d 错误尝试后服务器依然无法通信 已强制关闭httpToSocks"), new Object[] { Integer.valueOf(this.socksServerConfig.requestErrRetry.get()) }));
      close();
      return null;
    } 
    return ret;
  }
  public static byte[] Dcrypt(byte[] bs, byte[] key) throws Exception {
    SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(2, skeySpec, new IvParameterSpec(new byte[16]));
    return functions.gzipD(cipher.doFinal(bs));
  }
  public static byte[] Encrypt(byte[] bs, byte[] key) throws Exception {
    SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(1, skeySpec, new IvParameterSpec(new byte[16]));
    return cipher.doFinal(functions.gzipE(bs));
  }
  public static byte[] encryptRequestData(byte[] pars, byte[] sessionKey) throws Exception {
    return Encrypt(pars, sessionKey);
  }
  public byte[] getSessionKey(String key) {
    byte[] md = key.getBytes();
    for (int i = 0; i <= 20; i++) {
      md = functions.md5(md);
    }
    return md;
  }
  public static HttpResponse SendRequestPostFrom(String url, String data, HttpRequestHandle requestHandle) {
    HttpRequest httpRequest = new HttpRequest();
    httpRequest.getHttpRequestHeader().setContentType("application/x-www-form-urlencoded");
    httpRequest.setUrl(url);
    httpRequest.setRequestData(data.getBytes());
    httpRequest.setMethod("POST");
    return sendHttpRequest(httpRequest, requestHandle);
  }
  public static HttpResponse SendRequestPostRaw(String url, byte[] data, HttpRequestHandle requestHandle) {
    HttpRequest httpRequest = new HttpRequest();
    httpRequest.getHttpRequestHeader().setContentType("application/octet-stream");
    httpRequest.setUrl(url);
    httpRequest.setRequestData(data);
    httpRequest.setMethod("POST");
    return sendHttpRequest(httpRequest, requestHandle);
  }
  
  public static HttpResponse sendHttpRequest(HttpRequest httpRequest, HttpRequestHandle requestHandle) {
    return requestHandle.sendHttpRequest(httpRequest);
  }
}
