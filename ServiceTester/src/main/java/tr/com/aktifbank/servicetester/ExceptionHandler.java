package tr.com.aktifbank.servicetester;

import java.io.Serializable;

import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import tr.com.aktifbank.servicetester.parser.ParseException;
import tr.com.aktifbank.servicetester.servicecall.ServiceCallException;
import tr.com.aktifbank.servicetester.servicecall.ServiceTimeoutException;

public class ExceptionHandler implements IExceptionHandler, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7289789032672052955L;

    @Override
    public void handle(Exception e) {
        Page page = Page.getCurrent();
        if (page != null) {
            new Notification("ERROR", e.getMessage(), Type.ERROR_MESSAGE).show(page);
        }
        
        if (!(e instanceof ServiceCallException
                || e instanceof ServiceTimeoutException
                || e instanceof ParseException
                || e instanceof EmptyValueException)) {
            throw new RuntimeException(e);
        }
    }

}
