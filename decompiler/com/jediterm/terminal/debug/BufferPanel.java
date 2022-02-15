package com.jediterm.terminal.debug;

import com.jediterm.terminal.ui.TerminalSession;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

public class BufferPanel extends JPanel {
  public BufferPanel(final TerminalSession terminal) {
    super(new BorderLayout());
    final JTextArea area = new JTextArea();
    area.setEditable(false);
    
    add(area, "North");
    
    DebugBufferType[] choices = DebugBufferType.values();
    
    final JComboBox<DebugBufferType> chooser = new JComboBox<>(choices);
    add(chooser, "North");
    
    area.setFont(Font.decode("Monospaced-14"));
    add(new JScrollPane(area), "Center");
    class Updater
      implements ActionListener, ItemListener {
      private String myLastUpdate = "";
      
      void update() {
        DebugBufferType type = (DebugBufferType)chooser.getSelectedItem();
        String text = terminal.getBufferText(type);
        if (!text.equals(this.myLastUpdate)) {
          area.setText(text);
          this.myLastUpdate = text;
        } 
      }
      
      public void actionPerformed(ActionEvent e) {
        update();
      }
      
      public void itemStateChanged(ItemEvent e) {
        update();
      }
    };
    Updater up = new Updater();
    chooser.addItemListener(up);
    Timer timer = new Timer(1000, up);
    timer.setRepeats(true);
    timer.start();
  }
}
