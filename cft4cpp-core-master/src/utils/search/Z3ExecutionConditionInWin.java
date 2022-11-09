package utils.search;

import java.io.File;

import config.AbstractSetting;
import config.ISettingv2;
import tree.object.ExeNode;
import tree.object.INode;

public class Z3ExecutionConditionInWin extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof ExeNode
                && n.getAbsolutePath().endsWith(File.separator + AbstractSetting.getValue(ISettingv2.SOLVER_Z3_NAME)))
            return true;
        return false;
    }
}
