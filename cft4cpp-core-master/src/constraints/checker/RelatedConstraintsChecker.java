package constraints.checker;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;

import testdatagen.se.IPathConstraints;
import testdatagen.se.PathConstraint;
import testdatagen.se.memory.ISymbolicVariable;
import tree.object.IFunctionNode;
import tree.object.IVariableNode;
import utils.ASTUtils;

/**
 * Check satisfiability of path constraints based on the last condition.
 *
 * @author ducanhnguyen
 */
public class RelatedConstraintsChecker implements IConstraintsChecker {
	final static Logger logger = Logger.getLogger(RelatedConstraintsChecker.class);

	private IPathConstraints constraints;
	private IFunctionNode function;

	public RelatedConstraintsChecker(IPathConstraints constraints, IFunctionNode function) {
		this.constraints = constraints;
		this.function = function;
	}

	@Override
	public boolean check() {
		boolean isRelated = false;

		PathConstraint lastConstraint = constraints.getNormalConstraints()
				.get(constraints.getNormalConstraints().size() - 1);

		IASTNode astLast = ASTUtils.convertToIAST(lastConstraint.getConstraint());
		List<String> usedVariablesInLastCondition = collectUsedVariables(astLast);

		List<String> usedVariables = new ArrayList<>();
		if (constraints.getNumofConditions() == 1)
			isRelated = true;
		else
			for (int i = 0; i < constraints.getNumofConditions() - 1; i++) {
				IASTNode astCon = ASTUtils.convertToIAST(constraints.getElementAt(i));
				if (astCon != null)
					usedVariables = collectUsedVariables(astCon);

				for (String usedVariable : usedVariables)
					if (usedVariablesInLastCondition.contains(usedVariable)) {
						isRelated = true;
						break;
					}

				if (isRelated)
					break;
			}

		if (isRelated) {
			logger.debug(constraints.toString());
		} else
			logger.debug("cut-cut");
		return isRelated;
	}

	private List<String> collectUsedVariables(IASTNode astCondition) {
		// System.out.println(astCondition.getRawSignature());
		List<String> usedVariables = new ArrayList<>();
		ASTVisitor visitor = new ASTVisitor() {

			@Override
			public int visit(IASTExpression statement) {
				if (statement instanceof CPPASTIdExpression) {
					// System.out.println(":" + statement.getRawSignature());
					for (IVariableNode variable : function.getPassingVariables()) {
						// All variables in constraints are started with prefix,
						// e.g., tvw
						if (statement.getRawSignature()
								.equals(ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + variable.getName())) {

							usedVariables.add(variable.getName());
							return ASTVisitor.PROCESS_SKIP;
						}
					}
					return ASTVisitor.PROCESS_CONTINUE;
				} else
					return ASTVisitor.PROCESS_CONTINUE;
			}
		};
		visitor.shouldVisitStatements = true;
		visitor.shouldVisitExpressions = true;
		astCondition.accept(visitor);
		// System.out.println(usedVariables + "\n\n\n");
		return usedVariables;
	}

	public IPathConstraints getConstraints() {
		return constraints;
	}

	public void setConstraints(IPathConstraints constraints) {
		this.constraints = constraints;
	}

}
