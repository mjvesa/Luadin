package com.github.mjvesa.luadin;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;


public class LuaRunner  {

    
    public static void runLuaFile(String fileName, ComponentContainer cc) {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue script = globals.loadFile(fileName);
        script.invoke(CoerceJavaToLua.coerce(cc));
    }
    
    public static void runLuaString(String code, ComponentContainer cc) {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue script = globals.loadString(code, "code");
        script.invoke(CoerceJavaToLua.coerce(cc));
    }
}
