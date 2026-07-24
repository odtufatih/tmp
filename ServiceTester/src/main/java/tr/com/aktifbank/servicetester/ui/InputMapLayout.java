package tr.com.aktifbank.servicetester.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;

public class InputMapLayout extends MapLayout {

    /**
     * 
     */
    private static final long serialVersionUID = 6582776714038634624L;

    public InputMapLayout() {
		super();
		
		table.setEditable(true);
		table.setPageLength(5);

		final HorizontalLayout buttonLayout = new HorizontalLayout();
		final Button addButton = new Button("+");
		final Button removeAllButton = new Button("Remove All");
		
		addButton.setDescription("Add new item");
		addButton.addClickListener(new ClickListener() {

            /**
             * 
             */
            private static final long serialVersionUID = 7263740431316921847L;

            @Override
            public void buttonClick(ClickEvent event) {
                addNewItem();
            }
        });
		
		removeAllButton.addClickListener(new ClickListener() {
            
            /**
             * 
             */
            private static final long serialVersionUID = -8079122782415268266L;

            @Override
            public void buttonClick(ClickEvent event) {
                table.removeAllItems();
            }
        });
		
		buttonLayout.setSpacing(true);
		buttonLayout.setWidth("100%");
		buttonLayout.addComponent(addButton);
		buttonLayout.addComponent(removeAllButton);
		
		addComponent(buttonLayout);
	}
	
	private void addNewItem() {
	    final ValueComponent valueComponent = new ValueComponent();
        
	    addItem(null, valueComponent);
	}

}
