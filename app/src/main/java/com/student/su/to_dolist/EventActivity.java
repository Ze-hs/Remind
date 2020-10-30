package com.student.su.to_dolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * @version May 27, 2019
 *
 * This class is the second screen for the to-do list app which can be accessed when
 * the user clicks on an items to edit or creates a new event. Here they can either
 * make changes to the event or delete them.
 */

public class EventActivity extends Activity {
    private Button saveEventButton;
    private Button deleteEventButton;
    private EditText eventEditText;
    private EditText dateEditText;
    private EditText descriptionEditText;
    private String eventName;
    private String date;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        //Initializing the buttons and edit text for this screen
        saveEventButton = findViewById(R.id.saveEventButton);
        deleteEventButton = findViewById(R.id.deleteEventButton);

        eventEditText = findViewById(R.id.eventEditText);
        dateEditText= findViewById(R.id.dateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);

        OnClickListener buttonEventListener = new ButtonListener();
        saveEventButton.setOnClickListener(buttonEventListener);
        deleteEventButton.setOnClickListener(buttonEventListener);

        //Retrieve info given by the main screen and sets the data onto the widgets
        unPackInfo();
        setInfo();
    }

    /*
     * This method get the information passed on by the main screen to build the event
     */
    private void unPackInfo(){
        Bundle eventInfo = getIntent().getExtras();
        //If no info is given, the default values are given
        if (eventInfo == null){
            eventName = "Event Name";
            date = "Date";
            description = "Description";
        }
        else {
            eventName = eventInfo.getString("name");
            date = eventInfo.getString("date");
            description = eventInfo.getString("description");
        }
    }

    /*
     * This method sets the data given by the main screen and sets them to the buttons
     * and editText widgets in this screen
     */
    private void setInfo(){
        eventEditText.setText(eventName);
        dateEditText.setText(date);
        descriptionEditText.setText(description);
    }

    /*
     *This class allows actions to be taken when the user clicks on a button
     */
    class ButtonListener implements OnClickListener {
        //When this user clicks on a button, it stores data so it can be sent back to
        // the main screen
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("newName", eventEditText.getText().toString());
            intent.putExtra("newDate", dateEditText.getText().toString());
            intent.putExtra("newDescription", descriptionEditText.getText().toString());

            if (v.getId() == R.id.saveEventButton) {
                intent.putExtra("option", "add");
            }
            else if(v.getId() == R.id.deleteEventButton){
                intent.putExtra("option", "rev");
            }

            //Sends back the data
            setResult(Activity.RESULT_OK, intent);
            //Change back to the main screen
            finish();
        }
    }
}
