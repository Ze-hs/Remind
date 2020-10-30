package com.student.su.to_dolist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import java.util.ArrayList;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
 @version May 27, 2019
 This class shows the main screen for a to-do list app. It shows a group of objectives or goals
 that the user has put and displays it in a list. The user can click on each objective to edit
 them or create a new one using the bottom at the bottom of the screen
 */
public class MainActivity extends Activity {
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> description = new ArrayList<>();
    private Button addNewButton;
    private CustomAdapter adapter;
    private ItemListener itemRowListener;
    private SharedPreferences savedPrefs;
    private String action = "";
    private String[] newInfo = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNewButton = findViewById(R.id.addEventButton);
        OnClickListener buttonEventListener = new ButtonListener();
        addNewButton.setOnClickListener(buttonEventListener);

        //This custom adapter allows two textViews to be shown on each row on the list
        adapter = new CustomAdapter(this, names);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        //Listener checks which row of the list the user clicked on
        itemRowListener = new ItemListener();
        listView.setOnItemClickListener(itemRowListener);

        savedPrefs = getSharedPreferences( "eventsPrefs", MODE_PRIVATE );
    }

    /*
     * This method allows for instance variables to be stored when the user leaves the screen
     */
    @Override
    public void onPause() {
        Editor prefsEditor = savedPrefs.edit();

        //Converting arrayLists into strings and saving them
        prefsEditor.putString( "storedName", listToString(names,30) );
        prefsEditor.putString( "storedDate", listToString(dates,30) );
        prefsEditor.putString( "storedDescription", listToString(description,300) );
        prefsEditor.commit();

        super.onPause();
    }

    /*
     * This method allows for instance variables which were previously saved to be retrieved
     * when the user comes back to this screen
     */
    @Override
    public void onResume() {
        super.onResume();

        //Load the instance variables back(or default values)
        if (savedPrefs.getString("storedDescription", null) != null) {
            //Clear the list to allows the saved data to be stored
            names.clear();
            dates.clear();
            description.clear();

            names.addAll(stringToList(savedPrefs.getString("storedName", ""), 30));
            dates.addAll(stringToList(savedPrefs.getString("storedDate", ""), 30));
            description.addAll(stringToList(savedPrefs.getString("storedDescription", ""), 300));
        }

        //If the user chose delete on an existing event, remove it
        if (action.equals("del")) {
            names.remove(Integer.parseInt(newInfo[0]));
            dates.remove(Integer.parseInt(newInfo[0]));
            description.remove(Integer.parseInt(newInfo[0]));
        }
        //If the user added a new event
        else if (action.equals("add")) {
            names.add(newInfo[1]);
            dates.add(newInfo[2]);
            description.add(newInfo[3]);
        }
        //If the user is editing an existing event
        else if (action.equals("edit")) {
            names.set(Integer.parseInt(newInfo[0]), newInfo[1]);
            dates.set(Integer.parseInt(newInfo[0]), newInfo[2]);
            description.set(Integer.parseInt(newInfo[0]), newInfo[3]);
        }
        action = "";
    }

    /*
     * This methods allows data to be given back from another activity. It takes in
     * 2 int values and an Intent object
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Stores an int whose value depend on which row the user clicked on the list
        int position = itemRowListener.getClickPosition();

        //If the transfer was successful, retrieve the data and save it into an array
        if(resultCode == Activity.RESULT_OK) {
            Bundle eventData = data.getExtras();
            newInfo[0] = Integer.toString(position);
            newInfo[1] = eventData.getString("newName");
            newInfo[2] = eventData.getString("newDate");
            newInfo[3] = eventData.getString("newDescription");

            //If the user chose delete on an existing event, set action to delete
            if (eventData.getString("option").equals("rev") && requestCode == 0) {
                action = "del";
            }
            //If the user is editing an existing event, set action to edit
            else if (eventData.getString("option").equals("add") && requestCode == 0) {
                action = "edit";
            }
            //If the user added a new event, set action to add
            else if (eventData.getString("option").equals("add") && requestCode == 1) {
                action = "add";
            }
        }
        //Updates the listView
        adapter.notifyDataSetChanged();
    }

    /*
     * This helper method changes an arrayList into a string. It takes in 2 parameters,
     * an arrayList and a int which determines the number of characters in each element
     */
    private static String listToString(ArrayList<String> list, int maxChar){
        String stringList = "";
        String tempString;

        for (int element = 0; element < list.size(); element++){
            //Make each element maxChar characters long and appends them to a string
            tempString = String.format("%" + -maxChar + "s", list.get(element));
            stringList += tempString;
        }
        return stringList;
    }

    /*
     * This helper method changes a string into an arrayList. It takes in 2 parameters,
     * a string and a int which determines the number of characters in each element
     */
    private static ArrayList<String> stringToList(String string, int separator){
        ArrayList<String> list = new ArrayList<>();
        //Splits the string using substring with int length given by the user
        for (int elements = string.length()/ separator; elements > 0; elements--){
            list.add(string.substring(0, separator).trim());
            string = string.substring(separator);
        }
        return list;
    }

    /*
     * This class allows for a custom view for each row in the list view
     */
    class CustomAdapter extends ArrayAdapter<String> {
        public CustomAdapter(Context context, ArrayList<String> name) {
            super(context, R.layout.list_row, name);
        }

        /*
         * This method sets the view for each individual row on the listView, It takes in an
         * int, a View object and a ViewGroup object which are automatically given
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Creates a new view object or layout from an xml file
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View customView = convertView;

            if(customView == null){
                customView = inflater.inflate(R.layout.list_row, parent, false);
            }

            //set the text for labels in each row of the list
            TextView nameTextLabel = customView.findViewById(R.id.eventTextLabel);
            TextView dateTextLabel= customView.findViewById(R.id.dateTextLabel);

            //sets the text for the labels in each row of the list
            nameTextLabel.setText(names.get(position));
            dateTextLabel.setText(dates.get(position));
            return customView;
        }
    }

    /*
     *This class allows actions to be taken when the user click on a list item
     */
    class ItemListener implements OnItemClickListener{
        private int clickPosition;

        /*
         * This method takes in an AdapterView Object, View object, an int and a long.
         * It acts when an item on the list is clicked
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            clickPosition = position;
            Intent intent = new Intent(MainActivity.this, EventActivity.class);

            //Packs extra info for the event edit screen
            intent.putExtra("name", names.get(position));
            intent.putExtra("date", dates.get(position));
            intent.putExtra("description", description.get(position));
            //Change the current screen to the event edit screen
            startActivityForResult(intent, 0);
        }

        /*
         * This method return an integer representing the row the user clicked on the list
         */
        public int getClickPosition(){
            return clickPosition;
        }
    }

    /*
     *This class allows actions to be taken when the user clicks on a button
     */
    class ButtonListener implements OnClickListener {
        /*
        This method takes in a View object which is automatically given. It does not
        return anything and acts when a button is clicked
        */
        @Override
        public void onClick(View v) {
            //Moves to the individual event edit screen
            if (v.getId() == R.id.addEventButton) {
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                startActivityForResult(intent, 1);
            }
        }
    }
}
