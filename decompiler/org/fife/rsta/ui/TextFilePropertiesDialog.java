package org.fife.rsta.ui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.TextEditorPane;





















public class TextFilePropertiesDialog
  extends EscapableDialog
  implements ActionListener
{
  private static final long serialVersionUID = 1L;
  private JTextField filePathField;
  private JComboBox<String> terminatorCombo;
  private JComboBox<String> encodingCombo;
  private JButton okButton;
  private TextEditorPane textArea;
  private static final ResourceBundle MSG = ResourceBundle.getBundle("org.fife.rsta.ui.TextFilePropertiesDialog");

  
  private static final String[] LINE_TERMINATOR_LABELS = new String[] { MSG
      .getString("SysDef"), MSG
      .getString("CR"), MSG
      .getString("LF"), MSG
      .getString("CRLF") };

  
  private static final String[] LINE_TERMINATORS = new String[] {
      System.getProperty("line.separator"), "\r", "\n", "\r\n"
    };






  
  public TextFilePropertiesDialog(Dialog parent, TextEditorPane textArea) {
    super(parent);
    init(textArea);
  }







  
  public TextFilePropertiesDialog(Frame parent, TextEditorPane textArea) {
    super(parent);
    init(textArea);
  }








  
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    
    if ("TerminatorComboBox".equals(command)) {
      this.okButton.setEnabled(true);
    
    }
    else if ("encodingCombo".equals(command)) {
      this.okButton.setEnabled(true);
    
    }
    else if ("OKButton".equals(command)) {
      String terminator = getSelectedLineTerminator();
      if (terminator != null) {
        String old = (String)this.textArea.getLineSeparator();
        if (!terminator.equals(old)) {
          this.textArea.setLineSeparator(terminator);
        }
      } 
      String encoding = (String)this.encodingCombo.getSelectedItem();
      if (encoding != null) {
        this.textArea.setEncoding(encoding);
      }
      setVisible(false);
    
    }
    else if ("CancelButton".equals(command)) {
      escapePressed();
    } 
  }



  
  private int calculateWordCount(TextEditorPane textArea) {
    int wordCount = 0;
    RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
    
    BreakIterator bi = BreakIterator.getWordInstance();
    bi.setText(new DocumentCharIterator(textArea.getDocument())); int nextBoundary;
    for (nextBoundary = bi.first(); nextBoundary != -1; 
      nextBoundary = bi.next()) {

      
      try {
        char ch = doc.charAt(nextBoundary);
        if (Character.isLetterOrDigit(ch)) {
          wordCount++;
        }
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    } 
    
    return wordCount;
  }










  
  protected Container createButtonFooter(JButton ok, JButton cancel) {
    JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
    buttonPanel.add(ok);
    buttonPanel.add(cancel);
    
    JPanel panel = new JPanel(new BorderLayout());
    ComponentOrientation o = getComponentOrientation();
    int padding = 8;
    int left = o.isLeftToRight() ? 0 : 8;
    int right = o.isLeftToRight() ? 8 : 0;
    panel.setBorder(BorderFactory.createEmptyBorder(10, left, 0, right));
    panel.add(buttonPanel, "After");
    return panel;
  }








  
  protected String createTitle(String fileName) {
    return MessageFormat.format(MSG
        .getString("Title"), new Object[] { this.textArea.getFileName() });
  }









  
  private static String getFileSizeStringFor(File file) {
    int count = 0;
    double tempSize = file.length();
    double prevSize = tempSize;


    
    while (count < 4 && (tempSize = prevSize / 1024.0D) >= 1.0D) {
      prevSize = tempSize;
      count++;
    } 

    
    switch (count)
    { case 0:
        suffix = "bytes";















        
        fileSizeFormat = NumberFormat.getNumberInstance();
        fileSizeFormat.setGroupingUsed(true);
        fileSizeFormat.setMinimumFractionDigits(0);
        fileSizeFormat.setMaximumFractionDigits(1);
        return fileSizeFormat.format(prevSize) + " " + suffix;case 1: suffix = "KB"; fileSizeFormat = NumberFormat.getNumberInstance(); fileSizeFormat.setGroupingUsed(true); fileSizeFormat.setMinimumFractionDigits(0); fileSizeFormat.setMaximumFractionDigits(1); return fileSizeFormat.format(prevSize) + " " + suffix;case 2: suffix = "MB"; fileSizeFormat = NumberFormat.getNumberInstance(); fileSizeFormat.setGroupingUsed(true); fileSizeFormat.setMinimumFractionDigits(0); fileSizeFormat.setMaximumFractionDigits(1); return fileSizeFormat.format(prevSize) + " " + suffix;case 3: suffix = "GB"; fileSizeFormat = NumberFormat.getNumberInstance(); fileSizeFormat.setGroupingUsed(true); fileSizeFormat.setMinimumFractionDigits(0); fileSizeFormat.setMaximumFractionDigits(1); return fileSizeFormat.format(prevSize) + " " + suffix; }  String suffix = "TB"; NumberFormat fileSizeFormat = NumberFormat.getNumberInstance(); fileSizeFormat.setGroupingUsed(true); fileSizeFormat.setMinimumFractionDigits(0); fileSizeFormat.setMaximumFractionDigits(1); return fileSizeFormat.format(prevSize) + " " + suffix;
  }


  
  private String getSelectedLineTerminator() {
    return LINE_TERMINATORS[this.terminatorCombo.getSelectedIndex()];
  }

  
  private void init(TextEditorPane textArea) {
    String modifiedString;
    this.textArea = textArea;
    setTitle(createTitle(textArea.getFileName()));

    
    ComponentOrientation o = ComponentOrientation.getOrientation(getLocale());
    
    JPanel contentPane = new ResizableFrameContentPane(new BorderLayout());
    contentPane.setBorder(UIUtil.getEmpty5Border());

    
    JPanel content2 = new JPanel();
    content2.setLayout(new SpringLayout());
    contentPane.add(content2, "North");
    
    this.filePathField = new JTextField(40);
    this.filePathField.setText(textArea.getFileFullPath());
    this.filePathField.setEditable(false);
    JLabel filePathLabel = UIUtil.newLabel(MSG, "Path", this.filePathField);
    
    JLabel linesLabel = new JLabel(MSG.getString("Lines"));
    
    JLabel linesCountLabel = new JLabel(Integer.toString(textArea.getLineCount()));
    
    JLabel charsLabel = new JLabel(MSG.getString("Characters"));
    
    JLabel charsCountLabel = new JLabel(Integer.toString(textArea.getDocument().getLength()));
    
    JLabel wordsLabel = new JLabel(MSG.getString("Words"));
    
    JLabel wordsCountLabel = new JLabel(Integer.toString(calculateWordCount(textArea)));
    
    this.terminatorCombo = new JComboBox<>(LINE_TERMINATOR_LABELS);
    if (textArea.isReadOnly()) {
      this.terminatorCombo.setEnabled(false);
    }
    UIUtil.fixComboOrientation(this.terminatorCombo);
    setSelectedLineTerminator((String)textArea.getLineSeparator());
    this.terminatorCombo.setActionCommand("TerminatorComboBox");
    this.terminatorCombo.addActionListener(this);
    JLabel terminatorLabel = UIUtil.newLabel(MSG, "LineTerminator", this.terminatorCombo);

    
    this.encodingCombo = new JComboBox<>();
    if (textArea.isReadOnly()) {
      this.encodingCombo.setEnabled(false);
    }
    UIUtil.fixComboOrientation(this.encodingCombo);

    
    Map<String, Charset> availableCharsets = Charset.availableCharsets();
    Set<String> charsetNames = availableCharsets.keySet();
    for (String charsetName : charsetNames) {
      this.encodingCombo.addItem(charsetName);
    }
    setEncoding(textArea.getEncoding());
    this.encodingCombo.setActionCommand("encodingCombo");
    this.encodingCombo.addActionListener(this);
    JLabel encodingLabel = UIUtil.newLabel(MSG, "Encoding", this.encodingCombo);
    
    JLabel sizeLabel = new JLabel(MSG.getString("FileSize"));
    File file = new File(textArea.getFileFullPath());
    String size = "";
    if (file.exists() && !file.isDirectory()) {
      size = getFileSizeStringFor(file);
    }
    JLabel sizeLabel2 = new JLabel(size);
    
    long temp = textArea.getLastSaveOrLoadTime();
    
    if (temp <= 0L) {
      modifiedString = "";
    } else {
      
      Date modifiedDate = new Date(temp);
      SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a  EEE, MMM d, yyyy");
      
      modifiedString = sdf.format(modifiedDate);
    } 
    JLabel modifiedLabel = new JLabel(MSG.getString("LastModified"));
    JLabel modified = new JLabel(modifiedString);
    
    if (o.isLeftToRight()) {
      content2.add(filePathLabel); content2.add(this.filePathField);
      content2.add(linesLabel); content2.add(linesCountLabel);
      content2.add(charsLabel); content2.add(charsCountLabel);
      content2.add(wordsLabel); content2.add(wordsCountLabel);
      content2.add(terminatorLabel); content2.add(this.terminatorCombo);
      content2.add(encodingLabel); content2.add(this.encodingCombo);
      content2.add(sizeLabel); content2.add(sizeLabel2);
      content2.add(modifiedLabel); content2.add(modified);
    } else {
      
      content2.add(this.filePathField); content2.add(filePathLabel);
      content2.add(linesCountLabel); content2.add(linesLabel);
      content2.add(charsCountLabel); content2.add(charsLabel);
      content2.add(wordsCountLabel); content2.add(wordsLabel);
      content2.add(this.terminatorCombo); content2.add(terminatorLabel);
      content2.add(this.encodingCombo); content2.add(encodingLabel);
      content2.add(sizeLabel2); content2.add(sizeLabel);
      content2.add(modified); content2.add(modifiedLabel);
    } 
    
    UIUtil.makeSpringCompactGrid(content2, 8, 2, 0, 0, 5, 5);

    
    this.okButton = UIUtil.newButton(MSG, "OK");
    this.okButton.setActionCommand("OKButton");
    this.okButton.addActionListener(this);
    this.okButton.setEnabled(false);
    JButton cancelButton = UIUtil.newButton(MSG, "Cancel");
    cancelButton.setActionCommand("CancelButton");
    cancelButton.addActionListener(this);
    Container buttons = createButtonFooter(this.okButton, cancelButton);
    contentPane.add(buttons, "South");
    
    setContentPane(contentPane);
    setModal(true);
    applyComponentOrientation(o);
    pack();
    setLocationRelativeTo(getParent());
  }









  
  private void setEncoding(String encoding) {
    Charset cs1 = Charset.forName(encoding);
    
    int count = this.encodingCombo.getItemCount(); int i;
    for (i = 0; i < count; i++) {
      String item = this.encodingCombo.getItemAt(i);
      Charset cs2 = Charset.forName(item);
      if (cs1.equals(cs2)) {
        this.encodingCombo.setSelectedIndex(i);
        
        return;
      } 
    } 
    
    cs1 = StandardCharsets.US_ASCII;
    for (i = 0; i < count; i++) {
      String item = this.encodingCombo.getItemAt(i);
      Charset cs2 = Charset.forName(item);
      if (cs1.equals(cs2)) {
        this.encodingCombo.setSelectedIndex(i);
        return;
      } 
    } 
  }


  
  private void setSelectedLineTerminator(String terminator) {
    for (int i = 0; i < LINE_TERMINATORS.length; i++) {
      if (LINE_TERMINATORS[i].equals(terminator)) {
        this.terminatorCombo.setSelectedIndex(i);
        break;
      } 
    } 
  }








  
  public void setVisible(boolean visible) {
    if (visible) {
      SwingUtilities.invokeLater(() -> {
            this.filePathField.requestFocusInWindow();
            this.filePathField.selectAll();
          });
    }
    super.setVisible(visible);
  }

  
  private static class DocumentCharIterator
    implements CharacterIterator
  {
    private Document doc;
    
    private int index;
    
    private Segment s;
    
    DocumentCharIterator(Document doc) {
      this.doc = doc;
      this.index = 0;
      this.s = new Segment();
    }

    
    public Object clone() {
      try {
        return super.clone();
      } catch (CloneNotSupportedException cnse) {
        throw new InternalError("Clone not supported???");
      } 
    }

    
    public char current() {
      if (this.index >= getEndIndex()) {
        return Character.MAX_VALUE;
      }
      try {
        this.doc.getText(this.index, 1, this.s);
        return this.s.first();
      } catch (BadLocationException ble) {
        return Character.MAX_VALUE;
      } 
    }

    
    public char first() {
      this.index = getBeginIndex();
      return current();
    }

    
    public int getBeginIndex() {
      return 0;
    }

    
    public int getEndIndex() {
      return this.doc.getLength();
    }

    
    public int getIndex() {
      return this.index;
    }

    
    public char last() {
      this.index = Math.max(0, getEndIndex() - 1);
      return current();
    }

    
    public char next() {
      this.index = Math.min(this.index + 1, getEndIndex());
      return current();
    }

    
    public char previous() {
      this.index = Math.max(this.index - 1, getBeginIndex());
      return current();
    }

    
    public char setIndex(int pos) {
      if (pos < getBeginIndex() || pos > getEndIndex()) {
        throw new IllegalArgumentException("Illegal index: " + this.index);
      }
      this.index = pos;
      return current();
    }
  }
}
