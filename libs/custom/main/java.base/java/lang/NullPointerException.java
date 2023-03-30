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

public class NullPointerException extends RuntimeException {
  public NullPointerException() {
    super();
  }

  public NullPointerException(String s) {
    super(s);
  }

  // 0: no backtrace filled in, no message computed.
  // 1: backtrace filled in, no message computed.
  // 2: message computed
  private transient int extendedMessageState;
  private transient String extendedMessage;

  public synchronized Throwable fillInStackTrace() {
    // If the stack trace is changed the extended NPE algorithm
    // will compute a wrong message. So compute it beforehand.
    if (extendedMessageState == 0) {
      extendedMessageState = 1;
    } else if (extendedMessageState == 1) {
      extendedMessage = getExtendedNPEMessage();
      extendedMessageState = 2;
    }
    return super.fillInStackTrace();
  }

  public String getMessage() {
    String message = super.getMessage();
    if (message == null) {
      synchronized (this) {
        if (extendedMessageState == 1) {
          // Only the original stack trace was filled in. Message will
          // compute correctly.
          extendedMessage = getExtendedNPEMessage();
          extendedMessageState = 2;
        }
        return extendedMessage;
      }
    }
    return message;
  }

  private native String getExtendedNPEMessage();
}
