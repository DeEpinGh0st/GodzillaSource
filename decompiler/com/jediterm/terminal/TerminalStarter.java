package com.jediterm.terminal;

import com.jediterm.terminal.emulator.Emulator;
import com.jediterm.terminal.emulator.JediEmulator;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;






public class TerminalStarter
  implements TerminalOutputStream
{
  private static final Logger LOG = Logger.getLogger(TerminalStarter.class);
  
  private final Emulator myEmulator;
  
  private final Terminal myTerminal;
  
  private final TtyConnector myTtyConnector;
  
  private final ScheduledExecutorService myEmulatorExecutor = Executors.newSingleThreadScheduledExecutor();
  
  public TerminalStarter(Terminal terminal, TtyConnector ttyConnector, TerminalDataStream dataStream) {
    this.myTtyConnector = ttyConnector;
    this.myTerminal = terminal;
    this.myTerminal.setTerminalOutput(this);
    this.myEmulator = (Emulator)createEmulator(dataStream, terminal);
  }
  
  protected JediEmulator createEmulator(TerminalDataStream dataStream, Terminal terminal) {
    return new JediEmulator(dataStream, terminal);
  }
  
  private void execute(Runnable runnable) {
    if (!this.myEmulatorExecutor.isShutdown()) {
      this.myEmulatorExecutor.execute(runnable);
    }
  }
  
  public void start() {
    try {
      while (!Thread.currentThread().isInterrupted() && this.myEmulator.hasNext()) {
        this.myEmulator.next();
      }
    }
    catch (InterruptedIOException e) {
      LOG.info("Terminal exiting");
    }
    catch (Exception e) {
      if (!this.myTtyConnector.isConnected()) {
        this.myTerminal.disconnected();
        return;
      } 
      LOG.error("Caught exception in terminal thread", e);
    } 
  }
  
  public byte[] getCode(int key, int modifiers) {
    return this.myTerminal.getCodeForKey(key, modifiers);
  }
  
  public void postResize(@NotNull Dimension dimension, @NotNull RequestOrigin origin) {
    if (dimension == null) $$$reportNull$$$0(0);  if (origin == null) $$$reportNull$$$0(1);  execute(() -> resize(this.myEmulator, this.myTerminal, this.myTtyConnector, dimension, origin, ()));
  }












  
  public static void resize(@NotNull Emulator emulator, @NotNull Terminal terminal, @NotNull TtyConnector ttyConnector, @NotNull Dimension newTermSize, @NotNull RequestOrigin origin, @NotNull BiConsumer<Long, Runnable> taskScheduler) {
    if (emulator == null) $$$reportNull$$$0(2);  if (terminal == null) $$$reportNull$$$0(3);  if (ttyConnector == null) $$$reportNull$$$0(4);  if (newTermSize == null) $$$reportNull$$$0(5);  if (origin == null) $$$reportNull$$$0(6);  if (taskScheduler == null) $$$reportNull$$$0(7);  CompletableFuture<?> promptUpdated = ((JediEmulator)emulator).getPromptUpdatedAfterResizeFuture(taskScheduler);
    terminal.resize(newTermSize, origin, promptUpdated);
    ttyConnector.resize(newTermSize);
  }

  
  public void sendBytes(byte[] bytes) {
    execute(() -> {
          
          try {
            this.myTtyConnector.write(bytes);
          } catch (IOException e) {
            throw new RuntimeException(e);
          } 
        });
  }

  
  public void sendString(String string) {
    execute(() -> {
          
          try {
            this.myTtyConnector.write(string);
          } catch (IOException e) {
            throw new RuntimeException(e);
          } 
        });
  }
  
  public void close() {
    execute(() -> {
          
          try {
            this.myTtyConnector.close();
          } catch (Exception e) {
            LOG.error("Error closing terminal", e);
          } finally {
            this.myEmulatorExecutor.shutdown();
          } 
        });
  }
}
