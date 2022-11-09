package testdatagen.se.memory;

public class LogicCell {
    private PhysicalCell physicalCell;

    /**
     * The location of the physical cell in its block
     */
    private String index;

    public LogicCell(PhysicalCell physicalCell, String index) {
        this.physicalCell = physicalCell;
        this.index = index;
    }

    public PhysicalCell getPhysicalCell() {
        return physicalCell;
    }

    public void setCell(PhysicalCell physicalCell) {
        this.physicalCell = physicalCell;
    }

    public String getIndex() {
        return index;
    }

    /**
     * Set the index of cell in block. Notice that the value of index may be
     * concrete (e.g., 3) or not concrete (e.g., x+1).
     * <p>
     * We also shorten this index as much as possible. For example, index =
     * "1+3"---shorten----> "4".
     *
     * @param index
     */
    public void setIndex(String index) {
        this.index = index;
    }

    public String getFullIndex() {
        return !index.startsWith("[") ? "[" + index + "]" : index;
    }
}
