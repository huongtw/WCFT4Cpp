package tree.object;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTranslationUnit;

import tree.dependency.Dependency;
import tree.dependency.IncludeHeaderDependency;

public class CppFileNode extends SourcecodeFileNode<CPPASTTranslationUnit> {

    public CppFileNode() {
        try {
            Icon ICON_CPP = new ImageIcon(Node.class.getResource("/image/node/Soucecode-Cpp.png"));
            setIcon(ICON_CPP);
        } catch (Exception e) {
        }
    }

    public List<Dependency> getIncludeHeaderNodes() {
        List<Dependency> includedDependencies = new ArrayList<>();
        for (Dependency dependency : getDependencies())
            if (dependency instanceof IncludeHeaderDependency)
                includedDependencies.add(dependency);
        return includedDependencies;
    }

}
