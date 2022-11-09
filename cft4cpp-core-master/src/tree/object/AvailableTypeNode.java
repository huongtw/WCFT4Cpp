package tree.object;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;

/**
 * Represent available types such as int, float, char, char16_t, char32_t,
 * wchar_t, signed char, signed short, short int, signed int, v.v.
 * 
 * int*, float*, v.v.
 * 
 * int[], floa[],v.v.
 *
 * @author DucAnh
 */
public class AvailableTypeNode extends CustomASTNode<IASTDeclSpecifier> {
	public String type;

	@Override
	public String toString() {
		return type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
