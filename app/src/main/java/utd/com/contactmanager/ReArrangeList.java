package utd.com.contactmanager;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import static android.content.Context.SENSOR_SERVICE;

public class ReArrangeList extends Activity implements SensorEventListener {

    private int numOfItems;

    private static final String TAG = "MainActivity";
    private static final int shakeThreshold = 800;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ListView lv;
    private DBManager db;

    private float lastX, lastY, lastZ;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private long lastUpdate = 0;

    private Boolean shook =  false;
    private String querey;
    int shakeCount = 0;
    String[] contactArray;
    String[] reversedContacts;

    public ReArrangeList(SensorManager mSensorManager, DBManager db, String[] contacts, int num, ListView list)
    {
        //Initializing Sensor variables
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        this.lv = list;
        this.numOfItems = num;
        reversedContacts = new String[numOfItems];
        db = new DBManager(this);
        initArrays(contacts);
        //onPause();
    }

    protected  void onPause()
    {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    protected void onResume()
    {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void initArrays(String[] contacts)
    {
        contactArray = new String[numOfItems];
        for(int i = 0; i < numOfItems; i++)
        {
            contactArray[i] = contacts[i];
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "onSensorChanged: x: " + event.values[0] + " y: " + event.values[1] + " z: " + event.values[2]);
        Sensor thisSensor = event.sensor;

        if(thisSensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            deltaX = event.values[0];
            deltaY = event.values[1];
            deltaZ = event.values[2];
        }
        long currentTime = System.currentTimeMillis();
        if((currentTime - lastUpdate) > 100)
        {
            long diffTime = (currentTime - lastUpdate);
            lastUpdate = currentTime;
            float speed = Math.abs(deltaX + deltaY + deltaZ - lastX - lastY - lastZ)/diffTime * 10000;

            if(speed > shakeThreshold){
                shakeCount++;
                if(shakeCount%2 == 1)
                {
                    shook = true;
                    //Toast.makeText(ReArrangeList.this, "SHOOK", Toast.LENGTH_LONG);
                }
            }
        }

        lastX = deltaX;
        lastY = deltaY;
        lastZ = deltaZ;
        //setNewDataToList();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void setNewDataToList() {
        ListView lv = (ListView) findViewById(R.id.contactListView);
        String[] linesForDisplay = new String[numOfItems];

        for(int i = 0; i < numOfItems; i++)
        {
            //ListItems[i] = BufferedReaderLoader.readLine();//reads lines
            String items[] = reversedContacts[i].split("\\\\t"); //splits the files
            String val = items[1] + " "  + items[0]; // formats the line
            linesForDisplay[i] = val;
        }
        // put reversed contracts into list view
        ArrayAdapter<String> adapter = new ArrayAdapter<>( this, R.layout.contact_layout, R.id.nameView, linesForDisplay);
        lv.setAdapter(adapter);
    }

    public boolean ifShake()
    {
        return shook;
    }

    public void setShook(Boolean shook) {
        this.shook = shook;
    }
}
