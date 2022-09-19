package dev.ishikawa.lovejvm.klass.constantpool;

import dev.ishikawa.lovejvm.klass.LAttr;
import dev.ishikawa.lovejvm.klass.LMethod;

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

    public int size() {
        return 2 // num to show how many attrs it has
                + attrs.stream().map(LAttr::size).reduce(0, Integer::sum);
    }

    public int offsetToAttr(AttrName attrName) {
        // TODO: check if the attrName is contained or not. if not, throw error

        int result = 2;

        for (LAttr attr: attrs) {
            // TODO: support having two entries with the same attrName in a attr table
            if(AttrName.findBy(attr.getAttrName().getLabel()) != attrName) {
                result += attr.size();
            } else {
                break;
            }
        }

        return result;
    }
}
