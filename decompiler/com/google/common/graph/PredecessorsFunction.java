package com.google.common.graph;

import com.google.common.annotations.Beta;

@Beta
public interface PredecessorsFunction<N> {
  Iterable<? extends N> predecessors(N paramN);
}
