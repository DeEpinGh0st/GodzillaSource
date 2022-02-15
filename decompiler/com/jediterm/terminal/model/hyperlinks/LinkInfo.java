package com.jediterm.terminal.model.hyperlinks;

import com.jediterm.terminal.ui.TerminalAction;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;




public class LinkInfo
{
  private final Runnable myNavigateCallback;
  private final PopupMenuGroupProvider myPopupMenuGroupProvider;
  private final HoverConsumer myHoverConsumer;
  
  public LinkInfo(@NotNull Runnable navigateCallback) {
    this(navigateCallback, null, null);
  }


  
  private LinkInfo(@NotNull Runnable navigateCallback, @Nullable PopupMenuGroupProvider popupMenuGroupProvider, @Nullable HoverConsumer hoverConsumer) {
    this.myNavigateCallback = navigateCallback;
    this.myPopupMenuGroupProvider = popupMenuGroupProvider;
    this.myHoverConsumer = hoverConsumer;
  }
  
  public void navigate() {
    this.myNavigateCallback.run();
  }
  @Nullable
  public PopupMenuGroupProvider getPopupMenuGroupProvider() {
    return this.myPopupMenuGroupProvider;
  }
  @Nullable
  public HoverConsumer getHoverConsumer() {
    return this.myHoverConsumer;
  }




  
  public static final class Builder
  {
    private Runnable myNavigateCallback;



    
    private LinkInfo.PopupMenuGroupProvider myPopupMenuGroupProvider;



    
    private LinkInfo.HoverConsumer myHoverConsumer;



    
    @NotNull
    public Builder setNavigateCallback(@NotNull Runnable navigateCallback) {
      if (navigateCallback == null) $$$reportNull$$$0(0);  this.myNavigateCallback = navigateCallback;
      if (this == null) $$$reportNull$$$0(1);  return this;
    }
    @NotNull
    public Builder setPopupMenuGroupProvider(@Nullable LinkInfo.PopupMenuGroupProvider popupMenuGroupProvider) {
      this.myPopupMenuGroupProvider = popupMenuGroupProvider;
      if (this == null) $$$reportNull$$$0(2);  return this;
    }
    @NotNull
    public Builder setHoverConsumer(@Nullable LinkInfo.HoverConsumer hoverConsumer) {
      this.myHoverConsumer = hoverConsumer;
      if (this == null) $$$reportNull$$$0(3);  return this;
    }
    @NotNull
    public LinkInfo build() {
      return new LinkInfo(this.myNavigateCallback, this.myPopupMenuGroupProvider, this.myHoverConsumer);
    }
  }
  
  public static interface HoverConsumer {
    void onMouseEntered(@NotNull JComponent param1JComponent, @NotNull Rectangle param1Rectangle);
    
    void onMouseExited();
  }
  
  public static interface PopupMenuGroupProvider {
    @NotNull
    List<TerminalAction> getPopupMenuGroup(@NotNull MouseEvent param1MouseEvent);
  }
}
