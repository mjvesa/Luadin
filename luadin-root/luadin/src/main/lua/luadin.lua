-- Luadin - Vaadin API wrapper for Lua
local luadin = {}

-------------------
-- basic components
-------------------
local function component()
	local comp = {}
	
	-- This must be changed for each component
	comp.componentInstance = luajava.newInstance("com.vaadin.ui.Label", "Unimplemented component")
	
	function comp.getComponentInstance()
		return comp.componentInstance
	end
	
    return comp
end

-- Button
function luadin.Button(caption)

	local button = component()
	button.componentInstance = luajava.newInstance("com.vaadin.ui.Button", caption)
	
	-- adds the provided function as click listener to this button
	function button.addClickListener(clickListener)
		local listener = luajava.createProxy("com.vaadin.ui.Button$ClickListener", {
			buttonClick = function (event)
				print "Click took place"
				clickListener() 
			end,
			hashCode = function()
				-- TODO doest this really call Object.hashCode for this interface?
				o = luajava.bindClass("java.lang.Object")
				return o:hashCode()
			end
			
			})
			
		button.componentInstance:addClickListener(listener)
	end

	return button 
end

-- Label
function luadin.Label(caption)
	local label = component()
	label.componentInstance = luajava.newInstance("com.vaadin.ui.Label", caption)
	return label
end

-- Base class of TextField and TextArea
local function AbstractTextField()
	local atf = component()
	atf.componentInstance = luajava.newInstance("com.vaadin.ui.Label", "Do not use AbstractTextField")
	function atf.getValue()
		atf.componentInstance:getValue()
	end
	function atf.addTextChangeListener(listener)
		local tcl = luajava.createProxy("com.vaadin.event.FieldEvents$TextChangeListener", {
			texthange = function(event)
				listener(event:getText())
			end
		})
		atf.componentInstance:addTextChangeListener(tcl);
	end
	return atf
end

-- TextField
function luadin.TextField(caption)
	local tf = AbstractTextField(caption)
	tf.componentInstance = luajava.newInstance("com.vaadin.ui.TextField", caption)
	return tf
end

-- TextArea
function luadin.TextArea(caption)
	local ta = AbstractTextField(caption)
	ta.componentInstance = luajava.newInstance("com.vaadin.ui.TextArea", caption)
	return ta
end


local function AbstractField()
	local af = component()
	function af.addValueChangeListener(listener)
		local vcl = luajava.createProxy("com.vaadin.data.Property$ValueChangeListener", {
			valueChange = function(prop)
				listener(prop:getValue())
			end
		})
		af.componentIstance:addValueChangeListener(vcl)
	end
	return af
end

-- CheckBox
function luadin.CheckBox(caption)
	local cb = component()
	cb.componentInstance = luajava.newInstance("com.vaadin.ui.CheckBox", caption)
	-- TODO implement
	return cb
end

-- OptionGroup
function luadin.OptionGroup(caption)
	local og = component()
	og.componentInstance = luajava.newInstance("com.vaadin.ui.OptionGroup", caption)
	-- TODO implement
	return og
end

-- ComboBox
function luadin.ComboBox(caption)
	local cb = component()
	cb.componentInstance = luajava.newInstance("com.vaadin.ui.ComboBox", caption)
	-- TODO handle new items and value select
	return cb
end

-- ListSelect
function luadin.ListSelect(caption)
	local ls = component()
	ls.componentInstance = luajava.newInstance("com.vaadin.ui.ListSelect", caption)
	-- TODO 
	return ls
end

-- Table
function luadin.Table(caption)
	local table = component()
	table.componentInstance = luajava.newInstance("com.vaadin.ui.Table", caption)	
	
	function table.setColumnHeaders(columnHeaders)
		table.componentInstance:setColumnHeaders(columnHeaders)
	end
	function table.setVisibleColumns(visibleColumns)
		table.componentInstance:setVisibleColumns(visibleColumns)
	end
	
	function table.setContainerDataSource(container)
		table.componentInstance:setContainerDataSource(container.getContainerInstance())
	end
	
	return table
end


-----------
-- Tabsheet
-----------


------------------------
-- Component containers
------------------------
local function componentContainer()
	local cc = {}
	cc.containerInstance = luajava.newInstance("com.vaadin.ui.VerticalLayout")
	
	function cc.getContainerInstance()
		return cc.containerInstance
	end
	
	function cc.getComponentInstance()
		return cc.containerInstance
	end
	
	function cc.addComponent(component)
		 cc.containerInstance:addComponent(component.getComponentInstance())
	end
	
	function setMargin(margin)
		cc:setMargin(margin)
	end
	
	function setSpacing(spacing)
		cc:setSpacing(spacing)
	end
	
	return cc
end
 
function luadin.VerticalLayout()
	local vl = componentContainer() 
	vl.containerInstance = luajava.newInstance("com.vaadin.ui.VerticalLayout")
	return vl
end

function luadin.HorizontalLayout()
	local vl = componentContainer() 
	vl.containerInstance = luajava.newInstance("com.vaadin.ui.HorizontalLayout")
	return vl
end

function luadin.CssLayout()
	local vl = componentContainer() 
	vl.containerInstance = luajava.newInstance("com.vaadin.ui.CssLayout")
	return vl
end

function luadin.GridLayout(cols, rows)
	local gl = componentContainer()
	gl.containerInstance = luajava.newInstance("com.vaadin.ui.GridLayout", cols, rows)
	return gl
end


------
--Item
------
function luadin.Item(nativeItem)
	item = {}
	item.item = nativeItem
	function item.getPropertyValue(prop)
		return item.item:getItemProperty(prop):getValue()
	end
	function item.setPropertyValue(prop, value)
		local p = item.item:getItemProperty(prop)
		p:setValue(value)
	end
	return item 
end 

------------
-- Container
------------
function luadin.Container(propertyNames) 
	c = {}
	c.containerInstance = luajava.newInstance("com.vaadin.data.util.IndexedContainer")
	o = luajava.bindClass("java.lang.Object")
	for _, name in ipairs(propertyNames) do
		 c.containerInstance:addContainerProperty(name, o, nil)
	end
	
	function c.getContainerInstance()
		return c.containerInstance
	end
	
	function c.getItem(id)
		return luadin.Item(c.containerInstance:getItem(id))
	end
	
	function c.addItem()
		local id = c.containerInstance:addItem();
		return luadin.Item(c.containerInstance:getItem(id))
	end
	
	function c.addItemWithValues(values)
		local item = c.addItem()
		for k, v in pairs(values) do
			print ("key " .. k)
			print ("value " .. v)
			item.setPropertyValue(k, v)
		end
		return item 
	end
	
	return c
	
end



return luadin