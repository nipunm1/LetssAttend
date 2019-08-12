package com.example.letssattend.Fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letssattend.R;
import com.example.letssattend.model.Attendance;
import com.example.letssattend.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashboardFragment extends Fragment{
    private static final String TAG="DashboardFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    String date1;
    String date2;

    MyAdapter myAdapter;

    Button btn,btn2;
    ImageButton imgbtn;
    ListView lv;
    ArrayList<Attendance>filter_list = new ArrayList<>();
    ArrayList<Attendance>attend_list = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference attend_dbrefer;

    private OnFragmentInteractionListener mListener;

    public DashboardFragment() {
    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Log.d(TAG, "OnCreateView dashboard");
        btn = view.findViewById(R.id.button);
        btn2 = view.findViewById(R.id.button2);
        imgbtn = view.findViewById(R.id.imgbutton);
        lv = (ListView) view.findViewById(R.id.listView);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "From button clicked");
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Log.d(TAG, "FROM DATA SET"+i+i1+i2);
                        String month = String.format("%02d", i1+1);
                        String day = String.format("%02d", i2);
                        int year = i;
                        date1 = day+"-"+month+"-"+year;
                        btn.setText(date1+"");
                        date1=year+""+month+""+day+"";
                        Log.d(TAG, "date1 = "+date1);
                    }
                };
                getCurrentDate(listener);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "To button clicked");
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Log.d(TAG, "TO DATA SET"+i+i1+i2);
                        String month = String.format("%02d", i1+1);
                        String day = String.format("%02d", i2);
                        int year = i;
                        date2 = day+"-"+month+"-"+year;
                        btn2.setText(date2+"");
                        date2=year+""+month+""+day+"";
                        Log.d(TAG, "date2 = "+date2);
                    }
                };
                getCurrentDate(listener);
            }
        });
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Search button clicked");
                if(date1==null){
                    Toast.makeText(getContext(), "Please enter From date", Toast.LENGTH_LONG).show();
                    return;
                }
                if(date2==null){
                    Toast.makeText(getContext(), "Please enter To date", Toast.LENGTH_LONG).show();
                    return;
                }
                getAttendByDate(date1, date2);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        attend_dbrefer = database.getReference(Constants.ATTENDENCE);
        getFullAttendance();
        return view;
    }
    private void getAttendByDate(String d1,String d2){
        Log.d(TAG, "getAttendByDate function "+ d1 + d2);
        filter_list.clear();
        for(Attendance attendance : attend_list){
            if(attendance.getDate().compareTo(d1)>=0&&attendance.getDate().compareTo(d2)<=0){
                filter_list.add(attendance);
                Log.d(TAG, "filtered list according to date = "+filter_list);
            }
        }
        MyAdapter myAdapter = new MyAdapter(getContext(), filter_list);
        lv.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }
    private void getCurrentDate(DatePickerDialog.OnDateSetListener listener){
        Calendar calendar = Calendar.getInstance();
        int year= calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert, listener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }
    private void getFullAttendance(){
        Log.d(TAG, "Get Full Attendance");
        String id = mAuth.getCurrentUser().getUid();
        attend_dbrefer.orderByChild("userid").equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     Log.d(TAG, "Data of attendance "+dataSnapshot.getValue());
                     if(dataSnapshot.getValue()!=null) {
                         Log.d(TAG, "User data is not null");
                         for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                            Log.d(TAG, "dataSnap Values " + dataSnap.getValue());
                            Attendance attendance = dataSnap.getValue(Attendance.class);
                            attend_list.add(attendance);
                            Log.d(TAG, "attendance values added "+dataSnap.getValue(Attendance.class));
                             Log.d(TAG, "Before Adapter");
                             myAdapter = new MyAdapter(getContext(),  attend_list);
                             lv.setAdapter(myAdapter);
                             Log.d(TAG, "After Adapter");
                         }

                     }
                     else{
                         Log.d(TAG, "User data is null");
                     }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "Data cancelled "+databaseError.getMessage());
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    class MyAdapter extends ArrayAdapter<Attendance> {
        Context context;
        ArrayList<Attendance> list = new ArrayList<>();
        public MyAdapter(Context context, ArrayList<Attendance> list) {
            super(context, R.layout.attendance_view,list);
            AlarmManager am = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
            this.context = context;
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.attendance_view, parent,false);
            TextView textView,textView2,textView3;
            textView = view.findViewById(R.id.text);
            textView2 = view.findViewById(R.id.text2);
            textView3 = view.findViewById(R.id.text3);
            Attendance attendance = list.get(position);
            textView.setText(getDate(attendance.getDate()));
            textView2.setText(attendance.getTime());
            textView3.setText(attendance.getCourse());
            return view;
        }
    }
    public String getDate(String date){
        String day,year,month;
        year = date.substring(0,4);
        month = date.substring(4,6);
        day = date.substring(6,8);
        switch (month){
            case "01" : month = "Jan";break;
            case "02" : month = "Feb";break;
            case "03" : month = "Mar";break;
            case "04" : month = "Apr";break;
            case "05" : month = "May";break;
            case "06" : month = "Jun";break;
            case "07" : month = "Jul";break;
            case "08" : month = "Aug";break;
            case "09" : month = "Sep";break;
            case "10" : month = "Oct";break;
            case "11" : month = "Nov";break;
            case "12" : month = "Dec";break;
        }
        date = day+"-"+month+"-"+year;
        return date;
    }
}