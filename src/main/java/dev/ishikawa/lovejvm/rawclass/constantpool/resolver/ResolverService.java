package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantDynamic;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantFieldref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantInterfaceMethodref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantInvokeDynamic;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodHandle;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodType;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantModule;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPackage;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolEntry;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantString;
import java.util.Map;
import java.util.Optional;

public class ResolverService implements Resolver<ConstantPoolEntry> {
  public static final ResolverService INSTANCE = new ResolverService();

  private ResolverService() {}

  private final Map<Class, Resolver> resolverMap =
      Map.ofEntries(
          Map.entry(ConstantClass.class, new ConstClassResolver()),
          Map.entry(ConstantDynamic.class, new ConstantDynamicResolver()),
          Map.entry(ConstantFieldref.class, new ConstantFieldrefResolver()),
          Map.entry(ConstantInterfaceMethodref.class, new ConstantInterfaceMethodrefResolver()),
          Map.entry(ConstantInvokeDynamic.class, new ConstantInvokeDynamicResolver()),
          Map.entry(ConstantMethodHandle.class, new ConstantMethodHandleResolver()),
          Map.entry(ConstantMethodref.class, new ConstantMethodrefResolver()),
          Map.entry(ConstantMethodType.class, new ConstantMethodTypeResolver()),
          Map.entry(ConstantModule.class, new ConstantModuleResolver()),
          Map.entry(ConstantNameAndType.class, new ConstantNameAndTypeResolver()),
          Map.entry(ConstantPackage.class, new ConstPackageResolver()),
          Map.entry(ConstantString.class, new ConstStringResolver()));

  @Override
  public void resolve(ConstantPool constantPool, ConstantPoolEntry entry) {
    if (entry.isResolved()) {
      return;
    }
    getResolver(entry).resolve(constantPool, entry);
  }

  private Resolver getResolver(ConstantPoolEntry entry) {
    return Optional.ofNullable(resolverMap.get(entry.getClass()))
        .orElseThrow(() -> new RuntimeException("resolver is not found"));
  }
}
