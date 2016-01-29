/**
 *  EndpointExample
 *
 *  Copyright 2015 Terry Honn
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
definition(
    name: "Endpoint",
    namespace: "TLH",
    author: "Terry Honn",
    description: "endpoint",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


/**
 *  App Endpoint API Access Example
 *
 *  Author: SmartThings
 */

preferences {
	section("Allow Endpoint to Control These Things...") {
		input "switches", "capability.switch", title: "Which Switches?", multiple: true, required: false
        input "dimmers", "capability.switchLevel", title: "Which Dimmers?", multiple: true,required: false
        input "locks", "capability.lock", title: "Which Locks?", multiple: true, required: false
        
	}
}

mappings {

	path("/switches") {
		action: [
			GET: "listSwitches"
		]
	}
	path("/switches/:id") {
		action: [
			GET: "showSwitch"
		]
	}
	path("/switches/:id/:command") {
		action: [
			GET: "updateSwitch"
		]
	}
	path("/switches/:id/:command/:level") {
		action: [
			GET: "updateSwitch"
		]
	}
    path("/dimmers") {
		action: [
			GET: "listDimmers"
		]
    }
    path("/dimmers/:id") {
		action: [
			GET: "showDimmer"
		]
	}
	path("/dimmers/:id/:command") {
		action: [
			GET: "updateDimmer"
		]
	}

	path("/locks") {
		action: [
			GET: "listLocks"
		]
	}
	path("/locks/:id") {
		action: [
			GET: "showLock"
		]
	}
	path("/locks/:id/:command") {
		action: [
			GET: "updateLock"
		]
	}    
    
}

def installed() {}

def updated() {}

//dimmers
def listDimmers() {
	dimmers.collect{device(it,"dimmer")}
}

def showDimmer() {
	show(dimmers, "dimmer")
}
void updateDimmer() {
	update(dimmers)
}

//switches
def listSwitches() {
	switches.collect{device(it,"switch")}
}

def showSwitch() {
	show(switches, "switch")
}
void updateSwitch() {
	update(switches)
}

//locks
def listLocks() {
	locks.collect{device(it,"lock")}
}

def showLock() {
	show(locks, "lock")
}

void updateLock() {
	update(locks)
}



def deviceHandler(evt) {}

private void update(devices) {
	log.debug "update, request: params: ${params}, devices: $devices.id"
    
    
	//def command = request.JSON?.command
    def command = params.command
    //let's create a toggle option here
	if (command) 
    {
		def device = devices.find { it.id == params.id }
		if (!device) {
			httpError(404, "Device not found")
		} else {
        	if(command == "toggle")
       		{
            	if(device.currentValue('switch') == "on")
                  device.off();
                else
                  device.on();
       		}
       		else
       		{
				//device.setLevel("$command")
                if (command == "on"){
                 device."$command"()
                 }
                 else
                 {
                 if (command == "off") {
                 device."$command"()
                 }
                 else{
                 if (command == "level") {
                    log.debug "got command of level to ${level}"
                 def leveltoset = params.level
                 device.on()
                 device.setLevel(leveltoset as int)
                 }
                 }
                }
            }
		}
	}
}

private show(devices, type) {
	def device = devices.find { it.id == params.id }
	if (!device) {
		httpError(404, "Device not found")
	}
	else {
		def attributeName = type == "motionSensor" ? "motion" : type
		def s = device.currentState(attributeName)
		[id: device.id, label: device.displayName, value: s?.value, unitTime: s?.date?.time, type: type]
	}
}


private device(it, type) {
	it ? [id: it.id, label: it.label, type: type] : null
}