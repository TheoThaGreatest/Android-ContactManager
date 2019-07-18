package utd.com.contactmanager;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import static java.sql.Types.NULL;

/***************************************************************************************************
 * Content Manager Application
 * Written By: Theophilus Ojukwu II and Keerthana Ramesh
 **************************************************************************************************/
public class MainActivity extends AppCompatActivity {

    public String[] ListItems;

    InputStream InputStreamCounter;
    BufferedReader BufferedReaderCounter;

    InputStream InputStreamLoader;
    BufferedReader BufferedReaderLoader;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    int intCount = 0;
    DBManager db;

    ArrayList<String> listItems;
    ArrayAdapter adapter;
    ListView lv;


    ReArrangeList reArrangeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final MainActivity self = this;
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Click for new contact", Snackbar.LENGTH_LONG)
                        .setAction("New Contact", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                self.clickAction();
                            }
                        }).show();

            }
        });

        db = new DBManager(this);
        listItems = new ArrayList<>();
        lv = (ListView) findViewById(R.id.contactListView);

        //Sensor initalization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        reArrangeList = new ReArrangeList(mSensorManager, db, ListItems, intCount, lv); // db

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = lv.getItemAtPosition(position).toString();
                position+=1;
                //get id of name...send that through
                Intent intent = new Intent(MainActivity.this, ContactInfo.class);
                Bundle b=new Bundle();

                String rowID=String.valueOf(position);

                b.putString("name",rowID);
                //db.getRow(text);
                Toast.makeText(MainActivity.this, "clicked: " + text, Toast.LENGTH_SHORT).show();

                intent.putExtras(b); //adding data to be sent to ContactInfo.java
                startActivity(intent); //sending control to ContactInfo.java
            }
        });

        viewData();

        if(reArrangeList.ifShake() == true)
        {
            viewData();
            Toast.makeText(MainActivity.this, "This device is shook!", Toast.LENGTH_LONG);
        }
        //viewData();
    }

    private void viewData()
    {
        Cursor cursor;
        cursor = db.viewData();
        if(reArrangeList.ifShake() == true)
            cursor = db.reArrange();

        if(cursor.getCount() == 0)
        {
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        }else{
            while(cursor.moveToNext())
                listItems.add(cursor.getString(2) + " " + cursor.getString(1)); // index 2 is last_name index 1 is first_name, index 0 is id
        }
        adapter = new ArrayAdapter<>(this, R.layout.contact_layout, R.id.nameView, listItems);
        lv.setAdapter(adapter);
    }

    private void clickAction(){
        Intent intent = new Intent(MainActivity.this, ContactInfo.class);
        startActivity(intent);
    }

    /***********************************************************************************************
     * Function: populateContactList
     * Returns: Nothing
     * Description: This function open and reads the file counts the number of lines and stores the
     * lines in a SQlite database. Splits each line and outputs each contact to the list view
     *
     * Written by Theophilus Ojukwu II and Keerthana Ramesh
     **********************************************************************************************/
    private void populateContactList()
    {
        //opens the contacts.txt file in the raw resource folder
        InputStreamCounter = this.getResources().openRawResource(R.raw.contacts);
        BufferedReaderCounter = new BufferedReader(new InputStreamReader(InputStreamCounter));

        //counts number of lines
        try{
            while(BufferedReaderCounter.readLine() != null)
            {
                intCount++; //counts number of lines...
            }
            InputStreamCounter.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        InputStreamLoader = this.getResources().openRawResource(R.raw.contacts);
        BufferedReaderLoader = new BufferedReader(new InputStreamReader(InputStreamLoader));
        ListItems = new String[intCount];
        String displayLines[] = new String[intCount];
        try{

            for(int i = 0; i < intCount; i++)
            {
                ListItems[i] = BufferedReaderLoader.readLine();//reads lines
                String items[] = ListItems[i].split("\\\\t"); //splits the files
                String val = items[1] + " "  + items[0]; // formats the line --> last_name first_name
                displayLines[i] = val;

                if(db.insertData(items[0], items[1], items[2], null))
                {
                    Toast.makeText(MainActivity.this, "Data imported", Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(MainActivity.this, "Data was not imported", Toast.LENGTH_SHORT);
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /***********************************************************************************************
     * Function: listItemClick
     * Returns: nothing
     *
     * Description: This function is called when item on the listview is clicked and it sends
     * the position and the number of contacts to the second activity.
     *
     * Written by Theophilus Ojukwu II and Keerthana Ramesh
     **********************************************************************************************/
    private void listItemClick() {
        ListView list = (ListView) findViewById(R.id.contactListView); //accessing the list view

        //called when a row is clicked
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                position+=1; //incrementing the row number to range it between 1 and numOfLines
                String pos=String.valueOf(position); //gives the position of item clicked
                String num=String.valueOf(intCount); //gives the number of lines in the file
                Intent intent = new Intent(MainActivity.this, ContactInfo.class);
                Bundle b=new Bundle();

                b.putString("Position",pos);
                //b.putString("con", null);

                Toast.makeText(MainActivity.this, "clicked: " + pos, Toast.LENGTH_SHORT).show();
                //b.putString("TotalLines",num);
                //intent.putExtras(b); //adding data to be sent to ContactInfo.java
                //startActivity(intent); //sending control to ContactInfo.java
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.newContactAction){
            newContact_Click();
        }
        if(id == R.id.clearContactList)
        {
            clearButton_click();
        }
        if(id == R.id.importConatcts)
        {
            importFile_Cick();
        }

        return super.onOptionsItemSelected(item);
    }

    public void importFile_Cick() {
        populateContactList();
        viewData();
    }

    public void newContact_Click() {
        Intent newIntent = new Intent(MainActivity.this, ContactInfo.class);
        startActivity(newIntent);
    }

    public void clearButton_click() {
        db.clearTable();
        viewData();
    }
}
