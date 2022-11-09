package testdatagen.se.solver;

interface IConvertToSmtLibNormalizer {
    static final String LON_HON_HOAC_BANG_RUT_GON = "$";
    static final String NHO_HON_HOAC_BANG_RUT_GON = "@";

    static final String SO_SANH_BANG_RUT_GON = "=";

    static final String SO_SANH_LON_HON = ">";

    static final String SO_SANH_NHO_HON = "<";

    static final String PHEP_AND_RUT_GON = "&";

    static final String SO_SANH_KHAC_RUT_GON = "#";

    static final String OR_RUT_GON = "|";

    static final String PHEP_DU_SMT_FORMAT = "mod";

    static final String PHEP_PHU_DINH_SMT_FORMAT = "not";

    static final String PHEP_PHU_DINH = "!";
}