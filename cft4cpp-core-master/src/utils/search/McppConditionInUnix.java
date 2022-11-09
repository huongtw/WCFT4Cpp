package utils.search;

import java.io.File;

import config.AbstractSetting;
import config.ISettingv2;
import tree.object.INode;
import tree.object.UnknowObjectNode;

public class McppConditionInUnix extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof UnknowObjectNode && (new File(n.getAbsolutePath()).canExecute())
                && n.getAbsolutePath().endsWith(File.separator + AbstractSetting.getValue(ISettingv2.MCPP_NAME)))
            return true;
        return false;
    }

}
