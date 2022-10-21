/*
 * Copyright (c) 2002, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.lang;


import java.io.PrintStream;

public class Throwable {
  private static final StackTraceElement[] UNASSIGNED_STACK = new StackTraceElement[0];

  private String detailMessage;
  private Throwable cause = this;
  private StackTraceElement[] stackTrace = UNASSIGNED_STACK;

  public Throwable() {}

  public Throwable(String message) {
    detailMessage = message;
  }

  public Throwable(String message, Throwable cause) {
    detailMessage = message;
    this.cause = cause;
  }

  public Throwable(Throwable cause) {
    detailMessage = (cause == null ? null : cause.toString());
    this.cause = cause;
  }

  protected Throwable(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    stackTrace = null;
    detailMessage = message;
    this.cause = cause;
  }

  public void printStackTrace() {
    printStackTrace(System.err);
  }

  public void printStackTrace(PrintStream s) {
    for (int i = 0; i < stackTrace.length; i++) {
      s.println(stackTrace[i]);
    }
  }

  public synchronized Throwable fillInStackTrace() {
    throw new UnsupportedOperationException("");
  }

  public String getMessage() {
    return detailMessage;
  }

  public String toString() {
    throw new UnsupportedOperationException("");
  }

  public String getLocalizedMessage() {
    return getMessage();
  }
}
