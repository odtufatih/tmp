package tr.com.aktifbank.servicetester.servicecall;

public class ServiceTimeoutException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -8238754375300737894L;

    public ServiceTimeoutException() {
        super();
    }

    public ServiceTimeoutException(String msg) {
        super(msg);
    }

    public ServiceTimeoutException(Throwable throwable) {
        super(throwable);
    }

    public ServiceTimeoutException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
