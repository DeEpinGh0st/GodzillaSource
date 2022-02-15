package org.fife.ui.rtextarea;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;































public abstract class RecordableTextAction
  extends TextAction
{
  private boolean isRecordable;
  
  public RecordableTextAction(String text) {
    this(text, (Icon)null, (String)null, (Integer)null, (KeyStroke)null);
  }











  
  public RecordableTextAction(String text, Icon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
    super(text);
    putValue("SmallIcon", icon);
    putValue("ShortDescription", desc);
    putValue("AcceleratorKey", accelerator);
    putValue("MnemonicKey", mnemonic);
    setRecordable(true);
  }











  
  public final void actionPerformed(ActionEvent e) {
    JTextComponent textComponent = getTextComponent(e);
    if (textComponent instanceof RTextArea) {
      RTextArea textArea = (RTextArea)textComponent;
      
      if (RTextArea.isRecordingMacro() && isRecordable()) {
        int mod = e.getModifiers();



        
        String macroID = getMacroID();



        
        if (!"default-typed".equals(macroID) || ((mod & 0x8) == 0 && (mod & 0x2) == 0 && (mod & 0x4) == 0)) {



          
          String command = e.getActionCommand();
          RTextArea.addToCurrentMacro(macroID, command);
        } 
      } 
      
      actionPerformedImpl(e, textArea);
    } 
  }









  
  public abstract void actionPerformedImpl(ActionEvent paramActionEvent, RTextArea paramRTextArea);








  
  public KeyStroke getAccelerator() {
    return (KeyStroke)getValue("AcceleratorKey");
  }






  
  public String getDescription() {
    return (String)getValue("ShortDescription");
  }






  
  public Icon getIcon() {
    return (Icon)getValue("SmallIcon");
  }











  
  public abstract String getMacroID();










  
  public int getMnemonic() {
    Integer i = (Integer)getValue("MnemonicKey");
    return (i != null) ? i.intValue() : -1;
  }







  
  public String getName() {
    return (String)getValue("Name");
  }








  
  public boolean isRecordable() {
    return this.isRecordable;
  }







  
  public void setAccelerator(KeyStroke accelerator) {
    putValue("AcceleratorKey", accelerator);
  }








  
  public void setMnemonic(char mnemonic) {
    setMnemonic(Integer.valueOf(mnemonic));
  }








  
  public void setMnemonic(Integer mnemonic) {
    putValue("MnemonicKey", mnemonic);
  }







  
  public void setName(String name) {
    putValue("Name", name);
  }










  
  public void setProperties(ResourceBundle msg, String keyRoot) {
    setName(msg.getString(keyRoot + ".Name"));
    setMnemonic(msg.getString(keyRoot + ".Mnemonic").charAt(0));
    setShortDescription(msg.getString(keyRoot + ".Desc"));
  }









  
  public void setRecordable(boolean recordable) {
    this.isRecordable = recordable;
  }






  
  public void setShortDescription(String shortDesc) {
    putValue("ShortDescription", shortDesc);
  }
}
