package org.fife.ui.rsyntaxtextarea;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;































class StyledTextTransferable
  implements Transferable
{
  private String html;
  private byte[] rtfBytes;
  private static final DataFlavor[] FLAVORS = new DataFlavor[] { DataFlavor.fragmentHtmlFlavor, new DataFlavor("text/rtf", "RTF"), DataFlavor.stringFlavor, DataFlavor.plainTextFlavor };












  
  StyledTextTransferable(String html, byte[] rtfBytes) {
    this.html = html;
    this.rtfBytes = rtfBytes;
  }




  
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (flavor.equals(FLAVORS[0])) {
      return this.html;
    }
    
    if (flavor.equals(FLAVORS[1])) {
      return new ByteArrayInputStream((this.rtfBytes == null) ? new byte[0] : this.rtfBytes);
    }
    
    if (flavor.equals(FLAVORS[2])) {
      return (this.rtfBytes == null) ? "" : RtfToText.getPlainText(this.rtfBytes);
    }
    
    if (flavor.equals(FLAVORS[3])) {
      String text = "";
      if (this.rtfBytes != null) {
        text = RtfToText.getPlainText(this.rtfBytes);
      }
      return new StringReader(text);
    } 
    
    throw new UnsupportedFlavorException(flavor);
  }


  
  public DataFlavor[] getTransferDataFlavors() {
    return (DataFlavor[])FLAVORS.clone();
  }


  
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    for (DataFlavor flavor1 : FLAVORS) {
      if (flavor.equals(flavor1)) {
        return true;
      }
    } 
    return false;
  }
}
