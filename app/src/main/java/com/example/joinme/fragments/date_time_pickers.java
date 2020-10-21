package com.example.joinme.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.example.joinme.R;

import java.util.Calendar;

public class date_time_pickers extends DialogFragment implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(
                R.layout.date_time_pickers, null))

                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().getSupportFragmentManager().findFragmentByTag()
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}
