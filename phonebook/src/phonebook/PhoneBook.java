package phonebook;

import java.io.*;
import java.util.ArrayList;
import java.util.Locale;

public class PhoneBook {

    final File book;

    final String bookExtension = ".pb";
    final String rawBookName = "phonebook";
    final String bookName;

    final ArrayList<Entry> entries = new ArrayList<>();

    int highestID = -1;

    /**
     * @param customBookName if provided the file containing the phonebook will use it as a name
     */

    PhoneBook(String... customBookName) {
        if(customBookName.length > 0) {
            bookName = customBookName[0]+bookExtension;
        } else {
            bookName = rawBookName+bookExtension;
        }

        book = new File(bookName);

        if(!book.exists()) {
            try {
                boolean bookFileCreated = book.createNewFile();

                if(bookFileCreated) {
                    System.out.println("Utworzono nową książkę telefoniczną: "+bookName);
                } else {
                    System.out.println("Nie udało się utworzyć książki telefonicznej!");
                }
            } catch (IOException e) { e.printStackTrace(); }
        } else {
            loadBook();
        }
    }

    /**
     * Adds one entry based on provided data
     * @param fname first name
     * @param lname last name
     * @param mainPhoneNumber main phone number
     * @param additionalPhoneNumber additional phone numbers (optional)
     */

    void addEntry(String fname, String lname, String mainPhoneNumber, String... additionalPhoneNumber) {
        String identifier = String.valueOf(++highestID);

        Entry entry = new Entry(
                identifier,
                fname,
                lname,
                mainPhoneNumber,
                additionalPhoneNumber
        );

        entries.add(entry);
        writeChanges();
    }

    /**
     * Adds multiple entries based on provided array
     * @param data two-dimensional array containing the info needed to create an entry
     */

    void addEntry(String[][] data) {
        String identifier;

        for(String[] entry : data) {
            identifier = String.valueOf(++highestID);

            entries.add(new Entry(
                    identifier,
                    entry[0],
                    entry[1],
                    entry[2],
                    entry[3],
                    entry[4]
                )
            );
        }

        writeChanges();
    }

    /**
     * Overwrites entry with a given ID with the provided data
     * @param fname first name
     * @param lname last name
     * @param mainPhoneNumber main phone number
     * @param additionalPhoneNumber additional phone numbers (optional)
     */

    void modifyEntry(String id, String fname, String lname, String mainPhoneNumber, String... additionalPhoneNumber) {

        for(int x = 0; x < entries.size(); x++) {
            if(entries.get(x).id.equals(id.toLowerCase(Locale.ROOT))) {
                System.out.println(entries.get(x).id+", "+id);
                entries.set(x, new Entry(
                        id,
                        fname,
                        lname,
                        mainPhoneNumber,
                        additionalPhoneNumber
                ));
            }
        }



        writeChanges();
    }

    /**
     * Deletes one entry based on a given ID
     * @param entryID a string containing entries' ID
     */

    void deleteEntry(String entryID) {

        int iterator = entries.size();

        while(iterator-- > 0) {
            if(entries.get(iterator).id.equals(entryID)) {
                entries.remove(iterator);
            }
        }

        writeChanges();
    }

    /**
    *   Deletes multiple entries based on their IDs
    *   @param entryIDs an array of strings containing entries' IDs
    **/

    void deleteEntry(String[] entryIDs) {
        for(int x = 0; x < entries.size(); x++) {
            for(int y = 0; y < entryIDs.length; y++) {
                if(entries.get(x).id.equals(entryIDs[y])) {
                    entries.remove(x);
                }
            }
        }

        writeChanges();
    }

    /**
     * Searches for entries containing a provided keyword
     * @param keyword string containing the data you want to look for
     * @return array of entries that contain provided keyword
     */

    Entry[] search(String keyword) {

        ArrayList<Entry> matchedEntries = new ArrayList<>();

        for(Entry entry : entries) {
            if(entry.getEntryAsString().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT))) {
                matchedEntries.add(entry);
            }
        }

        Entry[] matchedEntriesArr = new Entry[matchedEntries.size()];

        for(int x = 0; x < matchedEntries.size(); x++) {
            matchedEntriesArr[x] = matchedEntries.get(x);
        }

        return matchedEntriesArr;

    }

    /**
     * Writes any changes made to ArrayList entries into the phonebook file
     */

    void writeChanges() {
        try {

            BufferedWriter bookWriter = new BufferedWriter(new FileWriter(book));
            StringBuilder stringBuilder = new StringBuilder();

            for(Entry entry : entries) {
                stringBuilder.append(entry.getEntryForWrite()).append(System.lineSeparator());
            }

            bookWriter.write(stringBuilder.toString());
            bookWriter.close();

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads phonebook file's content into ArrayList entries
     */

    void loadBook() {

        try {

            BufferedReader bookReader = new BufferedReader(new FileReader(book));

            while (bookReader.ready()) {

                String currentLine = bookReader.readLine();

                if (currentLine.contains("{startEntry}")) {

                    currentLine = bookReader.readLine();
                    ArrayList<String> data = new ArrayList<>();

                    while(!currentLine.contains("{endEntry}")) {
                        data.add(currentLine);
                        currentLine = bookReader.readLine();
                    }

                    String[] additionalNumbers = new String[2];

                    try {
                        additionalNumbers[0] = data.get(4);
                        additionalNumbers[1] = data.get(5);
                    } catch (Exception e) {
                        e.getSuppressed();
                    }

                    int idToInt = Integer.parseInt(data.get(0));

                    if(idToInt > highestID) highestID = idToInt;

                    entries.add(
                            new Entry(
                                    data.get(0),
                                    data.get(1),
                                    data.get(2),
                                    data.get(3),
                                    additionalNumbers[0],
                                    additionalNumbers[1]
                            )
                    );

                }

            }

            bookReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
