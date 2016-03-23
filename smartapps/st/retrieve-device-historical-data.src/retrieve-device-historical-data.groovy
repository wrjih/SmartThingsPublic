/**
 *  RESTful Web Service for retrieve device status history 
 *       - the client will use a WS endpoint URL to retrieve the historical data 
 *       - Event history is limited to the last seven days. 
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
 **/
definition(
    name: "Retrieve Device Historical Data",
    namespace: "ST",
    author: "Wan-rong Jih",
    description: "Build a RESTful Web Service, get endpoint URL only.  This SmartApp will using OAuth.  The client program can use the URL to accessing ST devices.",
    category: "My Apps",
    iconUrl: "https://wrjih.files.wordpress.com/2015/09/st_ws_iagent4.png",
    iconX2Url: "https://wrjih.files.wordpress.com/2015/09/st_ws_iagent4.png",
    iconX3Url: "https://wrjih.files.wordpress.com/2015/09/st_ws_iagent4.png",
    oauth: [displayName: "RESTful WS for retrieve historical sensor data", displayLink: ""]
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
           log.debug "<Event History> Preferences Exception: $e"
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
           log.debug "<event History> Mapping Exception: $e"
   }
}
  
// returns a list like, i.e. JSON format
// [[name: "kitchen lamp", value: "off"], [name: "bathroom", value: "on"]]
def   listThings() {
   try {
       [
       contacts: contacts.collect{ device(it) },
       motions: motions.collect{ device(it) },
       switches: switches.collect{ device(it) },
       presences: presence.collect{ device(it) },
       thermostats: thermostats.collect{ device(it) },
       powerMeters: powermeters.collect{ device(it) },
       alarms: alarms.collect{ device(it) },
       water: water.collect{ device(it) },
       smoke: smoke.collect{ device(it) } 
       ]
   } catch (e) {
       log.debug "<event History> listThings Exception: $e"
   }

}

private device(it)
{
   def event_list = it.events()
   def device_event = [Date: it.isoDate, EventId: it.id, DeviceId: it.deviceId, 
                       displayName: it.displayName, description: it.description, 
                       descriptionText: it.descriptionText]    
   def results = []
   
   try {
       for ( evt in event_list )
       {
           device_event.isoDate = evt.isoDate
           device_event.id = evt.id
           device_event.deviceId = evt.deviceId
           device_event.displayName = evt.displayName
           device_event.descriptionText = evt.descriptionText
           device_event.description = evt.description
           results += device_event
       }
   } catch (e) {
       log.debug "<event History> device Exception: $e"
   }
   
   results
}  