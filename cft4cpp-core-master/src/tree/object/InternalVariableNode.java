package tree.object;

/**
 * Represent parameters of function <br/>
 * Ex: "int f(int a, int b)" ----> 2 parameters: a, and b
 *
 * @author DucAnh
 */
public class InternalVariableNode extends VariableNode {
	@Override
	public String getAbsolutePath() {
		return "";
	}
}
