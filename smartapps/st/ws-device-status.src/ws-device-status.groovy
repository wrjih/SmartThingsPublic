/**
 *  A web service for getting current device status
 *
 *  Copyright 2015 Wan-rong Jih
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
 *******************************************************************************************/
definition(
    name: "WS - Device Status",
    namespace: "ST",
    author: "Wan-rong Jih",
    description: "Build a RESTful Web Service, get endpoint URL only.  This SmartApp will using OAuth.  The client program can use the URL to accessing ST devices.",
    category: "My Apps",
    iconUrl: "https://wrjih.files.wordpress.com/2015/09/st_ws_iagent4.png",
    iconX2Url: "https://wrjih.files.wordpress.com/2015/09/st_ws_iagent4.png",
    iconX3Url: "https://wrjih.files.wordpress.com/2015/09/st_ws_iagent4.png",
    oauth: [displayName: "RESTful Web Service Endpoint URL Only", displayLink: ""]
)

preferences {
    section ("Allow external service to retrieve these things...")     {
        try {
            input name: "contacts", type: "capability.contactSensor", title: "Contact Sensors", required: false, multiple: true
            input name: "motions", type: "capability.motionSensor", title: "Motion Sensors", required: false, multiple: true
            input name: "switches", type: "capability.switch", title: "Switches", required: false, multiple: true
            input name: "presences", type: "capability.presenceSensor", title: "Presence Sensors", required: false, multiple: true
            input name: "thermostats", type: "capability.thermostat", title: "Thermostats", required: false, multiple: true
            input name: "powermeters", type: "capability.powerMeter", title: "Power Meters", required: false, multiple: true
            input name: "alarms", type: "capability.alarm", title: "Siren Alarms", required: false, multiple: true
            input name: "water", type: "capability.waterSensor", title: "Water Sensors", required: false, multiple: true
            input name: "smoke", type: "capability.smokeDetector", title: "Smoke Detectors", required: false, multiple: true            
       } catch (e) {
           log.debug "Preferences Exception: $e"
       }
  }
}

// Specify Endpoints
mappings {
   // This is where you get call backs. This code will get errors until you define a legit endpoint (below). 
   // Error example "Service Manager DOES NOT RESPOND TO UPDATED HANDLER" 
   try {
       path("/listAll") {
           action: [ GET: "listThings"     ]
       }
   } catch (e) {
           log.debug "Mapping Exception: $e"
   }
}
  
// returns a list like
// [[name: "kitchen lamp", value: "off"], [name: "bathroom", value: "on"]]
def   listThings() {
   try {
       [
       contacts: contacts.collect{device(it,"contact")},
       motions: motions.collect{device(it,"motion")},
       switches: switches.collect{device(it,"switch")},
       presences: presences.collect{device(it,"presence")},
       thermostats: thermostats.collect{device(it, "thermostatOperatingState")},
       powerMeters: powermeters.collect{device(it,"power")},
       alarms: alarms.collect{device(it, "alarm")},
       water: water.collect{device(it,"water")},
       smoke: smoke.collect{device(it,"smoke")},
       location: [houseMode()]
       ]
   } catch (e) {
       log.debug "listThings Exception: $e"
   }

}

private device(it, type) {
   def device_state = [label:it.label, displayName: it.displayName, type:type, id:it.id, time:now()]
   
   try {
       for (attribute in it.supportedAttributes) {
           device_state."${attribute}" = it.currentValue("${attribute}")
       }
   } catch (e) {
       log.debug "device Exception: $e $device_state"
   }
   
   device_state ? device_state : null
}

private houseMode()
{
   def mode = [time:now(), locationId:location.id, name:location.name, 
               timeZone:location.timeZone.getID(), 
               timeOffset:location.timeZone.getRawOffset(),
               mode:location.mode]
               
   mode            
}