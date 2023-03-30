package jdk.internal.lambda;


import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

public class LambdaFactory {
  /**
   * BootstrapMethod calls this.
   * */
  public static CallSite createCallSite(
      MethodType invokedType,
      String invokedName,
      MethodHandle implMethod,
      MethodType instantiatedMethodType) {
    Object lambdaImpl =
        createLambdaImplObject(invokedType, invokedName, implMethod, instantiatedMethodType);
    // TODO: MethodHandle(()Lhoge/geho/InnerClass;)でwrapし、CollSiteでwrapする
    return new ConstantCallSite(implMethod, lambdaImpl);
  }

  /**
   * createInnerClass creates an innerClass for the specific pair {outerClass, implMethod, instantiatedMethodType}
   *   class name = OuterClass$$Lambda$N
   * and then return one instance of the inner class
   *
   * this innerClass often cached.
   *
   * @return a ref to the instance of this inner class
   * */
  private static native Object createLambdaImplObject(
      MethodType invokedType,
      String invokedName, // apply|run|accept, etc
      MethodHandle implMethod, // ref to actual logic to use. ex: REF_invokeStatic
      // book/LambdaSample.lambda$test3$2:(Ljava/lang/Integer;)Ljava/lang/String;
      MethodType instantiatedMethodType // descriptor of this functional method
      );
}
