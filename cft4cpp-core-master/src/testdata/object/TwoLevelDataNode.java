package testdata.object;

import testdatagen.testdatainit.VariableTypes;
import utils.SpecialCharacter;
import utils.Utils;

public class TwoLevelDataNode extends PointerDataNode {

    /**
     * The size of allocated memory ([allocatedSizeA][allocatedSizeB])
     */
    private int allocatedSizeA;
    private int allocatedSizeB;

    @Override
    public String getInputForGoogleTest() throws Exception {
        String input = "";
        if (!this.isAttribute()) {
            String type = VariableTypes
                    .deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));
            String coreType = type.replace(IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR, "");
            String allocation = "";

            if (getExternelVariable() == true) {
                type = "";
            }

            if (this.isNotNull()) {
                /*
				 * For example,
				 * 
				 * int** p = new int*[3];
				 * 
				 * int* p1 = new int[3]; p[0] = p1;
				 * 
				 * int* p2 = new int[3]; p[1] = p2;
				 * 
				 * int* p3 = new int[3]; p[2] = p3; </pre>
				 */
                allocation = String.format(
                        "%s %s = new %s" + IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR + "[%s]"
                                + SpecialCharacter.END_OF_STATEMENT,
                        type, this.getVituralName(), coreType, this.getAllocatedSizeA());

                String[] elementsAllocation = new String[this.getAllocatedSizeB()];
                for (int i = 0; i < this.getAllocatedSizeB(); i++) {

                    String nameElement = this.getVituralName() + i;
                    elementsAllocation[i] = String.format(
                            "%s" + IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR + " %s = new %s[%s]"
                                    + SpecialCharacter.END_OF_STATEMENT,
                            coreType, nameElement, coreType, this.getAllocatedSizeB());

                    elementsAllocation[i] += this.getVituralName() + Utils.asIndex(i) + "=" + nameElement
                            + SpecialCharacter.END_OF_STATEMENT;

                }
                for (String elementAllocation : elementsAllocation)
                    allocation += elementAllocation;

            } else
                allocation = String.format("%s %s = " + IAbstractDataNode.NULL + SpecialCharacter.END_OF_STATEMENT,
                        type, this.getVituralName());
            input += allocation;

        } else {
            String type = VariableTypes
                    .deleteStorageClasses(this.getType().replace(IAbstractDataNode.REFERENCE_OPERATOR, ""));
            String coreType = type.replace(IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR, "");

            if (this.isNotNull())
                input += this.getSetterInStr("new " + coreType + IAbstractDataNode.ONE_LEVEL_POINTER_OPERATOR
                        + Utils.asIndex(this.getAllocatedSizeA())) + SpecialCharacter.END_OF_STATEMENT;
            else
                input += this.getSetterInStr(IAbstractDataNode.NULL) + SpecialCharacter.END_OF_STATEMENT;
        }

        input += super.getInputForGoogleTest();
        return input;
    }

    public boolean isNotNull() {
        return this.allocatedSizeA >= 1 && this.allocatedSizeB >= 1;
    }

    public int getAllocatedSizeA() {
        return this.allocatedSizeA;
    }

    public void setAllocatedSizeA(int allocatedSizeA) {
        this.allocatedSizeA = allocatedSizeA;
    }

    public int getAllocatedSizeB() {
        return this.allocatedSizeB;
    }

    public void setAllocatedSizeB(int allocatedSizeB) {
        this.allocatedSizeB = allocatedSizeB;
    }
}
