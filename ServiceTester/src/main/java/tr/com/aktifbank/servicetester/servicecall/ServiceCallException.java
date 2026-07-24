package tr.com.aktifbank.servicetester.servicecall;

public class ServiceCallException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -7904836951398023012L;

    public ServiceCallException() {
        super();
    }

    public ServiceCallException(String msg) {
        super(msg);
    }

    public ServiceCallException(Throwable throwable) {
        super(throwable);
    }

    public ServiceCallException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
