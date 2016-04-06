/**
 *   This SmartApp helps you monitor the status of your SmartThings devices with batteries.
 *
 *  Copyright 2016 Wan-rong Jih
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
 *  version: 1.1.01 2016-04-06
 *      - The "battery failed" level divids into two level: failed & potential failed
 *        . failed: devices do not have any activity for a long time
 *        . potential failed: devices returns invalid capacity, e.g. null, non-numeric value 
 *      - Use the latest updated date of the device attributes instead of the battery latest 
 *        update date
 *  version: 1.0.01 2016-04-03
 *      - if invalid input, reset to default value
 *  version: 1.0 2016-04-02
 *      - select batteries that want to monitor
 *      - input low & medium level thresholds
 *      - input the allowed latest updated days
 **/
definition(
    name: "Battery Status",
    namespace: "ST",
    author: "Wan-rong Jih",
    description: "Display device battery level",
    category: "My Apps",
    iconUrl: "https://cdn1.iconfinder.com/data/icons/technology-media-part-2/32/battery-low-512.png",
    iconX2Url: "https://cdn1.iconfinder.com/data/icons/technology-media-part-2/32/battery-low-512.png",
    iconX3Url: "https://cdn1.iconfinder.com/data/icons/technology-media-part-2/32/battery-low-512.png"
)

preferences
{
   page ( name:"pageStatus" )
   page ( name:"pageConfigure" )
/*
   input( name: "batteries", type: "capability.battery", title: "Select devices",
                  multiple: true, required: true )
   */
}

// Show Status page
def pageStatus()
{
   def image_url = [
       dead:"https://cdn2.iconfinder.com/data/icons/bitsies/128/BatteryDead-512.png",
       potentialFail:"https://cdn3.iconfinder.com/data/icons/cosmo-color-multimedia-1/40/battery_3-512.png",
       low:"https://cdn1.iconfinder.com/data/icons/technology-media-part-2/32/battery-low-512.png",
       medium:"https://cdn1.iconfinder.com/data/icons/smallicons-controls/32/614334-buttery_medium-512.png",
       health:"https://cdn1.iconfinder.com/data/icons/devices-and-networking-3/64/battery-medium-512.png"
   ]
   def image_name = ["dead", "potentialFail", "low", "medium", "health"]
   def low = validateLowThreshold()
   def medium = validateMediumThreshold()
   def minus_days = validateWithinDays()
   def level_name = ( batteries ) ?
                      ["Failed (over ${minus_days} days without an update)",
                      "Potential Failed (incorrect battery capacity)",
                      "0%-${low}%", "${low+1}%-${medium}%", "${medium+1}%-100%"]
                      : null

   dynamicPage( name:"pageStatus", title:"Battery Status", install:true, uninstall:true )
   {
       if ( batteries )
       {
           def results = batteryLevels()
           for (int idx=0; idx<results.size(); idx++ )
           {
               if ( results[idx] )
               {
                   section( "${level_name[idx]}" )
                   {
                       def image_file =  image_url[image_name[idx]]
                       for ( data in results[idx] )
                       {
                           paragraph image: "${image_file}", "${data[0]}"
                       }
                   }
               }
           }
       }

       def section_name = ( batteries ) ? "Change settings" : "No device selected!"
       section( "${section_name}" )
       {
           href name:"gotoConfig", title: "", description: "Tap to change settings", 
                page:"pageConfigure", style:"page"
       }
   }
}

def pageConfigure()
{
   dynamicPage( name:"pageConfigure", title:"Device settings",
         install:false, uninstall:false )
   {
       section( "Devices" )
       {   input( name: "batteries", type: "capability.battery", title: "Select devices",
                  multiple: true, required: true )
       }

       section( "Thresholds" )
       {   input( name: "lowValue", type: "number", title: "Low battery threshold [1..99]",
                  defaultValue: "20", range: "1..99", required: true )
           input( name: "mediumValue", type: "number", title: "Medium battery threshold [1..99]",
                  defaultValue: "70", range: "1..99", required: true )
       }

       section( "Battery last updated date" )
       {   input( name: "withinDays", type: "number", title: "Within [1..365] days",
                  defaultValue: "10", range: "1..365", required: false,
                  description: "If the battery does not update its status for a long time, it may be crashed" )
       }

       section( "Give this SmartApp a name", mobileOnly:true )
       {   label title: "Assign a name", required: false
       }
   }
}

def installed() {
    log.info "<BATTERY> Initialized with settings: ${settings}"

    initialize()
}

def updated() {
    initialize()
}

def initialize()
{
   def results = ( batteries ) ?  batteryLevels() : ["No device selected!"]

   results
}

private batteryLevels()
{
   // battery levels index: 0->failed, 1->potential failed, 2->0%-low%, 
   //                       3->(low+1)%-medium%, 4->(medium+1)%-100%
   def levels = [ [], [], [], [], [] ]

   // battery level thresholds
   def low = validateLowThreshold()
   def medium = validateMediumThreshold()
   def minus_days = validateWithinDays()

   try
   {   batteries.each
       {   // battery status
           def batteryValue = getBatteryValue(it) ?: -99
           def value_str = "${getBatteryValue(it)}% ${it.displayName}"

           // the status updated date should later then the 'allowed_earliest_date'
           def allowed_earliest_date = new Date().minus( minus_days )
           // determine level for each battery
           if ( allowed_earliest_date.after( getLastUpdatedDate(it) ) )
           {
               value_str += " (last updated ${getLastUpdatedDate(it)})"
               levels[0] << [value_str]
           } else
           if ( batteryValue < 0 || batteryValue > 100 )
           {   levels[1] << [value_str]
           } else
           if ( batteryValue <= low )
           {   levels[2] << [value_str]
           } else
           if ( batteryValue > low && batteryValue <= medium )
           {   levels[3] << [value_str]
           } else
           if ( batteryValue > medium && batteryValue <= 100 )
           {   levels[4] << [value_str]
           }
       }
   } catch ( e )
   {
       log.debug "<BATTERY> device battery error: ${e}"
       if ( it && it.hasProperty("displayName") )
           levels[0] << ["${it.displayName}:${e}"]
       else
           levels[0] << ["${e}"]
   }

   log.info "<BATTERY> ${levels}"

   levels
}

private getBatteryValue( dev )
{
   def battery_value = null

   try
   {   def batteryStatus = dev.currentValue( "battery" )

       if ( batteryStatus )
       {   batteryStatus = batteryStatus.toString()
           if ( batteryStatus.isNumber() )
               battery_value = batteryStatus.toInteger()
       }
   } catch ( e )
   {   log.debug "<BATTERY> get battery value error: ${e}"
   }

   battery_value
}

private getLastUpdatedDate( dev )
{
   def update_date = new Date( 0 )

   try
   {   for ( atr in dev.supportedAttributes )
       {   def st = dev.currentState( "${atr}" )
           if ( st && st.hasProperty("date") && update_date.before( st.date ) )
           {   update_date = st.date
           }
       }
   } catch ( e )
   {   log.debug "<BATTERY> get last active date error: ${e}"
   }

   update_date
}

private validateLowThreshold()
{
   def result = 20
   if ( lowValue && lowValue.isNumber() &&
       lowValue.toInteger() >= 1 && lowValue.toInteger() <= 99 &&
       lowValue.toInteger() < mediumValue.toInteger() )
   {   result = lowValue.toInteger()
   }

   result
}

private validateMediumThreshold()
{
   def result = 70
   if ( mediumValue && mediumValue.isNumber() &&
       mediumValue.toInteger() >= 1 && mediumValue.toInteger() <= 99 &&
       lowValue.toInteger() < mediumValue.toInteger() )
   {   result = mediumValue.toInteger()
   }

   result
}

private validateWithinDays()
{
   def result = 10

   if ( withinDays && withinDays.isNumber() &&
       withinDays.toInteger() >= 1 && withinDays.toInteger() <= 365 )
   {   result = withinDays.toInteger()
   }

   result
}
