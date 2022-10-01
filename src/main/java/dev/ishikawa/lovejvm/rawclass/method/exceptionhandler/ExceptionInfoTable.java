package dev.ishikawa.lovejvm.rawclass.method.exceptionhandler;


import java.util.List;
import java.util.Optional;

public class ExceptionInfoTable {
  private final int entrySize;
  private final List<ExceptionInfo> exceptionInfoEntries;

  public ExceptionInfoTable(int entrySize, List<ExceptionInfo> exceptionInfoEntries) {
    if (entrySize != exceptionInfoEntries.size())
      throw new RuntimeException(
          "invalid ExceptionHandlers. the size doesn't match with num of entries");
    this.entrySize = entrySize;
    this.exceptionInfoEntries = exceptionInfoEntries;
  }

  public Optional<ExceptionInfo> findBy(String exceptionClassBinaryName, int offset) {
    return getExceptionInfoEntries().stream()
        .filter(it -> it.shouldCatch(exceptionClassBinaryName, offset))
        .findFirst();
  }

  public List<ExceptionInfo> getExceptionInfoEntries() {
    return exceptionInfoEntries;
  }

  public int getEntrySize() {
    return entrySize;
  }

  public int size() {
    return 2 // num of exception handlers(2B)
        + exceptionInfoEntries.stream().map(ExceptionInfo::size).reduce(0, Integer::sum);
  }
}
