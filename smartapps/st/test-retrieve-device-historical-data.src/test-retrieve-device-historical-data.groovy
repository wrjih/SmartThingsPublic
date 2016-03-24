definition(
    name: "TEST: Retrieve Device Historical Data",
    namespace: "ST",
    author: "Wan-rong Jih",
    description: "Build a RESTful Web Service, get endpoint URL only.  This SmartApp will using OAuth.  The client program can use the URL to accessing ST devices.",
    category: "My Apps",
    iconUrl: "https://wrjih.files.wordpress.com/2015/09/st_ws_iagent4.png",
    iconX2Url: "https://wrjih.files.wordpress.com/2015/09/st_ws_iagent4.png",
    iconX3Url: "https://wrjih.files.wordpress.com/2017/09/st_ws_iagent4.png",
    oauth: [displayName: "RESTful WS for retrieve historical sensor data", displayLink: ""]
)

preferences {
    section ("Select the devices...")     {
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
           log.debug "<TEST> Preferences Exception: $e"
       }
  }
}

def installed() {
    initialize()
}

def updated() {
    initialize()
}

def initialize() {
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
       log.debug "<TEST> initialize Exception: $e"
   }

}

private device(it)
{
   def event_list = it.events()
   def results = []
   def idx = 0
   
   try {
       for ( evt in event_list )
       {    
           def evt_map = [ : ]
           evt_map.date = evt.isoDate
           evt_map.id = evt.id
           evt_map.deviceId = evt.deviceId
           evt_map.displayName = evt.displayName
           evt_map.descriptionText = evt.descriptionText
           evt_map.description = evt.description
            
           results[idx++] = evt_map
       }
   } catch (e) {
       log.debug "<TEST> device Exception: $e"
   }
   
   log.debug "<TEST> $results"
   
   results
}  