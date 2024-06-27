package phonebook;

public class Entry {

    String id, name, surname;
    String[] phoneNumbers = new String[3];


    Entry(String identifier, String fname, String lname, String mainPhoneNumber, String... additionalPhoneNumber) {

        id = identifier;
        name = fname;
        surname = lname;
        phoneNumbers[0] = mainPhoneNumber;

        if(additionalPhoneNumber[0] != null) {
            if(additionalPhoneNumber[0].matches("^[0-9]{9,11}$")) phoneNumbers[1] = additionalPhoneNumber[0];
        }

        if(additionalPhoneNumber[1] != null) {
            if(additionalPhoneNumber[1].matches("^[0-9]{9,11}$")) phoneNumbers[2] = additionalPhoneNumber[1];
        }

    }

    String getEntryForWrite() {
        StringBuilder stringBuilder = new StringBuilder();
        String lineSep = System.lineSeparator();

        stringBuilder.append("{startEntry}").append(lineSep);

        stringBuilder.append(id).append(lineSep);
        stringBuilder.append(name).append(lineSep);
        stringBuilder.append(surname).append(lineSep);
        stringBuilder.append(phoneNumbers[0]).append(lineSep);

        if(phoneNumbers[1] != null) stringBuilder.append(phoneNumbers[1]).append(lineSep);
        if(phoneNumbers[2] != null) stringBuilder.append(phoneNumbers[2]).append(lineSep);

        stringBuilder.append("{endEntry}");

        return stringBuilder.toString();
    }

    String getEntryAsString() {

        StringBuilder stringBuilder = new StringBuilder();
        String lineSep = System.lineSeparator();

        stringBuilder.append(name).append(lineSep);
        stringBuilder.append(surname).append(lineSep);
        stringBuilder.append(phoneNumbers[0]).append(lineSep);

        if(phoneNumbers[1] != null) stringBuilder.append(phoneNumbers[1]).append(lineSep);
        if(phoneNumbers[2] != null) stringBuilder.append(phoneNumbers[2]).append(lineSep);

        return stringBuilder.toString();

    }

}
