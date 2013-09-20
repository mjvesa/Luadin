package com.github.mjvesa.luadin.demo;

import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;

import com.github.mjvesa.luadin.LuaRunner;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class LiveEditor extends HorizontalSplitPanel {
    
    
    private static final long serialVersionUID = 6254017255341686040L;
    
    private boolean keepRunning;
    private boolean codeHasChanged;
    private int updateDelay;


    public LiveEditor() {
        updateDelay = 500;
        keepRunning = false;
        codeHasChanged = false;

        final VerticalLayout content = new VerticalLayout();
        content.setSizeFull();

        final AceEditor editor = new AceEditor();
        editor.setMode(AceMode.lua);
        editor.setTheme(AceTheme.solarized_dark);
        editor.setSizeFull();
        editor.addTextChangeListener(new TextChangeListener() {
            @Override
            public void textChange(TextChangeEvent event) {
                codeHasChanged = true;
            }
        });
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.addComponent(editor);
        vl.setExpandRatio(editor, 1);
        Panel console = new Panel();
        final VerticalLayout consoleContent = new VerticalLayout();
        console.setContent(consoleContent);
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);

        hl.addComponent(new Button("Execute", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                String source = editor.getValue();
                try {
                    consoleContent.removeAllComponents();
                    LuaRunner.runLuaString(source, content);
                } catch (Exception e) {
                    consoleContent.addComponent(new Label(e.toString()));
                }
            }
        }));

        hl.addComponent(new Button("clear UI", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                content.removeAllComponents();
            }
        }));
        
        final UI ui = UI.getCurrent();

        Thread updater = new Thread() {

            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        sleep(500);
                        if (keepRunning && codeHasChanged) {

                            ui.access(new Runnable() {

                                @Override
                                public void run() {

                                    String source = editor.getValue();
                                    try {
                                        content.removeAllComponents();
                                        consoleContent.removeAllComponents();
                                        LuaRunner.runLuaString(source,
                                                content);
                                        ui.push();
                                        codeHasChanged = false;
                                    } catch (Exception e) {
                                        consoleContent
                                                .addComponent(new Label(e
                                                        .toString()));
                                    }
                                }
                            });

                        }
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                }
            }

        };

        CheckBox cb = new CheckBox("Run once per");
        cb.setImmediate(true);
        cb.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                keepRunning = (Boolean) event.getProperty().getValue();

            }
        });
        hl.addComponent(cb);
        
        TextField tf = new TextField();
        tf.addValueChangeListener( new ValueChangeListener() {
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                updateDelay = Integer.valueOf((String)event.getProperty().getValue());
            }
        });
        tf.setImmediate(true);
        tf.setWidth("5em");
        tf.setValue(updateDelay + "");
        hl.addComponent(tf);
        hl.addComponent(new Label("ms"));

        vl.addComponent(hl);

        VerticalSplitPanel vsp = new VerticalSplitPanel();
        vsp.setSizeFull();
        vsp.addComponent(vl);
        vsp.addComponent(console);
        addComponent(vsp);
        addComponent(content);

        updater.start();

        
    }
    

}