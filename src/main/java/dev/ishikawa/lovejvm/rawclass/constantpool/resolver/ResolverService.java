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
import dev.ishikawa.lovejvm.vm.RawSystem;
import java.util.Map;
import java.util.Optional;

public class ResolverService implements Resolver<ConstantPoolEntry> {
  private final Map<Class, Resolver> resolverMap;

  public ResolverService(RawSystem rawSystem) {
    this.resolverMap = Map.ofEntries(
        Map.entry(ConstantClass.class, new ConstClassResolver(rawSystem)),
        Map.entry(ConstantDynamic.class, new ConstantDynamicResolver(rawSystem)),
        Map.entry(ConstantFieldref.class, new ConstantFieldrefResolver(rawSystem)),
        Map.entry(ConstantInterfaceMethodref.class, new ConstantInterfaceMethodrefResolver(rawSystem)),
        Map.entry(ConstantInvokeDynamic.class, new ConstantInvokeDynamicResolver(rawSystem)),
        Map.entry(ConstantMethodHandle.class, new ConstantMethodHandleResolver(rawSystem)),
        Map.entry(ConstantMethodref.class, new ConstantMethodrefResolver(rawSystem)),
        Map.entry(ConstantMethodType.class, new ConstantMethodTypeResolver(rawSystem)),
        Map.entry(ConstantModule.class, new ConstantModuleResolver(rawSystem)),
        Map.entry(ConstantNameAndType.class, new ConstantNameAndTypeResolver(rawSystem)),
        Map.entry(ConstantPackage.class, new ConstPackageResolver(rawSystem)),
        Map.entry(ConstantString.class, new ConstStringResolver(rawSystem))
    );
  }

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
