package org.fife.print;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;
import javax.swing.text.TabExpander;
import javax.swing.text.Utilities;






















public abstract class RPrintUtilities
{
  private static int currentDocLineNumber;
  private static int numDocLines;
  private static Element rootElement;
  private static final char[] BREAK_CHARS = new char[] { ' ', '\t', ',', '.', ';', '?', '!' };







  
  private static int xOffset;







  
  private static int tabSizeInSpaces;






  
  private static FontMetrics fm;







  
  private static int getLineBreakPoint(String line, int maxCharsPerLine) {
    int breakPoint = -1;
    for (char breakChar : BREAK_CHARS) {
      int breakCharPos = line.lastIndexOf(breakChar, maxCharsPerLine - 1);
      if (breakCharPos > breakPoint) {
        breakPoint = breakCharPos;
      }
    } 
    
    return (breakPoint == -1) ? (maxCharsPerLine - 1) : breakPoint;
  }


















  
  public static int printDocumentMonospaced(Graphics g, Document doc, int fontSize, int pageIndex, PageFormat pageFormat, int tabSize) {
    g.setColor(Color.BLACK);
    g.setFont(new Font("Monospaced", 0, fontSize));

    
    tabSizeInSpaces = tabSize;
    fm = g.getFontMetrics();




    
    int fontWidth = fm.charWidth('w');
    int fontHeight = fm.getHeight();
    
    int maxCharsPerLine = (int)pageFormat.getImageableWidth() / fontWidth;
    int maxLinesPerPage = (int)pageFormat.getImageableHeight() / fontHeight;
    
    int startingLineNumber = maxLinesPerPage * pageIndex;



    
    xOffset = (int)pageFormat.getImageableX();
    int y = (int)pageFormat.getImageableY() + fm.getAscent() + 1;


    
    int numPrintedLines = 0;

    
    currentDocLineNumber = 0;
    rootElement = doc.getDefaultRootElement();
    numDocLines = rootElement.getElementCount();
    while (currentDocLineNumber < numDocLines) {


      
      Element currentLine = rootElement.getElement(currentDocLineNumber);
      int startOffs = currentLine.getStartOffset();
      try {
        curLineString = doc.getText(startOffs, currentLine.getEndOffset() - startOffs);
      } catch (BadLocationException ble) {
        ble.printStackTrace();
        return 1;
      } 

      
      String curLineString = curLineString.replaceAll("\n", "");

      
      if (tabSizeInSpaces == 0) {
        curLineString = curLineString.replaceAll("\t", "");
      } else {
        
        int tabIndex = curLineString.indexOf('\t');
        while (tabIndex > -1) {
          int spacesNeeded = tabSizeInSpaces - tabIndex % tabSizeInSpaces;
          StringBuilder stringBuilder = new StringBuilder();
          for (int i = 0; i < spacesNeeded; i++) {
            stringBuilder.append(" ");
          }
          
          curLineString = curLineString.replaceFirst("\t", stringBuilder.toString());
          tabIndex = curLineString.indexOf('\t');
        } 
      } 


      
      while (curLineString.length() > maxCharsPerLine) {
        
        numPrintedLines++;
        if (numPrintedLines > startingLineNumber) {
          g.drawString(curLineString.substring(0, maxCharsPerLine), xOffset, y);
          y += fontHeight;
          if (numPrintedLines == startingLineNumber + maxLinesPerPage) {
            return 0;
          }
        } 
        
        curLineString = curLineString.substring(maxCharsPerLine, curLineString.length());
      } 

      
      currentDocLineNumber++;
      
      numPrintedLines++;
      if (numPrintedLines > startingLineNumber) {
        g.drawString(curLineString, xOffset, y);
        y += fontHeight;
        if (numPrintedLines == startingLineNumber + maxLinesPerPage) {
          return 0;
        }
      } 
    } 



    
    if (numPrintedLines > startingLineNumber) {
      return 0;
    }
    return 1;
  }



















  
  public static int printDocumentMonospacedWordWrap(Graphics g, Document doc, int fontSize, int pageIndex, PageFormat pageFormat, int tabSize) {
    g.setColor(Color.BLACK);
    g.setFont(new Font("Monospaced", 0, fontSize));

    
    tabSizeInSpaces = tabSize;
    fm = g.getFontMetrics();




    
    int fontWidth = fm.charWidth('w');
    int fontHeight = fm.getHeight();
    
    int maxCharsPerLine = (int)pageFormat.getImageableWidth() / fontWidth;
    int maxLinesPerPage = (int)pageFormat.getImageableHeight() / fontHeight;
    
    int startingLineNumber = maxLinesPerPage * pageIndex;



    
    xOffset = (int)pageFormat.getImageableX();
    int y = (int)pageFormat.getImageableY() + fm.getAscent() + 1;


    
    int numPrintedLines = 0;

    
    currentDocLineNumber = 0;
    rootElement = doc.getDefaultRootElement();
    numDocLines = rootElement.getElementCount();
    while (currentDocLineNumber < numDocLines) {


      
      Element currentLine = rootElement.getElement(currentDocLineNumber);
      int startOffs = currentLine.getStartOffset();
      try {
        curLineString = doc.getText(startOffs, currentLine.getEndOffset() - startOffs);
      } catch (BadLocationException ble) {
        ble.printStackTrace();
        return 1;
      } 

      
      String curLineString = curLineString.replaceAll("\n", "");

      
      if (tabSizeInSpaces == 0) {
        curLineString = curLineString.replaceAll("\t", "");
      } else {
        
        int tabIndex = curLineString.indexOf('\t');
        while (tabIndex > -1) {
          int spacesNeeded = tabSizeInSpaces - tabIndex % tabSizeInSpaces;
          StringBuilder stringBuilder = new StringBuilder();
          for (int i = 0; i < spacesNeeded; i++) {
            stringBuilder.append(" ");
          }
          
          curLineString = curLineString.replaceFirst("\t", stringBuilder.toString());
          tabIndex = curLineString.indexOf('\t');
        } 
      } 


      
      while (curLineString.length() > maxCharsPerLine) {
        
        int breakPoint = getLineBreakPoint(curLineString, maxCharsPerLine) + 1;
        
        numPrintedLines++;
        if (numPrintedLines > startingLineNumber) {
          g.drawString(curLineString.substring(0, breakPoint), xOffset, y);
          y += fontHeight;
          if (numPrintedLines == startingLineNumber + maxLinesPerPage) {
            return 0;
          }
        } 
        
        curLineString = curLineString.substring(breakPoint, curLineString.length());
      } 

      
      currentDocLineNumber++;
      
      numPrintedLines++;
      if (numPrintedLines > startingLineNumber) {
        g.drawString(curLineString, xOffset, y);
        y += fontHeight;
        if (numPrintedLines == startingLineNumber + maxLinesPerPage) {
          return 0;
        }
      } 
    } 



    
    if (numPrintedLines > startingLineNumber) {
      return 0;
    }
    return 1;
  }






















  
  public static int printDocumentWordWrap(Graphics g, JTextComponent textComponent, Font font, int pageIndex, PageFormat pageFormat, int tabSize) {
    g.setColor(Color.BLACK);
    g.setFont((font != null) ? font : textComponent.getFont());

    
    tabSizeInSpaces = tabSize;
    fm = g.getFontMetrics();
    int fontHeight = fm.getHeight();
    
    int lineLengthInPixels = (int)pageFormat.getImageableWidth();
    int maxLinesPerPage = (int)pageFormat.getImageableHeight() / fontHeight;
    
    int startingLineNumber = maxLinesPerPage * pageIndex;

    
    RPrintTabExpander tabExpander = new RPrintTabExpander();



    
    xOffset = (int)pageFormat.getImageableX();
    int y = (int)pageFormat.getImageableY() + fm.getAscent() + 1;


    
    int numPrintedLines = 0;

    
    Document doc = textComponent.getDocument();
    rootElement = doc.getDefaultRootElement();
    numDocLines = rootElement.getElementCount();
    currentDocLineNumber = 0;
    int startingOffset = 0;
    while (currentDocLineNumber < numDocLines) {
      
      Segment currentLineSeg = new Segment();

      
      Element currentLine = rootElement.getElement(currentDocLineNumber);
      int currentLineStart = currentLine.getStartOffset();
      int currentLineEnd = currentLine.getEndOffset();



      
      try {
        doc.getText(currentLineStart + startingOffset, currentLineEnd - currentLineStart + startingOffset, currentLineSeg);
      }
      catch (BadLocationException ble) {
        System.err.println("BadLocationException in print (where there shouldn't be one!): " + ble);
        return 1;
      } 

      
      currentLineSeg = removeEndingWhitespace(currentLineSeg);

      
      int currentLineLengthInPixels = Utilities.getTabbedTextWidth(currentLineSeg, fm, 0, tabExpander, 0);


      
      if (currentLineLengthInPixels <= lineLengthInPixels) {
        currentDocLineNumber++;
        startingOffset = 0;

      
      }
      else {

        
        int currentPos = -1;
        while (currentLineLengthInPixels > lineLengthInPixels) {



          
          currentLineSeg = removeEndingWhitespace(currentLineSeg);

          
          currentPos = -1;
          String currentLineString = currentLineSeg.toString();
          for (char breakChar : BREAK_CHARS) {
            
            int pos = currentLineString.lastIndexOf(breakChar) + 1;

            
            if (pos > 0 && pos > currentPos && pos != currentLineString.length()) {
              currentPos = pos;
            }
          } 




          
          if (currentPos == -1) {


            
            currentPos = 0;
            while (true) {
              currentPos++;
              try {
                doc.getText(currentLineStart + startingOffset, currentPos, currentLineSeg);
              } catch (BadLocationException ble) {
                System.err.println(ble);
                return 1;
              } 
              
              currentLineLengthInPixels = Utilities.getTabbedTextWidth(currentLineSeg, fm, 0, tabExpander, 0);
              if (currentLineLengthInPixels > lineLengthInPixels) {
                currentPos--; break;
              } 
            } 
          } 
          try {
            doc.getText(currentLineStart + startingOffset, currentPos, currentLineSeg);
          } catch (BadLocationException ble) {
            System.err.println("BadLocationException in print (a):");
            System.err.println("==> currentLineStart: " + currentLineStart + "; startingOffset: " + startingOffset + "; currentPos: " + currentPos);
            
            System.err.println("==> Range: " + (currentLineStart + startingOffset) + " - " + (currentLineStart + startingOffset + currentPos));
            
            ble.printStackTrace();
            return 1;
          } 
          
          currentLineLengthInPixels = Utilities.getTabbedTextWidth(currentLineSeg, fm, 0, tabExpander, 0);
        } 
        
        startingOffset += currentPos;
      } 

      
      numPrintedLines++;
      if (numPrintedLines > startingLineNumber) {
        
        Utilities.drawTabbedText(currentLineSeg, xOffset, y, g, tabExpander, 0);
        y += fontHeight;
        if (numPrintedLines == startingLineNumber + maxLinesPerPage) {
          return 0;
        }
      } 
    } 


    
    if (numPrintedLines > startingLineNumber) {
      return 0;
    }
    return 1;
  }








  
  private static Segment removeEndingWhitespace(Segment segment) {
    int toTrim = 0;
    char currentChar = segment.setIndex(segment.getEndIndex() - 1);
    while ((currentChar == ' ' || currentChar == '\t') && currentChar != Character.MAX_VALUE) {
      toTrim++;
      currentChar = segment.previous();
    } 
    String stringVal = segment.toString();
    String newStringVal = stringVal.substring(0, stringVal.length() - toTrim);
    return new Segment(newStringVal.toCharArray(), 0, newStringVal.length());
  }








  
  private static class RPrintTabExpander
    implements TabExpander
  {
    public float nextTabStop(float x, int tabOffset) {
      if (RPrintUtilities.tabSizeInSpaces == 0) {
        return x;
      }
      int tabSizeInPixels = RPrintUtilities.tabSizeInSpaces * RPrintUtilities.fm.charWidth(' ');
      int ntabs = ((int)x - RPrintUtilities.xOffset) / tabSizeInPixels;
      return RPrintUtilities.xOffset + (ntabs + 1.0F) * tabSizeInPixels;
    }
  }
}
