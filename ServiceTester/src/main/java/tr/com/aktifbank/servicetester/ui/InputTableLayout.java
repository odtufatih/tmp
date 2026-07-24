package tr.com.aktifbank.servicetester.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;

public class InputTableLayout extends TableLayout {

    /**
     * 
     */
    private static final long serialVersionUID = -8673975062944407090L;

    public InputTableLayout() {
		super();

        final HorizontalLayout buttonLayout = new HorizontalLayout();
		final Button addButton = new Button("+");
		final Button addToNextIndexButton = new Button("+>");
		final Button removeAllButton = new Button("Remove All");
		
		addButton.setDescription("Add new item");
		addToNextIndexButton.setDescription("Add new item to next index");

		addButton.addClickListener(new ClickListener() {

            /**
             * 
             */
            private static final long serialVersionUID = -738419679395489065L;

            @Override
            public void buttonClick(ClickEvent event) {
                addNewItem();
            }
        });
		
		addToNextIndexButton.addClickListener(new ClickListener() {
            
            /**
             * 
             */
            private static final long serialVersionUID = 7661260162794623737L;

            @Override
            public void buttonClick(ClickEvent event) {
                addIndexAndNewItem();
            }
        });
		
		removeAllButton.addClickListener(new ClickListener() {
            
            /**
             * 
             */
            private static final long serialVersionUID = 7661260162794623737L;

            @Override
            public void buttonClick(ClickEvent event) {
                removeAll();
            }
        });
	      
        buttonLayout.setSpacing(true);
        buttonLayout.setWidth("100%");
        buttonLayout.addComponent(addButton);
        buttonLayout.addComponent(addToNextIndexButton);
        buttonLayout.addComponent(removeAllButton);

		addComponent(buttonLayout);
	}

}
