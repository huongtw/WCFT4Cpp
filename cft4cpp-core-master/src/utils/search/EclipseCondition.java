package utils.search;

import java.io.File;

import config.AbstractSetting;
import config.ISettingv2;
import tree.object.ExeNode;
import tree.object.INode;
import tree.object.UnknowObjectNode;
import utils.Utils;

public class EclipseCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (Utils.isUnix()) {
            if (n instanceof UnknowObjectNode && (new File(n.getAbsolutePath()).canExecute())
                    && n.getAbsolutePath().endsWith(File.separator + AbstractSetting.getValue(ISettingv2.ECLIPSE_NAME)))
                return true;
        } else if (Utils.isWindows()) {
            if (n instanceof ExeNode && (new File(n.getAbsolutePath()).getName()
                    .equals(AbstractSetting.getValue(ISettingv2.ECLIPSE_NAME))))
                return true;
        }
        return false;
    }

}
