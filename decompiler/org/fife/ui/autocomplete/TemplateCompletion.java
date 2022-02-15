package org.fife.ui.autocomplete;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
































































public class TemplateCompletion
  extends AbstractCompletion
  implements ParameterizedCompletion
{
  private List<TemplatePiece> pieces;
  private String inputText;
  private String definitionString;
  private String shortDescription;
  private String summary;
  private List<ParameterizedCompletion.Parameter> params;
  
  public TemplateCompletion(CompletionProvider provider, String inputText, String definitionString, String template) {
    this(provider, inputText, definitionString, template, null, null);
  }



  
  public TemplateCompletion(CompletionProvider provider, String inputText, String definitionString, String template, String shortDescription, String summary) {
    super(provider);
    this.inputText = inputText;
    this.definitionString = definitionString;
    this.shortDescription = shortDescription;
    this.summary = summary;
    this.pieces = new ArrayList<>(3);
    this.params = new ArrayList<>(3);
    parse(template);
  }

  
  private void addTemplatePiece(TemplatePiece piece) {
    this.pieces.add(piece);
    if (piece instanceof TemplatePiece.Param && !"cursor".equals(piece.getText())) {
      String type = null;
      ParameterizedCompletion.Parameter param = new ParameterizedCompletion.Parameter(type, piece.getText());
      this.params.add(param);
    } 
  }


  
  public String getInputText() {
    return this.inputText;
  }

  
  private String getPieceText(int index, String leadingWS) {
    TemplatePiece piece = this.pieces.get(index);
    String text = piece.getText();
    if (text.indexOf('\n') > -1) {
      text = text.replaceAll("\n", "\n" + leadingWS);
    }
    return text;
  }








  
  public String getReplacementText() {
    return null;
  }


  
  public String getSummary() {
    return this.summary;
  }


  
  public String getDefinitionString() {
    return this.definitionString;
  }
  
  public String getShortDescription() {
    return this.shortDescription;
  }





  
  public boolean getShowParameterToolTip() {
    return false;
  }



  
  public ParameterizedCompletionInsertionInfo getInsertionInfo(JTextComponent tc, boolean replaceTabsWithSpaces) {
    String leadingWS;
    ParameterizedCompletionInsertionInfo info = new ParameterizedCompletionInsertionInfo();

    
    StringBuilder sb = new StringBuilder();
    int dot = tc.getCaretPosition();


    
    int minPos = dot;
    Position maxPos = null;
    int defaultEndOffs = -1;
    try {
      maxPos = tc.getDocument().createPosition(dot);
    } catch (BadLocationException ble) {
      ble.printStackTrace();
    } 
    info.setCaretRange(minPos, maxPos);
    int selStart = dot;
    int selEnd = selStart;
    
    Document doc = tc.getDocument();
    
    try {
      leadingWS = RSyntaxUtilities.getLeadingWhitespace(doc, dot);
    } catch (BadLocationException ble) {
      ble.printStackTrace();
      leadingWS = "";
    } 


    
    int start = dot;
    for (int i = 0; i < this.pieces.size(); i++) {
      TemplatePiece piece = this.pieces.get(i);
      String text = getPieceText(i, leadingWS);
      if (piece instanceof TemplatePiece.Text) {
        if (replaceTabsWithSpaces) {
          start = possiblyReplaceTabsWithSpaces(sb, text, tc, start);
        } else {
          
          sb.append(text);
          start += text.length();
        }
      
      } else if (piece instanceof TemplatePiece.Param && "cursor".equals(text)) {
        defaultEndOffs = start;
      } else {
        
        int end = start + text.length();
        sb.append(text);
        if (piece instanceof TemplatePiece.Param) {
          info.addReplacementLocation(start, end);
          if (selStart == dot) {
            selStart = start;
            selEnd = selStart + text.length();
          }
        
        } else if (piece instanceof TemplatePiece.ParamCopy) {
          info.addReplacementCopy(piece.getText(), start, end);
        } 
        start = end;
      } 
    } 


    
    if (selStart == minPos && selStart == selEnd && getParamCount() == 0 && 
      defaultEndOffs > -1) {
      selStart = selEnd = defaultEndOffs;
    }
    
    info.setInitialSelection(selStart, selEnd);
    
    if (defaultEndOffs > -1)
    {
      info.addReplacementLocation(defaultEndOffs, defaultEndOffs);
    }
    info.setDefaultEndOffs(defaultEndOffs);
    info.setTextToInsert(sb.toString());
    
    return info;
  }






  
  public ParameterizedCompletion.Parameter getParam(int index) {
    return this.params.get(index);
  }





  
  public int getParamCount() {
    return (this.params == null) ? 0 : this.params.size();
  }







  
  private boolean isParamDefined(String name) {
    for (int i = 0; i < getParamCount(); i++) {
      ParameterizedCompletion.Parameter param = getParam(i);
      if (name.equals(param.getName())) {
        return true;
      }
    } 
    return false;
  }








  
  private void parse(String template) {
    int lastOffs = 0;
    int offs;
    while ((offs = template.indexOf('$', lastOffs)) > -1 && offs < template.length() - 1) {
      int closingCurly;
      char next = template.charAt(offs + 1);
      switch (next) {
        case '$':
          addTemplatePiece(new TemplatePiece.Text(template
                .substring(lastOffs, offs + 1)));
          lastOffs = offs + 2;
        
        case '{':
          closingCurly = template.indexOf('}', offs + 2);
          if (closingCurly > -1) {
            addTemplatePiece(new TemplatePiece.Text(template
                  .substring(lastOffs, offs)));
            String varName = template.substring(offs + 2, closingCurly);
            if (!"cursor".equals(varName) && isParamDefined(varName)) {
              addTemplatePiece(new TemplatePiece.ParamCopy(varName));
            } else {
              
              addTemplatePiece(new TemplatePiece.Param(varName));
            } 
            lastOffs = closingCurly + 1;
          } 
      } 




    
    } 
    if (lastOffs < template.length()) {
      String text = template.substring(lastOffs);
      addTemplatePiece(new TemplatePiece.Text(text));
    } 
  }




  
  private int possiblyReplaceTabsWithSpaces(StringBuilder sb, String text, JTextComponent tc, int start) {
    int tab = text.indexOf('\t');
    if (tab > -1) {
      
      int startLen = sb.length();
      
      int size = 4;
      Document doc = tc.getDocument();
      if (doc != null) {
        Integer integer = (Integer)doc.getProperty("tabSize");
        if (integer != null) {
          size = integer.intValue();
        }
      } 
      
      StringBuilder sb2 = new StringBuilder();
      for (int i = 0; i < size; i++) {
        sb2.append(' ');
      }
      String tabStr = sb2.toString();
      
      int lastOffs = 0;
      do {
        sb.append(text, lastOffs, tab);
        sb.append(tabStr);
        lastOffs = tab + 1;
      } while ((tab = text.indexOf('\t', lastOffs)) > -1);
      sb.append(text.substring(lastOffs));
      
      start += sb.length() - startLen;
    }
    else {
      
      sb.append(text);
      start += text.length();
    } 
    
    return start;
  }








  
  public void setShortDescription(String shortDesc) {
    this.shortDescription = shortDesc;
  }


  
  public String toString() {
    return getDefinitionString();
  }
}
