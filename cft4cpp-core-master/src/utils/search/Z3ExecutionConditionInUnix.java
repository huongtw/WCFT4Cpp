package utils.search;

import java.io.File;

import config.AbstractSetting;
import config.ISettingv2;
import tree.object.INode;
import tree.object.UnknowObjectNode;

public class Z3ExecutionConditionInUnix extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof UnknowObjectNode && (new File(n.getAbsolutePath()).canExecute())
                && n.getAbsolutePath().endsWith(File.separator + AbstractSetting.getValue(ISettingv2.SOLVER_Z3_NAME)))
            return true;
        return false;
    }

}
