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
import com.vaadin.ui.Component;
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

    private AceEditor editor;
    private VerticalLayout content;
    private Panel console;
    private VerticalLayout consoleContent;

    private boolean keepRunning;
    private boolean codeHasChanged;
    private int updateDelay;
    private UI ui;

    public LiveEditor() {
        updateDelay = 500;
        keepRunning = false;
        codeHasChanged = false;
        ui = UI.getCurrent();

        content = new VerticalLayout();
        content.setSizeFull();

        VerticalSplitPanel vsp = new VerticalSplitPanel();
        vsp.setSizeFull();
        vsp.addComponent(constructEditorInterface());
        console = createConsole();
        vsp.addComponent(console);
        addComponent(vsp);
        addComponent(content);

        updater.start();

    }

    private Panel createConsole() {
        Panel p = new Panel();
        consoleContent = new VerticalLayout();
        p.setContent(consoleContent);
        return p;
    }

    private Component constructEditorInterface() {
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        editor = createEditor();
        vl.addComponent(editor);
        vl.setExpandRatio(editor, 1);
        vl.addComponent(constructButtonRow());

        return vl;

    }

    private Component constructButtonRow() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.addComponent(createExecuteButton());
        hl.addComponent(createClearUIButton());
        hl.addComponent(createLiveUpdateToggleCheckBox());
        hl.addComponent(createUpdateTimeTextField());
        hl.addComponent(new Label("ms"));
        return hl;
    }

    private Component createLiveUpdateToggleCheckBox() {
        CheckBox cb = new CheckBox("Run once per");
        cb.setImmediate(true);
        cb.addValueChangeListener(new ValueChangeListener() {

            private static final long serialVersionUID = -114416360494611917L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                keepRunning = (Boolean) event.getProperty().getValue();

            }
        });
        return cb;
    }

    private Component createClearUIButton() {
        return new Button("clear UI", new Button.ClickListener() {

            private static final long serialVersionUID = 4767684729607037873L;

            @Override
            public void buttonClick(ClickEvent event) {
                content.removeAllComponents();
            }
        });

    }

    private Component createExecuteButton() {
        return new Button("Execute", new Button.ClickListener() {
            private static final long serialVersionUID = -7019722617370612697L;

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
        });
    }

    private Component createUpdateTimeTextField() {
        TextField tf = new TextField();
        tf.addValueChangeListener(new ValueChangeListener() {

            private static final long serialVersionUID = 8983751011414138637L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                updateDelay = Integer.valueOf((String) event.getProperty()
                        .getValue());
            }
        });
        tf.setImmediate(true);
        tf.setWidth("5em");
        tf.setValue(updateDelay + "");
        return tf;
    }

    private AceEditor createEditor() {

        AceEditor editor = new AceEditor();
        editor.setMode(AceMode.lua);
        editor.setTheme(AceTheme.solarized_dark);
        editor.setSizeFull();
        editor.addTextChangeListener(new TextChangeListener() {
            private static final long serialVersionUID = 2606822312690094005L;

            @Override
            public void textChange(TextChangeEvent event) {
                codeHasChanged = true;
            }
        });
        return editor;
    }

    private Thread updater = new Thread() {

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
                                    LuaRunner.runLuaString(source, content);
                                    ui.push();
                                    codeHasChanged = false;
                                } catch (Exception e) {
                                    consoleContent.addComponent(new Label(e
                                            .toString()));
                                }
                            }
                        });

                    }
                } catch (InterruptedException e1) {
                    consoleContent.removeAllComponents();
                    consoleContent.addComponent(new Label(e1.toString()));
                }

            }
        }

    };


}
