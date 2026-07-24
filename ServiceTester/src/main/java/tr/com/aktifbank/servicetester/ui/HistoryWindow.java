package tr.com.aktifbank.servicetester.ui;

import java.util.List;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import tr.com.aktifbank.servicetester.ExceptionHandler;
import tr.com.aktifbank.servicetester.model.ServiceTesterModel;
import tr.com.aktifbank.servicetester.servicecall.CallParameter;

public class HistoryWindow extends Window {
    
    /**
     * 
     */
    private static final long serialVersionUID = 9113219711989013290L;
    
    private final Table table;
    private CallParameter callParameter;

	public HistoryWindow(final ServiceTesterModel model, final ExceptionHandler exceptionHandler) throws Exception {
		super("History");
		
		final VerticalLayout historyLayout = new VerticalLayout();
		final Button clearButton = new Button("Clear History");
		final TextField filterTextField = new TextField("Service Filter");
		
		filterTextField.addTextChangeListener(new TextChangeListener() {

            /**
             * 
             */
            private static final long serialVersionUID = -8170526500620141735L;

            @Override
            public void textChange(TextChangeEvent event) {
                SimpleStringFilter filter = new SimpleStringFilter("serviceNameColumn", event.getText(), true, false);
                IndexedContainer containerDataSource = (IndexedContainer) table.getContainerDataSource();
                containerDataSource.removeAllContainerFilters();
                containerDataSource.addContainerFilter(filter);
            }
        });
        
        table = new Table();
        
        table.addContainerProperty("dateTimeColumn", String.class, null, "Date / Time", null, Align.CENTER);
        table.addContainerProperty("serviceNameColumn", String.class, null, "Service Name", null, Align.CENTER);
        table.addContainerProperty("callParameterColumn", CallParameter.class, null, "Call Parameters", null, Align.CENTER);
        
        table.setColumnExpandRatio("dateTimeColumn", 3);
        table.setColumnExpandRatio("serviceNameColumn", 5);
        table.setColumnExpandRatio("callParameterColumn", 6);
        
        table.setPageLength(5);
        table.setEditable(false);
        table.setSelectable(true);
        table.setSizeFull();
        
        table.addItemClickListener(new ItemClickListener() {

            /**
             * 
             */
            private static final long serialVersionUID = -872534971889222071L;

            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    setParameter((CallParameter) event.getItem().getItemProperty("callParameterColumn").getValue());
                    close();
                }
            }
        });
        
        clearButton.addClickListener(new ClickListener() {
            
            /**
             * 
             */
            private static final long serialVersionUID = -5022307589587026490L;

            @Override
            public void buttonClick(ClickEvent event) {
                if (table.getItemIds().size() > 0) {
                    YesNoWindow yesNoWindow = new YesNoWindow("Clear History", "Are you sure?", new YesNoWindow.Callback() {
                        
                        @Override
                        public void onDialogResult(boolean isYes) {
                            try {
                                if (isYes) {
                                    model.clearHistory();
                                    close();
                                }
                            } catch (Exception e) {
                                exceptionHandler.handle(e);
                            }
                        }
                    });
                    
                    UI.getCurrent().addWindow(yesNoWindow);
                }
            }
        });
        
        historyLayout.addComponent(filterTextField);
        historyLayout.addComponent(table);
        historyLayout.addComponent(clearButton);

        historyLayout.setExpandRatio(table, 10.0f);
        historyLayout.setMargin(true);
        historyLayout.setSpacing(true);
        historyLayout.setSizeFull();
		
		setCaption("History");
		setWidth(50, Unit.PERCENTAGE);
		setHeight(50, Unit.PERCENTAGE);
		setModal(true);
		setContent(historyLayout);
		
		loadFromModel(model);
	}
	
	private void loadFromModel(ServiceTesterModel model) throws Exception {
	    List<Object[]> historyItemList = model.getHistory();
	    
	    for (Object[] item : historyItemList) {
	        table.addItem(item, item);
	    }
	}
	
	private void setParameter(CallParameter callParameter) {
	    this.callParameter = callParameter;
	}
	
	public CallParameter getParameter() {
	    return callParameter;
	}

}
