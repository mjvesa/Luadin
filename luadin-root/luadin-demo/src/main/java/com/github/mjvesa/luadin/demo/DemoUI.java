package com.github.mjvesa.luadin.demo;

import com.github.mjvesa.luadin.LuaRunner;

import javax.servlet.annotation.WebServlet;

import org.apache.tools.ant.taskdefs.Property;
import org.apache.xpath.compiler.PsuedoNames;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalSplitPanel;

@Push
@Theme("demo")
@Title("Lua live editor")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class,
            widgetset = "com.github.mjvesa.luadin.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        

        setContent(new LiveEditor());
        

    }
}
