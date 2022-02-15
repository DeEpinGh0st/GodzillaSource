package com.formdev.flatlaf.demo;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

class TabsPanel extends JPanel {
  private JToolBar tabPlacementToolBar;
  private JToggleButton topPlacementButton;
  private JToggleButton bottomPlacementButton;
  private JToggleButton leftPlacementButton;
  private JToggleButton rightPlacementButton;
  private JToggleButton scrollButton;
  private JToggleButton borderButton;
  private JTabbedPane tabPlacementTabbedPane;
  private JToolBar tabLayoutToolBar;
  private JToggleButton scrollTabLayoutButton;
  private JToggleButton wrapTabLayoutButton;
  private JLabel scrollLayoutNoteLabel;
  private JLabel wrapLayoutNoteLabel;
  private JTabbedPane scrollLayoutTabbedPane;
  private JTabbedPane wrapLayoutTabbedPane;
  private JToolBar closableTabsToolBar;
  private JToggleButton squareCloseButton;
  private JToggleButton circleCloseButton;
  private JToggleButton redCrossCloseButton;
  private JTabbedPane closableTabsTabbedPane;
  private JToolBar tabAreaComponentsToolBar;
  private JToggleButton leadingComponentButton;
  private JToggleButton trailingComponentButton;
  private JTabbedPane customComponentsTabbedPane;
  private JTabbedPane iconTopTabbedPane;
  private JTabbedPane iconBottomTabbedPane;
  private JTabbedPane iconLeadingTabbedPane;
  private JTabbedPane iconTrailingTabbedPane;
  
  TabsPanel() {
    initComponents();
    
    initTabPlacementTabs(this.tabPlacementTabbedPane);
    
    initScrollLayoutTabs(this.scrollLayoutTabbedPane);
    initWrapLayoutTabs(this.wrapLayoutTabbedPane);
    
    initClosableTabs(this.closableTabsTabbedPane);
    
    initCustomComponentsTabs(this.customComponentsTabbedPane);
    
    initMinimumTabWidth(this.minimumTabWidthTabbedPane);
    initMaximumTabWidth(this.maximumTabWidthTabbedPane);
    
    initTabIconPlacement(this.iconTopTabbedPane, 1);
    initTabIconPlacement(this.iconBottomTabbedPane, 3);
    initTabIconPlacement(this.iconLeadingTabbedPane, 10);
    initTabIconPlacement(this.iconTrailingTabbedPane, 11);
    
    initTabAreaAlignment(this.alignLeadingTabbedPane, "leading");
    initTabAreaAlignment(this.alignCenterTabbedPane, "center");
    initTabAreaAlignment(this.alignTrailingTabbedPane, "trailing");
    initTabAreaAlignment(this.alignFillTabbedPane, "fill");
    
    initTabAlignment(this.tabAlignLeadingTabbedPane, 10);
    initTabAlignment(this.tabAlignCenterTabbedPane, 0);
    initTabAlignment(this.tabAlignTrailingTabbedPane, 11);
    initTabAlignment(this.tabAlignVerticalTabbedPane, 11);
    
    initTabWidthMode(this.widthPreferredTabbedPane, "preferred");
    initTabWidthMode(this.widthEqualTabbedPane, "equal");
    initTabWidthMode(this.widthCompactTabbedPane, "compact");
  }
  private JTabbedPane alignLeadingTabbedPane; private JTabbedPane alignCenterTabbedPane; private JTabbedPane alignTrailingTabbedPane; private JTabbedPane alignFillTabbedPane; private JTabbedPane widthPreferredTabbedPane; private JTabbedPane widthEqualTabbedPane; private JTabbedPane widthCompactTabbedPane; private JTabbedPane minimumTabWidthTabbedPane; private JTabbedPane maximumTabWidthTabbedPane; private JPanel panel5; private JTabbedPane tabAlignLeadingTabbedPane; private JTabbedPane tabAlignVerticalTabbedPane; private JTabbedPane tabAlignCenterTabbedPane; private JTabbedPane tabAlignTrailingTabbedPane; private JSeparator separator2; private JLabel scrollButtonsPolicyLabel; private JToolBar scrollButtonsPolicyToolBar; private JToggleButton scrollAsNeededSingleButton; private JToggleButton scrollAsNeededButton; private JToggleButton scrollNeverButton; private JLabel scrollButtonsPlacementLabel; private JToolBar scrollButtonsPlacementToolBar; private JToggleButton scrollBothButton; private JToggleButton scrollTrailingButton; private JLabel tabsPopupPolicyLabel; private JToolBar tabsPopupPolicyToolBar; private JToggleButton popupAsNeededButton; private JToggleButton popupNeverButton; private JCheckBox showTabSeparatorsCheckBox;
  private void initTabPlacementTabs(JTabbedPane tabbedPane) {
    addTab(tabbedPane, "Tab 1", "tab content 1");
    
    JComponent tab2 = createTab("tab content 2");
    tab2.setBorder(new LineBorder(Color.magenta));
    tabbedPane.addTab("Second Tab", tab2);
    
    addTab(tabbedPane, "Disabled", "tab content 3");
    tabbedPane.setEnabledAt(2, false);
  }
  
  private void addTab(JTabbedPane tabbedPane, String title, String text) {
    tabbedPane.addTab(title, createTab(text));
  }
  
  private JComponent createTab(String text) {
    JLabel label = new JLabel(text);
    label.setHorizontalAlignment(0);
    
    JPanel tab = new JPanel(new BorderLayout());
    tab.add(label, "Center");
    return tab;
  }
  
  private void tabPlacementChanged() {
    int tabPlacement = 1;
    if (this.bottomPlacementButton.isSelected()) {
      tabPlacement = 3;
    } else if (this.leftPlacementButton.isSelected()) {
      tabPlacement = 2;
    } else if (this.rightPlacementButton.isSelected()) {
      tabPlacement = 4;
    } 
    this.tabPlacementTabbedPane.setTabPlacement(tabPlacement);
  }
  
  private void scrollChanged() {
    boolean scroll = this.scrollButton.isSelected();
    this.tabPlacementTabbedPane.setTabLayoutPolicy(scroll ? 1 : 0);
    
    int extraTabCount = 7;
    if (scroll) {
      int tabCount = this.tabPlacementTabbedPane.getTabCount();
      for (int i = tabCount + 1; i <= tabCount + extraTabCount; i++)
        addTab(this.tabPlacementTabbedPane, "Tab " + i, "tab content " + i); 
    } else {
      for (int i = 0; i < extraTabCount; i++)
        this.tabPlacementTabbedPane.removeTabAt(this.tabPlacementTabbedPane.getTabCount() - 1); 
    } 
  }
  
  private void borderChanged() {
    Boolean hasFullBorder = this.borderButton.isSelected() ? Boolean.valueOf(true) : null;
    this.tabPlacementTabbedPane.putClientProperty("JTabbedPane.hasFullBorder", hasFullBorder);
  }
  
  private void initScrollLayoutTabs(JTabbedPane tabbedPane) {
    tabbedPane.setTabLayoutPolicy(1);
    addDefaultTabsNoContent(tabbedPane, 9);
  }
  
  private void initWrapLayoutTabs(JTabbedPane tabbedPane) {
    tabbedPane.setTabLayoutPolicy(0);
    addDefaultTabsNoContent(tabbedPane, 9);
    
    this.wrapLayoutTabbedPane.setVisible(false);
    this.wrapLayoutNoteLabel.setVisible(false);
  }
  
  private void tabLayoutChanged() {
    boolean scroll = this.scrollTabLayoutButton.isSelected();
    
    this.scrollLayoutTabbedPane.setVisible(scroll);
    this.scrollLayoutNoteLabel.setVisible(scroll);
    this.wrapLayoutTabbedPane.setVisible(!scroll);
    this.wrapLayoutNoteLabel.setVisible(!scroll);
  }
  
  private void initClosableTabs(JTabbedPane tabbedPane) {
    tabbedPane.putClientProperty("JTabbedPane.tabClosable", Boolean.valueOf(true));
    tabbedPane.putClientProperty("JTabbedPane.tabCloseToolTipText", "Close");
    tabbedPane.putClientProperty("JTabbedPane.tabCloseCallback", (tabPane, tabIndex) -> {
          AWTEvent e = EventQueue.getCurrentEvent();

          
          int modifiers = (e instanceof MouseEvent) ? ((MouseEvent)e).getModifiers() : 0;
          
          JOptionPane.showMessageDialog(this, "Closed tab '" + tabPane.getTitleAt(tabIndex.intValue()) + "'.\n\n(modifiers: " + MouseEvent.getMouseModifiersText(modifiers) + ")", "Tab Closed", -1);
        });
    
    addDefaultTabsNoContent(tabbedPane, 3);
  }
  
  private void initCustomComponentsTabs(JTabbedPane tabbedPane) {
    addDefaultTabsNoContent(tabbedPane, 2);
    customComponentsChanged();
  }
  
  private void customComponentsChanged() {
    JToolBar leading = null;
    JToolBar trailing = null;
    if (this.leadingComponentButton.isSelected()) {
      leading = new JToolBar();
      leading.setFloatable(false);
      leading.setBorder((Border)null);
      leading.add(new JButton((Icon)new FlatSVGIcon("com/formdev/flatlaf/demo/icons/project.svg")));
    } 
    if (this.trailingComponentButton.isSelected()) {
      trailing = new JToolBar();
      trailing.setFloatable(false);
      trailing.setBorder((Border)null);
      trailing.add(new JButton((Icon)new FlatSVGIcon("com/formdev/flatlaf/demo/icons/buildLoadChanges.svg")));
      trailing.add(Box.createHorizontalGlue());
      trailing.add(new JButton((Icon)new FlatSVGIcon("com/formdev/flatlaf/demo/icons/commit.svg")));
      trailing.add(new JButton((Icon)new FlatSVGIcon("com/formdev/flatlaf/demo/icons/diff.svg")));
      trailing.add(new JButton((Icon)new FlatSVGIcon("com/formdev/flatlaf/demo/icons/listFiles.svg")));
    } 
    this.customComponentsTabbedPane.putClientProperty("JTabbedPane.leadingComponent", leading);
    this.customComponentsTabbedPane.putClientProperty("JTabbedPane.trailingComponent", trailing);
  }
  
  private void addDefaultTabsNoContent(JTabbedPane tabbedPane, int count) {
    tabbedPane.addTab("Tab 1", (Component)null);
    tabbedPane.addTab("Second Tab", (Component)null);
    if (count >= 3) {
      tabbedPane.addTab("3rd Tab", (Component)null);
    }
    for (int i = 4; i <= count; i++) {
      tabbedPane.addTab("Tab " + i, (Component)null);
    }
  }


  
  private void closeButtonStyleChanged() {
    if (this.circleCloseButton.isSelected()) {
      UIManager.put("TabbedPane.closeArc", Integer.valueOf(999));
      UIManager.put("TabbedPane.closeCrossFilledSize", Float.valueOf(5.5F));
      UIManager.put("TabbedPane.closeIcon", new FlatTabbedPaneCloseIcon());
      this.closableTabsTabbedPane.updateUI();
      UIManager.put("TabbedPane.closeArc", null);
      UIManager.put("TabbedPane.closeCrossFilledSize", null);
      UIManager.put("TabbedPane.closeIcon", null);
    } else if (this.redCrossCloseButton.isSelected()) {
      UIManager.put("TabbedPane.closeHoverForeground", Color.red);
      UIManager.put("TabbedPane.closePressedForeground", Color.red);
      UIManager.put("TabbedPane.closeHoverBackground", new Color(0, true));
      UIManager.put("TabbedPane.closeIcon", new FlatTabbedPaneCloseIcon());
      this.closableTabsTabbedPane.updateUI();
      UIManager.put("TabbedPane.closeHoverForeground", null);
      UIManager.put("TabbedPane.closePressedForeground", null);
      UIManager.put("TabbedPane.closeHoverBackground", null);
      UIManager.put("TabbedPane.closeIcon", null);
    } else {
      this.closableTabsTabbedPane.updateUI();
    } 
  }
  private void initTabIconPlacement(JTabbedPane tabbedPane, int iconPlacement) {
    boolean topOrBottom = (iconPlacement == 1 || iconPlacement == 3);
    int iconSize = topOrBottom ? 24 : 16;
    tabbedPane.putClientProperty("JTabbedPane.tabIconPlacement", Integer.valueOf(iconPlacement));
    if (topOrBottom) {
      tabbedPane.putClientProperty("JTabbedPane.tabAreaAlignment", "fill");
      tabbedPane.putClientProperty("JTabbedPane.tabWidthMode", "equal");
    } 
    tabbedPane.addTab("Search", (Icon)new FlatSVGIcon("com/formdev/flatlaf/demo/icons/search.svg", iconSize, iconSize), (Component)null);
    tabbedPane.addTab("Recents", (Icon)new FlatSVGIcon("com/formdev/flatlaf/demo/icons/RecentlyUsed.svg", iconSize, iconSize), (Component)null);
    if (topOrBottom)
      tabbedPane.addTab("Favorites", (Icon)new FlatSVGIcon("com/formdev/flatlaf/demo/icons/favorite.svg", iconSize, iconSize), (Component)null); 
  }
  
  private void initTabAreaAlignment(JTabbedPane tabbedPane, String tabAreaAlignment) {
    tabbedPane.putClientProperty("JTabbedPane.tabAreaAlignment", tabAreaAlignment);
    tabbedPane.addTab("Search", (Component)null);
    tabbedPane.addTab("Recents", (Component)null);
  }
  
  private void initTabAlignment(JTabbedPane tabbedPane, int tabAlignment) {
    boolean vertical = (tabbedPane.getTabPlacement() == 2 || tabbedPane.getTabPlacement() == 4);
    tabbedPane.putClientProperty("JTabbedPane.tabAlignment", Integer.valueOf(tabAlignment));
    if (!vertical)
      tabbedPane.putClientProperty("JTabbedPane.minimumTabWidth", Integer.valueOf(80)); 
    tabbedPane.addTab("A", (Component)null);
    if (vertical) {
      tabbedPane.addTab("Search", (Component)null);
      tabbedPane.addTab("Recents", (Component)null);
    } 
  }
  
  private void initTabWidthMode(JTabbedPane tabbedPane, String tabWidthMode) {
    tabbedPane.putClientProperty("JTabbedPane.tabWidthMode", tabWidthMode);
    if (tabWidthMode.equals("compact")) {
      tabbedPane.addTab("Search", (Icon)new FlatSVGIcon("com/formdev/flatlaf/demo/icons/search.svg", 16, 16), (Component)null);
      tabbedPane.addTab("Recents", (Icon)new FlatSVGIcon("com/formdev/flatlaf/demo/icons/RecentlyUsed.svg", 16, 16), (Component)null);
      tabbedPane.addTab("Favorites", (Icon)new FlatSVGIcon("com/formdev/flatlaf/demo/icons/favorite.svg", 16, 16), (Component)null);
    } else {
      tabbedPane.addTab("Short", (Component)null);
      tabbedPane.addTab("Longer Title", (Component)null);
    } 
  }
  
  private void initMinimumTabWidth(JTabbedPane tabbedPane) {
    tabbedPane.putClientProperty("JTabbedPane.minimumTabWidth", Integer.valueOf(80));
    tabbedPane.addTab("A", (Component)null);
    tabbedPane.addTab("Very long title", (Component)null);
  }
  
  private void initMaximumTabWidth(JTabbedPane tabbedPane) {
    tabbedPane.putClientProperty("JTabbedPane.maximumTabWidth", Integer.valueOf(80));
    tabbedPane.addTab("Very long title", (Component)null);
    tabbedPane.addTab("B", (Component)null);
    tabbedPane.addTab("C", (Component)null);
  }
  
  private void tabsPopupPolicyChanged() {
    String tabsPopupPolicy = this.popupNeverButton.isSelected() ? "never" : null;
    putTabbedPanesClientProperty("JTabbedPane.tabsPopupPolicy", tabsPopupPolicy);
  }


  
  private void scrollButtonsPolicyChanged() {
    String scrollButtonsPolicy = this.scrollAsNeededButton.isSelected() ? "asNeeded" : (this.scrollNeverButton.isSelected() ? "never" : null);

    
    putTabbedPanesClientProperty("JTabbedPane.scrollButtonsPolicy", scrollButtonsPolicy);
  }
  
  private void scrollButtonsPlacementChanged() {
    String scrollButtonsPlacement = this.scrollTrailingButton.isSelected() ? "trailing" : null;
    putTabbedPanesClientProperty("JTabbedPane.scrollButtonsPlacement", scrollButtonsPlacement);
  }
  
  private void showTabSeparatorsChanged() {
    Boolean showTabSeparators = this.showTabSeparatorsCheckBox.isSelected() ? Boolean.valueOf(true) : null;
    putTabbedPanesClientProperty("JTabbedPane.showTabSeparators", showTabSeparators);
  }
  
  private void putTabbedPanesClientProperty(String key, Object value) {
    updateTabbedPanesRecur(this, tabbedPane -> tabbedPane.putClientProperty(key, value));
  }
  
  private void updateTabbedPanesRecur(Container container, Consumer<JTabbedPane> action) {
    for (Component c : container.getComponents()) {
      if (c instanceof JTabbedPane) {
        JTabbedPane tabPane = (JTabbedPane)c;
        action.accept(tabPane);
      } 
      
      if (c instanceof Container) {
        updateTabbedPanesRecur((Container)c, action);
      }
    } 
  }
  
  private void initComponents() {
    JPanel panel1 = new JPanel();
    JLabel tabPlacementLabel = new JLabel();
    this.tabPlacementToolBar = new JToolBar();
    this.topPlacementButton = new JToggleButton();
    this.bottomPlacementButton = new JToggleButton();
    this.leftPlacementButton = new JToggleButton();
    this.rightPlacementButton = new JToggleButton();
    this.scrollButton = new JToggleButton();
    this.borderButton = new JToggleButton();
    this.tabPlacementTabbedPane = new JTabbedPane();
    JLabel tabLayoutLabel = new JLabel();
    this.tabLayoutToolBar = new JToolBar();
    this.scrollTabLayoutButton = new JToggleButton();
    this.wrapTabLayoutButton = new JToggleButton();
    this.scrollLayoutNoteLabel = new JLabel();
    this.wrapLayoutNoteLabel = new JLabel();
    this.scrollLayoutTabbedPane = new JTabbedPane();
    this.wrapLayoutTabbedPane = new JTabbedPane();
    JLabel closableTabsLabel = new JLabel();
    this.closableTabsToolBar = new JToolBar();
    this.squareCloseButton = new JToggleButton();
    this.circleCloseButton = new JToggleButton();
    this.redCrossCloseButton = new JToggleButton();
    this.closableTabsTabbedPane = new JTabbedPane();
    JLabel tabAreaComponentsLabel = new JLabel();
    this.tabAreaComponentsToolBar = new JToolBar();
    this.leadingComponentButton = new JToggleButton();
    this.trailingComponentButton = new JToggleButton();
    this.customComponentsTabbedPane = new JTabbedPane();
    JPanel panel2 = new JPanel();
    JLabel tabIconPlacementLabel = new JLabel();
    JLabel tabIconPlacementNodeLabel = new JLabel();
    this.iconTopTabbedPane = new JTabbedPane();
    this.iconBottomTabbedPane = new JTabbedPane();
    this.iconLeadingTabbedPane = new JTabbedPane();
    this.iconTrailingTabbedPane = new JTabbedPane();
    JLabel tabAreaAlignmentLabel = new JLabel();
    JLabel tabAreaAlignmentNoteLabel = new JLabel();
    this.alignLeadingTabbedPane = new JTabbedPane();
    this.alignCenterTabbedPane = new JTabbedPane();
    this.alignTrailingTabbedPane = new JTabbedPane();
    this.alignFillTabbedPane = new JTabbedPane();
    JPanel panel3 = new JPanel();
    JLabel tabWidthModeLabel = new JLabel();
    JLabel tabWidthModeNoteLabel = new JLabel();
    this.widthPreferredTabbedPane = new JTabbedPane();
    this.widthEqualTabbedPane = new JTabbedPane();
    this.widthCompactTabbedPane = new JTabbedPane();
    JLabel minMaxTabWidthLabel = new JLabel();
    this.minimumTabWidthTabbedPane = new JTabbedPane();
    this.maximumTabWidthTabbedPane = new JTabbedPane();
    JLabel tabAlignmentLabel = new JLabel();
    this.panel5 = new JPanel();
    JLabel tabAlignmentNoteLabel = new JLabel();
    JLabel tabAlignmentNoteLabel2 = new JLabel();
    this.tabAlignLeadingTabbedPane = new JTabbedPane();
    this.tabAlignVerticalTabbedPane = new JTabbedPane();
    this.tabAlignCenterTabbedPane = new JTabbedPane();
    this.tabAlignTrailingTabbedPane = new JTabbedPane();
    this.separator2 = new JSeparator();
    JPanel panel4 = new JPanel();
    this.scrollButtonsPolicyLabel = new JLabel();
    this.scrollButtonsPolicyToolBar = new JToolBar();
    this.scrollAsNeededSingleButton = new JToggleButton();
    this.scrollAsNeededButton = new JToggleButton();
    this.scrollNeverButton = new JToggleButton();
    this.scrollButtonsPlacementLabel = new JLabel();
    this.scrollButtonsPlacementToolBar = new JToolBar();
    this.scrollBothButton = new JToggleButton();
    this.scrollTrailingButton = new JToggleButton();
    this.tabsPopupPolicyLabel = new JLabel();
    this.tabsPopupPolicyToolBar = new JToolBar();
    this.popupAsNeededButton = new JToggleButton();
    this.popupNeverButton = new JToggleButton();
    this.showTabSeparatorsCheckBox = new JCheckBox();

    
    setName("this");
    setLayout((LayoutManager)new MigLayout("insets dialog,hidemode 3", "[grow,fill]para[fill]para[fill]", "[grow,fill]para[][]"));











    
    panel1.setName("panel1");
    panel1.setLayout((LayoutManager)new MigLayout("insets 0,hidemode 3", "[grow,fill]", "[][fill]para[]0[][]para[][]para[][]"));














    
    tabPlacementLabel.setText("Tab placement");
    tabPlacementLabel.setFont(tabPlacementLabel.getFont().deriveFont(tabPlacementLabel.getFont().getSize() + 4.0F));
    tabPlacementLabel.setName("tabPlacementLabel");
    panel1.add(tabPlacementLabel, "cell 0 0");


    
    this.tabPlacementToolBar.setFloatable(false);
    this.tabPlacementToolBar.setBorder(BorderFactory.createEmptyBorder());
    this.tabPlacementToolBar.setName("tabPlacementToolBar");

    
    this.topPlacementButton.setText("top");
    this.topPlacementButton.setSelected(true);
    this.topPlacementButton.setFont(this.topPlacementButton.getFont().deriveFont(this.topPlacementButton.getFont().getSize() - 2.0F));
    this.topPlacementButton.setName("topPlacementButton");
    this.topPlacementButton.addActionListener(e -> tabPlacementChanged());
    this.tabPlacementToolBar.add(this.topPlacementButton);

    
    this.bottomPlacementButton.setText("bottom");
    this.bottomPlacementButton.setFont(this.bottomPlacementButton.getFont().deriveFont(this.bottomPlacementButton.getFont().getSize() - 2.0F));
    this.bottomPlacementButton.setName("bottomPlacementButton");
    this.bottomPlacementButton.addActionListener(e -> tabPlacementChanged());
    this.tabPlacementToolBar.add(this.bottomPlacementButton);

    
    this.leftPlacementButton.setText("left");
    this.leftPlacementButton.setFont(this.leftPlacementButton.getFont().deriveFont(this.leftPlacementButton.getFont().getSize() - 2.0F));
    this.leftPlacementButton.setName("leftPlacementButton");
    this.leftPlacementButton.addActionListener(e -> tabPlacementChanged());
    this.tabPlacementToolBar.add(this.leftPlacementButton);

    
    this.rightPlacementButton.setText("right");
    this.rightPlacementButton.setFont(this.rightPlacementButton.getFont().deriveFont(this.rightPlacementButton.getFont().getSize() - 2.0F));
    this.rightPlacementButton.setName("rightPlacementButton");
    this.rightPlacementButton.addActionListener(e -> tabPlacementChanged());
    this.tabPlacementToolBar.add(this.rightPlacementButton);
    this.tabPlacementToolBar.addSeparator();

    
    this.scrollButton.setText("scroll");
    this.scrollButton.setFont(this.scrollButton.getFont().deriveFont(this.scrollButton.getFont().getSize() - 2.0F));
    this.scrollButton.setName("scrollButton");
    this.scrollButton.addActionListener(e -> scrollChanged());
    this.tabPlacementToolBar.add(this.scrollButton);

    
    this.borderButton.setText("border");
    this.borderButton.setFont(this.borderButton.getFont().deriveFont(this.borderButton.getFont().getSize() - 2.0F));
    this.borderButton.setName("borderButton");
    this.borderButton.addActionListener(e -> borderChanged());
    this.tabPlacementToolBar.add(this.borderButton);
    
    panel1.add(this.tabPlacementToolBar, "cell 0 0,alignx right,growx 0");


    
    this.tabPlacementTabbedPane.setName("tabPlacementTabbedPane");
    
    panel1.add(this.tabPlacementTabbedPane, "cell 0 1,width 300:300,height 100:100");

    
    tabLayoutLabel.setText("Tab layout");
    tabLayoutLabel.setFont(tabLayoutLabel.getFont().deriveFont(tabLayoutLabel.getFont().getSize() + 4.0F));
    tabLayoutLabel.setName("tabLayoutLabel");
    panel1.add(tabLayoutLabel, "cell 0 2");


    
    this.tabLayoutToolBar.setFloatable(false);
    this.tabLayoutToolBar.setBorder(BorderFactory.createEmptyBorder());
    this.tabLayoutToolBar.setName("tabLayoutToolBar");

    
    this.scrollTabLayoutButton.setText("scroll");
    this.scrollTabLayoutButton.setFont(this.scrollTabLayoutButton.getFont().deriveFont(this.scrollTabLayoutButton.getFont().getSize() - 2.0F));
    this.scrollTabLayoutButton.setSelected(true);
    this.scrollTabLayoutButton.setName("scrollTabLayoutButton");
    this.scrollTabLayoutButton.addActionListener(e -> tabLayoutChanged());
    this.tabLayoutToolBar.add(this.scrollTabLayoutButton);

    
    this.wrapTabLayoutButton.setText("wrap");
    this.wrapTabLayoutButton.setFont(this.wrapTabLayoutButton.getFont().deriveFont(this.wrapTabLayoutButton.getFont().getSize() - 2.0F));
    this.wrapTabLayoutButton.setName("wrapTabLayoutButton");
    this.wrapTabLayoutButton.addActionListener(e -> tabLayoutChanged());
    this.tabLayoutToolBar.add(this.wrapTabLayoutButton);
    
    panel1.add(this.tabLayoutToolBar, "cell 0 2,alignx right,growx 0");

    
    this.scrollLayoutNoteLabel.setText("(use mouse wheel to scroll; arrow button shows hidden tabs)");
    this.scrollLayoutNoteLabel.setEnabled(false);
    this.scrollLayoutNoteLabel.setFont(this.scrollLayoutNoteLabel.getFont().deriveFont(this.scrollLayoutNoteLabel.getFont().getSize() - 2.0F));
    this.scrollLayoutNoteLabel.setName("scrollLayoutNoteLabel");
    panel1.add(this.scrollLayoutNoteLabel, "cell 0 3");

    
    this.wrapLayoutNoteLabel.setText("(probably better to use scroll layout?)");
    this.wrapLayoutNoteLabel.setEnabled(false);
    this.wrapLayoutNoteLabel.setFont(this.wrapLayoutNoteLabel.getFont().deriveFont(this.wrapLayoutNoteLabel.getFont().getSize() - 2.0F));
    this.wrapLayoutNoteLabel.setName("wrapLayoutNoteLabel");
    panel1.add(this.wrapLayoutNoteLabel, "cell 0 3");


    
    this.scrollLayoutTabbedPane.setName("scrollLayoutTabbedPane");
    
    panel1.add(this.scrollLayoutTabbedPane, "cell 0 4");


    
    this.wrapLayoutTabbedPane.setName("wrapLayoutTabbedPane");
    
    panel1.add(this.wrapLayoutTabbedPane, "cell 0 4,width 100:100,height pref*2px");

    
    closableTabsLabel.setText("Closable tabs");
    closableTabsLabel.setFont(closableTabsLabel.getFont().deriveFont(closableTabsLabel.getFont().getSize() + 4.0F));
    closableTabsLabel.setName("closableTabsLabel");
    panel1.add(closableTabsLabel, "cell 0 5");


    
    this.closableTabsToolBar.setFloatable(false);
    this.closableTabsToolBar.setBorder(BorderFactory.createEmptyBorder());
    this.closableTabsToolBar.setName("closableTabsToolBar");

    
    this.squareCloseButton.setText("square");
    this.squareCloseButton.setFont(this.squareCloseButton.getFont().deriveFont(this.squareCloseButton.getFont().getSize() - 2.0F));
    this.squareCloseButton.setSelected(true);
    this.squareCloseButton.setName("squareCloseButton");
    this.squareCloseButton.addActionListener(e -> closeButtonStyleChanged());
    this.closableTabsToolBar.add(this.squareCloseButton);

    
    this.circleCloseButton.setText("circle");
    this.circleCloseButton.setFont(this.circleCloseButton.getFont().deriveFont(this.circleCloseButton.getFont().getSize() - 2.0F));
    this.circleCloseButton.setName("circleCloseButton");
    this.circleCloseButton.addActionListener(e -> closeButtonStyleChanged());
    this.closableTabsToolBar.add(this.circleCloseButton);

    
    this.redCrossCloseButton.setText("red cross");
    this.redCrossCloseButton.setFont(this.redCrossCloseButton.getFont().deriveFont(this.redCrossCloseButton.getFont().getSize() - 2.0F));
    this.redCrossCloseButton.setName("redCrossCloseButton");
    this.redCrossCloseButton.addActionListener(e -> closeButtonStyleChanged());
    this.closableTabsToolBar.add(this.redCrossCloseButton);
    
    panel1.add(this.closableTabsToolBar, "cell 0 5,alignx right,growx 0");


    
    this.closableTabsTabbedPane.setName("closableTabsTabbedPane");
    
    panel1.add(this.closableTabsTabbedPane, "cell 0 6");

    
    tabAreaComponentsLabel.setText("Custom tab area components");
    tabAreaComponentsLabel.setFont(tabAreaComponentsLabel.getFont().deriveFont(tabAreaComponentsLabel.getFont().getSize() + 4.0F));
    tabAreaComponentsLabel.setName("tabAreaComponentsLabel");
    panel1.add(tabAreaComponentsLabel, "cell 0 7");


    
    this.tabAreaComponentsToolBar.setFloatable(false);
    this.tabAreaComponentsToolBar.setBorder(BorderFactory.createEmptyBorder());
    this.tabAreaComponentsToolBar.setName("tabAreaComponentsToolBar");

    
    this.leadingComponentButton.setText("leading");
    this.leadingComponentButton.setFont(this.leadingComponentButton.getFont().deriveFont(this.leadingComponentButton.getFont().getSize() - 2.0F));
    this.leadingComponentButton.setSelected(true);
    this.leadingComponentButton.setName("leadingComponentButton");
    this.leadingComponentButton.addActionListener(e -> customComponentsChanged());
    this.tabAreaComponentsToolBar.add(this.leadingComponentButton);

    
    this.trailingComponentButton.setText("trailing");
    this.trailingComponentButton.setFont(this.trailingComponentButton.getFont().deriveFont(this.trailingComponentButton.getFont().getSize() - 2.0F));
    this.trailingComponentButton.setSelected(true);
    this.trailingComponentButton.setName("trailingComponentButton");
    this.trailingComponentButton.addActionListener(e -> customComponentsChanged());
    this.tabAreaComponentsToolBar.add(this.trailingComponentButton);
    
    panel1.add(this.tabAreaComponentsToolBar, "cell 0 7,alignx right,growx 0");


    
    this.customComponentsTabbedPane.setName("customComponentsTabbedPane");
    
    panel1.add(this.customComponentsTabbedPane, "cell 0 8");
    
    add(panel1, "cell 0 0");


    
    panel2.setName("panel2");
    panel2.setLayout((LayoutManager)new MigLayout("insets 0,hidemode 3", "[grow,fill]", "[]0[][fill][center][center][center]para[center]0[][center][center][center][]"));

















    
    tabIconPlacementLabel.setText("Tab icon placement");
    tabIconPlacementLabel.setFont(tabIconPlacementLabel.getFont().deriveFont(tabIconPlacementLabel.getFont().getSize() + 4.0F));
    tabIconPlacementLabel.setName("tabIconPlacementLabel");
    panel2.add(tabIconPlacementLabel, "cell 0 0");

    
    tabIconPlacementNodeLabel.setText("(top/bottom/leading/trailing)");
    tabIconPlacementNodeLabel.setEnabled(false);
    tabIconPlacementNodeLabel.setFont(tabIconPlacementNodeLabel.getFont().deriveFont(tabIconPlacementNodeLabel.getFont().getSize() - 2.0F));
    tabIconPlacementNodeLabel.setName("tabIconPlacementNodeLabel");
    panel2.add(tabIconPlacementNodeLabel, "cell 0 1");


    
    this.iconTopTabbedPane.setName("iconTopTabbedPane");
    
    panel2.add(this.iconTopTabbedPane, "cell 0 2");


    
    this.iconBottomTabbedPane.setName("iconBottomTabbedPane");
    
    panel2.add(this.iconBottomTabbedPane, "cell 0 3");


    
    this.iconLeadingTabbedPane.setName("iconLeadingTabbedPane");
    
    panel2.add(this.iconLeadingTabbedPane, "cell 0 4");


    
    this.iconTrailingTabbedPane.setName("iconTrailingTabbedPane");
    
    panel2.add(this.iconTrailingTabbedPane, "cell 0 5");

    
    tabAreaAlignmentLabel.setText("Tab area alignment");
    tabAreaAlignmentLabel.setFont(tabAreaAlignmentLabel.getFont().deriveFont(tabAreaAlignmentLabel.getFont().getSize() + 4.0F));
    tabAreaAlignmentLabel.setName("tabAreaAlignmentLabel");
    panel2.add(tabAreaAlignmentLabel, "cell 0 6");

    
    tabAreaAlignmentNoteLabel.setText("(leading/center/trailing/fill)");
    tabAreaAlignmentNoteLabel.setEnabled(false);
    tabAreaAlignmentNoteLabel.setFont(tabAreaAlignmentNoteLabel.getFont().deriveFont(tabAreaAlignmentNoteLabel.getFont().getSize() - 2.0F));
    tabAreaAlignmentNoteLabel.setName("tabAreaAlignmentNoteLabel");
    panel2.add(tabAreaAlignmentNoteLabel, "cell 0 7");


    
    this.alignLeadingTabbedPane.setName("alignLeadingTabbedPane");
    
    panel2.add(this.alignLeadingTabbedPane, "cell 0 8");


    
    this.alignCenterTabbedPane.setName("alignCenterTabbedPane");
    
    panel2.add(this.alignCenterTabbedPane, "cell 0 9");


    
    this.alignTrailingTabbedPane.setName("alignTrailingTabbedPane");
    
    panel2.add(this.alignTrailingTabbedPane, "cell 0 10");


    
    this.alignFillTabbedPane.setName("alignFillTabbedPane");
    
    panel2.add(this.alignFillTabbedPane, "cell 0 11");
    
    add(panel2, "cell 1 0,growy");


    
    panel3.setName("panel3");
    panel3.setLayout((LayoutManager)new MigLayout("insets 0,hidemode 3", "[grow,fill]", "[]0[][][][]para[][][]para[]0[]"));















    
    tabWidthModeLabel.setText("Tab width mode");
    tabWidthModeLabel.setFont(tabWidthModeLabel.getFont().deriveFont(tabWidthModeLabel.getFont().getSize() + 4.0F));
    tabWidthModeLabel.setName("tabWidthModeLabel");
    panel3.add(tabWidthModeLabel, "cell 0 0");

    
    tabWidthModeNoteLabel.setText("(preferred/equal/compact)");
    tabWidthModeNoteLabel.setFont(tabWidthModeNoteLabel.getFont().deriveFont(tabWidthModeNoteLabel.getFont().getSize() - 2.0F));
    tabWidthModeNoteLabel.setEnabled(false);
    tabWidthModeNoteLabel.setName("tabWidthModeNoteLabel");
    panel3.add(tabWidthModeNoteLabel, "cell 0 1");


    
    this.widthPreferredTabbedPane.setName("widthPreferredTabbedPane");
    
    panel3.add(this.widthPreferredTabbedPane, "cell 0 2");


    
    this.widthEqualTabbedPane.setName("widthEqualTabbedPane");
    
    panel3.add(this.widthEqualTabbedPane, "cell 0 3");


    
    this.widthCompactTabbedPane.setName("widthCompactTabbedPane");
    
    panel3.add(this.widthCompactTabbedPane, "cell 0 4");

    
    minMaxTabWidthLabel.setText("Minimum/maximum tab width");
    minMaxTabWidthLabel.setFont(minMaxTabWidthLabel.getFont().deriveFont(minMaxTabWidthLabel.getFont().getSize() + 4.0F));
    minMaxTabWidthLabel.setName("minMaxTabWidthLabel");
    panel3.add(minMaxTabWidthLabel, "cell 0 5");


    
    this.minimumTabWidthTabbedPane.setName("minimumTabWidthTabbedPane");
    
    panel3.add(this.minimumTabWidthTabbedPane, "cell 0 6");


    
    this.maximumTabWidthTabbedPane.setName("maximumTabWidthTabbedPane");
    
    panel3.add(this.maximumTabWidthTabbedPane, "cell 0 7");

    
    tabAlignmentLabel.setText("Tab title alignment");
    tabAlignmentLabel.setFont(tabAlignmentLabel.getFont().deriveFont(tabAlignmentLabel.getFont().getSize() + 4.0F));
    tabAlignmentLabel.setName("tabAlignmentLabel");
    panel3.add(tabAlignmentLabel, "cell 0 8");


    
    this.panel5.setName("panel5");
    this.panel5.setLayout((LayoutManager)new MigLayout("insets 0,hidemode 3", "[grow,fill]para[fill]", "[][][][]"));










    
    tabAlignmentNoteLabel.setText("(leading/center/trailing)");
    tabAlignmentNoteLabel.setEnabled(false);
    tabAlignmentNoteLabel.setFont(tabAlignmentNoteLabel.getFont().deriveFont(tabAlignmentNoteLabel.getFont().getSize() - 2.0F));
    tabAlignmentNoteLabel.setName("tabAlignmentNoteLabel");
    this.panel5.add(tabAlignmentNoteLabel, "cell 0 0");

    
    tabAlignmentNoteLabel2.setText("(trailing)");
    tabAlignmentNoteLabel2.setEnabled(false);
    tabAlignmentNoteLabel2.setFont(tabAlignmentNoteLabel2.getFont().deriveFont(tabAlignmentNoteLabel2.getFont().getSize() - 2.0F));
    tabAlignmentNoteLabel2.setName("tabAlignmentNoteLabel2");
    this.panel5.add(tabAlignmentNoteLabel2, "cell 1 0,alignx right,growx 0");


    
    this.tabAlignLeadingTabbedPane.setName("tabAlignLeadingTabbedPane");
    
    this.panel5.add(this.tabAlignLeadingTabbedPane, "cell 0 1");


    
    this.tabAlignVerticalTabbedPane.setTabPlacement(2);
    this.tabAlignVerticalTabbedPane.setName("tabAlignVerticalTabbedPane");
    
    this.panel5.add(this.tabAlignVerticalTabbedPane, "cell 1 1 1 3,growy");


    
    this.tabAlignCenterTabbedPane.setName("tabAlignCenterTabbedPane");
    
    this.panel5.add(this.tabAlignCenterTabbedPane, "cell 0 2");


    
    this.tabAlignTrailingTabbedPane.setName("tabAlignTrailingTabbedPane");
    
    this.panel5.add(this.tabAlignTrailingTabbedPane, "cell 0 3");
    
    panel3.add(this.panel5, "cell 0 9");
    
    add(panel3, "cell 2 0");

    
    this.separator2.setName("separator2");
    add(this.separator2, "cell 0 1 3 1");


    
    panel4.setName("panel4");
    panel4.setLayout((LayoutManager)new MigLayout("insets 0,hidemode 3", "[][fill]para[fill][fill]para", "[][center]"));










    
    this.scrollButtonsPolicyLabel.setText("Scroll buttons policy:");
    this.scrollButtonsPolicyLabel.setName("scrollButtonsPolicyLabel");
    panel4.add(this.scrollButtonsPolicyLabel, "cell 0 0");


    
    this.scrollButtonsPolicyToolBar.setFloatable(false);
    this.scrollButtonsPolicyToolBar.setBorder(BorderFactory.createEmptyBorder());
    this.scrollButtonsPolicyToolBar.setName("scrollButtonsPolicyToolBar");

    
    this.scrollAsNeededSingleButton.setText("asNeededSingle");
    this.scrollAsNeededSingleButton.setFont(this.scrollAsNeededSingleButton.getFont().deriveFont(this.scrollAsNeededSingleButton.getFont().getSize() - 2.0F));
    this.scrollAsNeededSingleButton.setSelected(true);
    this.scrollAsNeededSingleButton.setName("scrollAsNeededSingleButton");
    this.scrollAsNeededSingleButton.addActionListener(e -> scrollButtonsPolicyChanged());
    this.scrollButtonsPolicyToolBar.add(this.scrollAsNeededSingleButton);

    
    this.scrollAsNeededButton.setText("asNeeded");
    this.scrollAsNeededButton.setFont(this.scrollAsNeededButton.getFont().deriveFont(this.scrollAsNeededButton.getFont().getSize() - 2.0F));
    this.scrollAsNeededButton.setName("scrollAsNeededButton");
    this.scrollAsNeededButton.addActionListener(e -> scrollButtonsPolicyChanged());
    this.scrollButtonsPolicyToolBar.add(this.scrollAsNeededButton);

    
    this.scrollNeverButton.setText("never");
    this.scrollNeverButton.setFont(this.scrollNeverButton.getFont().deriveFont(this.scrollNeverButton.getFont().getSize() - 2.0F));
    this.scrollNeverButton.setName("scrollNeverButton");
    this.scrollNeverButton.addActionListener(e -> scrollButtonsPolicyChanged());
    this.scrollButtonsPolicyToolBar.add(this.scrollNeverButton);
    
    panel4.add(this.scrollButtonsPolicyToolBar, "cell 1 0");

    
    this.scrollButtonsPlacementLabel.setText("Scroll buttons placement:");
    this.scrollButtonsPlacementLabel.setName("scrollButtonsPlacementLabel");
    panel4.add(this.scrollButtonsPlacementLabel, "cell 2 0");


    
    this.scrollButtonsPlacementToolBar.setFloatable(false);
    this.scrollButtonsPlacementToolBar.setBorder(BorderFactory.createEmptyBorder());
    this.scrollButtonsPlacementToolBar.setName("scrollButtonsPlacementToolBar");

    
    this.scrollBothButton.setText("both");
    this.scrollBothButton.setFont(this.scrollBothButton.getFont().deriveFont(this.scrollBothButton.getFont().getSize() - 2.0F));
    this.scrollBothButton.setSelected(true);
    this.scrollBothButton.setName("scrollBothButton");
    this.scrollBothButton.addActionListener(e -> scrollButtonsPlacementChanged());
    this.scrollButtonsPlacementToolBar.add(this.scrollBothButton);

    
    this.scrollTrailingButton.setText("trailing");
    this.scrollTrailingButton.setFont(this.scrollTrailingButton.getFont().deriveFont(this.scrollTrailingButton.getFont().getSize() - 2.0F));
    this.scrollTrailingButton.setName("scrollTrailingButton");
    this.scrollTrailingButton.addActionListener(e -> scrollButtonsPlacementChanged());
    this.scrollButtonsPlacementToolBar.add(this.scrollTrailingButton);
    
    panel4.add(this.scrollButtonsPlacementToolBar, "cell 3 0");

    
    this.tabsPopupPolicyLabel.setText("Tabs popup policy:");
    this.tabsPopupPolicyLabel.setName("tabsPopupPolicyLabel");
    panel4.add(this.tabsPopupPolicyLabel, "cell 0 1");


    
    this.tabsPopupPolicyToolBar.setFloatable(false);
    this.tabsPopupPolicyToolBar.setBorder(BorderFactory.createEmptyBorder());
    this.tabsPopupPolicyToolBar.setName("tabsPopupPolicyToolBar");

    
    this.popupAsNeededButton.setText("asNeeded");
    this.popupAsNeededButton.setFont(this.popupAsNeededButton.getFont().deriveFont(this.popupAsNeededButton.getFont().getSize() - 2.0F));
    this.popupAsNeededButton.setSelected(true);
    this.popupAsNeededButton.setName("popupAsNeededButton");
    this.popupAsNeededButton.addActionListener(e -> tabsPopupPolicyChanged());
    this.tabsPopupPolicyToolBar.add(this.popupAsNeededButton);

    
    this.popupNeverButton.setText("never");
    this.popupNeverButton.setFont(this.popupNeverButton.getFont().deriveFont(this.popupNeverButton.getFont().getSize() - 2.0F));
    this.popupNeverButton.setName("popupNeverButton");
    this.popupNeverButton.addActionListener(e -> tabsPopupPolicyChanged());
    this.tabsPopupPolicyToolBar.add(this.popupNeverButton);
    
    panel4.add(this.tabsPopupPolicyToolBar, "cell 1 1");

    
    this.showTabSeparatorsCheckBox.setText("Show tab separators");
    this.showTabSeparatorsCheckBox.setName("showTabSeparatorsCheckBox");
    this.showTabSeparatorsCheckBox.addActionListener(e -> showTabSeparatorsChanged());
    panel4.add(this.showTabSeparatorsCheckBox, "cell 2 1 2 1");
    
    add(panel4, "cell 0 2 3 1");

    
    ButtonGroup tabPlacementButtonGroup = new ButtonGroup();
    tabPlacementButtonGroup.add(this.topPlacementButton);
    tabPlacementButtonGroup.add(this.bottomPlacementButton);
    tabPlacementButtonGroup.add(this.leftPlacementButton);
    tabPlacementButtonGroup.add(this.rightPlacementButton);

    
    ButtonGroup tabLayoutButtonGroup = new ButtonGroup();
    tabLayoutButtonGroup.add(this.scrollTabLayoutButton);
    tabLayoutButtonGroup.add(this.wrapTabLayoutButton);

    
    ButtonGroup closableTabsButtonGroup = new ButtonGroup();
    closableTabsButtonGroup.add(this.squareCloseButton);
    closableTabsButtonGroup.add(this.circleCloseButton);
    closableTabsButtonGroup.add(this.redCrossCloseButton);

    
    ButtonGroup scrollButtonsPolicyButtonGroup = new ButtonGroup();
    scrollButtonsPolicyButtonGroup.add(this.scrollAsNeededSingleButton);
    scrollButtonsPolicyButtonGroup.add(this.scrollAsNeededButton);
    scrollButtonsPolicyButtonGroup.add(this.scrollNeverButton);

    
    ButtonGroup scrollButtonsPlacementButtonGroup = new ButtonGroup();
    scrollButtonsPlacementButtonGroup.add(this.scrollBothButton);
    scrollButtonsPlacementButtonGroup.add(this.scrollTrailingButton);

    
    ButtonGroup tabsPopupPolicyButtonGroup = new ButtonGroup();
    tabsPopupPolicyButtonGroup.add(this.popupAsNeededButton);
    tabsPopupPolicyButtonGroup.add(this.popupNeverButton);

    
    if (FlatLafDemo.screenshotsMode) {
      Component[] components = { tabPlacementLabel, this.tabPlacementToolBar, this.tabPlacementTabbedPane, this.iconBottomTabbedPane, this.iconTrailingTabbedPane, this.alignLeadingTabbedPane, this.alignTrailingTabbedPane, this.alignFillTabbedPane, panel3, this.separator2, panel4 };





      
      for (Component c : components) {
        c.setVisible(false);
      }
      
      MigLayout layout1 = (MigLayout)panel1.getLayout();
      AC rowSpecs1 = ConstraintParser.parseRowConstraints((String)layout1.getRowConstraints());
      rowSpecs1.gap("0!", new int[] { 0, 1 });
      layout1.setRowConstraints(rowSpecs1);
      
      MigLayout layout2 = (MigLayout)panel2.getLayout();
      AC rowSpecs2 = ConstraintParser.parseRowConstraints((String)layout2.getRowConstraints());
      rowSpecs2.gap("0!", new int[] { 2, 4, 8 });
      layout2.setRowConstraints(rowSpecs2);
    } 
  }
}
