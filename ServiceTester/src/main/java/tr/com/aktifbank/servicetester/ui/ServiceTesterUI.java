package tr.com.aktifbank.servicetester.ui;


import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import tr.com.aktifbank.servicetester.Channel;
import tr.com.aktifbank.servicetester.Environment;
import tr.com.aktifbank.servicetester.ExceptionHandler;
import tr.com.aktifbank.servicetester.Server;
import tr.com.aktifbank.servicetester.data.MapDataContainer;
import tr.com.aktifbank.servicetester.model.ServiceTesterModel;
import tr.com.aktifbank.servicetester.servicecall.CallParameter;

@SuppressWarnings("serial")
@Theme("servicetester")
@Title("ServiceTester")
public class ServiceTesterUI extends UI implements CloseListener {
    
    private final ExceptionHandler exceptionHandler = new ExceptionHandler();
    private final ServiceTesterModel model = new ServiceTesterModel(exceptionHandler);
    
    private final HorizontalLayout mainLayout = new HorizontalLayout();
    private final HorizontalLayout serializedInputLayout = new HorizontalLayout();
    private final HorizontalLayout environmentLayout = new HorizontalLayout();
    private final VerticalLayout mainLeftLayout = new VerticalLayout();
    private final VerticalLayout inputLayout = new VerticalLayout();
    private final VerticalLayout callLayout = new VerticalLayout();
    private final VerticalLayout outputLayout = new VerticalLayout();
    private final Panel inputPanel = new Panel("Input");
    private final Panel callPanel = new Panel("Service Call");
    private final Panel outputPanel = new Panel("Output");
    private final MapLayout inputMapLayout = new InputMapLayout();
    private final MapLayout outputMapLayout = new OutputMapLayout(inputMapLayout);
    private final TextField serializedInputText = new TextField();
    private final TextField serviceNameText = new TextField();
    private final Button deserializeButton = new Button("Deserialize / Add");
    private final Button callButton = new Button("Call Service");
    private final Button historyButton = new Button("Load From History");
    private final ComboBox environmentComboBox = new ComboBox("Environment");
    private final ComboBox serverComboBox = new ComboBox("Server");
    private final ComboBox channelComboBox = new ComboBox("Channel");

    private HistoryWindow historyWindow;

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ServiceTesterUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		
		callButton.addClickListener(new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    serviceNameText.validate();
                    environmentComboBox.validate();
                    serverComboBox.validate();
                    channelComboBox.validate();
                    
                    model.setServiceName(serviceNameText.getValue());
                    model.setChannel((Channel) channelComboBox.getValue());
                    model.setEnvironment((Environment) environmentComboBox.getValue());
                    model.setServer((Server) serverComboBox.getValue());
                    
                    MapDataContainer response = model.call(inputMapLayout.getDataContainer());
                    outputMapLayout.setDataContainer(response);
                } catch (Exception e) {
                    exceptionHandler.handle(e);
                }
            }
        });
		
		historyButton.addClickListener(new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    historyWindow = new HistoryWindow(model, exceptionHandler);
                    historyWindow.addCloseListener(ServiceTesterUI.this);
    
                    UI.getCurrent().addWindow(historyWindow);
                } catch (Exception e) {
                    exceptionHandler.handle(e);
                }
            }
        });
		
		deserializeButton.addClickListener(new ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    MapDataContainer mapDataContainer = model.deserialize(serializedInputText.getValue());
                    inputMapLayout.setDataContainer(mapDataContainer);
                } catch (Exception e) {
                    exceptionHandler.handle(e);
                }
            }
        });

		serializedInputText.setCaption("Serialized Input");
		serializedInputText.setWidth(100, Unit.PERCENTAGE);
		
		serviceNameText.setCaption("Service Name");
		serviceNameText.setWidth(100, Unit.PERCENTAGE);
		serviceNameText.setRequired(true);
		serviceNameText.setRequiredError("Enter service name");

		serializedInputLayout.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);
		serializedInputLayout.setSpacing(true);
		serializedInputLayout.setSizeFull();
		serializedInputLayout.addComponent(serializedInputText);
		serializedInputLayout.addComponent(deserializeButton);
		serializedInputLayout.setExpandRatio(serializedInputText, 3.0f);
		serializedInputLayout.setExpandRatio(deserializeButton, 1.0f);
		
		inputLayout.setMargin(true);
		inputLayout.setSpacing(true);
		inputLayout.setSizeFull();
		inputLayout.addComponent(inputMapLayout);
		inputLayout.addComponent(historyButton);
		inputLayout.addComponent(serializedInputLayout);
		inputLayout.setExpandRatio(inputMapLayout, 5.0f);
		inputLayout.setExpandRatio(serializedInputLayout, 1.0f);
		
		inputPanel.setContent(inputLayout);
		inputPanel.setSizeFull();
		
		environmentLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		environmentLayout.setSpacing(true);
		environmentLayout.addComponent(environmentComboBox);
		environmentLayout.addComponent(serverComboBox);
		environmentLayout.addComponent(channelComboBox);
		
		environmentComboBox.setRequired(true);
		environmentComboBox.setRequiredError("Select environment");
		environmentComboBox.setNullSelectionAllowed(false);
		serverComboBox.setRequired(true);
		serverComboBox.setRequiredError("Select target");
		serverComboBox.setNullSelectionAllowed(false);
		channelComboBox.setRequired(true);
		channelComboBox.setRequiredError("Select channel");
		channelComboBox.setNullSelectionAllowed(false);
		channelComboBox.setPageLength(0);
		
		callLayout.setSpacing(true);
		callLayout.setMargin(true);
		callLayout.setSizeFull();
		callLayout.addComponent(serviceNameText);
		callLayout.addComponent(environmentLayout);
		callLayout.addComponent(callButton);
		
		callPanel.setContent(callLayout);
		callPanel.setSizeFull();
		
		outputLayout.setMargin(true);
		outputLayout.setSizeFull();
		outputLayout.addComponent(outputMapLayout);
		
		outputPanel.setContent(outputLayout);
		outputPanel.setSizeFull();
		
		mainLeftLayout.setSpacing(true);
		mainLeftLayout.setSizeFull();
		mainLeftLayout.addComponent(inputPanel);
		mainLeftLayout.addComponent(callPanel);
		mainLeftLayout.setExpandRatio(inputPanel, 2);
		mainLeftLayout.setExpandRatio(callPanel, 1);
		
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		mainLayout.setSizeFull();
		mainLayout.addComponent(mainLeftLayout);
		mainLayout.addComponent(outputPanel);
		
		setContent(mainLayout);
		
		loadFromModel();
	}
	
	private void loadFromModel() {
	    environmentComboBox.addItems(model.getEnvironments());
	    serverComboBox.addItems(model.getServers());
	    channelComboBox.addItems(model.getChannels());
	}

    @Override
    public void windowClose(CloseEvent e) {
        // History window close
        CallParameter callParameter = historyWindow.getParameter();
        
        if (callParameter != null) {
            serviceNameText.setValue(callParameter.getServiceName());
            environmentComboBox.setValue(callParameter.getEnvironment());
            serverComboBox.setValue(callParameter.getServer());
            channelComboBox.setValue(callParameter.getChannel());
            inputMapLayout.setDataContainer(callParameter.getMapDataContainer());
        }
        
        historyWindow = null;
    }

}