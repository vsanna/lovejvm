package dev.ishikawa.lovejvm.rawclass.method.exceptionhandler;


import java.util.List;

public class ExceptionHandlers {
  private final int entrySize;
  private List<RawExceptionHandler> exceptionHandlers;

  public ExceptionHandlers(int entrySize, List<RawExceptionHandler> exceptionHandlers) {
    if (entrySize != exceptionHandlers.size())
      throw new RuntimeException(
          "invalid ExceptionHandlers. the size doesn't match with num of entries");
    this.entrySize = entrySize;
    this.exceptionHandlers = exceptionHandlers;
  }

  public int getEntrySize() {
    return entrySize;
  }

  public int size() {
    return 2 // num of exception handlers(2B)
        + exceptionHandlers.stream().map(RawExceptionHandler::size).reduce(0, Integer::sum);
  }
}
