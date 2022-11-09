package parser.projectparser;

import java.io.File;

import interfaces.IGeneration;
import tree.object.IProcessNotify;
import tree.object.IProjectNode;

/**
 * Parse the given project down to method, parameter, attribute level
 *
 * @author ducanh
 */
public interface IProjectParser extends IGeneration {

    IProcessNotify getNotify();

    void setNotify(IProcessNotify notify);

    /**
     * Get the path of the given C/C++ project
     *
     * @return
     */
    File getProjectPath();

    void setProjectPath(File projectPath);

    /**
     * Get the root of the structure tree
     *
     * @return
     */
    IProjectNode getRootTree();

}
