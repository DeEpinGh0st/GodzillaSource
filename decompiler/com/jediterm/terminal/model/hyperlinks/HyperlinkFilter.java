package com.jediterm.terminal.model.hyperlinks;

import org.jetbrains.annotations.Nullable;

public interface HyperlinkFilter {
  @Nullable
  LinkResult apply(String paramString);
}
