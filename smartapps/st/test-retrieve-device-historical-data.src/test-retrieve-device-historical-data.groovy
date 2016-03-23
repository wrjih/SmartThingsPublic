definition(
    name: "TEST: Retrieve Device Historical Data",
    namespace: "ST",
    author: "Wan-rong Jih",
    description: "TEST SmartAPP",
    category: "My Apps",
)

preferences {
    section ("Select devices .....")     {
        try {
            input name: "contacts", type: "capability.contactSensor", title: "Contact Sensors", required: false, multiple: true
            input name: "presences", type: "capability.presenceSensor", title: "Presence Sensors", required: false, multiple: true
       } catch (e) {
           log.debug "<TEST> Preferences Exception: $e"
       }
  }
}

def installed() {
    def event_list = contacts.events(max:2)
    def event_date = contacts.events().isoDate
    def event_id = contacts.events().id
    def event_deviceId = contacts.events().deviceId
    def event_displayName = contacts.events().displayName
    def event_descText = contacts.events().descriptionText
    def event_desc = contacts.events().description
    def pres_date = presences.events().isoDate
    def pres_id = presences.events().id
    def pres_deviceId = presences.events().deviceId
    def pres_displayName = presences.events().displayName
    def pres_descText = presences.events().descriptionText
    def pres_desc = presences.events().description
    
    try {
        for (int i=0; i<event_list.size(); i++)
        {   for (int j=0; j<event_list[i].size(); j++)
            {   String str = "date: ${event_list[i][j].isoDate}, event Id: ${event_list[i][j].id}, " + 
                      "device id: ${event_list[i][j].deviceId}, displayName: ${event_list[i][j].displayName}, " +
                       "description: ${event_list[i][j].descriptionText},  ${event_list[i][j].description}"
                log.debug "<TEST1> ${str}"
            }
        }
    } catch (e) {
        log.debug "<TEST> installed exception 1, ${e}"
    }
    
    //try {
        for (int i=0; i<pres_id.size(); i++)
        {   for (int j=0; j<pres_id[i].size(); j++)
            {   log.debug "<TEST2> date: ${pres_date[i][j]}, event Id: ${pres_id[i][j]}, " + 
                      "device id: ${pres_deviceId[i][j]}, displayName: ${pres_displayName[i][j]}, " +
                      "description: ${pres_descText[i][j]}, ${pres_desc[i][j]}"
            }
        }
        /*
    } catch (e) {
        log.debug "<TEST> installed exception 2, ${e}"
    }
    */
    
}
