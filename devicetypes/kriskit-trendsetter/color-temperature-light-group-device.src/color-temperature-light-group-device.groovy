/**
 *  Trend Setter - Color Temperature Light Group Device
 *
 *  Copyright 2017 Chris Kitch
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Color Temperature Light Group Device", namespace: "kriskit.trendSetter", author: "Chris Kitch") {
		capability "Actuator"
		capability "Sensor"
		capability "Switch"
		capability "Switch Level"
        capability "Color Temperature"
        
        command "adjustLevel"
        command "adjustColorTemp"
        command "setColorName"
    	command "setColorRelax"
    	command "setColorEveryday"
    	command "setColorFocus"
    	command "nextColor"
        
        attribute "onPercentage", "number"
        attribute "levelSync", "string"
        attribute "colorTempSync", "string"
	}

  preferences {
    input name: "colorTempMin", type: "number", title: "Color temperature at lowest level(1%)", defaultValue: 2200, range: "2200..6500", displayDuringSetup: true, required: false
    input name: "colorTempMax", type: "number", title: "Color temperature at highest level(100%)", defaultValue: 4000, range: "2200..6500", displayDuringSetup: true, required: false
  }
  
	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.lights.multi-light-bulb-on", backgroundColor: "#79b821", nextState: "turningOff"
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.lights.multi-light-bulb-off", backgroundColor: "#ffffff", nextState: "turningOn"
				attributeState "turningOn", label: '${name}', action: "switch.off", icon: "st.lights.multi-light-bulb-on", backgroundColor: "#79b821", nextState: "turningOff"
				attributeState "turningOff", label: '${name}', action: "switch.on", icon: "st.lights.multi-light-bulb-off", backgroundColor: "#ffffff", nextState: "turningOn"
                attributeState "half", label: '${name}', action: "switch.on", icon: "st.lights.multi-light-bulb-on", backgroundColor: "#a3d164", nextState: "turningOn"
                attributeState "mostlyOn", label: 'Onish', action: "switch.on", icon: "st.lights.multi-light-bulb-on", backgroundColor: "#79b821", nextState: "turningOn"
                attributeState "mostlyOff", label: 'Offish', action: "switch.off", icon: "st.lights.multi-light-bulb-off", backgroundColor: "#d1e5b5", nextState: "turninOff"
			}
            
			tileAttribute ("device.onPercentage", key: "SECONDARY_CONTROL") {
				attributeState "onPercentage", label:'${currentValue}% On'
                attributeState "100", label:'All On'
                attributeState "0", label:'All Off'
			}
            
            tileAttribute("device.level", key: "SLIDER_CONTROL") {
              attributeState "default", label: '', action: "switch level.setLevel"
    		}
		}
        
        standardTile("levelLabel", "levelLable", height:1, width:1, decoration: "flat", inactiveLabel: true) {
            state "default", label:"Level", unit:"", icon: "st.illuminance.illuminance.bright"
        }
        
        controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 3, inactiveLabel: false) {
            state "level", action:"switch level.setLevel"
        }
        
        valueTile("levelValue", "device.level", inactiveLabel: true, height:1, width:1, decoration: "flat") {
            state "default", label:'${currentValue}%', unit:""
        }
        
        valueTile("levelSync", "device.levelSync", height:1, width:1) {
            state "default", label:' Sync ', unit:"", action: "adjustLevel", backgroundColor: "#ff9900"
            state "ok", label:'', unit:"", backgroundColor: "#00b509"
        }
        
        standardTile("colorTempLabel", "colorTempLabel", height:1, width:1, decoration: "flat", inactiveLabel: true) {
            state "default", label:"Temp", unit:"", icon: "st.illuminance.illuminance.bright"
        }
        
        controlTile("colorTempSliderControl", "device.colorTemperature", "slider", height: 1, width: 2, inactiveLabel: false, range:"(2200..4000)") {
            state "level", action:"color temperature.setColorTemperature"
        }
        
        standardTile("nextColor", "device.default", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
          state "default", label:"", action:"nextColor", icon:"https://github.com/edvaldeysteinsson/SmartThingsResources/raw/master/images/next_color.png"
        }
        
        valueTile("colorTempValue", "device.colorTemperature", inactiveLabel: true, height:1, width:1, decoration: "flat") {
            state "default", label:'${currentValue} K', unit:""
        }
        
        valueTile("colorTempSync", "device.colorTempSync", height:1, width:1) {
            state "default", label:' Sync ', unit:"", action: "adjustColorTemp", backgroundColor: "#ff9900"
            state "ok", label:'', unit:"", backgroundColor: "#00b509"
        }

		standardTile("colorRelax", "device.default", inactiveLabel: false, width: 2, height: 2) {
          state "default", label:"", action:"setColorRelax", backgroundColor:"#ECCF73"
        }

        standardTile("colorEveryday", "device.default", inactiveLabel: false, width: 2, height: 2) {
          state "default", label:"", action:"setColorEveryday", backgroundColor:"#FBECCB"
        }

        standardTile("colorFocus", "device.default", inactiveLabel: false, width: 2, height: 2) {
          state "default", label:"", action:"setColorFocus", backgroundColor:"#F5FBFB"
        }
	}
    
    main "switch"
    details(["switch", "levelLabel", "levelSliderControl", "levelValue", "levelSync", "colorTempLabel", "colorTempSliderControl", "nextColor", "colorTempValue", "colorTempSync", "colorRelax", "colorEveryday", "colorFocus"])
}

def parse(String description) {
}

def groupSync(name, values) {
	try {
    	"sync${name.capitalize()}"(values)	
    } catch(ex) {
    	log.error "Error executing 'sync${name.capitalize()}' method: $ex"
    }
}

def mapAttributeToCommand(name, value) {
	switch (name) {
    	case "switch":
        	if (value == "on")
        		return [command: "on", arguments: null]

        	if (value == "off")
        		return [command: "off", arguments: null]
       	break;
        
        case "level":
        	return [command: "setLevel", arguments: [value.toInteger()]]
    }
    
    log.error "Could not map '$name' attribute with value '$value' to a command."
}

// SWITCH
def on() {
	on(true)
}

def on(triggerGroup) {
	sendEvent(name: "switch", value: "on")
    sendEvent(name: "onPercentage", value: 100, displayed: false)
    
    if (triggerGroup)
    	parent.performGroupCommand("on")
}

def off() {
	off(true)
}

def off(triggerGroup) {
	sendEvent(name: "switch", value: "off")
    sendEvent(name: "onPercentage", value: 0, displayed: false)
    
    if (triggerGroup)
    	parent.performGroupCommand("off")
}

def syncSwitch(values) {
	log.debug "syncSwitch(): $values"
    
    def onCount = values?.count { it == "on" }
    def percentOn = (int)Math.floor((onCount / values?.size()) * 100)
    
    log.debug "Percent On: $percentOn"
    
    if (percentOn == 0 || percentOn == 100) {
    	if (percentOn == 0)
        	off(false)
        else
        	on(false)            
        return
    }
    
    def value = null
    
    if (percentOn == 50)
    	value = "half"
    else if (percentOn > 0 && percentOn < 50)
		value = "mostlyOff"
    else if (percentOn > 50 && percentOn < 100)
		value = "mostlyOn"
        
	sendEvent(name: "switch", value: value)
	sendEvent(name: "onPercentage", value: percentOn, displayed: false)
}

// LEVEL
def setLevel(val){
	setLevel(val, true)
}

def setLevel(val, triggerGroup) {
	log.debug "Setting level to $val"

    if (val < 0)
    	val = 0
    
    if( val > 100)
    	val = 100
    
    if (triggerGroup) {
       if (val == 0)
    	   off()
       else
    	   on()
    }
        
    sendEvent(name: "level", value: val, isStateChange: true)
    sendEvent(name: "switch.setLevel", value: val, isStateChange: true)
    
    if (triggerGroup)
    	parent.performGroupCommand("setLevel", [val])
}

def syncLevel(values) {
	log.debug "syncLevel(): $values"
    
    def valueCount = values?.size()
    def valueCountBy = values?.countBy { it }
    def matchValue = "bad"
    def level = device.currentValue("level")
    
    valueCountBy.each { value, count -> 
    	if (count == valueCount) {
        	level = value
            matchValue = "ok"
        	return true
        }
    }
    
    if (matchValue == "bad")
    	level = getAdjustmentLevel(values)
    
    setLevel(level, false)
    sendEvent(name: "levelSync", value: matchValue, displayed: false)
}

def adjustLevel() {
	def values = parent.getGroupCurrentValues("level")
    
    if (!values)
    	return
        
    def valueCountBy = values?.countBy { it }
    valueCountBy = valueCountBy?.sort { a, b -> b.value <=> a.value }
    
    def level = getAdjustmentLevel(values)
    
    setLevel(level)
}

def getAdjustmentLevel(values) {
    if (!values)
    	return
        
    def valueCountBy = values?.countBy { it }
    valueCountBy = valueCountBy?.sort { a, b -> b.value <=> a.value }
    
    def level = device.currentValue("level")
    
    if (valueCountBy.size() > 1) {        
        if (valueCountBy.size() == values.size()) {
        	log.debug "Values are all different - making average"
            level = Math.round(values.sum() / values.size())
        } else {
			log.debug "Some values are the same, choosing most popular"
            def firstItem = valueCountBy.find { true }
            level = firstItem.key
        }
    }
    
    return level
}


// COLOR TEMPERATURE
def setColorTemperature(val){
	setColorTemperature(val, true)
}

def setColorTemperature(val, triggerGroup) {
	log.debug "Setting color temperature to $val"

    if (val < colorTempMin)
    	val = colorTempMin
    
    if( val > colorTempMax)
    	val = colorTempMax
        
    if (triggerGroup)
       on()
        
    sendEvent(name: "colorTemperature", value: val, isStateChange: true)
    
    if (triggerGroup)
    	parent.performGroupCommand("setColorTemperature", [val])
}

def syncColorTemperature(values) {
	log.debug "syncColorTemp(): $values"
    
    def valueCount = values?.size()
    def valueCountBy = values?.countBy { it }
    def matchValue = "bad"
    def colorTemp = device.currentValue("colorTemperature")
    
    valueCountBy.each { value, count -> 
    	if (count == valueCount) {
        	colorTemp = value
            matchValue = "ok"
        	return true
        }
    }
    
    if (matchValue == "bad")
    	colorTemp = getAdjustmentColorTemp(values)
    
    setColorTemperature(colorTemp, false)
    sendEvent(name: "colorTempSync", value: matchValue, displayed: false)
}

def adjustColorTemp() {
	def values = parent.getGroupCurrentValues("colorTemperature")
    
    if (!values)
    	return
        
    def valueCountBy = values?.countBy { it }
    valueCountBy = valueCountBy?.sort { a, b -> b.value <=> a.value }
    
    def colorTemp = getAdjustmentColorTemp(values)
    
    setColorTemperature(colorTemp)
}

def getAdjustmentColorTemp(values) {
    if (!values)
    	return
        
    def valueCountBy = values?.countBy { it }
    valueCountBy = valueCountBy?.sort { a, b -> b.value <=> a.value }
    
    def colorTemp = device.currentValue("colorTemperature")
    
    if (valueCountBy.size() > 1) {        
        if (valueCountBy.size() == values.size()) {
        	log.debug "Values are all different - making average"
            colorTemp = Math.round(values.sum() / values.size())
        } else {
			log.debug "Some values are the same, choosing most popular"
            def firstItem = valueCountBy.find { true }
            colorTemp = firstItem.key
        }
    }
    
    return colorTemp
}

def setColorRelax() {
  setColorTemperature(2200)
}

def setColorEveryday() {
  setColorTemperature(2700)
}

def setColorFocus() {
  setColorTemperature(4000)
}

def nextColor() {
  def colorTemp = device.currentValue("colorTemperature")
  if(colorTemp < 2450) {
    setColorEveryday()
  } else if (colorTemp < 2950) {
    setColorFocus()
  } else {
    setColorRelax()
  }
}