package parser.makefile.object;

import config.AbstractSetting;
import config.ISettingv2;
import tree.object.ExeNode;
import tree.object.INode;
import utils.search.SearchCondition;

/**
 * �?ại diện tệp g++.exe
 *
 * @author DucAnh
 */
public class MakeExeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof ExeNode && n.getNewType().equals(AbstractSetting.getValue(ISettingv2.GNU_MAKE_NAME)))
            return true;
        return false;
    }

}
