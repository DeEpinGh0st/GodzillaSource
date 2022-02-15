package org.springframework.util.concurrent;

public interface ListenableFutureCallback<T> extends SuccessCallback<T>, FailureCallback {}
