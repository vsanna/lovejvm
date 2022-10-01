package dev.ishikawa.lovejvm.rawclass.linterface;


import java.util.List;

public class Interfaces {
  private final int entrySize;
  private final List<RawInterface> interfaces;

  public Interfaces(int entrySize, List<RawInterface> interfaces) {
    if (entrySize != interfaces.size())
      throw new RuntimeException(
          "invalid Interfaces. the entrySize doesn't match with num of entries");
    this.entrySize = entrySize;
    this.interfaces = interfaces;
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
