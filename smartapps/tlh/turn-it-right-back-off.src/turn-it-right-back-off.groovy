/**
 *  Turn It Right Back Off
 *
 *  Author: Terry Honn
 */
definition(
    name: "Turn It Right Back Off",
    namespace: "TLH",
    author: "Terry Honn",
    description: "Turn something off when it gets turned on",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2x.png"
)

preferences {
	section("When this switch comes on..."){
		input "switches", "capability.switch", multiple: true
	}
}


def installed()
{
	subscribe(switches, "switch.on", switchOnHandler)
}

def updated()
{
	unsubscribe()
	subscribe(switches, "switch.on", switchOnHandler)
}

def switchOnHandler(evt) {
	//log.debug "$evt.value: $evt, $settings"
	//log.trace "Turning off switches: $switches"
	switches.off()
}