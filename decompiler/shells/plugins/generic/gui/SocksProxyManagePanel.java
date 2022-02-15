package shells.plugins.generic.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import core.EasyI18N;
import core.ui.component.DataView;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


































public class SocksProxyManagePanel
{
  private JPanel mainPanel;
  public JPanel startSocksProxyPanel;
  public JPanel socksProxyManagePanel;
  public JButton testButton;
  public JTextField socksBindAddressTextField;
  public JLabel socksBindAddressLabel;
  public JLabel socksBindPortLabel;
  public JTextField socksBindPortTextField;
  public JLabel remoteSocksProxyUrlLabel;
  public JTextField remoteSocksProxyUrlTextField;
  public JTextField remoteKeyTextField;
  public JTextField serverPacketSizeTextField;
  public JTextField serverSocketOnceReadSizeTextField;
  public JTextField clientSocketOnceReadSizeTextField;
  public JTextField clientPacketTextField;
  public JButton startSocksServerButton;
  public JButton addNewProxyButton;
  public JButton stopProxyButton;
  
  public SocksProxyManagePanel() {
    $$$setupUI$$$();
    this.proxyManageDataView.addColumn("ID");
    this.proxyManageDataView.addColumn(EasyI18N.getI18nString("监听地址"));
    this.proxyManageDataView.addColumn(EasyI18N.getI18nString("监听端口"));
    this.proxyManageDataView.addColumn(EasyI18N.getI18nString("类型"));
    this.proxyManageDataView.addColumn(EasyI18N.getI18nString("目标地址"));
    this.proxyManageDataView.addColumn(EasyI18N.getI18nString("目标端口"));
    this.proxyManageDataView.addColumn(EasyI18N.getI18nString("状态"));
    this.proxyManageDataView.addColumn(EasyI18N.getI18nString("错误信息"));
  } public JButton serverProxyConfigButton; public JTextField requestDelayTextField; public JTextField requestErrRetryTextField; public JTextField requestErrDelayTextField; public JLabel remoteKeyLabel; public JLabel serverSocketOnceReadSizeLabel; public JLabel serverPacketSizeLabel; public JLabel clientSocketOnceReadSizeLabel; public JLabel clientPacketSizeLabel; public JLabel requestDelayLabel; public JLabel requestErrRetryLabel; public JLabel requestErrDelayLabel; public JLabel statusLabel; public JLabel capacityLabel; public JTextField capacityTextField; public JPanel stopSocksProxy; public JScrollPane dataViewScrollPane; public DataView proxyManageDataView; public JButton addProxyType;
  private void $$$setupUI$$$() {
    this.mainPanel = new JPanel();
    this.mainPanel.setLayout(new CardLayout(0, 0));
    this.startSocksProxyPanel = new JPanel();
    this.startSocksProxyPanel.setLayout((LayoutManager)new GridLayoutManager(14, 2, new Insets(0, 0, 0, 0), -1, -1));
    this.mainPanel.add(this.startSocksProxyPanel, "startSocksProxy");
    this.socksBindAddressLabel = new JLabel();
    this.socksBindAddressLabel.setText("socks4a/5 绑定地址:");
    this.startSocksProxyPanel.add(this.socksBindAddressLabel, new GridConstraints(0, 0, 1, 1, 1, 0, 0, 0, null, null, null, 0, false));
    this.socksBindAddressTextField = new JTextField();
    this.socksBindAddressTextField.setText("127.0.0.1");
    this.startSocksProxyPanel.add(this.socksBindAddressTextField, new GridConstraints(0, 1, 1, 1, 1, 1, 4, 0, null, new Dimension(150, -1), null, 0, false));
    this.socksBindPortLabel = new JLabel();
    this.socksBindPortLabel.setText("socks4a/5 绑定端口:");
    this.startSocksProxyPanel.add(this.socksBindPortLabel, new GridConstraints(1, 0, 1, 1, 1, 0, 0, 0, null, null, null, 0, false));
    this.socksBindPortTextField = new JTextField();
    this.socksBindPortTextField.setText("10806");
    this.startSocksProxyPanel.add(this.socksBindPortTextField, new GridConstraints(1, 1, 1, 1, 9, 1, 4, 0, null, new Dimension(150, -1), null, 0, false));
    this.remoteSocksProxyUrlLabel = new JLabel();
    this.remoteSocksProxyUrlLabel.setText("远程Socks URL地址:");
    this.startSocksProxyPanel.add(this.remoteSocksProxyUrlLabel, new GridConstraints(2, 0, 1, 1, 1, 0, 0, 0, null, null, null, 0, false));
    this.remoteKeyLabel = new JLabel();
    this.remoteKeyLabel.setText("远程Socks 加密Key:");
    this.startSocksProxyPanel.add(this.remoteKeyLabel, new GridConstraints(3, 0, 1, 1, 1, 0, 0, 0, null, null, null, 0, false));
    this.remoteKeyTextField = new JTextField();
    this.remoteKeyTextField.setText("remoteKey");
    this.startSocksProxyPanel.add(this.remoteKeyTextField, new GridConstraints(3, 1, 1, 1, 9, 1, 4, 0, null, new Dimension(150, -1), null, 0, false));
    this.serverPacketSizeLabel = new JLabel();
    this.serverPacketSizeLabel.setText("Server单次读取大小:");
    this.startSocksProxyPanel.add(this.serverPacketSizeLabel, new GridConstraints(5, 0, 1, 1, 1, 0, 0, 0, null, null, null, 0, false));
    this.serverPacketSizeTextField = new JTextField();
    this.serverPacketSizeTextField.setText("1024000");
    this.startSocksProxyPanel.add(this.serverPacketSizeTextField, new GridConstraints(5, 1, 1, 1, 9, 1, 4, 0, null, new Dimension(150, -1), null, 0, false));
    this.serverSocketOnceReadSizeLabel = new JLabel();
    this.serverSocketOnceReadSizeLabel.setText("Server套接字单次读取大小:");
    this.startSocksProxyPanel.add(this.serverSocketOnceReadSizeLabel, new GridConstraints(4, 0, 1, 1, 0, 0, 0, 0, null, null, null, 0, false));
    this.serverSocketOnceReadSizeTextField = new JTextField();
    this.serverSocketOnceReadSizeTextField.setText("102400");
    this.startSocksProxyPanel.add(this.serverSocketOnceReadSizeTextField, new GridConstraints(4, 1, 1, 1, 8, 1, 4, 0, null, new Dimension(150, -1), null, 0, false));
    this.clientSocketOnceReadSizeTextField = new JTextField();
    this.clientSocketOnceReadSizeTextField.setText("102400");
    this.startSocksProxyPanel.add(this.clientSocketOnceReadSizeTextField, new GridConstraints(6, 1, 1, 1, 8, 1, 4, 0, null, new Dimension(150, -1), null, 0, false));
    this.clientPacketSizeLabel = new JLabel();
    this.clientPacketSizeLabel.setText("Client单次读取大小:");
    this.startSocksProxyPanel.add(this.clientPacketSizeLabel, new GridConstraints(7, 0, 1, 1, 0, 0, 0, 0, null, null, null, 0, false));
    this.clientPacketTextField = new JTextField();
    this.clientPacketTextField.setText("1024000");
    this.startSocksProxyPanel.add(this.clientPacketTextField, new GridConstraints(7, 1, 1, 1, 8, 1, 4, 0, null, new Dimension(150, -1), null, 0, false));
    JPanel panel1 = new JPanel();
    panel1.setLayout((LayoutManager)new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
    this.startSocksProxyPanel.add(panel1, new GridConstraints(12, 0, 1, 2, 0, 3, 3, 3, null, null, null, 0, false));
    this.testButton = new JButton();
    this.testButton.setText("测试连接");
    panel1.add(this.testButton, new GridConstraints(0, 0, 1, 1, 0, 1, 3, 0, null, null, null, 0, false));
    this.startSocksServerButton = new JButton();
    this.startSocksServerButton.setText("开启SocksServer");
    panel1.add(this.startSocksServerButton, new GridConstraints(0, 1, 1, 1, 0, 1, 3, 0, null, null, null, 0, false));
    JPanel panel2 = new JPanel();
    panel2.setLayout((LayoutManager)new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    this.startSocksProxyPanel.add(panel2, new GridConstraints(13, 0, 1, 2, 0, 3, 3, 3, null, null, null, 0, false));
    this.requestDelayLabel = new JLabel();
    this.requestDelayLabel.setText("请求抖动延迟(ms)");
    this.startSocksProxyPanel.add(this.requestDelayLabel, new GridConstraints(9, 0, 1, 1, 0, 0, 0, 0, null, null, null, 0, false));
    this.requestDelayTextField = new JTextField();
    this.requestDelayTextField.setText("10");
    this.startSocksProxyPanel.add(this.requestDelayTextField, new GridConstraints(9, 1, 1, 1, 8, 1, 4, 0, null, new Dimension(150, -1), null, 0, false));
    this.requestErrRetryLabel = new JLabel();
    this.requestErrRetryLabel.setText("错误重试最大次数");
    this.startSocksProxyPanel.add(this.requestErrRetryLabel, new GridConstraints(10, 0, 1, 1, 0, 0, 0, 0, null, null, null, 0, false));
    this.requestErrRetryTextField = new JTextField();
    this.requestErrRetryTextField.setText("20");
    this.startSocksProxyPanel.add(this.requestErrRetryTextField, new GridConstraints(10, 1, 1, 1, 8, 1, 4, 0, null, new Dimension(150, -1), null, 0, false));
    this.requestErrDelayLabel = new JLabel();
    this.requestErrDelayLabel.setText("请求错误重试抖动延时(ms)");
    this.startSocksProxyPanel.add(this.requestErrDelayLabel, new GridConstraints(11, 0, 1, 1, 0, 0, 0, 0, null, null, null, 1, false));
    this.requestErrDelayTextField = new JTextField();
    this.requestErrDelayTextField.setText("30");
    this.startSocksProxyPanel.add(this.requestErrDelayTextField, new GridConstraints(11, 1, 1, 1, 8, 1, 4, 0, null, new Dimension(150, -1), null, 0, false));
    this.capacityLabel = new JLabel();
    this.capacityLabel.setText("套接字缓冲队列数");
    this.startSocksProxyPanel.add(this.capacityLabel, new GridConstraints(8, 0, 1, 1, 0, 0, 0, 0, null, null, null, 0, false));
    this.capacityTextField = new JTextField();
    this.capacityTextField.setText("5");
    this.startSocksProxyPanel.add(this.capacityTextField, new GridConstraints(8, 1, 1, 1, 8, 1, 4, 0, null, new Dimension(150, -1), null, 0, false));
    this.remoteSocksProxyUrlTextField = new JTextField();
    this.remoteSocksProxyUrlTextField.setText("http://127.0.0.1:8088/");
    this.startSocksProxyPanel.add(this.remoteSocksProxyUrlTextField, new GridConstraints(2, 1, 1, 1, 9, 1, 4, 0, null, new Dimension(150, -1), null, 0, false));
    this.clientSocketOnceReadSizeLabel = new JLabel();
    this.clientSocketOnceReadSizeLabel.setText("Client套接字单次读取大小:");
    this.startSocksProxyPanel.add(this.clientSocketOnceReadSizeLabel, new GridConstraints(6, 0, 1, 1, 0, 0, 0, 0, null, null, null, 0, false));
    this.socksProxyManagePanel = new JPanel();
    this.socksProxyManagePanel.setLayout((LayoutManager)new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
    this.mainPanel.add(this.socksProxyManagePanel, "socksProxyManage");
    this.statusLabel = new JLabel();
    this.statusLabel.setText("当前连接数:10 当前速度:100k/s 已发包:1000 已上传:10mb 已下载:20mb  运行时间:1h");
    this.socksProxyManagePanel.add(this.statusLabel, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null, 0, false));
    this.stopSocksProxy = new JPanel();
    this.stopSocksProxy.setLayout((LayoutManager)new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
    this.socksProxyManagePanel.add(this.stopSocksProxy, new GridConstraints(3, 0, 1, 1, 0, 3, 3, 3, null, null, null, 0, false));
    this.addNewProxyButton = new JButton();
    this.addNewProxyButton.setText("添加代理类型");
    this.stopSocksProxy.add(this.addNewProxyButton, new GridConstraints(0, 0, 1, 1, 0, 1, 3, 0, null, null, null, 0, false));
    this.stopProxyButton = new JButton();
    this.stopProxyButton.setText("停止代理");
    this.stopSocksProxy.add(this.stopProxyButton, new GridConstraints(0, 2, 1, 1, 0, 1, 3, 0, null, null, null, 0, false));
    this.serverProxyConfigButton = new JButton();
    this.serverProxyConfigButton.setText("通信配置");
    this.stopSocksProxy.add(this.serverProxyConfigButton, new GridConstraints(0, 1, 1, 1, 0, 1, 3, 0, null, null, null, 0, false));
    JPanel panel3 = new JPanel();
    panel3.setLayout((LayoutManager)new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    this.socksProxyManagePanel.add(panel3, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null, 0, false));
    this.dataViewScrollPane = new JScrollPane();
    panel3.add(this.dataViewScrollPane, new GridConstraints(0, 0, 1, 1, 0, 3, 5, 5, null, null, null, 0, false));
    this.proxyManageDataView = new DataView();
    this.proxyManageDataView.setAutoCreateRowSorter(true);
    this.proxyManageDataView.setFillsViewportHeight(true);
    this.dataViewScrollPane.setViewportView((Component)this.proxyManageDataView);
  }
  public JPanel getMainPanel() {
    return this.mainPanel;
  }
  
  public JComponent $$$getRootComponent$$$() {
    return this.mainPanel;
  }
}
