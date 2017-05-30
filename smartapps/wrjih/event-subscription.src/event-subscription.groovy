/*
 * Subscribe SmartThings' device events and store in Google spreadsheet
 *
 * Copyright 2017 Wan-rong Jih
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 *
 * REFERENCES: 
 *    - https://github.com/loverso-smartthings/googleDocsLogging
 *
 * UPDATE:
 * 2017-05-28 async HTTP, for not exceeding execution limits 
 * 2017-05-21 add URL parameter location, so that the client can save events according to location
 * 2017-05-10 subscribe all capabilities that have attributes
 */
include 'asynchttp_v1'

definition(
    name: "Event subscription",
    namespace: "wrjih",
    author: "Wan-rong Jih",
    description: "Subscribed events to Google Spreadsheet",
    category: "My Apps",
    singleInstance: true,
    iconUrl: "https://d2jixqqjqj5d23.cloudfront.net/assets/developer/imgs/icons/google-spreadsheet-icon.png",
    iconX2Url: "https://d2jixqqjqj5d23.cloudfront.net/assets/developer/imgs/icons/google-spreadsheet-icon@2x.png",
    iconX3Url: "https://d2jixqqjqj5d23.cloudfront.net/assets/developer/imgs/icons/google-spreadsheet-icon@2x.png")

preferences {
    section ("Subscribe the following events...")     {
        try {
            input name: "acceleration", type: "capability.accelerationSensor", title: "Acceleration", required: false, multiple: true
            input name: "alarm", type: "capability.alarm", title: "Alarm", required: false, multiple: true
            input name: "battery", type: "capability.battery", title: "Battery", required: false, multiple: true
            input name: "beacon", type: "capability.beacon", title: "Beacon", required: false, multiple: true
            input name: "bulb", type: "capability.bulb", title: "Bulb", required: false, multiple: true
            input name: "button", type: "capability.button", title: "Button", required: false, multiple: true
            input name: "carbonDioxide", type: "capability.carbonDioxideMeasurement", title: "Carbon Dioxide Measurement", required: false, multiple: true
            input name: "carbonMonoxide", type: "capability.carbonMonoxideDetector", title: "Carbon Monoxide Detector", required: false, multiple: true
            input name: "colorControl", type: "capability.colorControl", title: "Color Control", required: false, multiple: true
            input name: "colorTemperature", type: "capability.colorTemperature", title: "Color Temperature", required: false, multiple: true
            input name: "consumable", type: "capability.consumable", title: "Consumable Sensor", required: false, multiple: true
            input name: "contacts", type: "capability.contactSensor", title: "Contact Sensor", required: false, multiple: true
            input name: "doorControl", type: "capability.doorControl", title: "Door Control Sensor", required: false, multiple: true
            input name: "energyMeter", type: "capability.energyMeter", title: "Energy Meter", required: false, multiple: true
            input name: "eta", type: "capability.estimatedTimeOfArrival", title: "Estimated Time of Arrival Sensor", required: false, multiple: true
            input name: "garageDoorControl", type: "capability.garageDoorControl", title: "Garage Door Control Sensor", required: false, multiple: true
            input name: "holdableButton", type: "capability.holdableButton", title: "Holdable Button", required: false, multiple: true
            input name: "illuminance", type: "capability.illuminanceMeasurement", title: "Illuminance Measurement Sensor", required: false, multiple: true
            input name: "imageCapture", type: "capability.imageCapture", title: "Image Capture", required: false, multiple: true
            input name: "indicator", type: "capability.indicator", title: "LED Indicator Sensors", required: false, multiple: true
            input name: "infraredLevel", type: "capability.infraredLevel", title: "Infrared Level", required: false, multiple: true
            input name: "light", type: "capability.light", title: "Light Sensors", required: false, multiple: true
            input name: "lock", type: "capability.lock", title: "Lock", required: false, multiple: true
            input name: "lockOnly", type: "capability.lockOnly", title: "Lock Only", required: false, multiple: true
            input name: "mediaController", type: "capability.mediaController", title: "Media Controller", required: false, multiple: true
            input name: "motionSensor", type: "capability.motionSensor", title: "Motion Sensor", required: false, multiple: true
            input name: "musicPlayer", type: "capability.musicPlayer", title: "Music Player", required: false, multiple: true
            input name: "outlet", type: "capability.outlet", title: "Outlet", required: false, multiple: true
            input name: "phMeasurement", type: "capability.phMeasurement", title: "PH Measurement", required: false, multiple: true
            input name: "powerMeter", type: "capability.powerMeter", title: "Power Meter", required: false, multiple: true
            input name: "powerSource", type: "capability.powerSource", title: "Power Source", required: false, multiple: true
            input name: "presenceSensor", type: "capability.presenceSensor", title: "Presence Sensor", required: false, multiple: true
            input name: "relativeHumidityMeasurement", type: "capability.relativeHumidityMeasurement", title: "Relative Humidity Measurement", required: false, multiple: true            
            input name: "relaySwitch", type: "capability.relaySwitch", title: "Relay Switch", required: false, multiple: true            
            input name: "shockSensor", type: "capability.shockSensor", title: "Shock Sensor", required: false, multiple: true            
            input name: "signalStrength", type: "capability.signalStrength", title: "Signal Strength", required: false, multiple: true            
            input name: "sleepSensor", type: "capability.sleepSensor", title: "Sleep Sensor", required: false, multiple: true            
            input name: "smokeDetector", type: "capability.smokeDetector", title: "Smoke Detector", required: false, multiple: true            
            input name: "soundPressureLevel", type: "capability.soundPressureLevel", title: "Sound Pressure Level", required: false, multiple: true
            input name: "soundSensor", type: "capability.soundSensor", title: "Sound Sensor", required: false, multiple: true
            input name: "speechRecognition", type: "capability.speechRecognition", title: "Speech Recognition", required: false, multiple: true
            input name: "stepSensor", type: "capability.stepSensor", title: "Step Sensor", required: false, multiple: true
            input name: "switches", type: "capability.switch", title: "Switches", required: false, multiple: true
            input name: "switchLevel", type: "capability.switchLevel", title: "Switch Level", required: false, multiple: true
            input name: "tamperAlert", type: "capability.tamperAlert", title: "Tamper Alert", required: false, multiple: true
            input name: "temperatureMeasurement", type: "capability.temperatureMeasurement", title: "Temperature Measurement", required: false, multiple: true
            input name: "thermostat", type: "capability.thermostat", title: "Thermostat", required: false, multiple: true
            input name: "thermostatCoolingSetpoint", type: "capability.thermostatCoolingSetpoint", title: "Thermostat Cooling Setpoint", required: false, multiple: true
            input name: "thermostatFanMode", type: "capability.thermostatFanMode", title: "Thermostat Fan Mode", required: false, multiple: true
            input name: "thermostatHeatingSetpoint", type: "capability.thermostatHeatingSetpoint", title: "Thermostat Heating Setpoint", required: false, multiple: true
            input name: "thermostatMode", type: "capability.thermostatMode", title: "Thermostat Mode", required: false, multiple: true
            input name: "thermostatOperatingState", type: "capability.thermostatOperatingState", title: "Thermostat Operating State", required: false, multiple: true
            input name: "thermostatSetpoint", type: "capability.thermostatSetpoint", title: "Thermostat Setpoint", required: false, multiple: true
            input name: "threeAxis", type: "capability.threeAxis", title: "Three Axis", required: false, multiple: true
            input name: "timedSession", type: "capability.timedSession", title: "Timed Session", required: false, multiple: true
            input name: "touchSensor", type: "capability.touchSensor", title: "Touch Sensor", required: false, multiple: true
            input name: "ultravioletIndex", type: "capability.ultravioletIndex", title: "Ultraviolet Index", required: false, multiple: true
            input name: "valve", type: "capability.valve", title: "Valve", required: false, multiple: true
            input name: "voltageMeasurement", type: "capability.voltageMeasurement", title: "Voltage Measurement", required: false, multiple: true
            input name: "waterSensor", type: "capability.waterSensor", title: "Water Sensor", required: false, multiple: true
            
            input name: "windowShade", type: "capability.windowShade", title: "Window Shade", required: false, multiple: true
       } 
       catch ( e ) 
       {
           log.debug( "Logging Events to GoogleSheets: Preferences Exception: ${e}" )
       }
    }
    section ("Google Sheets") {
            input "urlKey", "text", title: "Google Script URL key", required: true
    }
    
}

def installed() {
    initialize()
}

def updated() 
{
    unsubscribe()
    initialize()
}

def initialize() 
{
    subscribe( acceleration, "acceleration", handleStringEvent )
    subscribe( alarm, "alarm", handleStringEvent )
    subscribe( battery, "battery", handleNumberEvent )
    subscribe( beacon, "beacon", handleStringEvent )
    subscribe( bulb, "bulb", handleStringEvent )
    subscribe( button, "button", handleStringEvent )
    subscribe( carbonDioxide, "carbonDioxide", handleNumberEvent )
    subscribe( carbonMonoxide, "carbonMonoxide", handleStringEvent )
    subscribe( colorControl, "color", handleStringEvent )
    subscribe( colorControl, "hue", handleNumberEvent )
    subscribe( colorControl, "saturation", handleNumberEvent )
    subscribe( colorTemperature, "colorTemperature", handleNumberEvent )
    subscribe( consumable, "consumableStatus", handleStringEvent )
    subscribe( contacts, "contact", handleStringEvent )
    subscribe( doorControl, "door", handleStringEvent )
    subscribe( energyMeter, "energy", handleNumberEvent )
    subscribe( eta, "eta", handleStringEvent )
    subscribe( garageDoorControl, "door", handleStringEvent )
    subscribe( holdableButton, "button", handleStringEvent )
    subscribe( illuminance, "illuminance", handleStringEvent )
    subscribe( imageCapture, "image", handleStringEvent )
    subscribe( indicator, "indicatorStatus", handleStringEvent )
    subscribe( infraredLevel, "infraredLevel", handleStringEvent )
    subscribe( light, "switch", handleStringEvent )
    subscribe( lock, "lock", handleStringEvent )
    subscribe( lockOnly, "lock", handleStringEvent )
    subscribe( mediaController, "activities", handleStringEvent )
    subscribe( mediaController, "currentActivity", handleStringEvent )
    subscribe( motionSensor, "motion", handleStringEvent )
    subscribe( musicPlayer, "level", handleNumberEvent )
    subscribe( musicPlayer, "mute", handleStringEvent )
    subscribe( musicPlayer, "status", handleStringEvent )
    subscribe( musicPlayer, "trackData", handleStringEvent )
    subscribe( musicPlayer, "trackDescription", handleStringEvent )
    subscribe( outlet, "switch", handleStringEvent )
    subscribe( phMeasurement, "pH", handleNumberEvent )
    subscribe( powerMeter, "power", handleNumberEvent )
    subscribe( powerSource, "powerSource", handleStringEvent )
    subscribe( presenceSensor, "presence", handleStringEvent )
    subscribe( relativeHumidityMeasurement, "humidity", handleNumberEvent )
    subscribe( relaySwitch, "switch", handleStringEvent )
    subscribe( shockSensor, "shock", handleStringEvent )
    subscribe( signalStrength, "lqi", handleNumberEvent )
    subscribe( signalStrength, "rssi", handleNumberEvent )
    subscribe( sleepSensor, "sleeping", handleStringEvent )
    subscribe( smokeDetector, "smoke", handleStringEvent )
    subscribe( smokeDetector, "carbonMonoxide", handleStringEvent )
    subscribe( soundPressureLevel, "soundPressureLevel", handleNumberEvent )
    subscribe( soundSensor, "sound", handleStringEvent )
    subscribe( speechRecognition, "phraseSpoken", handleStringEvent )
    subscribe( stepSensor, "goal", handleNumberEvent )
    subscribe( stepSensor, "steps", handleNumberEvent )
    subscribe( switches, "switch", handleStringEvent )
    subscribe( switchLevel, "level", handleNumberEvent )
    subscribe( tamperAlert, "tamper", handleStringEvent )
    subscribe( temperatureMeasurement, "temperature", handleNumberEvent )
    subscribe( thermostat, "coolingSetpoint", handleNumberEvent )
    subscribe( thermostat, "heatingSetpoint", handleNumberEvent )
    subscribe( thermostat, "thermostatFanMode", handleStringEvent )
    subscribe( thermostat, "thermostatMode", handleStringEvent )
    subscribe( thermostat, "thermostatOperatingState", handleStringEvent )
    subscribe( thermostat, "thermostatSetpoint", handleNumberEvent )
    subscribe( thermostatCoolingSetpoint, "coolingSetpoint", handleNumberEvent )
    subscribe( thermostatCoolingSetpoint, "coolingSetpointMin", handleNumberEvent )
    subscribe( thermostatCoolingSetpoint, "coolingSetpointMax", handleNumberEvent )
    subscribe( thermostatFanMode, "thermostatFanMode", handleStringEvent )
    subscribe( thermostatHeatingSetpoint, "heatingSetpoint", handleNumberEvent )
    subscribe( thermostatHeatingSetpoint, "heatingSetpointMin", handleNumberEvent )
    subscribe( thermostatHeatingSetpoint, "heatingSetpointMax", handleNumberEvent )
    subscribe( thermostatMode, "thermostatMode", handleStringEvent )
    subscribe( thermostatOperatingState, "thermostatOperatingState", handleStringEvent )
    subscribe( thermostatSetpoint, "thermostatSetpoint", handleNumberEvent )
    subscribe( thermostatSetpoint, "thermostatSetpointMin", handleNumberEvent )
    subscribe( thermostatSetpoint, "thermostatSetpointMax", handleNumberEvent )
    subscribe( threeAxis, "threeAxis", handleStringEvent )
    subscribe( timedSession, "sessionStatus", handleStringEvent )
    subscribe( touchSensor, "touch", handleStringEvent )
    subscribe( ultravioletIndex, "ultravioletIndex", handleNumberEvent )
    subscribe( valve, "contact", handleStringEvent )
    subscribe( valve, "valve", handleStringEvent )
    subscribe( voltageMeasurement, "voltage", handleNumberEvent )
    subscribe( waterSensor, "water", handleStringEvent )    
    subscribe( windowShade, "windowShade", handleStringEvent )
    
    log.debug( "Logging Events to GoogleSheets: End initialize()" )
}

def handleStringEvent(evt) 
{
    sendValue(evt) { it }
}

def handleNumberEvent( evt ) 
{
    sendValue(evt) { it.toString() }
}

private sendValue(evt, Closure convert) 
{
   def loc = URLEncoder.encode( location.name )
   def tz = URLEncoder.encode( location.timeZone.getID() )
   def keyId = URLEncoder.encode( evt.displayName.trim() + " " + evt.name )
   def value = URLEncoder.encode( convert(evt.value) )
   def exec = "exec"  

   def url = "https://script.google.com/macros/s/${urlKey}/${exec}?location=${loc}&timezone=${tz}&${keyId}=${value}"
   log.debug( "Logging Events to GoogleSheets: ${url}" )
    
   def putParams = [ uri: url ]

   try 
   {   
       asynchttp_v1.get('responseHandler', putParams)
   } catch ( err )
   {
       log.error( "Logging Events to GoogleSheets: ${err}" )
   }
}

def responseHandler(response, data)
{
   log.debug( "Logging Events to GoogleSheets: response status ${response.status}" )
   if ( response.hasError() ) 
   {
       log.error( "Logging Events to GoogleSheets: response error ${response.getErrorMessage()}" )
   }
}