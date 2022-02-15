package org.apache.log4j.chainsaw;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;





















class ControlPanel
  extends JPanel
{
  private static final Logger LOG = Logger.getLogger(ControlPanel.class);






  
  ControlPanel(final MyTableModel aModel) {
    setBorder(BorderFactory.createTitledBorder("Controls: "));
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    setLayout(gridbag);

    
    c.ipadx = 5;
    c.ipady = 5;

    
    c.gridx = 0;
    c.anchor = 13;
    
    c.gridy = 0;
    JLabel label = new JLabel("Filter Level:");
    gridbag.setConstraints(label, c);
    add(label);
    
    c.gridy++;
    label = new JLabel("Filter Thread:");
    gridbag.setConstraints(label, c);
    add(label);
    
    c.gridy++;
    label = new JLabel("Filter Logger:");
    gridbag.setConstraints(label, c);
    add(label);
    
    c.gridy++;
    label = new JLabel("Filter NDC:");
    gridbag.setConstraints(label, c);
    add(label);
    
    c.gridy++;
    label = new JLabel("Filter Message:");
    gridbag.setConstraints(label, c);
    add(label);

    
    c.weightx = 1.0D;
    
    c.gridx = 1;
    c.anchor = 17;
    
    c.gridy = 0;
    Level[] allPriorities = { Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE };





    
    final JComboBox priorities = new JComboBox(allPriorities);
    Level lowest = allPriorities[allPriorities.length - 1];
    priorities.setSelectedItem(lowest);
    aModel.setPriorityFilter((Priority)lowest);
    gridbag.setConstraints(priorities, c);
    add(priorities);
    priorities.setEditable(false);
    priorities.addActionListener(new ActionListener() { private final MyTableModel val$aModel;
          public void actionPerformed(ActionEvent aEvent) {
            aModel.setPriorityFilter((Priority)priorities.getSelectedItem());
          }
          
          private final JComboBox val$priorities;
          private final ControlPanel this$0; }
      );
    c.fill = 2;
    c.gridy++;
    final JTextField threadField = new JTextField("");
    threadField.getDocument().addDocumentListener(new DocumentListener() { private final MyTableModel val$aModel;
          public void insertUpdate(DocumentEvent aEvent) {
            aModel.setThreadFilter(threadField.getText());
          } private final JTextField val$threadField; private final ControlPanel this$0;
          public void removeUpdate(DocumentEvent aEvente) {
            aModel.setThreadFilter(threadField.getText());
          }
          public void changedUpdate(DocumentEvent aEvent) {
            aModel.setThreadFilter(threadField.getText());
          } }
      );
    gridbag.setConstraints(threadField, c);
    add(threadField);
    
    c.gridy++;
    final JTextField catField = new JTextField("");
    catField.getDocument().addDocumentListener(new DocumentListener() { private final MyTableModel val$aModel; private final JTextField val$catField; private final ControlPanel this$0;
          public void insertUpdate(DocumentEvent aEvent) {
            aModel.setCategoryFilter(catField.getText());
          }
          public void removeUpdate(DocumentEvent aEvent) {
            aModel.setCategoryFilter(catField.getText());
          }
          public void changedUpdate(DocumentEvent aEvent) {
            aModel.setCategoryFilter(catField.getText());
          } }
      );
    gridbag.setConstraints(catField, c);
    add(catField);
    
    c.gridy++;
    final JTextField ndcField = new JTextField("");
    ndcField.getDocument().addDocumentListener(new DocumentListener() { private final MyTableModel val$aModel; private final JTextField val$ndcField; private final ControlPanel this$0;
          public void insertUpdate(DocumentEvent aEvent) {
            aModel.setNDCFilter(ndcField.getText());
          }
          public void removeUpdate(DocumentEvent aEvent) {
            aModel.setNDCFilter(ndcField.getText());
          }
          public void changedUpdate(DocumentEvent aEvent) {
            aModel.setNDCFilter(ndcField.getText());
          } }
      );
    gridbag.setConstraints(ndcField, c);
    add(ndcField);
    
    c.gridy++;
    final JTextField msgField = new JTextField("");
    msgField.getDocument().addDocumentListener(new DocumentListener() { private final MyTableModel val$aModel; private final JTextField val$msgField; private final ControlPanel this$0;
          public void insertUpdate(DocumentEvent aEvent) {
            aModel.setMessageFilter(msgField.getText());
          }
          public void removeUpdate(DocumentEvent aEvent) {
            aModel.setMessageFilter(msgField.getText());
          }
          public void changedUpdate(DocumentEvent aEvent) {
            aModel.setMessageFilter(msgField.getText());
          } }
      );

    
    gridbag.setConstraints(msgField, c);
    add(msgField);

    
    c.weightx = 0.0D;
    c.fill = 2;
    c.anchor = 13;
    c.gridx = 2;
    
    c.gridy = 0;
    JButton exitButton = new JButton("Exit");
    exitButton.setMnemonic('x');
    exitButton.addActionListener(ExitAction.INSTANCE);
    gridbag.setConstraints(exitButton, c);
    add(exitButton);
    
    c.gridy++;
    JButton clearButton = new JButton("Clear");
    clearButton.setMnemonic('c');
    clearButton.addActionListener(new ActionListener() { private final MyTableModel val$aModel; private final ControlPanel this$0;
          public void actionPerformed(ActionEvent aEvent) {
            aModel.clear();
          } }
      );
    gridbag.setConstraints(clearButton, c);
    add(clearButton);
    
    c.gridy++;
    final JButton toggleButton = new JButton("Pause");
    toggleButton.setMnemonic('p');
    toggleButton.addActionListener(new ActionListener() { private final MyTableModel val$aModel; private final JButton val$toggleButton; private final ControlPanel this$0;
          public void actionPerformed(ActionEvent aEvent) {
            aModel.toggle();
            toggleButton.setText(aModel.isPaused() ? "Resume" : "Pause");
          } }
      );
    
    gridbag.setConstraints(toggleButton, c);
    add(toggleButton);
  }
}
