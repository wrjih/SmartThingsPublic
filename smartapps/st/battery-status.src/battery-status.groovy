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
}

// Show Status page
def pageStatus() 
{
   def image_url = [
       dead:"https://cdn2.iconfinder.com/data/icons/bitsies/128/BatteryDead-512.png",
       low:"https://cdn1.iconfinder.com/data/icons/technology-media-part-2/32/battery-low-512.png",
       medium:"https://cdn1.iconfinder.com/data/icons/smallicons-controls/32/614334-buttery_medium-512.png",
       health:"https://cdn1.iconfinder.com/data/icons/devices-and-networking-3/64/battery-medium-512.png"
   ]
   def image_name = ["dead", "low", "medium", "health"]
   def level_name = ( batteries ) ?
                      ["Crashed", "0%-${lowValue}%", 
                       "${lowValue.toInteger()+1}%-${mediumValue}%", 
                       "${mediumValue.toInteger()+1}%-100%"]
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
           href name:"gotoConfig", title: "Tap to select devices", page:"pageConfigure", 
                style:"page"
       }
   }
}

def pageConfigure()
{
   dynamicPage( name:"pageConfigure", title:"Device settings", 
         install:true, uninstall:false )
   {
       section( "Devices" )
       {   input( name: "batteries", type: "capability.battery",  
                  title: "Select devices",
                  multiple: true, required: true )
       }
   
       section( "Thresholds" )
       {   input( name: "lowValue", type: "number", title: "Low battery threshold (1..99)",
                  defaultValue: "20", range: "1..99", required: true )
           input( name: "mediumValue", type: "number", title: "Medium battery threshold (1..99)",
                  defaultValue: "70", range: "1..99", required: true )
       }

       section( "Battery last updated date" )
       {   input( name: "withinDays", type: "number", title: "Within (1..99) days",
                  defaultValue: "10", range: "1..99", required: false )
       }
       
       section( name:"Give this SmartApp a name", mobileOnly:true ) 
       {   label title: "Assign this SmartApp a name", required: false
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
   // battery levels index: 0->dead, 1->0%-low%, 2->(low+1)%-medium%, 3->(medium+1)%-
   def levels = [ [], [], [], [] ]
   
   // Check threshold 
   try
   {   if ( !lowValue.isNumber() || !mediumValue.isNumber() ||
           lowValue.toInteger() > mediumValue.toInteger() || 
           lowValue.toInteger() < 0 || mediumValue.toInteger() > 100 )
       {
           lowValue = "20"
           mediumValue = "70"
       } 
   } catch ( e ) 
   {  
       log.debug "<BATTERY> Thresholds ${lowValue} & ${mediumValue} error: ${e}"
   } 
   // battery level thresholds
   def low = lowValue.toInteger()
   def medium = mediumValue.toInteger()
   
   try 
   {   batteries.each   
       {
           def batteryValue = it.currentState( "battery" ).integerValue
           def value_str = "${batteryValue}% ${it.displayName}"
           
           if ( batteryValue < 0 || batteryValue > 100 )
           {
               levels[0] << [value_str]
           } else
           if ( batteryValue <= low )
           {
               levels[1] << [value_str]
           } else 
           if ( batteryValue > low && batteryValue <= medium ) 
           {
               levels[2] << [value_str]
           } else 
           if ( batteryValue > medium && batteryValue <= 100 )
           {
               levels[3] << [value_str]
           } 
       }
   } catch ( e ) 
   {
       log.debug "<BATTERY> device battery error: ${e}"
       if ( it ) 
           levels[0] << ["${it.displayName}:${e}"] 
       else
           levels[0] << ["${e}"]
   }
   
   log.info "<BATTERY> ${levels}"
   
   levels 
}