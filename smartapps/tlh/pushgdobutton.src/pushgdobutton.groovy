/**
 *  opengarage
 *
 *  Copyright 2015 Terry Honn
 *
 */
definition(
    name: "PushGDOButton",
    namespace: "TLH",
    author: "Terry Honn",
    description: "PushGDOButton",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Activate when this switch/button is activated:") {
        input "theswitch", "capability.switch", title: "Push Button", required: false
        //input "doorOpener", "capability.momentary", title: "Door Opener", required: false, multiple: false
    }
}


def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
    //subscribe(doorOpener, "switch.on", handleContactEvent)
    //subscribe(theSwitch, "switch.on", handleContactEvent)
	// TODO: subscribe to attributes, devices, locations, etc.
}

def handleContactEvent(evt) {
    sendValue(evt) { it == "open" ? "true" : "false" }
}
