package testdatagen.se.solver;

public interface ISmtLibGeneration {
	static final String EMPTY_SMT_LIB_FILE = "";
	static final String OPTION_TIMEOUT = "(set-option :timeout 5000)";
	static final String SOLVE_COMMAND = "(check-sat)(get-model)";

	String getSmtLibContent();

	void generate() throws Exception;
}