package com.formdev.flatlaf.demo;

import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import com.formdev.flatlaf.ui.FlatPopupMenuBorder;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import net.miginfocom.swing.MigLayout;






class HintManager
{
  private static final List<HintPanel> hintPanels = new ArrayList<>();

  
  static void showHint(Hint hint) {
    if (DemoPrefs.getState().getBoolean(hint.prefsKey, false)) {
      if (hint.nextHint != null) {
        showHint(hint.nextHint);
      }
      return;
    } 
    HintPanel hintPanel = new HintPanel(hint);
    hintPanel.showHint();
    
    hintPanels.add(hintPanel);
  }
  
  static void hideAllHints() {
    HintPanel[] hintPanels2 = hintPanels.<HintPanel>toArray(new HintPanel[hintPanels.size()]);
    for (HintPanel hintPanel : hintPanels2) {
      hintPanel.hideHint();
    }
  }

  
  static class Hint
  {
    private final String message;
    private final Component owner;
    private final int position;
    private final String prefsKey;
    private final Hint nextHint;
    
    Hint(String message, Component owner, int position, String prefsKey, Hint nextHint) {
      this.message = message;
      this.owner = owner;
      this.position = position;
      this.prefsKey = prefsKey;
      this.nextHint = nextHint;
    }
  }

  
  private static class HintPanel
    extends JPanel
  {
    private final HintManager.Hint hint;
    private JPanel popup;
    private JLabel hintLabel;
    private JButton gotItButton;
    
    private HintPanel(HintManager.Hint hint) {
      this.hint = hint;
      
      initComponents();
      
      this.hintLabel.setText("<html>" + hint.message + "</html>");


      
      addMouseListener(new MouseAdapter() {  }
        );
    }
    
    public void updateUI() {
      super.updateUI();
      
      setBackground(UIManager.getColor("HintPanel.backgroundColor"));
      setBorder((Border)new FlatPopupMenuBorder());
    }
    
    void showHint() {
      JRootPane rootPane = SwingUtilities.getRootPane(this.hint.owner);
      if (rootPane == null) {
        return;
      }
      JLayeredPane layeredPane = rootPane.getLayeredPane();

      
      this.popup = new JPanel(new BorderLayout())
        {
          public void updateUI() {
            super.updateUI();
            
            setBorder((Border)new FlatDropShadowBorder(
                  UIManager.getColor("Popup.dropShadowColor"), 
                  UIManager.getInsets("Popup.dropShadowInsets"), 
                  FlatUIUtils.getUIFloat("Popup.dropShadowOpacity", 0.5F)));


            
            EventQueue.invokeLater(() -> {
                  validate();
                  setSize(getPreferredSize());
                });
          }
        };
      this.popup.setOpaque(false);
      this.popup.add(this);

      
      Point pt = SwingUtilities.convertPoint(this.hint.owner, 0, 0, layeredPane);
      int x = pt.x;
      int y = pt.y;
      Dimension size = this.popup.getPreferredSize();
      int gap = UIScale.scale(6);
      
      switch (this.hint.position) {
        case 2:
          x -= size.width + gap;
          break;
        
        case 1:
          y -= size.height + gap;
          break;
        
        case 4:
          x += this.hint.owner.getWidth() + gap;
          break;
        
        case 3:
          y += this.hint.owner.getHeight() + gap;
          break;
      } 

      
      this.popup.setBounds(x, y, size.width, size.height);
      layeredPane.add(this.popup, JLayeredPane.POPUP_LAYER);
    }
    
    void hideHint() {
      if (this.popup != null) {
        Container parent = this.popup.getParent();
        if (parent != null) {
          parent.remove(this.popup);
          parent.repaint(this.popup.getX(), this.popup.getY(), this.popup.getWidth(), this.popup.getHeight());
        } 
      } 
      
      HintManager.hintPanels.remove(this);
    }

    
    private void gotIt() {
      hideHint();

      
      DemoPrefs.getState().putBoolean(this.hint.prefsKey, true);

      
      if (this.hint.nextHint != null) {
        HintManager.showHint(this.hint.nextHint);
      }
    }
    
    private void initComponents() {
      this.hintLabel = new JLabel();
      this.gotItButton = new JButton();

      
      setLayout((LayoutManager)new MigLayout("insets dialog,hidemode 3", "[::200,fill]", "[]para[]"));







      
      this.hintLabel.setText("hint");
      add(this.hintLabel, "cell 0 0");

      
      this.gotItButton.setText("Got it!");
      this.gotItButton.setFocusable(false);
      this.gotItButton.addActionListener(e -> gotIt());
      add(this.gotItButton, "cell 0 1,alignx right,growx 0");
    }
  }
}
