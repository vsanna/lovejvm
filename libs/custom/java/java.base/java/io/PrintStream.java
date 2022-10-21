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

package java.io;

public class PrintStream {
  public PrintStream(String var1) throws FileNotFoundException {}

  public PrintStream(OutputStream var1) {}

  public void println() {
    write("\n");
  }

  public void println(boolean var1) {
    write(String.valueOf(var1));
  }

  public void println(char var1) {
    write(String.valueOf(var1));
  }

  public void println(int var1) {
    write(String.valueOf(var1));
  }

  public void println(long var1) {
    write(String.valueOf(var1));
  }

  public void println(float var1) {
    write(String.valueOf(var1));
  }

  public void println(double var1) {
    write(String.valueOf(var1));
  }

  public void println(char[] var1) {
    write(String.valueOf(var1));
  }

  public void println(String x) {
    write(x);
  }

  public void println(Object var1) {
    write(var1.toString());
  }

  public native void write(String message);
}
