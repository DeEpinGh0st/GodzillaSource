package com.jediterm.terminal.ui;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.settings.TabbedSettingsProvider;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTabbedTerminalWidget<T extends JediTermWidget> extends JPanel implements TerminalWidget, TerminalActionProvider {
  private final Object myLock = new Object();
  
  private TerminalPanelListener myTerminalPanelListener = null;
  
  private T myTermWidget = null;
  
  private AbstractTabs<T> myTabs;
  
  private TabbedSettingsProvider mySettingsProvider;
  
  private List<TabListener> myTabListeners = Lists.newArrayList();
  private List<TerminalWidgetListener> myWidgetListeners = new CopyOnWriteArrayList<>();
  
  private TerminalActionProvider myNextActionProvider;
  
  private final Function<AbstractTabbedTerminalWidget<T>, T> myCreateNewSessionAction;
  private JPanel myPanel;
  
  public AbstractTabbedTerminalWidget(@NotNull TabbedSettingsProvider settingsProvider, @NotNull Function<AbstractTabbedTerminalWidget<T>, T> createNewSessionAction) {
    super(new BorderLayout());
    this.mySettingsProvider = settingsProvider;
    this.myCreateNewSessionAction = createNewSessionAction;
    
    setFocusTraversalPolicy(new DefaultFocusTraversalPolicy());
    
    this.myPanel = new JPanel(new BorderLayout());
    this.myPanel.add(this, "Center");
  }

  
  public T createTerminalSession(TtyConnector ttyConnector) {
    T terminal = createNewTabWidget();
    
    initSession(ttyConnector, terminal);
    
    return terminal;
  }
  
  public void initSession(TtyConnector ttyConnector, T terminal) {
    terminal.createTerminalSession(ttyConnector);
    if (this.myTabs != null) {
      int index = this.myTabs.indexOfComponent((Component)terminal);
      if (index != -1) {
        this.myTabs.setTitleAt(index, generateUniqueName(terminal, this.myTabs));
      }
    } 
    setupTtyConnectorWaitFor(ttyConnector, terminal);
  }
  
  public T createNewTabWidget() {
    T terminal = createInnerTerminalWidget();
    
    terminal.setNextProvider(this);
    
    if (this.myTerminalPanelListener != null) {
      terminal.setTerminalPanelListener(this.myTerminalPanelListener);
    }
    
    if (this.myTermWidget == null && this.myTabs == null) {
      this.myTermWidget = terminal;
      Dimension size = terminal.getComponent().getSize();
      
      add(this.myTermWidget.getComponent(), "Center");
      setSize(size);
      
      if (this.myTerminalPanelListener != null) {
        this.myTerminalPanelListener.onPanelResize(RequestOrigin.User);
      }
      
      onSessionChanged();
    } else {
      
      if (this.myTabs == null) {
        this.myTabs = setupTabs();
      }
      
      addTab(terminal, this.myTabs);
    } 
    return terminal;
  }


  
  protected void setupTtyConnectorWaitFor(TtyConnector ttyConnector, T widget) {
    (new TtyConnectorWaitFor(ttyConnector, Executors.newSingleThreadExecutor())).setTerminationCallback(integer -> {
          if (this.mySettingsProvider.shouldCloseTabOnLogout(ttyConnector)) {
            closeTab((T)widget);
            if (this.myTabs.getTabCount() == 0) {
              for (TerminalWidgetListener widgetListener : this.myWidgetListeners) {
                widgetListener.allSessionsClosed(widget);
              }
            }
          } 
          return true;
        });
  }
  
  private void addTab(T terminal, AbstractTabs<T> tabs) {
    String name = generateUniqueName(terminal, tabs);
    
    addTab(terminal, tabs, name);
  }
  
  private String generateUniqueName(T terminal, AbstractTabs<T> tabs) {
    return generateUniqueName(this.mySettingsProvider.tabName(terminal.getTtyConnector(), terminal.getSessionName()), tabs);
  }
  
  private void addTab(T terminal, AbstractTabs<T> tabs, String name) {
    tabs.addTab(name, terminal);

    
    tabs.setTabComponentAt(tabs.getTabCount() - 1, createTabComponent(tabs, terminal));
    tabs.setSelectedComponent(terminal);
  }
  
  public void addTab(String name, T terminal) {
    if (this.myTabs == null) {
      this.myTabs = setupTabs();
    }
    
    addTab(terminal, this.myTabs, name);
  }
  
  private String generateUniqueName(String suggestedName, AbstractTabs<T> tabs) {
    Set<String> names = Sets.newHashSet();
    for (int i = 0; i < tabs.getTabCount(); i++) {
      names.add(tabs.getTitleAt(i));
    }
    String newSdkName = suggestedName;
    int j = 0;
    while (names.contains(newSdkName)) {
      newSdkName = suggestedName + " (" + ++j + ")";
    }
    return newSdkName;
  }
  
  private AbstractTabs<T> setupTabs() {
    AbstractTabs<T> tabs = createTabbedPane();
    
    tabs.addChangeListener(new AbstractTabs.TabChangeListener()
        {
          public void tabRemoved() {
            if (AbstractTabbedTerminalWidget.this.myTabs.getTabCount() == 1) {
              AbstractTabbedTerminalWidget.this.removeTabbedPane();
            }
          }

          
          public void selectionChanged() {
            AbstractTabbedTerminalWidget.this.onSessionChanged();
          }
        });
    
    remove((Component)this.myTermWidget);
    
    addTab(this.myTermWidget, tabs);
    
    this.myTermWidget = null;
    
    add(tabs.getComponent(), "Center");
    
    return tabs;
  }
  
  public boolean isNoActiveSessions() {
    return (this.myTabs == null && this.myTermWidget == null);
  }
  
  private void onSessionChanged() {
    T session = getCurrentSession();
    if (session != null) {
      if (this.myTerminalPanelListener != null) {
        this.myTerminalPanelListener.onSessionChanged((TerminalSession)session);
      }
      session.getTerminalPanel().requestFocusInWindow();
    } 
  }


  
  protected Component createTabComponent(AbstractTabs<T> tabs, T terminal) {
    return new TabComponent(tabs, (JediTermWidget)terminal);
  }
  
  public void closeTab(final T terminal) {
    if (terminal != null) {
      if (this.myTabs != null && this.myTabs.indexOfComponent((Component)terminal) != -1) {
        SwingUtilities.invokeLater(new Runnable()
            {
              public void run() {
                AbstractTabbedTerminalWidget.this.removeTab(terminal);
              }
            });
        fireTabClosed(terminal);
      } else if (this.myTermWidget == terminal) {
        this.myTermWidget = null;
        fireTabClosed(terminal);
      } 
    }
  }
  
  public void closeCurrentSession() {
    T session = getCurrentSession();
    if (session != null) {
      session.close();
      closeTab(session);
    } 
  }
  
  public void dispose() {
    for (TerminalSession s : getAllTerminalSessions()) {
      if (s != null) s.close(); 
    } 
  }
  
  private List<T> getAllTerminalSessions() {
    List<T> session = Lists.newArrayList();
    if (this.myTabs != null) {
      for (int i = 0; i < this.myTabs.getTabCount(); i++) {
        session.add(getTerminalPanel(i));
      
      }
    }
    else if (this.myTermWidget != null) {
      session.add(this.myTermWidget);
    } 
    
    return session;
  }
  
  public void removeTab(T terminal) {
    synchronized (this.myLock) {
      if (this.myTabs != null) {
        this.myTabs.remove(terminal);
      }
      onSessionChanged();
    } 
  }
  
  private void removeTabbedPane() {
    this.myTermWidget = getTerminalPanel(0);
    this.myTabs.removeAll();
    remove(this.myTabs.getComponent());
    this.myTabs = null;
    add(this.myTermWidget.getComponent(), "Center");
  }

  
  public List<TerminalAction> getActions() {
    return Lists.newArrayList((Object[])new TerminalAction[] { (new TerminalAction(this.mySettingsProvider
            .getNewSessionActionPresentation(), (Predicate<KeyEvent>)new Predicate<KeyEvent>()
            {
              public boolean apply(KeyEvent input) {
                AbstractTabbedTerminalWidget.this.handleNewSession();
                return true;
              }
            })).withMnemonicKey(Integer.valueOf(78)), (new TerminalAction(this.mySettingsProvider
            .getCloseSessionActionPresentation(), (Predicate<KeyEvent>)new Predicate<KeyEvent>()
            {
              public boolean apply(KeyEvent input) {
                AbstractTabbedTerminalWidget.this.closeCurrentSession();
                return true;
              }
            })).withMnemonicKey(Integer.valueOf(83)), (new TerminalAction(this.mySettingsProvider
            .getNextTabActionPresentation(), (Predicate<KeyEvent>)new Predicate<KeyEvent>()
            {
              public boolean apply(KeyEvent input) {
                AbstractTabbedTerminalWidget.this.selectNextTab();
                return true;
              }
            })).withEnabledSupplier((Supplier<Boolean>)new Supplier<Boolean>()
            {
              public Boolean get() {
                return Boolean.valueOf((AbstractTabbedTerminalWidget.this.myTabs != null && AbstractTabbedTerminalWidget.this.myTabs.getSelectedIndex() < AbstractTabbedTerminalWidget.this.myTabs.getTabCount() - 1));
              }
            }), (new TerminalAction(this.mySettingsProvider.getPreviousTabActionPresentation(), (Predicate<KeyEvent>)new Predicate<KeyEvent>()
            {
              public boolean apply(KeyEvent input) {
                AbstractTabbedTerminalWidget.this.selectPreviousTab();
                return true;
              }
            })).withEnabledSupplier((Supplier<Boolean>)new Supplier<Boolean>()
            {
              public Boolean get() {
                return Boolean.valueOf((AbstractTabbedTerminalWidget.this.myTabs != null && AbstractTabbedTerminalWidget.this.myTabs.getSelectedIndex() > 0));
              }
            }) });
  }

  
  private void selectPreviousTab() {
    this.myTabs.setSelectedIndex(this.myTabs.getSelectedIndex() - 1);
  }
  
  private void selectNextTab() {
    this.myTabs.setSelectedIndex(this.myTabs.getSelectedIndex() + 1);
  }

  
  public TerminalActionProvider getNextProvider() {
    return this.myNextActionProvider;
  }

  
  public void setNextProvider(TerminalActionProvider provider) {
    this.myNextActionProvider = provider;
  }
  
  private void handleNewSession() {
    this.myCreateNewSessionAction.apply(this);
  }







  
  public static class TabRenamer
  {
    public void install(final int selectedIndex, String text, final Component label, final RenameCallBack callBack) {
      final JTextField textField = createTextField();
      
      textField.setOpaque(false);
      
      textField.setDocument((Document)new JTextFieldLimit(50));
      textField.setText(text);
      
      final FocusAdapter focusAdapter = new FocusAdapter()
        {
          public void focusLost(FocusEvent focusEvent) {
            AbstractTabbedTerminalWidget.TabRenamer.finishRename(selectedIndex, label, textField.getText(), callBack);
          }
        };
      textField.addFocusListener(focusAdapter);
      textField.addKeyListener(new KeyAdapter()
          {
            public void keyPressed(KeyEvent keyEvent) {
              if (keyEvent.getKeyCode() == 27) {
                textField.removeFocusListener(focusAdapter);
                AbstractTabbedTerminalWidget.TabRenamer.finishRename(selectedIndex, label, null, callBack);
              }
              else if (keyEvent.getKeyCode() == 10) {
                textField.removeFocusListener(focusAdapter);
                AbstractTabbedTerminalWidget.TabRenamer.finishRename(selectedIndex, label, textField.getText(), callBack);
              } else {
                
                super.keyPressed(keyEvent);
              } 
            }
          });
      
      callBack.setComponent(textField);

      
      textField.requestFocus();
      textField.selectAll();
    }
    
    protected JTextField createTextField() {
      return new JTextField();
    }
    
    private static void finishRename(int index, Component label, String newName, RenameCallBack callBack) {
      if (newName != null) {
        callBack.setNewName(index, newName);
      }
      callBack.setComponent(label);
    }
    public static interface RenameCallBack { void setComponent(Component param2Component);
      
      void setNewName(int param2Int, String param2String); }
  }
  
  private class TabComponent extends JPanel implements FocusListener { private T myTerminal;
    private MyLabelHolder myLabelHolder = new MyLabelHolder();
    
    private class MyLabelHolder extends JPanel { private MyLabelHolder() {}
      
      public void set(Component c) {
        AbstractTabbedTerminalWidget.TabComponent.this.myLabelHolder.removeAll();
        AbstractTabbedTerminalWidget.TabComponent.this.myLabelHolder.add(c);
        AbstractTabbedTerminalWidget.TabComponent.this.myLabelHolder.validate();
        AbstractTabbedTerminalWidget.TabComponent.this.myLabelHolder.repaint();
      } }

    
    class TabComponentLabel extends JLabel {
      AbstractTabbedTerminalWidget<T>.TabComponent getTabComponent() {
        return AbstractTabbedTerminalWidget.TabComponent.this;
      }
      
      public String getText() {
        if (AbstractTabbedTerminalWidget.this.myTabs != null) {
          int i = AbstractTabbedTerminalWidget.this.myTabs.indexOfTabComponent(AbstractTabbedTerminalWidget.TabComponent.this);
          if (i != -1) {
            return AbstractTabbedTerminalWidget.this.myTabs.getTitleAt(i);
          }
        } 
        return null;
      }
    }
    
    private TabComponent(final AbstractTabs<T> tabs, final T terminal) {
      super(new FlowLayout(0, 0, 0));
      this.myTerminal = terminal;
      setOpaque(false);
      
      setFocusable(false);
      
      addFocusListener(this);

      
      JLabel label = new TabComponentLabel();
      
      label.addFocusListener(this);



      
      label.addMouseListener(new MouseAdapter()
          {
            public void mouseReleased(MouseEvent event)
            {
              AbstractTabbedTerminalWidget.TabComponent.this.handleMouse(event);
            }

            
            public void mousePressed(MouseEvent event) {
              tabs.setSelectedComponent(terminal);
              AbstractTabbedTerminalWidget.TabComponent.this.handleMouse(event);
            }
          });
      
      this.myLabelHolder.set(label);
      add(this.myLabelHolder);
    }
    
    protected void handleMouse(MouseEvent event) {
      if (event.isPopupTrigger()) {
        JPopupMenu menu = createPopup();
        menu.show(event.getComponent(), event.getX(), event.getY());
      
      }
      else if (event.getClickCount() == 2 && !event.isConsumed()) {
        event.consume();
        renameTab();
      } 
    }

    
    protected JPopupMenu createPopup() {
      JPopupMenu popupMenu = new JPopupMenu();
      
      TerminalAction.addToMenu(popupMenu, AbstractTabbedTerminalWidget.this);
      
      JMenuItem rename = new JMenuItem("Rename Tab");
      
      rename.addActionListener(new ActionListener()
          {
            public void actionPerformed(ActionEvent actionEvent) {
              AbstractTabbedTerminalWidget.TabComponent.this.renameTab();
            }
          });
      
      popupMenu.add(rename);
      
      return popupMenu;
    }
    
    private void renameTab() {
      int selectedIndex = AbstractTabbedTerminalWidget.this.myTabs.getSelectedIndex();
      JLabel label = (JLabel)this.myLabelHolder.getComponent(0);
      
      (new AbstractTabbedTerminalWidget.TabRenamer()).install(selectedIndex, label.getText(), label, new AbstractTabbedTerminalWidget.TabRenamer.RenameCallBack()
          {
            public void setComponent(Component c) {
              AbstractTabbedTerminalWidget.TabComponent.this.myLabelHolder.set(c);
            }

            
            public void setNewName(int index, String name) {
              if (AbstractTabbedTerminalWidget.this.myTabs != null) {
                AbstractTabbedTerminalWidget.this.myTabs.setTitleAt(index, name);
              }
            }
          });
    }

    
    public void focusGained(FocusEvent e) {
      this.myTerminal.getComponent().requestFocusInWindow();
    }


    
    public void focusLost(FocusEvent e) {} }


  
  public AbstractTabs<T> getTerminalTabs() {
    return this.myTabs;
  }

  
  public JComponent getComponent() {
    return this.myPanel;
  }
  
  public JComponent getFocusableComponent() {
    return (this.myTabs != null) ? this.myTabs.getComponent() : ((this.myTermWidget != null) ? (JComponent)this.myTermWidget : this);
  }

  
  public JComponent getPreferredFocusableComponent() {
    return getFocusableComponent();
  }

  
  public boolean canOpenSession() {
    return true;
  }

  
  public void setTerminalPanelListener(TerminalPanelListener terminalPanelListener) {
    if (this.myTabs != null) {
      for (int i = 0; i < this.myTabs.getTabCount(); i++) {
        getTerminalPanel(i).setTerminalPanelListener(terminalPanelListener);
      }
    } else if (this.myTermWidget != null) {
      this.myTermWidget.setTerminalPanelListener(terminalPanelListener);
    } 
    this.myTerminalPanelListener = terminalPanelListener;
  }

  
  @Nullable
  public T getCurrentSession() {
    if (this.myTabs != null) {
      return getTerminalPanel(this.myTabs.getSelectedIndex());
    }
    
    return this.myTermWidget;
  }


  
  public TerminalDisplay getTerminalDisplay() {
    return getCurrentSession().getTerminalDisplay();
  }
  
  @Nullable
  private T getTerminalPanel(int index) {
    if (index < this.myTabs.getTabCount() && index >= 0) {
      return this.myTabs.getComponentAt(index);
    }
    
    return null;
  }

  
  public void addTabListener(TabListener listener) {
    this.myTabListeners.add(listener);
  }
  
  public void removeTabListener(TabListener listener) {
    this.myTabListeners.remove(listener);
  }
  
  private void fireTabClosed(T terminal) {
    for (TabListener<T> l : this.myTabListeners) {
      l.tabClosed(terminal);
    }
  }





  
  public void addListener(TerminalWidgetListener listener) {
    this.myWidgetListeners.add(listener);
  }

  
  public void removeListener(TerminalWidgetListener listener) {
    this.myWidgetListeners.remove(listener);
  }
  
  public TabbedSettingsProvider getSettingsProvider() {
    return this.mySettingsProvider;
  }
  
  public abstract T createInnerTerminalWidget();
  
  protected abstract AbstractTabs<T> createTabbedPane();
  
  public static interface TabListener<T extends JediTermWidget> {
    void tabClosed(T param1T);
  }
}
