package tree.object;

public interface IProcessNotify {

    IProcessNotify DEFAULT = new IProcessNotify() {

        @Override
        public void notify(int status) {
        }

        @Override
        public void notify(String message) {
        }
    };

    void notify(int status);

    void notify(String message);
}
