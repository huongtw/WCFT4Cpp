package utils.tostring;

import java.io.File;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import config.Paths;
import parser.projectparser.ProjectLoader;
import tree.object.INode;
import tree.object.IProjectNode;

/**
 * Generate structure to display top-down tree
 *
 * @author ducanhnguyen
 */
public class StructureTreeDisplayer extends ToString {

    public StructureTreeDisplayer(INode root) {
        super(root);
    }

    public static void main(String[] args) {
        ProjectLoader loader = new ProjectLoader();
        IProjectNode projectRootNode = loader.load(new File(Paths.SIMBOLIC_EXECUTION_VS));

        /**
         * display tree of project
         */
        StructureTreeDisplayer treeDisplayer = new StructureTreeDisplayer(projectRootNode);
        System.out.println(treeDisplayer.getTreeInString());
    }

    @Override
    public String toString(INode n) {
        JSONObject obj = generateJson(n);
        return obj.toJSONString();
    }

    private JSONObject generateJson(INode n) {
        JSONObject jsonNode = new JSONObject();

        jsonNode.put("name", n.getNewType());
        jsonNode.put("id", n.getId());

        JSONArray jsonChildren = new JSONArray();
        for (INode child : n.getChildren())
            jsonChildren.add(generateJson(child));

        jsonNode.put("children", jsonChildren);

        return jsonNode;
    }
}
