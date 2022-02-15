package com.httpProxy.server.core;

import com.httpProxy.server.request.HttpRequest;
import java.net.Socket;

public interface HttpProxyHandle {
  void handler(Socket paramSocket, HttpRequest paramHttpRequest) throws Exception;
}
