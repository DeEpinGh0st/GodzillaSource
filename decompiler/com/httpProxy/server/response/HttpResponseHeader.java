package com.httpProxy.server.response;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class HttpResponseHeader
{
  ArrayList<String[]> headers = (ArrayList)new ArrayList<>();
  
  public HttpResponseHeader addHeader(String name, String value) {
    String[] header = new String[2];
    header[0] = (name == null) ? "" : name.trim();
    header[1] = (value == null) ? "" : value.trim();
    
    if (name != null) {
      this.headers.add(header);
    }
    return this;
  }
  
  public HttpResponseHeader setHeader(String name, String value) {
    Iterator<String[]> headers = (Iterator)getHeaders().iterator();
    String[] kv = null;
    while (headers.hasNext()) {
      kv = headers.next();
      if (kv[0].equals(name)) {
        break;
      }
      kv = null;
    } 
    if (kv != null) {
      getHeaders().remove(kv);
    }
    addHeader(name, value);
    
    return this;
  }
  
  public HttpResponseHeader removeHeader(String name) {
    List<String[]> removeList = (List)new ArrayList<>();
    Iterator<String[]> headers = (Iterator)getHeaders().iterator();
    String[] kv = null;
    while (headers.hasNext()) {
      kv = headers.next();
      if (kv[0].equals(name)) {
        removeList.add(kv);
      }
      kv = null;
    } 
    for (int i = 0; i < removeList.size(); i++) {
      String[] s = removeList.get(i);
      getHeaders().remove(s);
    } 
    
    return this;
  }
  public String decode() {
    StringBuilder stringBuilder = new StringBuilder();
    Iterator<String> iterator = this.headers.iterator();
    while (iterator.hasNext()) {
      String[] ex = (String[])iterator.next();
      stringBuilder.append(ex[0]);
      stringBuilder.append(": ");
      stringBuilder.append(ex[1]);
      stringBuilder.append("\r\n");
    } 
    return stringBuilder.toString();
  }
  public List<String[]> getHeaders() {
    return (List<String[]>)this.headers;
  }
  
  public String[] getHeaderValue(String headerKey) {
    return (String[])this.headers.stream().filter(v -> 
        (v.length == 2 && headerKey.equals(v[0])))




      
      .toArray(x$0 -> new String[x$0]);
  }

  
  public String toString() {
    return "HttpResponseHeader{headers=" + this.headers + '}';
  }
}
