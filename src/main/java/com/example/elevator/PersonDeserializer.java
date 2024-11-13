package com.example.elevator;
import com.google.gson.*;
import java.lang.reflect.Type;

public class PersonDeserializer implements JsonDeserializer<Person> {
    @Override
    public Person deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Check if the "type" field exists
        if (!jsonObject.has("type") || jsonObject.get("type").isJsonNull()) {
            throw new JsonParseException("Missing 'type' field in person JSON data.");
        }

        String personType = jsonObject.get("type").getAsString().toLowerCase();

        // Retrieve other fields, with checks for their existence
        String id = jsonObject.has("ID") && !jsonObject.get("ID").isJsonNull() ? jsonObject.get("ID").getAsString() : null;
        int weight = jsonObject.has("weight") && !jsonObject.get("weight").isJsonNull() ? jsonObject.get("weight").getAsInt() : 0;
        int height = jsonObject.has("height") && !jsonObject.get("height").isJsonNull() ? jsonObject.get("height").getAsInt() : 0;
        int destinationFloor = jsonObject.has("destinationFloor") && !jsonObject.get("destinationFloor").isJsonNull() ? jsonObject.get("destinationFloor").getAsInt() : 0;

        // Validate that required fields are present
        if (id == null || weight <= 0 || height <= 0 || destinationFloor < 0) {
            throw new JsonParseException("Missing required person attributes (ID, weight, height, or destination floor).");
        }

        Person person;
        switch (personType) {
            case "doctor":
                Doctor doctor = new Doctor(weight, height);
                if (jsonObject.has("emergencyLevel") && !jsonObject.get("emergencyLevel").isJsonNull()) {
                    doctor.setEmergencyLevel(jsonObject.get("emergencyLevel").getAsInt());
                }
                person = doctor;
                break;

            case "patient":
                Patient patient = new Patient(weight, height);
                if (jsonObject.has("walkingAid") && !jsonObject.get("walkingAid").isJsonNull()) {
                    try {
                        String aid = jsonObject.get("walkingAid").getAsString();
                        patient.setWalkingAid(WalkingAid.valueOf(aid.toUpperCase()));  // Case-insensitive enum handling
                    } catch (IllegalArgumentException e) {
                        throw new JsonParseException("Invalid walking aid type: " + jsonObject.get("walkingAid").getAsString());
                    }
                }
                person = patient;
                break;

            default:
                throw new JsonParseException("Unknown person type: " + personType);
        }

        person.setDestinationFloor(destinationFloor);
        return person;
    }
}
