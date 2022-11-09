package tree.object;

import java.io.File;

import config.Paths;
import parser.projectparser.ProjectParser;
import utils.search.Search;
import utils.search.StructNodeCondition;

/**
 * Represent a constructor
 *
 * @author DucAnh
 */
public class ConstructorNode extends AbstractFunctionNode {

    public static void main(String[] args) {
        ProjectParser parser = new ProjectParser(new File(Paths.SAMPLE01), null);
        StructureNode structureNode = (StructureNode) Search
                .searchNodes(parser.getRootTree(), new StructNodeCondition(), "Node").get(0);

        structureNode.getConstructors().get(0);
    }

}
