package core.ui.component;

import core.Encoding;
import core.annotation.DisplayName;
import core.imp.Payload;
import core.shell.ShellEntity;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import util.Log;
import util.automaticBindClick;
import util.functions;


@DisplayName(DisplayName = "网络详情")
public class ShellNetstat
  extends JPanel
{
  private static final Vector COLUMNS_VECTOR = new Vector(new CopyOnWriteArrayList((Object[])new String[] { "Proto", "Local Address", "Remote Address", "State" }));

  
  private static final HashMap<String, String> LINUX_INET_FILE_MAPPING = new HashMap<>();
  private static final HashMap<String, String> LINUX_TCP_STATUS_MAPPING = new HashMap<>();
  
  private final DataView dataView;
  private final JButton getButton;
  private final JSplitPane portScanSplitPane;
  private final ShellEntity shellEntity;
  private final Payload payload;
  private Encoding encoding;
  
  static {
    LINUX_INET_FILE_MAPPING.put("tcp4", "/proc/net/tcp");
    
    LINUX_INET_FILE_MAPPING.put("udp4", "/proc/net/udp");

    
    LINUX_TCP_STATUS_MAPPING.put("01", "ESTABLISHED");
    LINUX_TCP_STATUS_MAPPING.put("02", "SYN_SENT");
    LINUX_TCP_STATUS_MAPPING.put("03", "SYN_RECV");
    LINUX_TCP_STATUS_MAPPING.put("04", "FIN_WAIT1");
    LINUX_TCP_STATUS_MAPPING.put("05", "FIN_WAIT2");
    LINUX_TCP_STATUS_MAPPING.put("06", "TIME_WAIT");
    LINUX_TCP_STATUS_MAPPING.put("07", "CLOSE");
    LINUX_TCP_STATUS_MAPPING.put("08", "CLOSE_WAIT");
    LINUX_TCP_STATUS_MAPPING.put("09", "LAST_ACK");
    LINUX_TCP_STATUS_MAPPING.put("0A", "LISTEN");
    LINUX_TCP_STATUS_MAPPING.put("0B", "CLOSING");
  }
  
  public ShellNetstat(ShellEntity shellEntity) {
    this.shellEntity = shellEntity;
    this.payload = shellEntity.getPayloadModule();
    
    this.getButton = new JButton("scan");
    this.dataView = new DataView(null, COLUMNS_VECTOR, -1, -1);
    this.portScanSplitPane = new JSplitPane();
    
    this.portScanSplitPane.setOrientation(0);
    this.portScanSplitPane.setDividerSize(0);
    
    JPanel topPanel = new JPanel();
    topPanel.add(this.getButton);
    
    this.portScanSplitPane.setTopComponent(topPanel);
    this.portScanSplitPane.setBottomComponent(new JScrollPane(this.dataView));
    
    setLayout(new BorderLayout());
    add(this.portScanSplitPane);
    automaticBindClick.bindJButtonClick(this, this);
  }

  
  private void getButtonClick(ActionEvent actionEvent) {
    try {
      Vector<Vector<String>> rowsVector = null;
      if (!this.payload.isWindows()) {
        rowsVector = getLinuxNet();
      } else {
        rowsVector = getWinNet();
      } 
      this.dataView.AddRows(rowsVector);
    } catch (Exception e) {
      Log.error(e);
    } 
  }
  
  private Vector<Vector<String>> getLinuxNet() {
    Vector<Vector<String>> rows = new Vector<>();
    for (String protoType : (String[])LINUX_INET_FILE_MAPPING.keySet().toArray((Object[])new String[0])) {
      String resultString = new String(this.payload.downloadFile(LINUX_INET_FILE_MAPPING.get(protoType)));
      String[] lines = resultString.split("\n");
      Log.log(resultString, new Object[0]);
      for (String line : lines) {
        try {
          if (line.indexOf("local_address") == -1) {
            String[] infos = line.trim().split("\\s+");
            if (infos.length > 10) {
              Vector<String> oneRow = new Vector<>();
              oneRow.add(protoType);
              oneRow.add(Inet4Addr(infos[1]));
              oneRow.add(Inet4Addr(infos[2]));
              oneRow.add(LINUX_TCP_STATUS_MAPPING.get(infos[3]));
              rows.add(oneRow);
            } 
          } 
        } catch (Exception e) {
          Log.error(line);
          Log.error(e);
        } 
      } 
    } 
    return rows;
  }
  private Vector<Vector<String>> getWinNet() {
    Vector<Vector<String>> rows = new Vector<>();
    String cmdResult = this.payload.execCommand("netstat -an");
    String[] lines = cmdResult.replace("\r", "").split("\n");
    for (String line : lines) {
      if (line.indexOf("TCP") != -1 || line.indexOf("UDP") != -1) {
        String[] infos = line.split("\\s+");
        Vector<String> oneRow = new Vector<>();
        int pt = -1;
        for (int i = 0; i < infos.length; i++) {
          if (infos[i].indexOf("TCP") != -1 || infos[i].indexOf("UDP") != -1) {
            pt = i;
            break;
          } 
        } 
        if (pt != -1) {
          oneRow.addAll(new CopyOnWriteArrayList<>(Arrays.copyOfRange(infos, pt, infos.length)));
        }
        rows.add(oneRow);
      } 
    } 
    return rows;
  }
  
  private String Inet4Addr(String hex) {
    String[] strings = hex.split(":");
    String ip = linuxHexToIP(strings[0]);
    int port = functions.byteToInt2(functions.hexToByte(strings[1]));
    return ip + ":" + port;
  }
  
  public static String linuxHexToIP(String hexString) {
    ArrayList<String> arrayList = new ArrayList<>();
    byte[] bs = functions.hexToByte(hexString);
    for (byte b : bs) {
      arrayList.add(Integer.toString(b & 0xFF));
    }
    Collections.reverse(arrayList);
    return Arrays.toString(arrayList.toArray()).replace(" ", "").replace("[", "").replace("]", "").replace(",", ".")
      .trim();
  }
}
