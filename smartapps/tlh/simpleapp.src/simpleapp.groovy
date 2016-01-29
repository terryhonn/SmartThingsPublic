/**
 *  SimpleApp
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
    name: "SimpleApp",
    namespace: "TLH",
    author: "Terry Honn",
    description: "Simple",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("When activity on any of these sensors") {

        input "contactSensors", "capability.contactSensor",
            title: "Open/close sensors", multiple: true

        input "motionSensors", "capability.motionSensor",
            title: "Motion sensors?", multiple: true
    }
    section("Turn on these lights") {
        input "switches", "capability.switch", multiple: true
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	//initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
    log.debug "in initialize"
}

