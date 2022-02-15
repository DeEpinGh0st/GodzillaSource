package com.kitfox.svg.xml;

import java.net.URL;
import org.w3c.dom.Element;

public interface ReadableXMLElement {
  void read(Element paramElement, URL paramURL);
}
