package org.fife.ui.rtextarea;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.im.InputContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;












































public class RTATextTransferHandler
  extends TransferHandler
{
  private JTextComponent exportComp;
  private boolean shouldRemove;
  private int p0;
  private int p1;
  private boolean withinSameComponent;
  
  protected DataFlavor getImportFlavor(DataFlavor[] flavors, JTextComponent c) {
    DataFlavor refFlavor = null;
    DataFlavor stringFlavor = null;
    
    for (DataFlavor flavor : flavors) {
      
      String mime = flavor.getMimeType();
      if (mime.startsWith("text/plain")) {
        return flavor;
      }
      if (refFlavor == null && mime
        .startsWith("application/x-java-jvm-local-objectref") && flavor
        .getRepresentationClass() == String.class) {
        refFlavor = flavor;
      }
      else if (stringFlavor == null && flavor
        .equals(DataFlavor.stringFlavor)) {
        stringFlavor = flavor;
      } 
    } 

    
    if (refFlavor != null) {
      return refFlavor;
    }
    if (stringFlavor != null) {
      return stringFlavor;
    }
    
    return null;
  }







  
  protected void handleReaderImport(Reader in, JTextComponent c) throws IOException {
    char[] buff = new char[1024];
    
    boolean lastWasCR = false;
    
    StringBuilder sbuff = null;
    
    int nch;
    
    while ((nch = in.read(buff, 0, buff.length)) != -1) {
      
      if (sbuff == null) {
        sbuff = new StringBuilder(nch);
      }
      int last = 0;
      
      for (int counter = 0; counter < nch; counter++) {
        
        switch (buff[counter]) {
          case '\r':
            if (lastWasCR) {
              if (counter == 0) {
                sbuff.append('\n');
                break;
              } 
              buff[counter - 1] = '\n';
              
              break;
            } 
            lastWasCR = true;
            break;
          
          case '\n':
            if (lastWasCR) {
              if (counter > last + 1) {
                sbuff.append(buff, last, counter - last - 1);
              }

              
              lastWasCR = false;
              last = counter;
            } 
            break;
          default:
            if (lastWasCR) {
              if (counter == 0) {
                sbuff.append('\n');
              } else {
                
                buff[counter - 1] = '\n';
              } 
              lastWasCR = false;
            } 
            break;
        } 


      
      } 
      if (last < nch) {
        if (lastWasCR) {
          if (last < nch - 1) {
            sbuff.append(buff, last, nch - last - 1);
          }
          continue;
        } 
        sbuff.append(buff, last, nch - last);
      } 
    } 


    
    if (this.withinSameComponent) {
      ((RTextArea)c).beginAtomicEdit();
    }
    
    if (lastWasCR) {
      sbuff.append('\n');
    }
    c.replaceSelection((sbuff != null) ? sbuff.toString() : "");
  }














  
  public int getSourceActions(JComponent c) {
    if (((JTextComponent)c).isEditable()) {
      return 3;
    }
    
    return 1;
  }












  
  protected Transferable createTransferable(JComponent comp) {
    this.exportComp = (JTextComponent)comp;
    this.shouldRemove = true;
    this.p0 = this.exportComp.getSelectionStart();
    this.p1 = this.exportComp.getSelectionEnd();
    return (this.p0 != this.p1) ? new TextTransferable(this.exportComp, this.p0, this.p1) : null;
  }













  
  protected void exportDone(JComponent source, Transferable data, int action) {
    if (this.shouldRemove && action == 2) {
      TextTransferable t = (TextTransferable)data;
      t.removeText();
      if (this.withinSameComponent) {
        ((RTextArea)source).endAtomicEdit();
        this.withinSameComponent = false;
      } 
    } 
    this.exportComp = null;
    if (data instanceof TextTransferable) {
      ClipboardHistory.get().add(((TextTransferable)data).getPlainData());
    }
  }














  
  public boolean importData(JComponent comp, Transferable t) {
    JTextComponent c = (JTextComponent)comp;
    this.withinSameComponent = (c == this.exportComp);




    
    if (this.withinSameComponent && c.getCaretPosition() >= this.p0 && c.getCaretPosition() <= this.p1) {
      this.shouldRemove = false;
      return true;
    } 
    
    boolean imported = false;
    DataFlavor importFlavor = getImportFlavor(t.getTransferDataFlavors(), c);
    if (importFlavor != null) {
      try {
        InputContext ic = c.getInputContext();
        if (ic != null) {
          ic.endComposition();
        }
        Reader r = importFlavor.getReaderForText(t);
        handleReaderImport(r, c);
        imported = true;
      } catch (UnsupportedFlavorException|IOException e) {
        e.printStackTrace();
      } 
    }
    
    return imported;
  }












  
  public boolean canImport(JComponent comp, DataFlavor[] flavors) {
    JTextComponent c = (JTextComponent)comp;
    if (!c.isEditable() || !c.isEnabled()) {
      return false;
    }
    return (getImportFlavor(flavors, c) != null);
  }

  
  static class TextTransferable
    implements Transferable
  {
    private Position p0;
    
    private Position p1;
    
    private JTextComponent c;
    
    protected String plainData;
    
    private static DataFlavor[] stringFlavors;
    private static DataFlavor[] plainFlavors;
    
    TextTransferable(JTextComponent c, int start, int end) {
      this.c = c;
      Document doc = c.getDocument();
      try {
        this.p0 = doc.createPosition(start);
        this.p1 = doc.createPosition(end);
        this.plainData = c.getSelectedText();
      } catch (BadLocationException badLocationException) {}
    }




    
    protected String getPlainData() {
      return this.plainData;
    }












    
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
      if (isPlainFlavor(flavor)) {
        String data = getPlainData();
        data = (data == null) ? "" : data;
        if (String.class.equals(flavor.getRepresentationClass()))
          return data; 
        if (Reader.class.equals(flavor.getRepresentationClass()))
          return new StringReader(data); 
        if (InputStream.class.equals(flavor.getRepresentationClass())) {
          return new StringBufferInputStream(data);
        }
      }
      else if (isStringFlavor(flavor)) {
        String data = getPlainData();
        data = (data == null) ? "" : data;
        return data;
      } 
      throw new UnsupportedFlavorException(flavor);
    }









    
    public DataFlavor[] getTransferDataFlavors() {
      int plainCount = isPlainSupported() ? plainFlavors.length : 0;
      int stringCount = isPlainSupported() ? stringFlavors.length : 0;
      int totalCount = plainCount + stringCount;
      DataFlavor[] flavors = new DataFlavor[totalCount];

      
      int pos = 0;
      if (plainCount > 0) {
        System.arraycopy(plainFlavors, 0, flavors, pos, plainCount);
        pos += plainCount;
      } 
      if (stringCount > 0) {
        System.arraycopy(stringFlavors, 0, flavors, pos, stringCount);
      }

      
      return flavors;
    }








    
    public boolean isDataFlavorSupported(DataFlavor flavor) {
      DataFlavor[] flavors = getTransferDataFlavors();
      for (DataFlavor dataFlavor : flavors) {
        if (dataFlavor.equals(flavor)) {
          return true;
        }
      } 
      return false;
    }






    
    protected boolean isPlainFlavor(DataFlavor flavor) {
      DataFlavor[] flavors = plainFlavors;
      for (DataFlavor dataFlavor : flavors) {
        if (dataFlavor.equals(flavor)) {
          return true;
        }
      } 
      return false;
    }




    
    protected boolean isPlainSupported() {
      return (this.plainData != null);
    }






    
    protected boolean isStringFlavor(DataFlavor flavor) {
      DataFlavor[] flavors = stringFlavors;
      for (DataFlavor dataFlavor : flavors) {
        if (dataFlavor.equals(flavor)) {
          return true;
        }
      } 
      return false;
    }
    
    void removeText() {
      if (this.p0 != null && this.p1 != null && this.p0.getOffset() != this.p1.getOffset()) {
        try {
          Document doc = this.c.getDocument();
          doc.remove(this.p0.getOffset(), this.p1.getOffset() - this.p0.getOffset());
        } catch (BadLocationException badLocationException) {}
      }
    }



    
    static {
      try {
        plainFlavors = new DataFlavor[3];
        plainFlavors[0] = new DataFlavor("text/plain;class=java.lang.String");
        plainFlavors[1] = new DataFlavor("text/plain;class=java.io.Reader");
        plainFlavors[2] = new DataFlavor("text/plain;charset=unicode;class=java.io.InputStream");
        
        stringFlavors = new DataFlavor[2];
        stringFlavors[0] = new DataFlavor("application/x-java-jvm-local-objectref;class=java.lang.String");
        stringFlavors[1] = DataFlavor.stringFlavor;
      }
      catch (ClassNotFoundException cle) {
        System.err.println("Error initializing org.fife.ui.RTATextTransferHandler");
      } 
    }
  }
}
