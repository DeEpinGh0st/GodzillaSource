package org.springframework.util.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;













































public abstract class DomUtils
{
  public static List<Element> getChildElementsByTagName(Element ele, String... childEleNames) {
    Assert.notNull(ele, "Element must not be null");
    Assert.notNull(childEleNames, "Element names collection must not be null");
    List<String> childEleNameList = Arrays.asList(childEleNames);
    NodeList nl = ele.getChildNodes();
    List<Element> childEles = new ArrayList<>();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element && nodeNameMatch(node, childEleNameList)) {
        childEles.add((Element)node);
      }
    } 
    return childEles;
  }










  
  public static List<Element> getChildElementsByTagName(Element ele, String childEleName) {
    return getChildElementsByTagName(ele, new String[] { childEleName });
  }






  
  @Nullable
  public static Element getChildElementByTagName(Element ele, String childEleName) {
    Assert.notNull(ele, "Element must not be null");
    Assert.notNull(childEleName, "Element name must not be null");
    NodeList nl = ele.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element && nodeNameMatch(node, childEleName)) {
        return (Element)node;
      }
    } 
    return null;
  }






  
  @Nullable
  public static String getChildElementValueByTagName(Element ele, String childEleName) {
    Element child = getChildElementByTagName(ele, childEleName);
    return (child != null) ? getTextValue(child) : null;
  }





  
  public static List<Element> getChildElements(Element ele) {
    Assert.notNull(ele, "Element must not be null");
    NodeList nl = ele.getChildNodes();
    List<Element> childEles = new ArrayList<>();
    for (int i = 0; i < nl.getLength(); i++) {
      Node node = nl.item(i);
      if (node instanceof Element) {
        childEles.add((Element)node);
      }
    } 
    return childEles;
  }









  
  public static String getTextValue(Element valueEle) {
    Assert.notNull(valueEle, "Element must not be null");
    StringBuilder sb = new StringBuilder();
    NodeList nl = valueEle.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node item = nl.item(i);
      if ((item instanceof org.w3c.dom.CharacterData && !(item instanceof org.w3c.dom.Comment)) || item instanceof org.w3c.dom.EntityReference) {
        sb.append(item.getNodeValue());
      }
    } 
    return sb.toString();
  }





  
  public static boolean nodeNameEquals(Node node, String desiredName) {
    Assert.notNull(node, "Node must not be null");
    Assert.notNull(desiredName, "Desired name must not be null");
    return nodeNameMatch(node, desiredName);
  }





  
  public static ContentHandler createContentHandler(Node node) {
    return new DomContentHandler(node);
  }



  
  private static boolean nodeNameMatch(Node node, String desiredName) {
    return (desiredName.equals(node.getNodeName()) || desiredName.equals(node.getLocalName()));
  }



  
  private static boolean nodeNameMatch(Node node, Collection<?> desiredNames) {
    return (desiredNames.contains(node.getNodeName()) || desiredNames.contains(node.getLocalName()));
  }
}
