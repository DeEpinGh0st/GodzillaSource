package org.apache.log4j.rewrite;

import java.util.Enumeration;
import java.util.Properties;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.w3c.dom.Element;
































public class RewriteAppender
  extends AppenderSkeleton
  implements AppenderAttachable, UnrecognizedElementHandler
{
  private RewritePolicy policy;
  private final AppenderAttachableImpl appenders = new AppenderAttachableImpl();




  
  protected void append(LoggingEvent event) {
    LoggingEvent rewritten = event;
    if (this.policy != null) {
      rewritten = this.policy.rewrite(event);
    }
    if (rewritten != null) {
      synchronized (this.appenders) {
        this.appenders.appendLoopOnAppenders(rewritten);
      } 
    }
  }





  
  public void addAppender(Appender newAppender) {
    synchronized (this.appenders) {
      this.appenders.addAppender(newAppender);
    } 
  }




  
  public Enumeration getAllAppenders() {
    synchronized (this.appenders) {
      return this.appenders.getAllAppenders();
    } 
  }






  
  public Appender getAppender(String name) {
    synchronized (this.appenders) {
      return this.appenders.getAppender(name);
    } 
  }





  
  public void close() {
    this.closed = true;


    
    synchronized (this.appenders) {
      Enumeration iter = this.appenders.getAllAppenders();
      
      if (iter != null) {
        while (iter.hasMoreElements()) {
          Object next = iter.nextElement();
          
          if (next instanceof Appender) {
            ((Appender)next).close();
          }
        } 
      }
    } 
  }





  
  public boolean isAttached(Appender appender) {
    synchronized (this.appenders) {
      return this.appenders.isAttached(appender);
    } 
  }



  
  public boolean requiresLayout() {
    return false;
  }



  
  public void removeAllAppenders() {
    synchronized (this.appenders) {
      this.appenders.removeAllAppenders();
    } 
  }




  
  public void removeAppender(Appender appender) {
    synchronized (this.appenders) {
      this.appenders.removeAppender(appender);
    } 
  }




  
  public void removeAppender(String name) {
    synchronized (this.appenders) {
      this.appenders.removeAppender(name);
    } 
  }

  
  public void setRewritePolicy(RewritePolicy rewritePolicy) {
    this.policy = rewritePolicy;
  }



  
  public boolean parseUnrecognizedElement(Element element, Properties props) throws Exception {
    String nodeName = element.getNodeName();
    if ("rewritePolicy".equals(nodeName)) {
      Object rewritePolicy = DOMConfigurator.parseElement(element, props, RewritePolicy.class);

      
      if (rewritePolicy != null) {
        if (rewritePolicy instanceof OptionHandler) {
          ((OptionHandler)rewritePolicy).activateOptions();
        }
        setRewritePolicy((RewritePolicy)rewritePolicy);
      } 
      return true;
    } 
    return false;
  }
}
