package com.example.daumantas.explorer;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class FilterFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    String sortby = "Closest first";
    Spinner sortBySpinner;
    SeekBar minRatingBar;
    TextView tv_minRating;
    int minRating = 0;
    Date dateFrom;
    Date dateTo;
    private DatePickerDialog datePickerDialog;
    Button btn_dateFrom, btn_dateTo;
    Button btn_filter;
    boolean dateFromSet = false;
    boolean dateToSet = false;
    int mYearFrom = 2017, mMonthFrom = 1, mDayFrom = 1;
    int mYearTo, mMonthTo, mDayTo;

    public FilterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables();


    }


    void initVariables() {

        //init buttons
        btn_dateFrom = (Button) getActivity().findViewById(R.id.dateFrom);
        btn_dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        //make Date objeect from calendar
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, selectedyear);
                        cal.set(Calendar.MONTH, selectedmonth);
                        cal.set(Calendar.DAY_OF_MONTH, selectedday);
                        Date tempFromTime = cal.getTime();


                        if (dateToSet) {
                            if (tempFromTime.compareTo(dateTo) < 0) {
                                dateFrom = tempFromTime;
                                //set button text to formated date
                                btn_dateFrom.setText(DateFormat.getDateInstance().format(tempFromTime));
                            } else {
                                // Date From is higher than Date To, showing error message
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Wrong Data Input!")

                                        .setMessage("The end Date must be Before the start Date, please insert new Date values")

                                        .setNeutralButton("Ok",

                                                new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                }).show();
                            }
                        } else {
                            btn_dateFrom.setText(DateFormat.getDateInstance().format(tempFromTime));
                            dateFrom = tempFromTime;
                            dateFromSet = true;
                        }

                    }
                }, mYearFrom, mMonthFrom, mDayFrom);
                mDatePicker.setTitle("Show places posted from");
                mDatePicker.show();
                dateFromSet = true;
            }
        });

        btn_dateTo = (Button) getActivity().findViewById(R.id.dateTo);
        btn_dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                mYearTo = mcurrentDate.get(Calendar.YEAR);
                mMonthTo = mcurrentDate.get(Calendar.MONTH);
                mDayTo = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                        //make Date object from calendar
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, selectedyear);
                        cal.set(Calendar.MONTH, selectedmonth);
                        cal.set(Calendar.DAY_OF_MONTH, selectedday);
                        Date tempDateTo = cal.getTime();

                        //check if dateFrom is set
                        if (dateFromSet) {
                            // Check if date To is further than dateFrom
                            if (dateFrom.compareTo(tempDateTo) < 0) {
                                //set button text to formated date
                                btn_dateTo.setText(DateFormat.getDateInstance().format(tempDateTo));
                                dateTo = tempDateTo;
                                dateToSet = true;
                            } else {
                                // Date From is higher than Date To, showing error message
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Wrong Data Input!")

                                        .setMessage("The end Date must be Before the start Date, please insert new Date values")

                                        .setNeutralButton("Ok",

                                                new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }

                                                }).show();
                            }

                        } else {
                            btn_dateTo.setText(DateFormat.getDateInstance().format(tempDateTo));
                            dateTo = tempDateTo;
                            dateToSet = true;
                        }


                    }
                }, mYearTo, mMonthTo, mDayTo);
                mDatePicker.setTitle("Show places posted to");
                mDatePicker.show();
            }
        });

        btn_filter = (Button) getActivity().findViewById(R.id.filter_go);
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                if (dateFromSet) {
                    //bundle.putString("dateFrom", DateFormat.getDateInstance().format(dateFrom));
                    bundle.putString("dateFrom", formatDate(dateFrom));
                } else {
                    bundle.putString("dateFrom", "2000/01/01");
                }
                if (dateToSet) {
                    bundle.putString("dateTo", formatDate(dateTo));
                    //bundle.putString("dateTo", DateFormat.getDateInstance().format(dateTo));
                } else {
                    Date currentDate = Calendar.getInstance().getTime();
                    bundle.putString("dateTo", formatDate(currentDate));
                    //bundle.putString("dateTo", DateFormat.getDateInstance().format(currentDate));
                }
                bundle.putInt("minRating", minRating);
                bundle.putString("order", sortby);


                SearchPlacesFragment fragm = (SearchPlacesFragment) getActivity().getSupportFragmentManager().findFragmentByTag("currentFragment");
                fragm.updateList(bundle);

            }
        });

        // init TextViews
        tv_minRating = (TextView) getActivity().findViewById(R.id.seek_bar_value);

        // Init seek bar

        minRatingBar = (SeekBar) getActivity().findViewById(R.id.seek_bar);
        minRatingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                minRating = progress;
                tv_minRating.setText(String.valueOf(minRating));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // Init spinner
        sortBySpinner = (Spinner) getActivity().findViewById(R.id.spinner);
        //set ItemSelectedListener to get user input
        sortBySpinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sort_by_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sortBySpinner.setAdapter(adapter);
        //Set default selection
        sortBySpinner.setSelection(0);
    }

    String formatDate(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        return df.format(date);
    }

    //Spinner methods
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        sortby = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
