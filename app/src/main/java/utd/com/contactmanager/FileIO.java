package utd.com.contactmanager;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileIO {

    private Context context;
    private File dir;
    private String fileName;
    private int numOfLines;

    /***********************************************************************************************
     * Constructor for Class FileIO
     * returns nothing.
     **********************************************************************************************/
    public FileIO(Context cxt, String fn)
    {
        this.context = cxt;
        this.fileName = fn;
        this.numOfLines = 0;
    }

    /***********************************************************************************************
     * Function: numberOfLines
     * returns: Integer
     *
     * Description: This function counts the lines in the file and returns an integer
     *
     * Written by Theophilus Ojukwu II
     **********************************************************************************************/
    public int numberOfLines()
    {
        InputStream InputStreamCounter;
        BufferedReader BufferedReaderCounter;
        InputStreamCounter = context.getResources().openRawResource(R.raw.contacts);
        BufferedReaderCounter = new BufferedReader(new InputStreamReader(InputStreamCounter));

        //counts number of lines
        try{
            while(BufferedReaderCounter.readLine() != null)
            {
                numOfLines++; //counts number of lines...
            }
            InputStreamCounter.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return numOfLines;
    }

    /***********************************************************************************************
     * Function: readFile
     * returns: String array
     *
     * Description: This function reads the file and stores each in in a String array and returns
     * this array.
     *
     * Written by Theophilus Ojukwu II
     ***********************************************************************************************/
    public String [] readFile()
    {
        InputStream InputStreamLoader;
        BufferedReader BufferedReaderLoader;

        InputStreamLoader = context.getResources().openRawResource(R.raw.contacts);
        BufferedReaderLoader = new BufferedReader(new InputStreamReader(InputStreamLoader));
        String [] fileContent = new String[numOfLines];

        try{

            for(int i = 0; i < numOfLines; i++)
            {
                fileContent[i] = BufferedReaderLoader.readLine();//reads lines
            }

            InputStreamLoader.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return fileContent;
    }
}