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


import java.util.Objects;

public final class StackTraceElement {

  private String classLoaderName;
  private String moduleName;
  private String moduleVersion;
  private String declaringClass;
  private String methodName;
  private String fileName;
  private int lineNumber;

  public StackTraceElement(
      String classLoaderName,
      String moduleName,
      String moduleVersion,
      String declaringClass,
      String methodName,
      String fileName,
      int lineNumber) {
    this.classLoaderName = classLoaderName;
    this.moduleName = moduleName;
    this.moduleVersion = moduleVersion;
    this.declaringClass = Objects.requireNonNull(declaringClass, "Declaring class is null");
    this.methodName = Objects.requireNonNull(methodName, "Method name is null");
    this.fileName = fileName;
    this.lineNumber = lineNumber;
  }

  public String getFileName() {
    return fileName;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public String getModuleName() {
    return moduleName;
  }

  public String getModuleVersion() {
    return moduleVersion;
  }

  public String getClassLoaderName() {
    return classLoaderName;
  }

  public String getClassName() {
    return declaringClass;
  }

  public String getMethodName() {
    return methodName;
  }

  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (!(obj instanceof StackTraceElement)) return false;
    StackTraceElement e = (StackTraceElement) obj;
    return Objects.equals(classLoaderName, e.classLoaderName)
        && Objects.equals(moduleName, e.moduleName)
        && Objects.equals(moduleVersion, e.moduleVersion)
        && e.declaringClass.equals(declaringClass)
        && e.lineNumber == lineNumber
        && Objects.equals(methodName, e.methodName)
        && Objects.equals(fileName, e.fileName);
  }

  public int hashCode() {
    int result = 31 * declaringClass.hashCode() + methodName.hashCode();
    result = 31 * result + Objects.hashCode(classLoaderName);
    result = 31 * result + Objects.hashCode(moduleName);
    result = 31 * result + Objects.hashCode(moduleVersion);
    result = 31 * result + Objects.hashCode(fileName);
    result = 31 * result + lineNumber;
    return result;
  }
}
