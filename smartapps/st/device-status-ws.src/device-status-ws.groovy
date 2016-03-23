/**
 *  RESTful Web Service for getting device status - will get a WS endpoint URL  
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
 /** Getting access to a Web Services SmartApp, without creating an external application. 
 * You can use your web browser to make requests to get the API token and endpoint.
 * 
 * Assume that:
 * OAuth Client ID (CID): 968360a3-6cea-438b-a959-12baae18dbdc
 * OAuth Client Secret (CSID): bb76f4a4-210f-415d-bc17-77a43dea2926
 * 
 * Here are the steps:
 * 
 * ## Get the OAuth Authorization Code
 * In your web browser, paste in the following URL, replacing the CLIENT_ID with your OAuth Client ID:
 * https://graph.api.smartthings.com/oauth/authorize?response_type=code&client_id=#CID#&scope=app&redirect_uri=https%3A%2F%2Fgraph.api.smartthings.com%2Foauth%2Fcallback
 * 
 * This page will ask you to authorize your SmartThings devices. Select devices to authorize, and click Authorize.
 * 
 * This will redirect you to a page shows "Not Found. We're sorry, but that page doesn't exist."  That's OK.
 * The OAuth authorization code, which is the last parameter, the “code” parameter on the URL. 
 * 
 * For example, "code=Rl6OXF".
 *
 * 重覆測試時，不可以在browser用backward及refresh來取得Authorization Code。
 *
 * ## Get the API token
 * Paste the following into a new tab, replacing CLIENT_ID, CLIENT_SECRET, and CODE with the appropriate values:
 * https://graph.api.smartthings.com/oauth/token?grant_type=authorization_code&client_id=#CID#&client_secret=#CSID#&redirect_uri=https://graph.api.smartthings.com/oauth/callback&scope=app&code=#CODE#
 * 
 * Before you press enter, remember logout of your SmartThings.  Or you will receive error. 
 * 
 * The result will like the following:
 * {"access_token":"6f3efee5-ee81-4c41-92ab-8de778561113",
 *        "token_type":"bearer","expires_in":1576799999,"scope":"app"}
 * 
 * ## Discover the Endpoint URL
 * In a new window, replacing ACCESS_TOKEN with the access token you retrieved above.
 * https://graph.api.smartthings.com/api/smartapps/endpoints?access_token=#TOKEN#
 * 
 * The result will contain the endpoint URL for the SmartApp, i.e. the "url".
 * [{"oauthClient":{"clientId":"56d7f767-3c88-4c62-9654-b788d29d113e",
 *           "authorizedGrantTypes":"authorization_code"},
 *           "url":"/api/smartapps/installations/085b7c87-2d08-4293-acc6-78a08d164793"}]
 *
 * ## Make API Calls
 * In this SmartApp there is only one endpoint - listAll.  Paste the following into a new window, replacing 
 * Endpoint URL, Access Token with the appropriate values:
 * https://graph.api.smartthings.com#URL#/listAll/?access_token=#TOKEN#
 *
 * ## DONE 
 *
 * REFERENCES
 * URL OAuth - http://docs.smartthings.com/en/latest/smartapp-web-services-developers-guide/tutorial-part2.html#appendix-just-the-urls-please
 * Tutorial: Creating a REST SmartApp Endpoint - https://community.smartthings.com/t/tutorial-creating-a-rest-smartapp-endpoint/4331
 **/
definition(
    name: "Device Status WS",
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
       presences: presence.collect{device(it,"presence")},
       thermostats: thermostats.collect{device(it, "thermostatOperatingState")},
       powerMeters: powermeters.collect{device(it,"power")},
       alarms: alarms.collect{device(it, "alarm")},
       water: water.collect{device(it,"water")},
       smoke: smoke.collect{device(it,"smoke")}      
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
