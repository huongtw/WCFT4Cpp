package cfg.testpath;

import java.util.ArrayList;

public class Testpaths<N extends ITestpathInCFG> extends ArrayList<N> implements ITestpaths {

    /**
     *
     */
    private static final long serialVersionUID = 4098572130931158726L;

    @Override
    public ITestpathInCFG getLongestTestpath() {
        ITestpathInCFG longestTp = get(0);
        int lengthTp = longestTp.getRealSize();
        for (int i = 1; i < size(); i++)
            if (get(i).getRealSize() > lengthTp) {
                lengthTp = get(i).getRealSize();
                longestTp = get(i);
            }
        return longestTp;
    }

    @Override
    public boolean equals(Object o) {
        Testpaths<ITestpathInCFG> tp = (Testpaths<ITestpathInCFG>) o;
        if (size() == tp.size()) {
            for (int i = 0; i < size(); i++)
                if (!get(i).equals(tp.get(i)))
                    return false;
            return true;
        } else
            return false;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof ITestpaths) {
            Testpaths<ITestpathInCFG> tp = (Testpaths<ITestpathInCFG>) o;
            if (size() >= tp.size()) {
                for (ITestpathInCFG item : tp)
                    if (!this.contains(item))
                        return false;
                return true;
            } else
                return false;

        } else if (o instanceof ITestpathInCFG) {
            AbstractTestpath tp = (AbstractTestpath) o;
            for (ITestpathInCFG item : this)
                if (item.equals(tp))
                    return true;
            return false;
        } else
            return false;
    }

    @Override
    public Testpaths cast() {
        return this;
    }
}
