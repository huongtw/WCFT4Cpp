package testdatagen;

public interface IVenusTestdataGenerationStrategy {
    final int SELECTED_PRIOTITIES_TESTPATH = PRIOTITIES_TESTPATHS.IN_DECREASING_ORDER;

    /**
     * [0..number of conditions)
     */
    final int CONDITION_ORDER_CHECKER_ID = CONDITION_ORDER.THIRD;

    interface PRIOTITIES_TESTPATHS {
        final int IN_DECREASING_ORDER = 0;
        final int IN_INCREASING_ORDER = 1;
        final int CURRENT_ORDER = 2;
    }

    interface CONDITION_ORDER {
        final int NO_CHECK = -1;
        final int FIRST = 0;
        final int SECOND = 1;
        final int THIRD = 2;
        final int FOURTH = 3;
    }
}
