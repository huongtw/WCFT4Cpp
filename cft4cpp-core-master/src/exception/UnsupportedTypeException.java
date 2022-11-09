package exception;

/**
 * Represent unsupported exception
 *
 * @author ducanhnguyen
 */
public class UnsupportedTypeException extends GUINotifyException {

    /**
     *
     */
    private static final long serialVersionUID = 3511407097819501025L;

    public UnsupportedTypeException(String msg) {
        super(msg);
    }

}
