package org.fife.rsta.ac.xml;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import javax.swing.tree.MutableTreeNode;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.fife.io.DocumentReader;
import org.fife.rsta.ac.xml.tree.XmlTreeNode;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;




































public class XmlParser
  extends AbstractParser
{
  public static final String PROPERTY_AST = "XmlAST";
  private XmlLanguageSupport xls;
  private PropertyChangeSupport support;
  private XmlTreeNode curElem;
  private XmlTreeNode root;
  private Locator locator;
  private SAXParserFactory spf;
  private SAXParser sp;
  private ValidationConfig validationConfig;
  private int elemCount;
  
  public XmlParser(XmlLanguageSupport xls) {
    this.xls = xls;
    this.support = new PropertyChangeSupport(this);
    try {
      this.spf = SAXParserFactory.newInstance();
    } catch (FactoryConfigurationError fce) {
      fce.printStackTrace();
    } 
  }









  
  public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
    this.support.addPropertyChangeListener(prop, l);
  }







  
  public XmlTreeNode getAst() {
    return this.root;
  }









  
  private String getMainAttribute(Attributes attributes) {
    int nameIndex = -1;
    int idIndex = -1;
    int i;
    for (i = 0; i < attributes.getLength(); i++) {
      String name = attributes.getLocalName(i);
      if ("id".equals(name)) {
        idIndex = i;
        break;
      } 
      if ("name".equals(name)) {
        nameIndex = i;
      }
    } 
    
    i = idIndex;
    if (i == -1) {
      i = nameIndex;
      if (i == -1) {
        i = 0;
      }
    } 
    
    return attributes.getLocalName(i) + "=" + attributes.getValue(i);
  }


  
  public SAXParserFactory getSaxParserFactory() {
    return this.spf;
  }






  
  public ParseResult parse(RSyntaxDocument doc, String style) {
    (new ValidationConfigSniffer()).sniff(doc);
    
    DefaultParseResult result = new DefaultParseResult((Parser)this);
    this.curElem = this.root = new XmlTreeNode("Root");
    
    if (this.spf == null || doc.getLength() == 0) {
      return (ParseResult)result;
    }

    
    try {
      if (this.sp == null) {
        this.sp = this.spf.newSAXParser();
      }
      Handler handler = new Handler(doc, result);
      if (this.validationConfig != null) {
        this.validationConfig.configureHandler(handler);
      }
      DocumentReader r = new DocumentReader((Document)doc);
      InputSource input = new InputSource((Reader)r);
      this.sp.parse(input, handler);
      r.close();
    } catch (Exception exception) {}






    
    if (this.locator != null) {
      try {
        this.root.setStartOffset(doc.createPosition(0));
        this.root.setEndOffset(doc.createPosition(doc.getLength()));
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    }
    
    this.support.firePropertyChange("XmlAST", (Object)null, this.root);
    return (ParseResult)result;
  }









  
  public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
    this.support.removePropertyChangeListener(prop, l);
  }








  
  public void setValidationConfig(ValidationConfig config) {
    this.validationConfig = config;
    if (this.validationConfig != null) {
      this.validationConfig.configureParser(this);
    }
    this.sp = null;
  }

  
  public class Handler
    extends DefaultHandler
  {
    private DefaultParseResult result;
    
    private RSyntaxDocument doc;
    
    private Segment s;
    
    private EntityResolver entityResolver;
    
    public Handler(RSyntaxDocument doc, DefaultParseResult result) {
      this.doc = doc;
      this.result = result;
      this.s = new Segment();
    }

    
    private void doError(SAXParseException e, ParserNotice.Level level) {
      if (!XmlParser.this.xls.getShowSyntaxErrors()) {
        return;
      }
      int line = e.getLineNumber() - 1;
      Element root = this.doc.getDefaultRootElement();
      Element elem = root.getElement(line);
      int offs = elem.getStartOffset();
      int len = elem.getEndOffset() - offs;
      if (line == root.getElementCount() - 1) {
        len++;
      }
      
      DefaultParserNotice pn = new DefaultParserNotice((Parser)XmlParser.this, e.getMessage(), line, offs, len);
      pn.setLevel(level);
      this.result.addNotice((ParserNotice)pn);
    }



    
    public void endElement(String uri, String localName, String qName) {
      XmlParser.this.curElem = (XmlTreeNode)XmlParser.this.curElem.getParent();
    }


    
    public void error(SAXParseException e) {
      doError(e, ParserNotice.Level.ERROR);
    }


    
    public void fatalError(SAXParseException e) {
      doError(e, ParserNotice.Level.ERROR);
    }


    
    private int getTagStart(int end) {
      Element root = this.doc.getDefaultRootElement();
      int line = root.getElementIndex(end);
      Element elem = root.getElement(line);
      int start = elem.getStartOffset();
      int lastCharOffs = -1;
      
      try {
        while (line >= 0) {
          this.doc.getText(start, end - start, this.s);
          for (int i = this.s.offset + this.s.count - 1; i >= this.s.offset; i--) {
            char ch = this.s.array[i];
            if (ch == '<') {
              return lastCharOffs;
            }
            if (Character.isLetterOrDigit(ch)) {
              lastCharOffs = start + i - this.s.offset;
            }
          } 
          if (--line >= 0) {
            elem = root.getElement(line);
            start = elem.getStartOffset();
            end = elem.getEndOffset();
          } 
        } 
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
      
      return -1;
    }




    
    public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
      if (this.entityResolver != null) {
        return this.entityResolver.resolveEntity(publicId, systemId);
      }
      
      return new InputSource(new StringReader(" "));
    }



    
    public void setDocumentLocator(Locator l) {
      XmlParser.this.locator = l;
    }

    
    public void setEntityResolver(EntityResolver resolver) {
      this.entityResolver = resolver;
    }




    
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
      XmlTreeNode newElem = new XmlTreeNode(qName);
      if (attributes.getLength() > 0) {
        newElem.setMainAttribute(XmlParser.this.getMainAttribute(attributes));
      }
      if (XmlParser.this.locator != null) {
        int line = XmlParser.this.locator.getLineNumber();
        if (line != -1) {
          
          int offs = this.doc.getDefaultRootElement().getElement(line - 1).getStartOffset();
          int col = XmlParser.this.locator.getColumnNumber();
          if (col != -1) {
            offs += col - 1;
          }
          
          offs = getTagStart(offs);
          try {
            newElem.setStartOffset(this.doc.createPosition(offs));
            int endOffs = offs + qName.length();
            newElem.setEndOffset(this.doc.createPosition(endOffs));
          } catch (BadLocationException ble) {
            ble.printStackTrace();
          } 
        } 
      } 
      
      XmlParser.this.curElem.add((MutableTreeNode)newElem);
      XmlParser.this.curElem = newElem;
    }



    
    public void warning(SAXParseException e) {
      doError(e, ParserNotice.Level.WARNING);
    }
  }
}
