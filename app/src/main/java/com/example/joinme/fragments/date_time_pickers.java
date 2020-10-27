package com.example.joinme.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.joinme.R;
import com.example.joinme.interfaces.DateTimeClick;

import java.util.Calendar;

public class date_time_pickers extends DialogFragment
{
    private int year,month,day,hour,minute;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(
                R.layout.date_time_pickers, null))

                .setPositiveButton("Confirm", (dialog, id) -> {
                    TimePicker timePicker = date_time_pickers.this.getDialog().findViewById(R.id.time_picker);
                    DatePicker datePicker = date_time_pickers.this.getDialog().findViewById(R.id.date_picker);
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                    year = datePicker.getYear();
                    month = datePicker.getMonth();
                    day = datePicker.getDayOfMonth();
                    PublishEvent frag = (PublishEvent) getActivity().getSupportFragmentManager().findFragmentByTag("publish_event");
                    if(frag instanceof DateTimeClick){
                        frag.OnDateTimeSelected(year,month,day,hour,minute);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

}
