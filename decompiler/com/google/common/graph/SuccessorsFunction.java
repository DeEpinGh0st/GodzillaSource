package com.google.common.graph;

import com.google.common.annotations.Beta;

@Beta
public interface SuccessorsFunction<N> {
  Iterable<? extends N> successors(N paramN);
}
