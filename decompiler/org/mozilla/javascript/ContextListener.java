package org.mozilla.javascript;

@Deprecated
public interface ContextListener extends ContextFactory.Listener {
  @Deprecated
  void contextEntered(Context paramContext);
  
  @Deprecated
  void contextExited(Context paramContext);
}
