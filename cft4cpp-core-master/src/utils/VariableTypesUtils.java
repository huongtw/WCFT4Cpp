package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import config.Paths;
import parser.projectparser.ProjectParser;
import testdatagen.testdatainit.VariableTypes;
import tree.object.EnumNode;
import tree.object.INode;
import tree.object.TypedefDeclaration;
import utils.search.EnumNodeCondition;
import utils.search.Search;
import utils.search.SearchCondition;
import utils.search.TypedefNodeCondition;

/**
 * Created by DucToan on 27/07/2017
 */
public class VariableTypesUtils {
    public static boolean isEnumNode(String type) {
        ProjectParser parser = new ProjectParser(new File(Paths.CURRENT_PROJECT.CLONE_PROJECT_PATH));

        List<SearchCondition> conditions = new ArrayList<>();
        conditions.add(new EnumNodeCondition());

        List<INode> mydefines = Search.searchNodes(parser.getRootTree(), conditions);

        for (INode mydefine : mydefines) {
            if (mydefine.getNewType().equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static EnumNode findEnumNode(String type) {
        ProjectParser parser = new ProjectParser(new File(Paths.CURRENT_PROJECT.CLONE_PROJECT_PATH));

        List<SearchCondition> conditions = new ArrayList<>();
        conditions.add(new EnumNodeCondition());

        List<INode> mydefines = Search.searchNodes(parser.getRootTree(), conditions);

        for (INode mydefine : mydefines) {
            if (mydefine.getNewType().equals(type)) {
                return (EnumNode) mydefine;
            }
        }

        return null;
    }

    public static boolean isDefineNode(String type) {
        ProjectParser parser = new ProjectParser(new File(Paths.CURRENT_PROJECT.CLONE_PROJECT_PATH));

        List<SearchCondition> conditions = new ArrayList<>();
        conditions.add(new TypedefNodeCondition());

        List<INode> mydefines = Search.searchNodes(parser.getRootTree(), conditions);

        for (INode mydefine : mydefines) {
            if (mydefine.getNewType().equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isDefineNodeOfBasicType(String type) {
        ProjectParser parser = new ProjectParser(new File(Paths.CURRENT_PROJECT.CLONE_PROJECT_PATH));

        List<SearchCondition> conditions = new ArrayList<>();
        conditions.add(new TypedefNodeCondition());

        List<INode> mydefines = Search.searchNodes(parser.getRootTree(), conditions);

        for (INode mydefine : mydefines) {
            if (mydefine.getNewType().equals(type)) {
                String oldType = ((TypedefDeclaration) mydefine).getOldType();
                if (VariableTypes.isBasic(oldType))
                    return true;
            }
        }

        return false;
    }
}
