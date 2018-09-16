package ca.bcit.assignment.assignment1;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

public class SearchActivity extends AppCompatActivity {

    private TextView startDateTextView;
    private DatePicker startDatePicker;
    private TimePicker startTimePicker;
    private TextView endDateTextView;
    private DatePicker endDatePicker;
    private TimePicker endTimePicker;
    private Button applyFiltersButton;
    private TextInputLayout captionInputLayout;
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

                startActivity(intent);
            }
        });
    }
}
