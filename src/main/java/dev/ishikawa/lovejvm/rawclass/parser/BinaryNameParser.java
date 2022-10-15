package dev.ishikawa.lovejvm.rawclass.parser;

import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import dev.ishikawa.lovejvm.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class BinaryNameParser {
  // [Ljava/lang/String;
  // java/lang/Object;
  // [B
  // Z
  // {isArray: boolean, complexTypeBinaryName: string, primaryType: JvmType}
//  static public Result parseBinaryName(String binaryName) {
//    // ex: ([Ljava/lang/String;[[IDLjava/lang/String;II)V -> [, [, D, L, I, I
//    // [ -> L ~ ;まで飛ばす or 次の1文字飛ばす
//    // L -> ~; まで飛ばす
//    // 他 -> なにもしない
//    var binaryNameCharArray = binaryName.toCharArray();
//
//    boolean isArray = true;
//    String complexTypeBinaryName = null;
//    JvmType primaryType = null;
//
//    switch (binaryNameCharArray[0]) {
//      case '[':
//        isArray = true;
//
//        if(binaryNameCharArray[1] == 'L') {
//          var result = readComplexClassBinaryName(binaryNameCharArray, 1);
//          complexTypeBinaryName = result.getLeft();
//        } else if (){
//          primaryType = JvmType.findByJvmSignature(String.valueOf(binaryNameCharArray[1]));
//        }
//        return new Result(isArray, complexTypeBinaryName, primaryType);
//      case 'L':
//      {
//        isArray = false;
//        var result = readComplexClassBinaryName(binaryNameCharArray, 0);
//        complexTypeBinaryName = result.getLeft();
//        return new Result(isArray, complexTypeBinaryName, primaryType);
//      }
//      default:
//        break;
//    }
//
//    return argumentTypes.stream().map(JvmType::wordSize).reduce(0, Integer::sum);
//  }

  // [Ljava/lang/String;  =>  [Ljava/lang/String;
  //  ↑                                         ↑
  static private Pair<String, Integer> readComplexClassBinaryName(char[] chars, int startPos) {
    int j = 1;
    while (true) {
      char c2 = chars[startPos + j];
      if (c2 == ';') break;
      j++;
    }
    return Pair.of(String.valueOf(chars).substring(startPos + 1, startPos + j), startPos + j);
  }

  public static class Result {
    private final boolean isArray;
    @Nullable
    private final String complexTypeBinaryName;
    @Nullable
    private final JvmType primaryType;

    public Result(boolean isArray, @Nullable String complexTypeBinaryName,
        @Nullable JvmType primaryType) {
      assert Objects.isNull(complexTypeBinaryName) ^ Objects.isNull(primaryType);
      this.isArray = isArray;
      this.complexTypeBinaryName = complexTypeBinaryName;
      this.primaryType = primaryType;
    }
  }
}
