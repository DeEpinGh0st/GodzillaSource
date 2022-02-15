package com.jediterm.terminal.model.hyperlinks;

import com.google.common.collect.Lists;
import java.util.List;
import org.jetbrains.annotations.NotNull;




public class LinkResult
{
  private final LinkResultItem myItem;
  private List<LinkResultItem> myItemList;
  
  public LinkResult(@NotNull LinkResultItem item) {
    this.myItem = item;
    this.myItemList = null;
  }
  
  public LinkResult(@NotNull List<LinkResultItem> itemList) {
    this.myItemList = itemList;
    this.myItem = null;
  }
  
  public List<LinkResultItem> getItems() {
    if (this.myItemList == null) {
      this.myItemList = Lists.newArrayList((Object[])new LinkResultItem[] { this.myItem });
    }
    return this.myItemList;
  }
}
