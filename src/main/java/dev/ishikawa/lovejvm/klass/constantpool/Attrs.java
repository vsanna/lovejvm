package dev.ishikawa.lovejvm.klass.constantpool;

import dev.ishikawa.lovejvm.klass.LAttr;

import java.util.List;
import java.util.stream.Collectors;

public class Attrs {
    private final int size;
    private List<LAttr> attrs;

    public Attrs(int size, List<LAttr> attrs) {
        if(size != attrs.size()) throw new RuntimeException("invalid attrs. the size doesn't match with num of entries");

        this.size = size;
        this.attrs = attrs;
    }

    // > If no conditions are specified, then the attribute may appear any number of times in an attributes table.
    public List<LAttr> findAllBy(AttrName attrName) {
        return attrs.stream()
                .filter((it) -> AttrName.findBy(it.getAttrName().getLabel()) == attrName)
                .collect(Collectors.toList());
    }
}
