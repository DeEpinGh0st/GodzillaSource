package org.fife.ui.rtextarea;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.fife.io.UnicodeReader;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

















































public class Macro
{
  private String name;
  private ArrayList<MacroRecord> macroRecords;
  private static final String ROOT_ELEMENT = "macro";
  private static final String MACRO_NAME = "macroName";
  private static final String ACTION = "action";
  private static final String ID = "id";
  private static final String UNTITLED_MACRO_NAME = "<Untitled>";
  private static final String FILE_ENCODING = "UTF-8";
  
  public Macro() {
    this("<Untitled>");
  }










  
  public Macro(File file) throws IOException {
    Document doc;
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    
    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      
      InputSource is = new InputSource((Reader)new UnicodeReader(new FileInputStream(file), "UTF-8"));
      
      is.setEncoding("UTF-8");
      doc = db.parse(is);
    } catch (Exception e) {
      e.printStackTrace();
      String desc = e.getMessage();
      if (desc == null) {
        desc = e.toString();
      }
      throw new IOException("Error parsing XML: " + desc);
    } 
    
    this.macroRecords = new ArrayList<>();

    
    boolean parsedOK = initializeFromXMLFile(doc.getDocumentElement());
    if (!parsedOK) {
      this.name = null;
      this.macroRecords.clear();
      this.macroRecords = null;
      throw new IOException("Error parsing XML!");
    } 
  }







  
  public Macro(String name) {
    this(name, null);
  }








  
  public Macro(String name, List<MacroRecord> records) {
    this.name = name;
    
    if (records != null) {
      this.macroRecords = new ArrayList<>(records.size());
      this.macroRecords.addAll(records);
    } else {
      
      this.macroRecords = new ArrayList<>(10);
    } 
  }








  
  public void addMacroRecord(MacroRecord record) {
    if (record != null) {
      this.macroRecords.add(record);
    }
  }







  
  public List<MacroRecord> getMacroRecords() {
    return this.macroRecords;
  }









  
  public String getName() {
    return this.name;
  }























  
  private boolean initializeFromXMLFile(Element root) {
    NodeList childNodes = root.getChildNodes();
    int count = childNodes.getLength();
    
    for (int i = 0; i < count; i++) {
      String nodeName;
      Node node = childNodes.item(i);
      int type = node.getNodeType();
      switch (type) {


        
        case 1:
          nodeName = node.getNodeName();
          
          if (nodeName.equals("macroName")) {
            NodeList childNodes2 = node.getChildNodes();
            this.name = "<Untitled>";
            if (childNodes2.getLength() > 0) {
              node = childNodes2.item(0);
              int type2 = node.getNodeType();
              if (type2 != 4 && type2 != 3)
              {
                return false;
              }
              this.name = node.getNodeValue().trim();
            } 
            
            break;
          } 
          if (nodeName.equals("action")) {
            NamedNodeMap attributes = node.getAttributes();
            if (attributes == null || attributes.getLength() != 1) {
              return false;
            }
            Node node2 = attributes.item(0);
            MacroRecord macroRecord = new MacroRecord();
            if (!node2.getNodeName().equals("id")) {
              return false;
            }
            macroRecord.id = node2.getNodeValue();
            NodeList childNodes2 = node.getChildNodes();
            int length = childNodes2.getLength();
            if (length == 0) {
              
              macroRecord.actionCommand = "";
              
              this.macroRecords.add(macroRecord);
              
              break;
            } 
            node = childNodes2.item(0);
            int type2 = node.getNodeType();
            if (type2 != 4 && type2 != 3)
            {
              return false;
            }
            macroRecord.actionCommand = node.getNodeValue();
            this.macroRecords.add(macroRecord);
          } 
          break;
      } 








    
    } 
    return true;
  }












  
  public void saveToFile(File file) throws IOException {
    saveToFile(file.getAbsolutePath());
  }



























  
  public void saveToFile(String fileName) throws IOException {
    try {
      DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      DOMImplementation impl = db.getDOMImplementation();
      
      Document doc = impl.createDocument(null, "macro", null);
      Element rootElement = doc.getDocumentElement();

      
      Element nameElement = doc.createElement("macroName");
      nameElement.appendChild(doc.createCDATASection(this.name));
      rootElement.appendChild(nameElement);

      
      for (MacroRecord record : this.macroRecords) {
        Element actionElement = doc.createElement("action");
        actionElement.setAttribute("id", record.id);
        if (record.actionCommand != null && record.actionCommand
          .length() > 0) {






          
          String command = record.actionCommand;
          for (int j = 0; j < command.length(); j++) {
            if (command.charAt(j) < ' ') {
              command = command.substring(0, j);
              if (j < command.length() - 1) {
                command = command + command.substring(j + 1);
              }
            } 
          } 
          Node n = doc.createCDATASection(command);
          actionElement.appendChild(n);
        } 
        rootElement.appendChild(actionElement);
      } 

      
      StreamResult result = new StreamResult(new File(fileName));
      DOMSource source = new DOMSource(doc);
      TransformerFactory transFac = TransformerFactory.newInstance();
      Transformer transformer = transFac.newTransformer();
      transformer.setOutputProperty("indent", "yes");
      transformer.setOutputProperty("encoding", "UTF-8");
      transformer.transform(source, result);
    }
    catch (RuntimeException re) {
      throw re;
    } catch (Exception e) {
      throw new IOException("Error generating XML!");
    } 
  }










  
  public void setName(String name) {
    this.name = name;
  }



  
  static class MacroRecord
  {
    String id;

    
    String actionCommand;


    
    MacroRecord() {
      this(null, null);
    }
    
    MacroRecord(String id, String actionCommand) {
      this.id = id;
      this.actionCommand = actionCommand;
    }
  }
}
