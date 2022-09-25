package dev.ishikawa.lovejvm.rawclass.linterface;


import java.util.List;

public class Interfaces {
  private final int entrySize;
  private List<RawInterface> interfaces;

  public Interfaces(int entrySize, List<RawInterface> entries) {
    if (entrySize != entries.size())
      throw new RuntimeException(
          "invalid Interfaces. the entrySize doesn't match with num of entries");
    this.entrySize = entrySize;
    this.interfaces = entries;
  }

  public int getEntrySize() {
    return entrySize;
  }

  public List<RawInterface> getInterfaces() {
    return interfaces;
  }

  public int size() {
    return 2 + interfaces.stream().map(RawInterface::size).reduce(0, Integer::sum);
  }
}
