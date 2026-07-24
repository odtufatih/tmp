package tr.com.aktifbank.servicetester.ui;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class YesNoWindow extends Window implements Button.ClickListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1350437604532706568L;
    
    public interface Callback {
        
        public void onDialogResult(boolean isYes);
    }
    
    final Callback callback;
    final Button yesButton = new Button("Yes", this);
    final Button noButton = new Button("No", this);

    public YesNoWindow(String caption, String question, Callback callback) {
        super(caption);
        
        final VerticalLayout layout = new VerticalLayout();
        final HorizontalLayout buttonLayout = new HorizontalLayout();

        setModal(true);

        this.callback = callback;

        if (question != null) {
            Label label = new Label(question);
            layout.addComponent(label);
            layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        }

        buttonLayout.addComponent(yesButton);
        buttonLayout.addComponent(noButton);
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);
        buttonLayout.setSizeFull();
        
        layout.addComponent(buttonLayout);
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
        layout.setWidth("300px");
        layout.setHeight("200px");
        
        setContent(layout);
    }

    public void buttonClick(ClickEvent event) {
        close();
        callback.onDialogResult(event.getSource() == yesButton);
    }

}
