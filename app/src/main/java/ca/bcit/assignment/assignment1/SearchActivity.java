package ca.bcit.assignment.assignment1;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ca.bcit.assignment.assignment1.database.AppDatabase;
import ca.bcit.assignment.assignment1.models.Caption;

public class SearchActivity extends AppCompatActivity {

    private TextView startDateTextView;
    private DatePicker startDatePicker;
    private TimePicker startTimePicker;
    private TextView endDateTextView;
    private DatePicker endDatePicker;
    private TimePicker endTimePicker;
    private Button applyFiltersButton;
    private TextInputLayout captionInputLayout;
    private ListView locationListView;
    private AppDatabase db;
    private ArrayList<String> checkedLocations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        startDateTextView = (TextView) findViewById(R.id.startTextView);
        startDatePicker = (DatePicker) findViewById(R.id.startDatePicker);
        startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
        endDateTextView = (TextView) findViewById(R.id.endTextView);
        endDatePicker = (DatePicker) findViewById(R.id.endDatePicker);
        endTimePicker = (TimePicker) findViewById(R.id.endTimePicker);
        applyFiltersButton = (Button) findViewById(R.id.applyFiltersButton);
        captionInputLayout = (TextInputLayout) findViewById(R.id.captionInputLayout);
        locationListView = (ListView) findViewById(R.id.locationListView);

        db = AppDatabase.getInstance(this);

        checkedLocations = new ArrayList<>();

        startTimePicker.setCurrentHour(0);
        startTimePicker.setCurrentMinute(0);
        endTimePicker.setCurrentHour(23);
        endTimePicker.setCurrentMinute(59);
        applyFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putExtra("StartYear", startDatePicker.getYear());
                intent.putExtra("StartMonth", startDatePicker.getMonth());
                intent.putExtra("StartDay", startDatePicker.getDayOfMonth());
                intent.putExtra("StartHour", startTimePicker.getCurrentHour());
                intent.putExtra("StartMinute", startTimePicker.getCurrentMinute());
                intent.putExtra("EndYear", endDatePicker.getYear());
                intent.putExtra("EndMonth", endDatePicker.getMonth());
                intent.putExtra("EndDay", endDatePicker.getDayOfMonth());
                intent.putExtra("EndHour", endTimePicker.getCurrentHour());
                intent.putExtra("EndMinute", endTimePicker.getCurrentMinute());

                String caption = captionInputLayout.getEditText().getText().toString();
                if (!caption.equals("")) {
                    intent.putExtra("Caption", caption);
                }

                intent.putExtra("Locations", checkedLocations.toArray(new String[0]));
                startActivity(intent);
            }
        });

        List<String> locations = db.captionDao().getAllLocations();
        CustomAdapter customAdapter = new CustomAdapter(locations, SearchActivity.this);
        locationListView.setAdapter(customAdapter);
    }

    public class CustomAdapter extends ArrayAdapter<String> {

        private List<String> dataSet;
        Context mContext;

        public CustomAdapter(List<String> data, Context context) {
            super(context, R.layout.filter_location_listitem, data);
            this.dataSet = data;
            this.mContext=context;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            String location = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.filter_location_listitem, parent, false);
            }
            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

            if (location.equals("")) {
                checkBox.setText(R.string.locationDefault);
                checkedLocations.add("");
            } else {
                checkBox.setText(location);
                checkedLocations.add(checkBox.getText().toString());
            }

            checkBox.setChecked(true);
            final View v = convertView;

            checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        checkedLocations.add(getItem(position));
                    } else {
                        checkedLocations.remove(position);
                    }
                }
            });

            // Return the completed view to render on screen
            return convertView;
        }
    }
}
