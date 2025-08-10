package ae.oleapp.models;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import java.util.HashSet;

public class ContactFetcher {

    public static String fetchContactList(Context context) {
        HashSet<String> contactNumbers = new HashSet<>(); // Use HashSet to avoid duplicates
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            while (cursor.moveToNext()) {
                // Check if the column index is valid
                if (columnIndex >= 0) {
                    String phoneNumber = cursor.getString(columnIndex);
                    // Clean up the phone number (remove spaces, dashes, etc.)
                    phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
                    contactNumbers.add(phoneNumber);
                }
            }
            cursor.close();
        }

        StringBuilder contactListBuilder = new StringBuilder();
        for (String phoneNumber : contactNumbers) {
            contactListBuilder.append(phoneNumber);
            contactListBuilder.append(",");
        }

        // Remove the last comma, if any
        if (contactListBuilder.length() > 0) {
            contactListBuilder.deleteCharAt(contactListBuilder.length() - 1);
        }

        return contactListBuilder.toString();
    }
}
