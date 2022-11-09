package tree.object;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;

import testdatagen.testdatainit.VariableTypes;
import tree.dependency.Dependency;
import tree.dependency.GetterDependency;
import tree.dependency.SetterDependency;
import tree.dependency.TypeDependencyGeneration;
import utils.Utils;

/**
 * Represent attribute of class/struct/union/enum, arguments of a function,
 * global variables
 *
 * @author DucAnh
 */
public class VariableNode extends CustomASTNode<IASTNode> implements IVariableNode {

	/**
	 * Raw type (content of declaration - name of variable)
	 */
	protected String rawType = "";

	/**
	 * Remove storage class, &, * from raw type
	 */
	protected String coreType = "";

	/**
	 * Remove storage class including static, register, extern, mutable, const from
	 * raw type
	 */
	protected String reducedRawType = "";

	protected String[] arrayModifier;

	protected int levelOfPointer = 0;

	protected boolean isReference = false;

	protected boolean isPrivate = false;

	List<INode> visitedNodes = new ArrayList<>();

	/**
	 * Delete storage class including static, register, extern, mutable + const in
	 * rawType
	 *
	 * @param str
	 * @param type
	 * @return
	 */
	private String deleteStorageClass(String str, IASTDeclSpecifier type) {
		/**
		 * Xóa const
		 */
		if (type.isConst())
			str = str.replaceAll("^const\\s*", "");
		/**
		 * Xóa Storage Classes
		 */
		switch (type.getStorageClass()) {
		case IASTDeclSpecifier.sc_static:
			str = str.replaceAll("^static\\s*", "");
			break;
		case IASTDeclSpecifier.sc_register:
			str = str.replaceAll("^register\\s*", "");
			break;
		case IASTDeclSpecifier.sc_extern:
			str = str.replaceAll("^extern\\s*", "");
			break;
		case IASTDeclSpecifier.sc_mutable:
			str = str.replaceAll("^mutable\\s*", "");
			break;
		}
		return str;
	}

	@Override
	public List<String[]> getAllAttributes(INode n, int level) {
		List<String[]> attributes = new ArrayList<>();
		if (visitedNodes.contains(n)) {
			// nothing to do
		} else {
			visitedNodes.add(n);

			if (n instanceof VariableNode) {
				INode resolvedNode = ((IVariableNode) n).resolveCoreType();
				if (resolvedNode instanceof StructureNode) {
					StructureNode tmp = (StructureNode) resolvedNode;
					attributes.add(
							new String[] { getPrefixSpace(level) + n.getNewType(), ((IVariableNode) n).getRawType() });
					attributes.addAll(getAllAttributes(tmp, ++level));
					level--;
				} else {
					attributes.add(
							new String[] { getPrefixSpace(level) + n.getNewType(), ((IVariableNode) n).getRawType() });
					level--;
				}

			} else if (n instanceof StructureNode)
				for (IVariableNode item : ((StructureNode) n).getAttributes()) {
					attributes.addAll(getAllAttributes(item, ++level));
					level--;
				}
		}
		return attributes;
	}

	@Override
	public IASTDeclarator getASTDecName() {
		IASTNode ast = getAST();

		if (ast instanceof IASTSimpleDeclaration)
			return ((IASTSimpleDeclaration) ast).getDeclarators()[0];

		if (ast instanceof IASTParameterDeclaration)
			return ((IASTParameterDeclaration) ast).getDeclarator();

		return null;
	}

	@Override
	public IASTDeclSpecifier getASTType() {
		IASTNode ast = getAST();

		if (ast instanceof IASTSimpleDeclaration)
			return ((IASTSimpleDeclaration) ast).getDeclSpecifier();

		if (ast instanceof IASTParameterDeclaration)
			return ((IASTParameterDeclaration) ast).getDeclSpecifier();

		return null;
	}

	@Override
	public String getCoreType() {
		return coreType;
	}

	@Override
	public void setCoreType(String coreType) {
		this.coreType = coreType;
	}

	@Override
	public FunctionNode getGetterNode() {
		for (Dependency d : getDependencies())
			if (d instanceof GetterDependency)
				return (FunctionNode) d.getEndArrow();
		return null;
	}

	@Override
	public int getLevelOfPointer() {
		return levelOfPointer;
	}

	@Override
	public void setLevelOfPointer(int levelOfPointer) {
		this.levelOfPointer = levelOfPointer;
	}

	private String getPrefixSpace(int level) {
		String output = "";
		for (int i = 0; i < level; i++)
			output += "   ";
		return output;
	}

	@Override
	public String getFullType() {
		String prefixPath = "";

		INode currentVar = resolveCoreType();

		String realType = getCoreType();
		if (currentVar instanceof AvailableTypeNode)
			realType = ((AvailableTypeNode) currentVar).getType();
		else if (currentVar instanceof IVariableNode)
			realType = ((IVariableNode) currentVar).getCoreType();

		if (VariableTypes.isBasic(realType) || VariableTypes.isOneDimensionBasic(realType)
				|| VariableTypes.isTwoDimensionBasic(realType) || VariableTypes.isOneLevelBasic(realType)
				|| VariableTypes.isTwoLevelBasic(realType))
			prefixPath += getCoreType();
		else {
			INode originalVar = currentVar;

			while (currentVar != null && (currentVar instanceof StructureNode || currentVar instanceof NamespaceNode)) {
				if (prefixPath.length() > 0)
					prefixPath = currentVar.getNewType() + "::" + prefixPath;
				else
					prefixPath = currentVar.getNewType();
				currentVar = currentVar.getParent();
			}
			/*
			 * Add :: in case the scope if file level
			 */
			if (originalVar instanceof StructureNode)
				if (!prefixPath.contains("::"))
					prefixPath = "::" + prefixPath;
		}
		return prefixPath;
	}

	@Override
	public String getRawType() {
		return rawType;
	}

	@Override
	public void setRawType(String rawType) {
		this.rawType = rawType;
	}

	@Override
	public String getRealType() {
		return Utils.getRealType(getRawType(), getParent());
	}

	@Override
	public String getReducedRawType() {
		return reducedRawType;
	}

	@Override
	public void setReducedRawType(String reducedRawType) {
		this.reducedRawType = reducedRawType;
	}

	@Override
	public FunctionNode getSetterNode() {
		for (Dependency d : getDependencies())
			if (d instanceof SetterDependency)
				return (FunctionNode) d.getEndArrow();
		return null;
	}

	@Override
	public int getSizeOfArray() {
		// only for one dimension array
		return Utils.toInt(Utils.getIndexOfArray(getRawType()).get(0));
	}

	@Override
	public boolean isPrivate() {
		return isPrivate;
	}

	@Override
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	@Override
	public boolean isReference() {
		return isReference;
	}

	@Override
	public void setReference(boolean isReference) {
		this.isReference = isReference;
	}

	@Override
	public INode resolveCoreType() {
		INode correspondingNode = new TypeDependencyGeneration(this).getCorrespondingNode();
		return correspondingNode;
	}

	@Override
	public void setAST(IASTNode aST) {
		super.setAST(aST);

		this.setRawType();
		this.setCoreType();
		this.setName();
		this.setReducedRawType();
	}

	@Override
	public void setCoreType() {
		IASTDeclSpecifier type = getASTType();

		/*
		 * Delete two times
		 */
		coreType = deleteStorageClass(rawType, type);
		coreType = deleteStorageClass(coreType, type);

		/*
		 * Remove "union" and "struct" keyword
		 */
		coreType = VariableTypes.deleteUnionKeywork(coreType);
		coreType = VariableTypes.deleteStructKeywork(coreType);

		/*
		 * Handle std::vector<IndividualStore::hoaqua>
		 */
		if (type instanceof IASTNamedTypeSpecifier && coreType.contains(">"))
			coreType = coreType.substring(coreType.indexOf("<") + 1, coreType.indexOf(">"));
	}

	@Override
	public void setName() {
		String name = "";
		if (getAST() instanceof IASTSimpleDeclaration) {
			IASTSimpleDeclaration astNode = (IASTSimpleDeclaration) getAST();
			name = astNode.getDeclarators()[0].getName().toString();

		} else if (getAST() instanceof IASTParameterDeclaration)
			name = ((IASTParameterDeclaration) getAST()).getDeclarator().getName().toString();
		else
			name = getAST().getRawSignature();

		for (IASTNode child : getAST().getChildren())
			if (child instanceof CPPASTDeclarator) {

				int pointerLv = 0;
				for (IASTNode operator : ((IASTDeclarator) child).getPointerOperators())
					if (operator.getRawSignature().equals("&"))
						setReference(true);
					else if (operator.getRawSignature().equals("*"))
						pointerLv++;
				setLevelOfPointer(pointerLv);

				/*
				 *
				 */
				String initializer = "";
				if (((CPPASTDeclarator) child).getInitializer() != null)
					initializer = ((CPPASTDeclarator) child).getInitializer().getRawSignature();

				String type = getRawType() + child.getRawSignature().replace(initializer, "")
						.replaceAll("\\s*" + name + "\\s*", "").replace(" ", "");
				this.setRawType(type);
			}
		super.setName(name);
	}

	@Override
	public void setRawType() {
		rawType = getASTType().getRawSignature().replaceAll("\\s+", " ");
	}

	@Override
	public void setReducedRawType() {
		IASTDeclSpecifier type = getASTType();

		/*
		 * Delete two times
		 */
		reducedRawType = deleteStorageClass(rawType, type);
		reducedRawType = deleteStorageClass(reducedRawType, type);
	}

	@Override
	public String toString() {
		return getNewType();
	}

	@Override
	public boolean isExtern() {
		if (getAST() instanceof CPPASTSimpleDeclaration) {
			IASTDeclSpecifier declSpecifier = ((CPPASTSimpleDeclaration) getAST()).getDeclSpecifier();

			switch (declSpecifier.getStorageClass()) {
			case IASTDeclSpecifier.sc_extern:
				return true;
			}
		}
		return false;
	}

	@Override
	public IASTInitializer getInitializer() {
		if (getAST() instanceof CPPASTSimpleDeclaration) {
			CPPASTSimpleDeclaration ast = (CPPASTSimpleDeclaration) getAST();
			IASTDeclarator[] declarators = ast.getDeclarators();

			if (declarators.length > 0) {
				IASTDeclarator firstDeclarator = declarators[0];
				IASTInitializer initializer = firstDeclarator.getInitializer();
				if (initializer != null)
					return initializer;
			}
		}
		return null;
	}

	@Override
	public INode clone() {
		IVariableNode clone = new VariableNode();
		clone.setAbsolutePath(getAbsolutePath());
		clone.setChildren(getChildren());
		clone.setDependencies(getDependencies());
		clone.setId(getId());
		clone.setName(getNewType());
		clone.setParent(getParent());

		if (getAST() != null)
			clone.setAST(getAST());
		else {
			clone.setRawType(getRawType());
			clone.setCoreType(getCoreType());
			clone.setReducedRawType(getReducedRawType());
		}

		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VariableNode) {
			VariableNode objCast = (VariableNode) obj;
			if (objCast.getRawType().equals(getRawType()) && objCast.getName().equals(getName()))
				return true;
			else
				return false;
		} else
			return false;
	}
}
