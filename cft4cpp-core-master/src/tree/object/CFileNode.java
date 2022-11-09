package tree.object;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.eclipse.cdt.internal.core.dom.parser.c.CASTTranslationUnit;

import tree.dependency.Dependency;
import tree.dependency.IncludeHeaderDependency;

public class CFileNode extends SourcecodeFileNode<CASTTranslationUnit> {

    public CFileNode() {
        try {
            Icon ICON_C = new ImageIcon(Node.class.getResource("/image/node/Soucecode-C.png"));
            setIcon(ICON_C);
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
