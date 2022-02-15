package org.fife.rsta.ui.search;
































final class SearchUtil
{
  public static String getToolTip(FindReplaceButtonsEnableResult res) {
    String tooltip = res.getError();
    if (tooltip != null && tooltip.indexOf('\n') > -1) {
      tooltip = tooltip.replaceFirst("\\\n", "</b><br><pre>");
      tooltip = "<html><b>" + tooltip;
    } 
    return tooltip;
  }
}
