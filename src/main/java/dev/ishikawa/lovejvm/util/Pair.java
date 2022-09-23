package dev.ishikawa.lovejvm.util;

public class Pair<S, T> {
  private S left;
  private T right;

  private Pair(S left, T right) {
    this.left = left;
    this.right = right;
  }

  public S getLeft() {
    return left;
  }

  public T getRight() {
    return right;
  }

  public static <A, B> Pair of(A left, B right) {
    return new Pair<>(left, right);
  }
}
