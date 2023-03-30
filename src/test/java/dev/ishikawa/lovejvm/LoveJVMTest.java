package dev.ishikawa.lovejvm;

import dev.ishikawa.lovejvm.option.Options;
import dev.ishikawa.lovejvm.option.OptionsParser;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;
import org.junit.jupiter.api.Test;

public class LoveJVMTest {

  @Test
  void test() {

    Options options = OptionsParser.parse(new String[]{"hoge"});
    LoveJVM jvm = new LoveJVM(options);
    jvm.run();

    List<Word> output = jvm.getRawSystem().getMainThread().getOutput();
  }
}
