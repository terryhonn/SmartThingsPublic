/**
 *  DoorChecker
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
    name: "DoorChecker",
    namespace: "TLH",
    author: "Terry Honn",
    description: "Check when the house goes to sleep and do stuff if I left a door open",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("When I enter these modes") {
		input "newMode", "mode", title: "Modes?", multiple: true, required: false
	}
	section(" or when any of the following devices trigger..."){
		input "motion", "capability.motionSensor", title: "Motion Sensor?", required: false
		input "contact", "capability.contactSensor", title: "Contact Sensor?", required: false
		input "acceleration", "capability.accelerationSensor", title: "Acceleration Sensor?", required: false
		input "mySwitch", "capability.switch", title: "Switch?", required: false
		input "myPresence", "capability.presenceSensor", title: "Presence Sensor?", required: false
	}
    section("Or at these times...") {
    	input "time1", "time", title: "When?", required: false
        input "time2", "time", title: "When?", required: false
    }
    section("or these are not closed...") {
		input "doors", "capability.contactSensor", multiple: true, required: false
	}
	section("Then flash..."){
		input "switches", "capability.switch", title: "These lights", multiple: true
		input "numFlashes", "number", title: "This number of times (default 3)", required: false
	}
    section("And tell me via..."){
    	input "sonos", "capability.musicPlayer", title: "this Sonos player", required: true
    }
	section("Time settings in milliseconds (optional)..."){
		input "onFor", "number", title: "On for (default 1000)", required: false
		input "offFor", "number", title: "Off for (default 1000)", required: false
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	subscribe()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unschedule()
	unsubscribe()
	subscribe()
}
	
def subscribe() {
	if (contact) {
		subscribe(contact, "contact.open", contactOpenHandler)
	}
	if (acceleration) {
		subscribe(acceleration, "acceleration.active", accelerationActiveHandler)
	}
	if (motion) {
		subscribe(motion, "motion.active", motionActiveHandler)
	}
	if (mySwitch) {
		subscribe(mySwitch, "switch.on", switchOnHandler)
	}
	if (myPresence) {
		subscribe(myPresence, "presence", presenceHandler)
	}
    if (time1) {
    	schedule(time1, scheduleHandler)
    }
    if (time2) {
    	schedule(time2, scheduleHandler)
    }
	if (newMode != null) {
		subscribe(location, modeChangeHandler)
    }
}

def modeChangeHandler(evt) {
	log.debug "DoorChecker: Mode change to: ${evt.value}"

    // Have to handle when they select one mode or multiple
    if (newMode.any{ it == evt.value } || newMode == evt.value) {
		checkDoors()
    }
}

def appTouchHandler(evt) {
	tellThem("nothing")
}

private takeAction(evt) {

	log.trace "takeAction()"
	sonos.playTrackAndRestore(state.sound.uri, state.sound.duration, volume)
}




def motionActiveHandler(evt) {
	log.debug "motion $evt.value"
	flashLights()
}

def contactOpenHandler(evt) {
	log.debug "contact $evt.value"
	flashLights()
}

def accelerationActiveHandler(evt) {
	log.debug "acceleration $evt.value"
	flashLights()
}

def switchOnHandler(evt) {
	log.debug "switch $evt.value"
	flashLights()
}

def presenceHandler(evt) {
	log.debug "presence $evt.value"
	if (evt.value == "present") {
		flashLights()
	} else if (evt.value == "not present") {
		flashLights()
	}
}

def checkDoors() {
	log.debug "DoorChecker: Checking doors"
	def open = doors.findAll { it?.latestValue("contact") == "open" }
    if(open) {
    	def opendoors = ""
    	open.each {
			opendoors = it.name + ',' + opendoors
            }
    	//def msg = "Excuse me, but ${doors.name} is open!"
        def msg = "Excuse me, but ${opendoors} is open!"
        log.info msg
        //sendPush(msg)
        tellThem(msg)
        flashLights()
    } else {
    	def msg = "All doors & windows are closed."
        log.info msg
        sendNotificationEvent(msg)
    }
}

def scheduleHandler() {
	log.debug "scheduled time to flash lights"
    flashLights()
}

private tellThem(msg){
		if (msg) {
				state.sound = textToSpeech(msg instanceof List ? msg[0] : msg) // not sure why this is (sometimes) needed)
			}
			else {
				state.sound = textToSpeech("You selected the custom message option but did not enter a message in the $app.label Smart App")
			}
        sonos.playTrackAndRestore(state.sound.uri, state.sound.duration, volume)            
}

private flashLights() {
	def doFlash = true
	def onFor = onFor ?: 1000
	def offFor = offFor ?: 1000
	def numFlashes = numFlashes ?: 3

	log.debug "LAST ACTIVATED IS: ${state.lastActivated}"
	if (state.lastActivated) {
		def elapsed = now() - state.lastActivated
		def sequenceTime = (numFlashes + 1) * (onFor + offFor)
		doFlash = elapsed > sequenceTime
		log.debug "DO FLASH: $doFlash, ELAPSED: $elapsed, LAST ACTIVATED: ${state.lastActivated}"
	}

	if (doFlash) {
		log.debug "FLASHING $numFlashes times"
		state.lastActivated = now()
		log.debug "LAST ACTIVATED SET TO: ${state.lastActivated}"
		def initialActionOn = switches.collect{it.currentSwitch != "on"}
		def delay = 0L
		numFlashes.times {
			log.trace "Switch on after  $delay msec"
			switches.eachWithIndex {s, i ->
				if (initialActionOn[i]) {
					s.on(delay: delay)
				}
				else {
					s.off(delay:delay)
				}
			}
			delay += onFor
			log.trace "Switch off after $delay msec"
			switches.eachWithIndex {s, i ->
				if (initialActionOn[i]) {
					s.off(delay: delay)
				}
				else {
					s.on(delay:delay)
				}
			}
			delay += offFor
		}
	}
}