/**
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
    name: "PostToPi",
    namespace: "terryhonn",
    author: "Terry Honn",
    description: "Post to a Pi",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")



preferences {
    section("What switches...") {
        input "contacts", "capability.contactSensor", title: "Contacts", required: false, multiple: true
        input "switches", "capability.switch", title: "Switches", required: false, multiple: true
    }

}

def installed() {
    initialize()
}

def updated() {
    unsubscribe()
    initialize()
}

def initialize() {
    subscribe(contacts, "contact", handleContactEvent)
}

def handleContactEvent(evt) {
    sendValue(evt) { it == "open" ? "true" : "false" }
}



private sendValue(evt, Closure convert) {
    def compId = URLEncoder.encode(evt.displayName.trim())
    def streamId = evt.name
    def value = convert(evt.value)

    log.debug "Posting to Pi ${compId}, ${streamId} = ${value}"

//	def url = "http://grovestreams.com/api/feed?api_key=${channelKey}&compId=${compId}&${streamId}=${value}"
//def url = "http://162.195.229.213:85/api_command/gpio_set_value/23/1"
def url = "http://162.195.229.213:86/GarageDoor/toggle.php"

    def putParams = [
        uri: url,
        body: []
    ]

    httpPut(putParams) { 
        response -> 
        if (response.status != 200 ) {
            log.debug "post to pi failed, status = ${response.status}"
        }
    }

}