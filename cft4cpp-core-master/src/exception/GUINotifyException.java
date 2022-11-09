package exception;

/**
 * This exception will fire to GUI to announce to user
 *
 * @author DucAnh
 */
public class GUINotifyException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 3949355435877471241L;

    public GUINotifyException(String message) {
        super(message);
    }

}
