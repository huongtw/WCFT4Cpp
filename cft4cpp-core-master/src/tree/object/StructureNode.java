package tree.object;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompositeTypeSpecifier;

import config.Paths;
import parser.projectparser.ProjectParser;
import tree.dependency.Dependency;
import tree.dependency.ExtendDependency;
import utils.ASTUtils;
import utils.search.ClassNodeCondition;
import utils.search.Search;

/**
 * Represent Class, Struct
 *
 * @author DucAnh
 */
public abstract class StructureNode extends CustomASTNode<IASTSimpleDeclaration> {

	protected List<String> extendedNames = new ArrayList<>();

	protected ArrayList<ArrayList<INode>> extendPaths = new ArrayList<>();

	public static void main(String[] args) {
		ProjectParser parser = new ProjectParser(new File(Paths.COMBINED_STATIC_AND_DYNAMIC_GENERATION), null);
		ClassNode structureNode = (ClassNode) Search
				.searchNodes(parser.getRootTree(), new ClassNodeCondition(), "SinhVien").get(0);

		System.out.println(structureNode.findAttributeByName("other[0].eeee").getAbsolutePath());
		System.out.println(structureNode.findAttributeByName("other").getAbsolutePath());
		System.out.println(structureNode.findAttributeByName("other[0]"));
		System.out.println(structureNode.findAttributeByName("other[0].eeee[0]"));
	}

	private List<String> getExtendNames() {
		List<String> output = new ArrayList<>();

		IASTDeclSpecifier d = AST.getDeclSpecifier();
		ICPPASTDeclSpecifier d1 = (ICPPASTDeclSpecifier) d;

		if (d1 instanceof CPPASTCompositeTypeSpecifier)
			for (ICPPASTBaseSpecifier b : ((CPPASTCompositeTypeSpecifier) d1).getBaseSpecifiers())
				output.add(b.getNameSpecifier().getRawSignature());

		return output;
	}

	private void getExtendPaths(INode n, ArrayList<INode> path) {
		path.add(n);

		List<INode> extendedNodes = ((StructureNode) n).getExtendNodes();
		if (extendedNodes.size() > 0)
			for (INode child : extendedNodes)
				this.getExtendPaths(child, path);
		else
			extendPaths.add((ArrayList<INode>) path.clone());
		path.remove(path.size() - 1);
	}

	/**
	 * Find all functions declared in the structure node <br/>
	 * Ex:<br/>
	 * class A{<br/>
	 * void x(int a){...}<br/>
	 * void y(int a){...}<br/>
	 * } <br/>
	 * 
	 * With the given name "x", it returns the node of the function "x(int)"
	 * 
	 * @param simpleName
	 *            the name of the function
	 * @return
	 */
	public List<FunctionNode> findFunctionsBySimpleName(String simpleName) {
		List<FunctionNode> output = new ArrayList<>();
		for (INode child : getChildren())
			if (child instanceof FunctionNode) {
				FunctionNode n = (FunctionNode) child;
				if (n.getSimpleName().equals(simpleName))
					output.add(n);
			}
		return output;
	}

	/**
	 * Get all attributes of the given structure node Ex: <br/>
	 * class A{int a; int b;...} <br/>
	 * -----------------------> return "a", "b"
	 * 
	 * @return
	 */
	public ArrayList<IVariableNode> getAttributes() {
		ArrayList<IVariableNode> attributes = getPrivateAttributes();
		attributes.addAll(getPublicAttributes());
		return attributes;
	}

	public ArrayList<Node> getConstructors() {
		ArrayList<Node> methods = new ArrayList<>();
		for (INode node : getChildren())
			if (node instanceof ConstructorNode) {
				ConstructorNode f = (ConstructorNode) node;
				IASTFunctionDefinition ast = f.getAST();

				IASTDeclSpecifier decl = ast.getDeclSpecifier();
				IASTFunctionDeclarator declarator = ast.getDeclarator();
				IASTNode firstChildOfDeclarator = declarator.getChildren()[0];

				/**
				 * if it is constructor/destructor class/structure
				 */
				if (decl.getRawSignature().equals("") && firstChildOfDeclarator.getRawSignature().equals(getNewType()))
					methods.add(f);
			}
		return methods;
	}

	public List<INode> getExtendNodes() {
		List<INode> extendedNode = new ArrayList<>();
		for (Dependency d : getDependencies())
			if (d instanceof ExtendDependency && d.getStartArrow().equals(this))
				extendedNode.add(d.getEndArrow());
		return extendedNode;
	}

	public ArrayList<ArrayList<INode>> getExtendPaths() {
		ArrayList<INode> path = new ArrayList<>();
		this.getExtendPaths(this, path);
		return extendPaths;
	}

	public List<String> getExtendedNames() {
		return extendedNames;
	}

	public ArrayList<IVariableNode> getPrivateAttributes() {
		ArrayList<IVariableNode> attributes = new ArrayList<>();
		for (INode node : getChildren())
			if (node instanceof IVariableNode && ((VariableNode) node).isPrivate)
				attributes.add((VariableNode) node);
		return attributes;
	}

	public ArrayList<IVariableNode> getPublicAttributes() {
		ArrayList<IVariableNode> attributes = new ArrayList<>();
		for (INode node : getChildren())
			if (node instanceof IVariableNode && !((VariableNode) node).isPrivate)
				attributes.add((IVariableNode) node);
		return attributes;
	}

	@Override
	public void setAST(IASTSimpleDeclaration aST) {
		super.setAST(aST);
		extendedNames = getExtendNames();
	}

	/**
	 * Two cases might happen. <br/>
	 * <ol>
	 * <li>Case 1. <b>name</b> is an attribute, it therefore returns the node
	 * corresponding to this attribute <br/>
	 * Ex:<i> class A{B* x;} class B{C* y;}<br/>
	 * Consider class A, if name = "x.y" ----------output---------> attribute "C*
	 * y"</i></li>
	 * 
	 * 
	 * <li>Case 2. <b>name</b> is an element of pointer/array attribute, <br/>
	 * - If the kind of the attribute is basic (e.g., int*, int[], char*, v.v.), it
	 * return an instance of @see{AvailableTypeNode.class} <br/>
	 * Ex: <i>class A{int* x;}<br/>
	 * Consider class A, if name = "x[0]" ----------output---------> "int" </i><br/>
	 * <br/>
	 * - Otherwise, it returns the definition of attribute element.<br/>
	 * Ex: <i>class A{B* x;}class B{C* y;}<br/>
	 * Consider class A, if name = "x.y[1]" ----------output---------> definition of
	 * class C</i></li>
	 * </ol>
	 * 
	 * @param name
	 *            Ex1: name. Ex2: name[0]. Ex3: sv.other[0].name
	 * @return
	 */
	public INode findAttributeByName(String name) {
		IASTNode astName = ASTUtils.convertToIAST(name);

		/**
		 * STEP 1: Get all elements in the given name
		 * 
		 * Ex: "other[0].name" --------------> 3 elements: "other, [0], name"
		 */
		List<String> elements = new ArrayList<>();
		boolean stop = false;
		while (!stop) {
			// Ex: "other[0]"
			if (astName instanceof ICPPASTArraySubscriptExpression) {
				// Add "[0]" to the list
				elements.add(0, "[" + astName.getChildren()[astName.getChildren().length - 1].getRawSignature() + "]");
				astName = astName.getChildren()[0];
			} else
			// Ex: "other"
			if (astName instanceof IASTIdExpression) {
				elements.add(0, astName.getRawSignature());
				stop = true;
			} else
			// Ex: "other[0].name"
			if (astName instanceof IASTFieldReference) {
				// Add "name" to the list
				elements.add(0, astName.getChildren()[1].getRawSignature());

				astName = astName.getChildren()[0];
			}
		}
		// STEP 2. Parse elements
		INode currentNode = this;
		stop = false;
		final String ONE_LEVEL_POINTER = "*";
		while (elements.size() > 0 && !stop) {
			String item = elements.get(0);
			elements.remove(0);

			// Case 2: Access the element of an attribute
			if (item.startsWith("[")) {

				if (currentNode instanceof IVariableNode) {
					IVariableNode castCurrentNode = (IVariableNode) currentNode;
					currentNode = castCurrentNode.resolveCoreType();

					if (currentNode instanceof AvailableTypeNode) {
						String type = ((AvailableTypeNode) currentNode).getType().replace(ONE_LEVEL_POINTER, "");
						currentNode = new AvailableTypeNode();
						((AvailableTypeNode) currentNode).setType(type);
					}
				} else if (currentNode instanceof AvailableTypeNode) {
					String type = ((AvailableTypeNode) currentNode).getType();
					currentNode = new AvailableTypeNode();
					((AvailableTypeNode) currentNode).setType(type.replace(ONE_LEVEL_POINTER, ""));
				}

			} else
			// Case 1
			if (currentNode instanceof StructureNode) {
				StructureNode castCurrentNode = (StructureNode) currentNode;
				for (IVariableNode attribute : castCurrentNode.getAttributes())
					if (attribute.getName().equals(item)) {
						currentNode = attribute;
						break;
					}
			} else
				stop = true;
		}
		// STEP 3.
		// If all elements has been analyzed
		if (elements.size() == 0)
			return currentNode;
		else
			return null;
	}
}
