package testdatagen.testdatainit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import testdatagen.testdatainit.VariableTypes.BASIC.CHARACTER;
import testdatagen.testdatainit.VariableTypes.BASIC.NUMBER;
import testdatagen.testdatainit.VariableTypes.BASIC.NUMBER.FLOAT;
import testdatagen.testdatainit.VariablesSize.BASIC.NUMBER.INTEGER;
import tree.object.IVariableNode;
import utils.IRegex;
import utils.Utils;

public class VariableTypes {
    public static final String REFERENCE = "&";
    public static final String ONE_LEVEL = "*";
    public static final String TWO_LEVEL = "**";
    public static final String ONE_DIMENSION = "[]";
    public static final String TWO_DIMENSION = "[][]";
    public static final String THROW = "throw";
    public static final String UNSUPPORTED = "undefined";

    public static void main(String[] args) {
        for (String s : VariableTypes
                .getAllBasicFieldNames(VariableTypes.BASIC.class))
            System.out.println(s);
        
    }

    /**
     * Delete const, static, register, extern, mutable
     *
     * @param type
     * @return
     */
    public static String deleteStorageClasses(String type) {
        type = type.replaceAll("^const\\s+", "").replaceAll("\\s+const$", "")
                .replaceAll("\\s+const&", "&").replaceAll("\\s+const\\*", "*");
        for (int i = 0; i < 2; i++) {
            type = type.replaceAll("^static\\s*", "");
            type = type.replaceAll("^register\\s*", "");
            type = type.replaceAll("^extern\\s*", "");
            type = type.replaceAll("^mutable\\s*", "");
        }
        return type;
    }

    /**
     * Ex:"union Color x"-----delete----> "Color x"
     *
     * @return
     */
    public static String deleteUnionKeywork(String type) {
        return type.replaceAll("^union\\s*", "");
    }

    /**
     * Get type of variable
     *
     * @param rawType raw type of variable (may contain const, static, register,
     *                extern, mutable, &, *, **, [], [][],union, enum, struct, etc.)
     *                Ex1: const int& <br/>
     *                Ex2: struct SV
     * @return
     */
    public static String getType(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        if (rawType.equals(VOID_TYPE.VOID))
            return VariableTypes.VOID_TYPE.VOID;

        for (String type : VariableTypes.getAllBasicFieldNames(BASIC.class)) {
            String[] types = new String[]{type,
                    type + VariableTypes.REFERENCE,
                    type + VariableTypes.ONE_LEVEL,
                    type + VariableTypes.ONE_DIMENSION,
                    type + VariableTypes.TWO_LEVEL,
                    type + VariableTypes.TWO_DIMENSION};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return typeItem;

        }

        if (rawType.matches(VariableTypes.THROW))
            return VariableTypes.THROW;

        if (rawType.matches(VariableTypes.STRUCTURE.ONE_LEVEL_STRUCTURE_REGEX))
            return VariableTypes.STRUCTURE.ONE_LEVEL_STRUCTURE_REGEX;

        else if (rawType
                .matches(VariableTypes.STRUCTURE.TWO_LEVEL_STRUCTURE_REGEX))
            return VariableTypes.STRUCTURE.TWO_LEVEL_STRUCTURE_REGEX;

        else if (rawType
                .matches(VariableTypes.STRUCTURE.SIMPLE_STRUCTURE_REGEX))
            return VariableTypes.STRUCTURE.SIMPLE_STRUCTURE_REGEX;

        else if (rawType
                .matches(VariableTypes.STRUCTURE.ONE_DIMENSION_STRUCTURE_REGEX))
            return VariableTypes.STRUCTURE.ONE_DIMENSION_STRUCTURE_REGEX;

        else if (rawType
                .matches(VariableTypes.STRUCTURE.TWO_DIMENSION_STRUCTURE_REGEX))
            return VariableTypes.STRUCTURE.TWO_DIMENSION_STRUCTURE_REGEX;

        return VariableTypes.UNSUPPORTED;
    }

    public static String getType(IVariableNode var) {
        String rawType = var.getRawType();
        return VariableTypes.getType(rawType);
    }

    /**
     * Check whether is basic type. Ex: int, int&
     *
     * @param rawType
     * @return
     */
    public static boolean isBasic(String rawType) {
        return VariableTypes.isChBasic(rawType)
                || VariableTypes.isNumBasic(rawType);
    }

    public static boolean isChOneLevel(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(CHARACTER.class)) {
            String[] types = new String[]{type + VariableTypes.ONE_LEVEL};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;

    }

    public static boolean isChTwoLevel(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(CHARACTER.class)) {
            String[] types = new String[]{type + VariableTypes.TWO_LEVEL};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;

    }

    public static boolean isChOneDimension(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(CHARACTER.class)) {
            String[] types = new String[]{type + VariableTypes.ONE_DIMENSION};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;

    }

    public static boolean isCh(String rawType) {
        return VariableTypes.isChBasic(rawType)
                || VariableTypes.isChOneDimension(rawType)
                || VariableTypes.isChOneLevel(rawType)
                || VariableTypes.isChTwoLevel(rawType);
    }

    public static boolean isChBasic(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(CHARACTER.class)) {
            String[] types = new String[]{type,
                    type + VariableTypes.REFERENCE};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;
    }

    public static boolean isMultipleDimensionArray(String rawType) {
        return Utils.getIndexOfArray(
                VariableTypes.deleteStorageClasses(rawType)).size() > 1;
    }

    public static boolean isNotReturn(String rawType) {
        return VariableTypes.getType(rawType).equals(VOID_TYPE.VOID);
    }

    /**
     * Return true if the type of variable is number or character
     *
     * @param rawType Ex: const int&
     * @return
     */
    public static boolean isNumBasic(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(NUMBER.class)) {
            String[] types = new String[]{type,
                    type + VariableTypes.REFERENCE};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;
    }

    public static boolean isNumOneLevel(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(NUMBER.class)) {
            String[] types = new String[]{type + VariableTypes.ONE_LEVEL};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;
    }

    /**
     * Ex: const int **
     *
     * @param rawType
     * @return
     */
    public static boolean isNumTwoLevel(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(NUMBER.class)) {
            String[] types = new String[]{type + VariableTypes.TWO_LEVEL};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;
    }

    public static boolean isNumOneDimension(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(NUMBER.class)) {
            String[] types = new String[]{type + VariableTypes.ONE_DIMENSION};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;
    }

    public static boolean isNumTwoDimension(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(NUMBER.class)) {
            String[] types = new String[]{type + VariableTypes.TWO_DIMENSION};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;
    }

    public static boolean isNum(String rawType) {
        return VariableTypes.isNumBasic(rawType)
                || VariableTypes.isNumOneDimension(rawType)
                || VariableTypes.isNumOneLevel(rawType)
                || VariableTypes.isNumTwoLevel(rawType);
    }

    public static boolean isNumFloat(String rawType) {
        return VariableTypes.isNumBasicFloat(rawType)
                || VariableTypes.isNumOneLevelFloat(rawType)
                || VariableTypes.isNumTwoLevelFloat(rawType)
                || VariableTypes.isNumOneDimensionFloat(rawType)
                || VariableTypes.isNumTwoDimensionFloat(rawType);

    }

    public static boolean isNumBasicFloat(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(FLOAT.class)) {
            String[] types = new String[]{type,
                    type + VariableTypes.REFERENCE};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;
    }

    public static boolean isNumOneDimensionFloat(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(FLOAT.class)) {
            String[] types = new String[]{type + VariableTypes.ONE_DIMENSION};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;
    }

    public static boolean isNumTwoDimensionFloat(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(FLOAT.class)) {
            String[] types = new String[]{type + VariableTypes.TWO_DIMENSION};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;
    }

    public static boolean isNumOneLevelFloat(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(FLOAT.class)) {
            String[] types = new String[]{type + VariableTypes.ONE_LEVEL};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;
    }

    public static boolean isNumTwoLevelFloat(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(FLOAT.class)) {
            String[] types = new String[]{type + VariableTypes.TWO_LEVEL};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;
    }

    public static boolean isOneDimension(String rawType) {
        return Utils.getIndexOfArray(
                VariableTypes.deleteStorageClasses(rawType)).size() == 1;
    }

    public static boolean isOneDimensionBasic(String rawType) {
        return VariableTypes.isChOneDimension(rawType)
                || VariableTypes.isNumOneDimension(rawType);
    }

    public static boolean isTwoDimension(String rawType) {
        return Utils.getIndexOfArray(
                VariableTypes.deleteStorageClasses(rawType)).size() == 2;
    }

    public static boolean isTwoDimensionBasic(String rawType) {
        return VariableTypes.isChTwoDimension(rawType)
                || VariableTypes.isNumTwoDimension(rawType);
    }

    public static boolean isOneLevel(String rawType) {
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        return VariableTypes.isNumOneLevel(rawType)
                || VariableTypes.isChOneLevel(rawType)
                || VariableTypes.isStructureOneLevel(rawType);
    }

    public static boolean isOneLevelBasic(String rawType) {
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        return VariableTypes.isNumOneLevel(rawType)
                || VariableTypes.isChOneLevel(rawType);
    }

    public static boolean isTwoLevel(String rawType) {
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        return VariableTypes.isNumTwoLevel(rawType)
                || VariableTypes.isChTwoLevel(rawType)
                || VariableTypes.isStructureTwoLevel(rawType);
    }

    public static boolean isTwoLevelBasic(String rawType) {
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        return VariableTypes.isNumTwoLevel(rawType)
                || VariableTypes.isChTwoLevel(rawType);
    }

    /**
     * Get all fields in a class in lower case, including all fields in child
     * class
     *
     * @param c
     * @return
     */
    public static List<String> getAllBasicFieldNames(Class<?> c) {
        List<String> fields = new ArrayList<>();
        java.lang.reflect.Field[] f = c.getFields();

        final String SEPARATE = "_";

        for (Field element : f) {
            String type = element.getName().toLowerCase();
            /**
             * for "wchar_t" and "__wchar_t";
             */
            type = type.replace(SEPARATE + SEPARATE, "@");
            type = type.replace(SEPARATE, " ");
            type = type.replace("@", "_");
            fields.add(type);
        }

        for (Class child : c.getClasses())
            fields.addAll(VariableTypes.getAllBasicFieldNames(child));

        return fields;

    }

    /**
     * Get all fields in a class in lower case, including all fields in child
     * class
     *
     * @param c
     * @return
     */
    public static List<java.lang.reflect.Field> getAllBasicFields(Class<?> c) {
        List<java.lang.reflect.Field> fields = new ArrayList<>();
        java.lang.reflect.Field[] f = c.getFields();

        for (Field element : f)
            fields.add(element);

        for (Class child : c.getClasses())
            fields.addAll(VariableTypes.getAllBasicFields(child));

        return fields;

    }

    /**
     * Delete size from array variable type
     * <p>
     * Ex: int[3] ==> int[]
     *
     * @param type
     * @return
     */
    public static String deleteSizeFromArray(String type) {
        return type.replaceAll("\\[[0-9]+\\]", "[]");
    }

    public static boolean isStructureOneLevel(String type) {
        if (isChOneLevel(type) || isNumOneLevel(type))
            return false;
        else
            return type
                    .matches(VariableTypes.STRUCTURE.ONE_LEVEL_STRUCTURE_REGEX);
    }

    public static boolean isStructureTwoLevel(String type) {
        if (isChTwoLevel(type) || isNumTwoLevel(type))
            return false;
        else
            return type
                    .matches(VariableTypes.STRUCTURE.TWO_LEVEL_STRUCTURE_REGEX);
    }

    public static boolean isStructureOneDimension(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        if (isChOneDimension(rawType) || isNumOneDimension(rawType))
            return false;
        else
            return rawType
                    .matches(VariableTypes.STRUCTURE.ONE_DIMENSION_STRUCTURE_REGEX);
    }

    public static boolean isStructureTwoDimension(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        if (isChTwoDimension(rawType) || isNumTwoDimension(rawType))
            return false;
        else
            return rawType
                    .matches(VariableTypes.STRUCTURE.TWO_DIMENSION_STRUCTURE_REGEX);
    }

    public static boolean isStructureSimple(String type) {
        type = VariableTypes.deleteUnionKeywork(type);
        type = VariableTypes.deleteStructKeywork(type);
        type = VariableTypes.deleteStorageClasses(type);

        if (isBasic(type))
            return false;
        else
            return type.matches(VariableTypes.STRUCTURE.SIMPLE_STRUCTURE_REGEX);
    }

    public static boolean isVoid(String rawType) {
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(VOID_TYPE.class)) {
            String[] types = new String[]{type};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;
    }

    public static boolean isBool(String rawType) {
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        String[] types = new String[]{"bool", "bool*", "bool[]", "bool[][]"};
        for (String typeItem : types)
            if (rawType.equals(typeItem))
                return true;
        return false;
    }

    /**
     * Return true if the type of variable is auto, e.g., auto x = new int[2]
     *
     * @param rawType
     * @return
     */
    public static boolean isAuto(String rawType) {
        return Utils.containRegex(rawType, Utils.toRegex(" auto "));
    }

    public static String deleteStructKeywork(String type) {
        return type.replaceAll("^struct\\s*", "");
    }

    public static boolean isChTwoDimension(String rawType) {
        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        for (String type : VariableTypes.getAllBasicFieldNames(CHARACTER.class)) {
            String[] types = new String[]{type + VariableTypes.ONE_DIMENSION};
            for (String typeItem : types)
                if (rawType.equals(typeItem))
                    return true;

        }
        return false;
    }

    /**
     * Ex1: "auto x_alias = x*(-1.1);" -------> "float", or "double" <br/>
     * Ex2: "auto x_alias = 1;" -------> "int", "short", etc. <br/>
     * Ex2: "auto x_alias = new int[2];" -------> "int[]" <br/>
     *
     * @param intializer
     * @return
     */
    public static String getTypeOfAutoVariable(String intializer) {
        final String DEFAULT_TYPE = "int";
        final String FLOAT_TYPE = "float";
        final String ONE_LEVEL_INTEGER_TYPE = "int*";
        final String TWO_LEVEL_INTEGER_TYPE = "int**";
        final String ONE_LEVEL_FLOAT_TYPE = "float*";
        final String TWO_LEVEL_FLOAT_TYPE = "float**";
        /*
		 * Ex1: initializer = "1.2"
		 *
		 * Ex2: initializer = "1/2"
		 */
        final String[] FLOAT_SIGNALS = new String[]{".", "/"};
        for (String floatSignal : FLOAT_SIGNALS)
            if (intializer.contains(floatSignal))
                return FLOAT_TYPE;

		/*
		 * Ex: initializer = "new int  [2]"
		 */
        for (String integerType : VariableTypes
                .getAllBasicFieldNames(INTEGER.class)) {
            String signalRegex = "new " + integerType + "[";
            if (Utils.containRegex(intializer, Utils.toRegex(signalRegex)))
                return ONE_LEVEL_INTEGER_TYPE;
        }
		/*
		 * Ex: initializer = "new int * [2]"
		 */
        for (String integerType : VariableTypes
                .getAllBasicFieldNames(INTEGER.class)) {
            String signalRegex = "new " + integerType + "* [";
            if (Utils.containRegex(intializer, Utils.toRegex(signalRegex)))
                return TWO_LEVEL_INTEGER_TYPE;
        }

		/*
		 * Ex: initializer = "new float[2]"
		 */
        for (String integerType : VariableTypes
                .getAllBasicFieldNames(FLOAT.class)) {
            String signalRegex = "new " + integerType + "[";
            if (Utils.containRegex(intializer, Utils.toRegex(signalRegex)))
                return ONE_LEVEL_FLOAT_TYPE;
        }

		/*
		 * Ex: initializer = "new float*[2]"
		 */
        for (String integerType : VariableTypes
                .getAllBasicFieldNames(FLOAT.class)) {
            String signalRegex = "new " + integerType + "* [";
            if (Utils.containRegex(intializer, Utils.toRegex(signalRegex)))
                return TWO_LEVEL_FLOAT_TYPE;
        }

		/*
		 * If not map all, it may be integer type
		 */

        return DEFAULT_TYPE;
    }

    public static boolean isThrow(String rawType) {
        return rawType.equals(VariableTypes.THROW);
    }

    public class BASIC {
        public class NUMBER {
            public class FLOAT {
                public static final String FLOAT = "float";
                public static final String DOUBLE = "double";
            }

            public class INTEGER {
                public static final String BOOL = "bool";

                public static final String INT = "int";
                public static final String SIGNED_INT = "signed int";
                public static final String UNSIGNED_INT = "unsigned int";
                public static final String SHORT_INT = "short int";
                public static final String SIGNED_SHORT = "signed short";
                public static final String UNSIGNED_SHORT_INT = "unsigned short int";
                public static final String SIGNED_SHORT_INT = "signed short int";
                public static final String LONG_INT = "long int";
                public static final String SIGNED_LONG_INT = "signed long int";
                public static final String UNSIGNED_LONG_INT = "unsigned long int";

                public static final String SHORT = "short";

                public static final String LONG = "long";
                public static final String LONG_LONG = "long long";
                public static final String SIGNED_LONG_LONG = "signed long long";
                public static final String LONG_LONG_INT = "long long int";
                public static final String SIGNED_LONG_LONG_INT = "signed long long int";

                public static final String UNSIGNED = "unsigned";
                public static final String UNSIGNED_SHORT = "unsigned short";
                public static final String UNSIGNED_LONG = "unsigned long";
                public static final String SIGNED_LONG = "signed long";

                public static final String UNSIGNED_LONG_LONG_INT = "unsigned long long int";
                public static final String UNSIGNED_LONG_LONG = "unsigned long long";
                public static final String LONG_DOUBLE = "long double";
            }
        }

        public class CHARACTER {
            public static final String CHAR = "char";
            public static final String SIGNED_CHAR = "signed char";
            public static final String UNSIGNED_CHAR = "unsigned char";
            public static final String WCHAR__T = "wchar_t";
            public static final String ____WCHAR__T = "__wchar_t";
        }
    }

    public class SPECICAL_TYPE {

        public static final String SIMPLE_LIST_REGEX = "list\\<"
                + IRegex.NAME_REGEX + "\\>";
        public static final String SIMPLE_VECTOR_REGEX = "vector\\<"
                + IRegex.NAME_REGEX + "\\>";
    }

    /**
     * Represent name of variables that its type is struct, class, union.
     */
    public class STRUCTURE {
        /**
         * Ex1: abc[]
         * <p>
         * Ex2: A::B::C::abc[]
         */
        public static final String ONE_DIMENSION_STRUCTURE_REGEX = "[a-zA-Z0-9:]+"
                + "(\\[\\]){1}";
        /**
         * Ex: abc[][]
         * <p>
         * Ex2: A::B::C::abc[][]
         */
        public static final String TWO_DIMENSION_STRUCTURE_REGEX = "[a-zA-Z0-9:]+"
                + "(\\[\\]){2}";
        /**
         * Ex: abc*
         * <p>
         * Ex2: A::B::C::abc*
         */
        public static final String ONE_LEVEL_STRUCTURE_REGEX = "[a-zA-Z0-9:]+"
                + "\\*{1}";
        /**
         * Ex: abc**
         * <p>
         * Ex2: A::B::C::abc**
         */
        public static final String TWO_LEVEL_STRUCTURE_REGEX = "[a-zA-Z0-9:]+"
                + "\\*{2}";
        /**
         * Ex: abc
         * <p>
         * Ex2: A::B::C::abc
         */
        public static final String SIMPLE_STRUCTURE_REGEX = IRegex.NAME_REGEX
                + "$";
    }

    public class VOID_TYPE {

        public static final String VOID = "void";
    }
}
