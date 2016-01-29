/**
 *  SimpleButtonPushed
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
    name: "SendGarageDoorCommand",
    namespace: "TLH",
    author: "Terry Honn",
    description: "Simple button push",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("When this button is pushed, send the command.") {
		input "trigger", "capability.momentary", title: "Which switch?", required: true
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
	subscribe(trigger, "switch.on", switchOnHandler)
}


def switchOnHandler(evt) {
	log.debug 'in switchOnHandler, evt is ' + evt   
    def host = "192.168.1.100"
    def CameraPort= "80"
    def hosthex = convertIPtoHex(host)
    def porthex = convertPortToHex(CameraPort)
    def theAddress = "$hosthex:$porthex" 
        
    def path = "/GarageDoor/toggle.php" 
    log.debug "path is: $path"
    def headers = [:] 
    headers.put("HOST", "$host:$CameraPort") 
    def ip = "192.168.1.100:80"
	sendHubCommand(new physicalgraph.device.HubAction("""GET /GarageDoor/toggle.php HTTP/1.1\r\nHOST: $ip\r\n\r\n""", physicalgraph.device.Protocol.LAN))

}    
def subscribeToEvents() {
	subscribe(master, "switch.on", onHandler, [filterEvents: false])
    subscribe(location, null, lanResponseHandler, [filterEvents:false])
}

def onHandler(evt) {
    doHAMB()
}

def lanResponseHandler(evt) {
	log.debug "In response handler"
	log.debug "I got back ${evt.description}"
}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    log.debug "IP address entered is $ipAddress and the converted hex code is $hex"
    return hex

}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
    log.debug hexport
    return hexport
}

private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}


private String convertHexToIP(hex) {
	log.debug("Convert hex to ip: $hex") 
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}
