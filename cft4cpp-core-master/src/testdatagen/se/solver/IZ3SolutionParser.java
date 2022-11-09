package testdatagen.se.solver;

import cfg.testpath.IStaticSolutionGeneration;
import interfaces.IGeneration;

public interface IZ3SolutionParser extends IGeneration {

    String NO_SOLUTION = IStaticSolutionGeneration.NO_SOLUTION;
    String KHAI_BAO = "define-fun";
    String IF_THEN_ELSE = "ite";
    String END = ")";
    String MODEL = "model";
    String SAT = "sat";
    String UNSAT = "unsat";
    String UNKNOWN = "unknown";
    int KHAI_BAO_ID = 0;
    int MODEL_ID = -1;
    int IF_THEN_ELSE_ID = 1;
    int VALUE_ID = 2;
    int END_ID = 3;
    int SAT_ID = 4;
    int UNSAT_ID = 5;
    int UNKNOWN_ID = 6;
    int ERROR = 7;

    String getSolution(String Z3Solution);

}