package javassist.util;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;















































































public class HotSwapper
{
  private static final String TRIGGER_NAME = Trigger.class.getName();







  
  public HotSwapper(int port) throws IOException, IllegalConnectorArgumentsException {
    this(Integer.toString(port));
  }








  
  private VirtualMachine jvm = null;
  private MethodEntryRequest request = null;
  private Map<ReferenceType, byte[]> newClassFiles = null;
  private Trigger trigger = new Trigger();
  public HotSwapper(String port) throws IOException, IllegalConnectorArgumentsException {
    AttachingConnector connector = (AttachingConnector)findConnector("com.sun.jdi.SocketAttach");
    
    Map<String, Connector.Argument> arguments = connector.defaultArguments();
    ((Connector.Argument)arguments.get("hostname")).setValue("localhost");
    ((Connector.Argument)arguments.get("port")).setValue(port);
    this.jvm = connector.attach(arguments);
    EventRequestManager manager = this.jvm.eventRequestManager();
    this.request = methodEntryRequests(manager, TRIGGER_NAME);
  }
  private static final String HOST_NAME = "localhost";
  private Connector findConnector(String connector) throws IOException {
    List<Connector> connectors = Bootstrap.virtualMachineManager().allConnectors();
    
    for (Connector con : connectors) {
      if (con.name().equals(connector))
        return con; 
    } 
    throw new IOException("Not found: " + connector);
  }


  
  private static MethodEntryRequest methodEntryRequests(EventRequestManager manager, String classpattern) {
    MethodEntryRequest mereq = manager.createMethodEntryRequest();
    mereq.addClassFilter(classpattern);
    mereq.setSuspendPolicy(1);
    return mereq;
  }




  
  private void deleteEventRequest(EventRequestManager manager, MethodEntryRequest request) {
    manager.deleteEventRequest((EventRequest)request);
  }






  
  public void reload(String className, byte[] classFile) {
    ReferenceType classtype = toRefType(className);
    Map<ReferenceType, byte[]> map = (Map)new HashMap<>();
    map.put(classtype, classFile);
    reload2(map, className);
  }








  
  public void reload(Map<String, byte[]> classFiles) {
    Map<ReferenceType, byte[]> map = (Map)new HashMap<>();
    String className = null;
    for (Map.Entry<String, byte[]> e : classFiles.entrySet()) {
      className = e.getKey();
      map.put(toRefType(className), e.getValue());
    } 
    
    if (className != null)
      reload2(map, className + " etc."); 
  }
  
  private ReferenceType toRefType(String className) {
    List<ReferenceType> list = this.jvm.classesByName(className);
    if (list == null || list.isEmpty())
      throw new RuntimeException("no such class: " + className); 
    return list.get(0);
  }
  
  private void reload2(Map<ReferenceType, byte[]> map, String msg) {
    synchronized (this.trigger) {
      startDaemon();
      this.newClassFiles = map;
      this.request.enable();
      this.trigger.doSwap();
      this.request.disable();
      Map<ReferenceType, byte[]> ncf = this.newClassFiles;
      if (ncf != null) {
        this.newClassFiles = null;
        throw new RuntimeException("failed to reload: " + msg);
      } 
    } 
  }
  
  private void startDaemon() {
    (new Thread() {
        private void errorMsg(Throwable e) {
          System.err.print("Exception in thread \"HotSwap\" ");
          e.printStackTrace(System.err);
        }

        
        public void run() {
          EventSet events = null;
          try {
            events = HotSwapper.this.waitEvent();
            EventIterator iter = events.eventIterator();
            while (iter.hasNext()) {
              Event event = iter.nextEvent();
              if (event instanceof com.sun.jdi.event.MethodEntryEvent) {
                HotSwapper.this.hotswap();
                
                break;
              } 
            } 
          } catch (Throwable e) {
            errorMsg(e);
          } 
          try {
            if (events != null) {
              events.resume();
            }
          } catch (Throwable e) {
            errorMsg(e);
          } 
        }
      }).start();
  }
  
  EventSet waitEvent() throws InterruptedException {
    EventQueue queue = this.jvm.eventQueue();
    return queue.remove();
  }
  
  void hotswap() {
    Map<ReferenceType, byte[]> map = this.newClassFiles;
    this.jvm.redefineClasses(map);
    this.newClassFiles = null;
  }
}
