package testdatagen.testdatainit;

/**
 * http://en.cppreference.com/w/cpp/language/types
 *
 * @author ducanhnguyen
 */
public class VariablesSize {
    public static final int UNSPECIFIED_SIZE = -1;

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
        System.out.println(VariablesSize.getSizeofTypeInByte("signed long long"));
    }

    /**
     * Get size of type
     *
     * @param rawType the raw type of variable. Ex: "const int&" ---> 4 (byte)
     * @return size of variable
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static int getSizeofTypeInByte(String rawType) throws IllegalArgumentException, IllegalAccessException {
        final String SEPARATE = "_";

        rawType = VariableTypes.deleteUnionKeywork(rawType);
        rawType = VariableTypes.deleteStructKeywork(rawType);
        rawType = VariableTypes.deleteStorageClasses(rawType);
        rawType = VariableTypes.deleteSizeFromArray(rawType);

        if (VariableTypes.isNumBasic(rawType) || VariableTypes.isChBasic(rawType))
            for (java.lang.reflect.Field typeItem : VariableTypes.getAllBasicFields(BASIC.class)) {
                /*
				 * Normalize name of field
				 */
                String type = typeItem.getName().toLowerCase();
                // for "wchar_t" and "__wchar_t";
                type = type.replace(SEPARATE + SEPARATE, "@");
                type = type.replace(SEPARATE, " ");
                type = type.replace("@", "_");

				/*
				 * 
				 */
                if (type.equals(rawType)) {
                    // 1 byte = 8 bits
                    int size = (int) typeItem.get(typeItem) / 8;
                    return size;
                }
            }
        return VariablesSize.UNSPECIFIED_SIZE;
    }

    public class BASIC {
        public class NUMBER {
            public class FLOAT {
                public static final int FLOAT = 32;// bit
                public static final int DOUBLE = 64;
            }

            public class INTEGER {
                public static final int BOOL = 8;

                public static final int INT = 32;
                public static final int SIGNED_INT = 32;
                public static final int UNSIGNED_INT = 32;
                public static final int SHORT_INT = 16;
                public static final int SIGNED_SHORT = 16;
                public static final int UNSIGNED_SHORT_INT = 16;
                public static final int SIGNED_SHORT_INT = 16;
                public static final int LONG_INT = 32;
                public static final int SIGNED_LONG_INT = 32;
                public static final int UNSIGNED_LONG_INT = 32;

                public static final int SHORT = 16;

                public static final int LONG = 32;
                public static final int LONG_LONG = 64;
                public static final int SIGNED_LONG_LONG = 64;
                public static final int LONG_LONG_INT = 64;
                public static final int SIGNED_LONG_LONG_INT = 64;

                public static final int UNSIGNED = 32;
                public static final int UNSIGNED_SHORT = 16;
                public static final int UNSIGNED_LONG = 32;
                public static final int SIGNED_LONG = 32;

                public static final int UNSIGNED_LONG_LONG_INT = 64;
                public static final int UNSIGNED_LONG_LONG = 64;
                public static final int LONG_DOUBLE = 80;
            }
        }

        public class CHARACTER {
            public static final int CHAR = 8;
            public static final int SIGNED_CHAR = 8;
            public static final int UNSIGNED_CHAR = 8;
            public static final int WCHAR__T = 16;
            public static final int ____WCHAR__T = 16;// check later
        }
    }
}
