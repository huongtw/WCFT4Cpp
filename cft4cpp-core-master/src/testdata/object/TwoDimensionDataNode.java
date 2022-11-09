package testdata.object;

import java.util.List;

import testdatagen.testdatainit.VariableTypes;
import utils.SpecialCharacter;
import utils.Utils;

/**
 * Represent variable as two dimension array
 *
 * @author ducanhnguyen
 */
public class TwoDimensionDataNode extends ArrayDataNode {
    /**
     * The size of variable, [sizeA][sizeB]
     */
    private int sizeA;
    private int sizeB;

    @Override
    public String getInputForGoogleTest() throws Exception {
        String input = "";
        String initialization = "";
        String declaration = "";

        String type = VariableTypes
                .deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));
        String coreType = type.replaceAll("\\[.*\\]", "");
        List<String> indexes = Utils.getIndexOfArray(type);
        if (getExternelVariable() == true) {
            type = "";
        }

		/*
         * Get the declaration of variable. Ex: "int a[3][3];"
		 */
        if (indexes.size() > 0) {
            String dimension = "";
            for (String index : indexes)
                if (index.length() == 0)
					/*
					 * Choose a random size in pair (sizeA, sizeB). We choose
					 * sizeA
					 */
                    dimension += Utils.asIndex(this.getSizeA());
                else
                    dimension += Utils.asIndex(index);

            declaration = String.format("%s %s%s" + SpecialCharacter.END_OF_STATEMENT, coreType, this.getVituralName(),
                    dimension);
        } else
            declaration = String.format("%s %s[%s][%s]" + SpecialCharacter.END_OF_STATEMENT, coreType,
                    this.getVituralName(), this.getSizeA(), this.getSizeB());

		/*
		 * Get the initialization of its children.
		 */
        for (IAbstractDataNode child : this.getChildren())
            initialization += child.getInputForGoogleTest();
		/*
		 * Create complete input
		 */
        input = declaration + SpecialCharacter.END_OF_STATEMENT + initialization;

        if (this.isAttribute())
            input += this.getSetterInStr(this.getVituralName()) + SpecialCharacter.END_OF_STATEMENT;
        return input;
    }

    public boolean canConvertToString() {
        return false;
    }

    public int getSizeA() {
        return this.sizeA;
    }

    public void setSizeA(int sizeA) {
        this.sizeA = sizeA;
    }

    public int getSizeB() {
        return this.sizeB;
    }

    public void setSizeB(int sizeB) {
        this.sizeB = sizeB;
    }
}
