package utd.com.contactmanager;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ContactInfo extends AppCompatActivity {

    public EditText first_name; //First Name text field
    public EditText last_name; //Last Name text field
    public EditText phone; //Phone text field
    public EditText addressLine1;
    public EditText addressLine2;
    public EditText state;
    public EditText city;
    public EditText zip;

    public Button saveButton; //save button
    public Button deleteButon; //delete button

    public TextView tv;

    DBManager db;
    String firstName, lastName, phoneNumber, address;

    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        first_name = (EditText) findViewById(R.id.editFirstNameText);
        last_name = (EditText) findViewById(R.id.editLastNameText);
        phone =  (EditText) findViewById(R.id.editPhoneNumberText);
        addressLine1 = (EditText) findViewById(R.id.addressLine1editText);
        addressLine2 = (EditText) findViewById(R.id.addressline2editText);
        state = (EditText) findViewById(R.id.stateEditText);
        city = (EditText) findViewById(R.id.cityEditText);
        zip = (EditText) findViewById(R.id.zipEditText);

        saveButton = (Button) findViewById(R.id.saveButton);
        deleteButon = (Button) findViewById(R.id.deleteButton);

        tv = (TextView) findViewById(R.id.contactTitleView);

        //make the database
        db = new DBManager(this);
        saveButton.setText("Add");
        Bundle b = getIntent().getExtras(); //catching the data sent by MainActivity.java

        if(b != null)
        {
            String name_Clicked = b.getString("name"); //extracting position value
            pos = Integer.parseInt(name_Clicked);
            saveButton.setText("Save");
            editContact(db,pos);
        }



    }
    /***********************************************************************************************
     * Function editContact
     * returns: nothing
     *
     * Description: This function is called after an item is clicked from the list view. This
     * populates the contact page with the given data.
     *
     * Written by Theophilus Ojukwu II and Keerthana Ramesh
    ***********************************************************************************************/
    public void editContact(DBManager db, int pos) {
        //Displaying respective data
        tv.setText("Existing Contact");
        db.getReadableDatabase();
        Cursor row_data = db.getRow(pos);

        if(row_data != null && row_data.moveToNext()){

            firstName = row_data.getString(row_data.getColumnIndex("FIRST_NAME"));
            lastName = row_data.getString(row_data.getColumnIndex("LAST_NAME"));
            phoneNumber = row_data.getString(row_data.getColumnIndex("PHONE"));
            address = row_data.getString(row_data.getColumnIndex("ADDRESS"));
        }

        //initialize each text field on the activity
        first_name.setText(firstName);
        last_name.setText(lastName);
        phone.setText(phoneNumber);
        //addressLine1.setText(address);

        //ex 408 Spring Leaf Ct, Allen, Texas 75002
        /*String items[] = address.split(","); //splits the address
        //items[0] address line 1
        addressLine1.setText(items[0]);
        //items[1] city
        city.setText(items[1]);
        //items[2]  state and zip
        String splitLine[] = items[2].split(" ");
        state.setText(splitLine[0]);
        zip.setText(splitLine[1]);*/

    }

    public String retreiveAddress()
    {
        // ex. 408 Spring Leaf Ct, Allen, Tx 75002
        return addressLine1.getText().toString() + ", " +
                city.getText().toString() + ", " +
                state.getText().toString() + " " +
                zip.getText().toString();
    }

    public void saveContact_click(View view) {
        String newFirstName = first_name.getText().toString();
        String newLastName = last_name.getText().toString();
        String newPhoneNumber = phone.getText().toString();
        String newAddress = retreiveAddress();

        if(tv.getText().toString().equals("New Contact"))
        {
            db.insertData(newFirstName, newLastName, newPhoneNumber, newAddress);
            Toast.makeText(ContactInfo.this, "New Contact Saved.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            db.updateTable(newFirstName, newLastName, newPhoneNumber, newAddress, pos);
            Toast.makeText(ContactInfo.this, "Contact Saved.", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteContact_Click(View view) {

        if(tv.getText().toString().equals("New Contact"))
        {
            first_name.setText("");
            last_name.setText("");
            phone.setText("");
            addressLine1.setText("");
            addressLine2.setText("");
            state.setText("");
            city.setText("");
            zip.setText("");
        }
        else
        {
            db.deleteRow(firstName);
            Toast.makeText(ContactInfo.this, "Contact Deleted.", Toast.LENGTH_SHORT).show();
            //startActivityFromChild(MainActivity.class, null, 0);
        }
    }

    public void launchGoogleMapsActivity(View view) {
        Intent intent = new Intent(ContactInfo.this, DisplayMapsActivity.class);

        intent.putExtra("key", retreiveAddress());
        startActivity(intent);
    }
}
