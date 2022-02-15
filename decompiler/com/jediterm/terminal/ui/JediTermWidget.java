package com.jediterm.terminal.ui;
import com.jediterm.terminal.SubstringFinder;
import com.jediterm.terminal.Terminal;
import com.jediterm.terminal.TerminalStarter;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.model.JediTerminal;
import com.jediterm.terminal.model.StyleState;
import com.jediterm.terminal.model.TerminalTextBuffer;
import com.jediterm.terminal.model.hyperlinks.HyperlinkFilter;
import com.jediterm.terminal.ui.settings.SettingsProvider;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class JediTermWidget extends JPanel implements TerminalSession, TerminalWidget, TerminalActionProvider {
  private static final Logger LOG = Logger.getLogger(JediTermWidget.class);
  
  protected final TerminalPanel myTerminalPanel;
  protected final JScrollBar myScrollBar;
  protected final JediTerminal myTerminal;
  protected final AtomicBoolean mySessionRunning = new AtomicBoolean();
  private SearchComponent myFindComponent;
  private final PreConnectHandler myPreConnectHandler;
  private TtyConnector myTtyConnector;
  private TerminalStarter myTerminalStarter;
  private Thread myEmuThread;
  protected final SettingsProvider mySettingsProvider;
  private TerminalActionProvider myNextActionProvider;
  private JLayeredPane myInnerPanel;
  private final TextProcessing myTextProcessing;
  private final List<TerminalWidgetListener> myListeners = new CopyOnWriteArrayList<>();
  
  public JediTermWidget(@NotNull SettingsProvider settingsProvider) {
    this(80, 24, settingsProvider);
  }
  
  public JediTermWidget(Dimension dimension, SettingsProvider settingsProvider) {
    this(dimension.width, dimension.height, settingsProvider);
  }
  
  public JediTermWidget(int columns, int lines, SettingsProvider settingsProvider) {
    super(new BorderLayout());
    
    this.mySettingsProvider = settingsProvider;
    
    StyleState styleState = createDefaultStyle();
    
    this
      .myTextProcessing = new TextProcessing(settingsProvider.getHyperlinkColor(), settingsProvider.getHyperlinkHighlightingMode());
    
    TerminalTextBuffer terminalTextBuffer = new TerminalTextBuffer(columns, lines, styleState, settingsProvider.getBufferMaxLinesCount(), this.myTextProcessing);
    this.myTextProcessing.setTerminalTextBuffer(terminalTextBuffer);
    
    this.myTerminalPanel = createTerminalPanel(this.mySettingsProvider, styleState, terminalTextBuffer);
    this.myTerminal = new JediTerminal(this.myTerminalPanel, terminalTextBuffer, styleState);
    
    this.myTerminal.setModeEnabled(TerminalMode.AltSendsEscape, this.mySettingsProvider.altSendsEscape());
    
    this.myTerminalPanel.addTerminalMouseListener((TerminalMouseListener)this.myTerminal);
    this.myTerminalPanel.setNextProvider(this);
    this.myTerminalPanel.setCoordAccessor((TerminalCoordinates)this.myTerminal);
    
    this.myPreConnectHandler = createPreConnectHandler(this.myTerminal);
    this.myTerminalPanel.addCustomKeyListener(this.myPreConnectHandler);
    this.myScrollBar = createScrollBar();
    
    this.myInnerPanel = new JLayeredPane();
    this.myInnerPanel.setFocusable(false);
    setFocusable(false);
    
    this.myInnerPanel.setLayout(new TerminalLayout());
    this.myInnerPanel.add(this.myTerminalPanel, "TERMINAL");
    this.myInnerPanel.add(this.myScrollBar, "SCROLL");
    
    add(this.myInnerPanel, "Center");
    
    this.myScrollBar.setModel(this.myTerminalPanel.getBoundedRangeModel());
    this.mySessionRunning.set(false);
    
    this.myTerminalPanel.init(this.myScrollBar);
    
    this.myTerminalPanel.setVisible(true);
  }
  
  protected JScrollBar createScrollBar() {
    JScrollBar scrollBar = new JScrollBar();
    scrollBar.setUI(new FindResultScrollBarUI());
    return scrollBar;
  }
  
  protected StyleState createDefaultStyle() {
    StyleState styleState = new StyleState();
    styleState.setDefaultStyle(this.mySettingsProvider.getDefaultStyle());
    return styleState;
  }
  
  protected TerminalPanel createTerminalPanel(@NotNull SettingsProvider settingsProvider, @NotNull StyleState styleState, @NotNull TerminalTextBuffer terminalTextBuffer) {
    if (settingsProvider == null) $$$reportNull$$$0(1);  if (styleState == null) $$$reportNull$$$0(2);  if (terminalTextBuffer == null) $$$reportNull$$$0(3);  return new TerminalPanel(settingsProvider, terminalTextBuffer, styleState);
  }
  
  protected PreConnectHandler createPreConnectHandler(JediTerminal terminal) {
    return new PreConnectHandler((Terminal)terminal);
  }
  
  public TerminalDisplay getTerminalDisplay() {
    return getTerminalPanel();
  }
  
  public TerminalPanel getTerminalPanel() {
    return this.myTerminalPanel;
  }
  
  public void setTtyConnector(@NotNull TtyConnector ttyConnector) {
    if (ttyConnector == null) $$$reportNull$$$0(4);  this.myTtyConnector = ttyConnector;
    
    this.myTerminalStarter = createTerminalStarter(this.myTerminal, this.myTtyConnector);
    this.myTerminalPanel.setTerminalStarter(this.myTerminalStarter);
  }
  
  protected TerminalStarter createTerminalStarter(JediTerminal terminal, TtyConnector connector) {
    return new TerminalStarter((Terminal)terminal, connector, (TerminalDataStream)new TtyBasedArrayDataStream(connector));
  }

  
  public TtyConnector getTtyConnector() {
    return this.myTtyConnector;
  }

  
  public Terminal getTerminal() {
    return (Terminal)this.myTerminal;
  }

  
  public String getSessionName() {
    if (this.myTtyConnector != null) {
      return this.myTtyConnector.getName();
    }
    return "Session";
  }

  
  public void start() {
    if (!this.mySessionRunning.get()) {
      this.myEmuThread = new Thread(new EmulatorTask());
      this.myEmuThread.start();
    } else {
      LOG.error("Should not try to start session again at this point... ");
    } 
  }
  
  public void stop() {
    if (this.mySessionRunning.get() && this.myEmuThread != null) {
      this.myEmuThread.interrupt();
    }
  }
  
  public boolean isSessionRunning() {
    return this.mySessionRunning.get();
  }
  
  public String getBufferText(DebugBufferType type) {
    return type.getValue(this);
  }

  
  public TerminalTextBuffer getTerminalTextBuffer() {
    return this.myTerminalPanel.getTerminalTextBuffer();
  }

  
  public boolean requestFocusInWindow() {
    SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            JediTermWidget.this.myTerminalPanel.requestFocusInWindow();
          }
        });
    return super.requestFocusInWindow();
  }

  
  public void requestFocus() {
    this.myTerminalPanel.requestFocus();
  }
  
  public boolean canOpenSession() {
    return !isSessionRunning();
  }

  
  public void setTerminalPanelListener(TerminalPanelListener terminalPanelListener) {
    this.myTerminalPanel.setTerminalPanelListener(terminalPanelListener);
  }

  
  public TerminalSession getCurrentSession() {
    return this;
  }

  
  public JediTermWidget createTerminalSession(TtyConnector ttyConnector) {
    setTtyConnector(ttyConnector);
    return this;
  }

  
  public JComponent getComponent() {
    return this;
  }

  
  public void close() {
    stop();
    if (this.myTerminalStarter != null) {
      this.myTerminalStarter.close();
    }
    this.myTerminalPanel.dispose();
  }

  
  public List<TerminalAction> getActions() {
    return Lists.newArrayList((Object[])new TerminalAction[] { (new TerminalAction(this.mySettingsProvider.getFindActionPresentation(), (Predicate<KeyEvent>)new Predicate<KeyEvent>()
            {
              public boolean apply(KeyEvent input)
              {
                JediTermWidget.this.showFindText();
                return true;
              }
            })).withMnemonicKey(Integer.valueOf(70)) });
  }
  
  private void showFindText() {
    if (this.myFindComponent == null) {
      this.myFindComponent = createSearchComponent();
      
      final JComponent component = this.myFindComponent.getComponent();
      this.myInnerPanel.add(component, "FIND");
      this.myInnerPanel.moveToFront(component);
      this.myInnerPanel.revalidate();
      this.myInnerPanel.repaint();
      component.requestFocus();
      
      this.myFindComponent.addDocumentChangeListener(new DocumentListener()
          {
            public void insertUpdate(DocumentEvent e) {
              textUpdated();
            }

            
            public void removeUpdate(DocumentEvent e) {
              textUpdated();
            }

            
            public void changedUpdate(DocumentEvent e) {
              textUpdated();
            }
            
            private void textUpdated() {
              JediTermWidget.this.findText(JediTermWidget.this.myFindComponent.getText(), JediTermWidget.this.myFindComponent.ignoreCase());
            }
          });
      
      this.myFindComponent.addIgnoreCaseListener(new ItemListener()
          {
            public void itemStateChanged(ItemEvent e) {
              JediTermWidget.this.findText(JediTermWidget.this.myFindComponent.getText(), JediTermWidget.this.myFindComponent.ignoreCase());
            }
          });
      
      this.myFindComponent.addKeyListener(new KeyAdapter()
          {
            public void keyPressed(KeyEvent keyEvent) {
              if (keyEvent.getKeyCode() == 27) {
                JediTermWidget.this.myInnerPanel.remove(component);
                JediTermWidget.this.myInnerPanel.revalidate();
                JediTermWidget.this.myInnerPanel.repaint();
                JediTermWidget.this.myFindComponent = null;
                JediTermWidget.this.myTerminalPanel.setFindResult((SubstringFinder.FindResult)null);
                JediTermWidget.this.myTerminalPanel.requestFocusInWindow();
              } else if (keyEvent.getKeyCode() == 10 || keyEvent.getKeyCode() == 38) {
                JediTermWidget.this.myFindComponent.nextFindResultItem(JediTermWidget.this.myTerminalPanel.selectNextFindResultItem());
              } else if (keyEvent.getKeyCode() == 40) {
                JediTermWidget.this.myFindComponent.prevFindResultItem(JediTermWidget.this.myTerminalPanel.selectPrevFindResultItem());
              } else {
                super.keyPressed(keyEvent);
              } 
            }
          });
    } else {
      this.myFindComponent.getComponent().requestFocusInWindow();
    } 
  }
  
  protected SearchComponent createSearchComponent() {
    return new SearchPanel();
  }




















  
  private void findText(String text, boolean ignoreCase) {
    SubstringFinder.FindResult results = this.myTerminal.searchInTerminalTextBuffer(text, ignoreCase);
    this.myTerminalPanel.setFindResult(results);
    this.myFindComponent.onResultUpdated(results);
    this.myScrollBar.repaint();
  }

  
  public TerminalActionProvider getNextProvider() {
    return this.myNextActionProvider;
  }
  
  public void setNextProvider(TerminalActionProvider actionProvider) {
    this.myNextActionProvider = actionProvider;
  } protected static interface SearchComponent {
    String getText(); boolean ignoreCase(); JComponent getComponent(); void addDocumentChangeListener(DocumentListener param1DocumentListener); void addKeyListener(KeyListener param1KeyListener); void addIgnoreCaseListener(ItemListener param1ItemListener); void onResultUpdated(SubstringFinder.FindResult param1FindResult); void nextFindResultItem(SubstringFinder.FindResult.FindItem param1FindItem);
    void prevFindResultItem(SubstringFinder.FindResult.FindItem param1FindItem); }
  class EmulatorTask implements Runnable { public void run() {
      try {
        JediTermWidget.this.mySessionRunning.set(true);
        Thread.currentThread().setName("Connector-" + JediTermWidget.this.myTtyConnector.getName());
        if (JediTermWidget.this.myTtyConnector.init(JediTermWidget.this.myPreConnectHandler)) {
          JediTermWidget.this.myTerminalPanel.addCustomKeyListener(JediTermWidget.this.myTerminalPanel.getTerminalKeyListener());
          JediTermWidget.this.myTerminalPanel.removeCustomKeyListener(JediTermWidget.this.myPreConnectHandler);
          JediTermWidget.this.myTerminalStarter.start();
        } 
      } catch (Exception e) {
        JediTermWidget.LOG.error("Exception running terminal", e);
      } finally {
        try {
          JediTermWidget.this.myTtyConnector.close();
        } catch (Exception exception) {}
        
        JediTermWidget.this.mySessionRunning.set(false);
        TerminalPanelListener terminalPanelListener = JediTermWidget.this.myTerminalPanel.getTerminalPanelListener();
        if (terminalPanelListener != null)
          terminalPanelListener.onSessionChanged(JediTermWidget.this.getCurrentSession()); 
        for (TerminalWidgetListener listener : JediTermWidget.this.myListeners) {
          listener.allSessionsClosed(JediTermWidget.this);
        }
        JediTermWidget.this.myTerminalPanel.addCustomKeyListener(JediTermWidget.this.myPreConnectHandler);
        JediTermWidget.this.myTerminalPanel.removeCustomKeyListener(JediTermWidget.this.myTerminalPanel.getTerminalKeyListener());
      } 
    } }

  
  public TerminalStarter getTerminalStarter() {
    return this.myTerminalStarter;
  }
  
  public class SearchPanel
    extends JPanel implements SearchComponent {
    private final JTextField myTextField = new JTextField();
    private final JLabel label = new JLabel();
    private final JButton prev;
    private final JButton next;
    private final JCheckBox ignoreCaseCheckBox = new JCheckBox("Ignore Case", true);
    
    public SearchPanel() {
      this.next = createNextButton();
      this.next.addActionListener(new ActionListener()
          {
            public void actionPerformed(ActionEvent e) {
              JediTermWidget.SearchPanel.this.nextFindResultItem(JediTermWidget.this.myTerminalPanel.selectNextFindResultItem());
            }
          });
      
      this.prev = createPrevButton();
      this.prev.addActionListener(new ActionListener()
          {
            public void actionPerformed(ActionEvent e) {
              JediTermWidget.SearchPanel.this.prevFindResultItem(JediTermWidget.this.myTerminalPanel.selectPrevFindResultItem());
            }
          });
      
      this.myTextField.setPreferredSize(new Dimension(JediTermWidget.this.myTerminalPanel.myCharSize.width * 30, JediTermWidget.this.myTerminalPanel.myCharSize.height + 3));

      
      this.myTextField.setEditable(true);
      
      updateLabel((SubstringFinder.FindResult.FindItem)null);
      
      add(this.myTextField);
      add(this.ignoreCaseCheckBox);
      add(this.label);
      add(this.next);
      add(this.prev);
      
      setOpaque(true);
    }
    
    protected JButton createNextButton() {
      return new BasicArrowButton(1);
    }
    
    protected JButton createPrevButton() {
      return new BasicArrowButton(5);
    }

    
    public void nextFindResultItem(SubstringFinder.FindResult.FindItem selectedItem) {
      updateLabel(selectedItem);
    }

    
    public void prevFindResultItem(SubstringFinder.FindResult.FindItem selectedItem) {
      updateLabel(selectedItem);
    }
    
    private void updateLabel(SubstringFinder.FindResult.FindItem selectedItem) {
      SubstringFinder.FindResult result = JediTermWidget.this.myTerminalPanel.getFindResult();
      this.label.setText(((selectedItem != null) ? selectedItem.getIndex() : 0) + " of " + ((result != null) ? result
          .getItems().size() : 0));
    }

    
    public void onResultUpdated(SubstringFinder.FindResult results) {
      updateLabel((SubstringFinder.FindResult.FindItem)null);
    }

    
    public String getText() {
      return this.myTextField.getText();
    }

    
    public boolean ignoreCase() {
      return this.ignoreCaseCheckBox.isSelected();
    }

    
    public JComponent getComponent() {
      return this;
    }
    
    public void requestFocus() {
      this.myTextField.requestFocus();
    }

    
    public void addDocumentChangeListener(DocumentListener listener) {
      this.myTextField.getDocument().addDocumentListener(listener);
    }

    
    public void addKeyListener(KeyListener listener) {
      this.myTextField.addKeyListener(listener);
    }

    
    public void addIgnoreCaseListener(ItemListener listener) {
      this.ignoreCaseCheckBox.addItemListener(listener);
    }
  }
  
  private class FindResultScrollBarUI extends BasicScrollBarUI {
    private FindResultScrollBarUI() {}
    
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
      super.paintTrack(g, c, trackBounds);
      
      SubstringFinder.FindResult result = JediTermWidget.this.myTerminalPanel.getFindResult();
      if (result != null) {
        int modelHeight = this.scrollbar.getModel().getMaximum() - this.scrollbar.getModel().getMinimum();
        int anchorHeight = Math.max(2, trackBounds.height / modelHeight);

        
        Color color = JediTermWidget.this.mySettingsProvider.getTerminalColorPalette().getBackground(Objects.<TerminalColor>requireNonNull(JediTermWidget.this.mySettingsProvider.getFoundPatternColor().getBackground()));
        g.setColor(color);
        for (SubstringFinder.FindResult.FindItem r : result.getItems()) {
          int where = trackBounds.height * (r.getStart()).y / modelHeight;
          g.fillRect(trackBounds.x, trackBounds.y + where, trackBounds.width, anchorHeight);
        } 
      } 
    }
  }
  
  private static class TerminalLayout
    implements LayoutManager {
    public static final String TERMINAL = "TERMINAL";
    public static final String SCROLL = "SCROLL";
    public static final String FIND = "FIND";
    private Component terminal;
    private Component scroll;
    private Component find;
    
    private TerminalLayout() {}
    
    public void addLayoutComponent(String name, Component comp) {
      if ("TERMINAL".equals(name))
      { this.terminal = comp; }
      else if ("FIND".equals(name))
      { this.find = comp; }
      else if ("SCROLL".equals(name))
      { this.scroll = comp; }
      else { throw new IllegalArgumentException("unknown component name " + name); }
    
    }
    
    public void removeLayoutComponent(Component comp) {
      if (comp == this.terminal) {
        this.terminal = null;
      }
      if (comp == this.scroll) {
        this.scroll = null;
      }
      if (comp == this.find) {
        this.find = comp;
      }
    }

    
    public Dimension preferredLayoutSize(Container target) {
      synchronized (target.getTreeLock()) {
        Dimension dim = new Dimension(0, 0);
        
        if (this.terminal != null) {
          Dimension d = this.terminal.getPreferredSize();
          dim.width = Math.max(d.width, dim.width);
          dim.height = Math.max(d.height, dim.height);
        } 
        
        if (this.scroll != null) {
          Dimension d = this.scroll.getPreferredSize();
          dim.width += d.width;
          dim.height = Math.max(d.height, dim.height);
        } 
        
        if (this.find != null) {
          Dimension d = this.find.getPreferredSize();
          dim.width = Math.max(d.width, dim.width);
          dim.height = Math.max(d.height, dim.height);
        } 
        
        Insets insets = target.getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;
        
        return dim;
      } 
    }

    
    public Dimension minimumLayoutSize(Container target) {
      synchronized (target.getTreeLock()) {
        Dimension dim = new Dimension(0, 0);
        
        if (this.terminal != null) {
          Dimension d = this.terminal.getMinimumSize();
          dim.width = Math.max(d.width, dim.width);
          dim.height = Math.max(d.height, dim.height);
        } 
        
        if (this.scroll != null) {
          Dimension d = this.scroll.getPreferredSize();
          dim.width += d.width;
          dim.height = Math.max(d.height, dim.height);
        } 
        
        if (this.find != null) {
          Dimension d = this.find.getMinimumSize();
          dim.width = Math.max(d.width, dim.width);
          dim.height = Math.max(d.height, dim.height);
        } 
        
        Insets insets = target.getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;
        
        return dim;
      } 
    }

    
    public void layoutContainer(Container target) {
      synchronized (target.getTreeLock()) {
        Insets insets = target.getInsets();
        int top = insets.top;
        int bottom = target.getHeight() - insets.bottom;
        int left = insets.left;
        int right = target.getWidth() - insets.right;
        
        Dimension scrollDim = new Dimension(0, 0);
        if (this.scroll != null) {
          scrollDim = this.scroll.getPreferredSize();
          this.scroll.setBounds(right - scrollDim.width, top, scrollDim.width, bottom - top);
        } 
        
        if (this.terminal != null) {
          this.terminal.setBounds(left, top, right - left - scrollDim.width, bottom - top);
        }
        
        if (this.find != null) {
          Dimension d = this.find.getPreferredSize();
          this.find.setBounds(right - d.width - scrollDim.width, top, d.width, d.height);
        } 
      } 
    }
  }

  
  public void addHyperlinkFilter(HyperlinkFilter filter) {
    this.myTextProcessing.addHyperlinkFilter(filter);
  }

  
  public void addListener(TerminalWidgetListener listener) {
    this.myListeners.add(listener);
  }

  
  public void removeListener(TerminalWidgetListener listener) {
    this.myListeners.remove(listener);
  }
}
