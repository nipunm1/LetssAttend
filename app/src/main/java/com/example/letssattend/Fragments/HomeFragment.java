package com.example.letssattend.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.letssattend.R;
import com.example.letssattend.model.Attendance;
import com.example.letssattend.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG="HomeFragment";

    private String mParam1;
    private String mParam2;

    TextView textView,textView2;
    Button button;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference attend_ref;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d(TAG, "onCreateView");
        database = FirebaseDatabase.getInstance();
        attend_ref = database.getReference(Constants.ATTENDENCE);
        DatabaseReference stud_Ref = database.getReference(Constants.STUDENT);
        String uid = auth.getInstance().getCurrentUser().getUid();
        textView = view.findViewById(R.id.text);
        textView2 = view.findViewById(R.id.text2);
        stud_Ref.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "OnDataChange Success");
                String name = (String) dataSnapshot.child("name").getValue();
                textView.setText("Welcome "+name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "OnDataCancelled");
            }
        });
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String currentdate = simpleDateFormat.format(new Date());
        textView2.setText("Today is "+currentdate);
        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    markAttendance();
            }
        });
        checkAttendance();
        return view;
    }

    private void markAttendance(){
        Log.d(TAG, "markAttendance function");
        String uid = auth.getInstance().getCurrentUser().getUid();
        DatabaseReference stud_ref = database.getReference(Constants.STUDENT);
        stud_ref.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Getting name and course");
                String name = (String)(dataSnapshot.child("name").getValue());
                String course = (String)(dataSnapshot.child("course").getValue());
                attendDetails(name,course);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Fail to get name and course");
            }
        });

    }
    private void attendDetails(String name,String course){
        Log.d(TAG, "attendDetails");
        String date;
        String year;
        String month;
        String day;
        String time;
        String uid = auth.getInstance().getCurrentUser().getUid();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        String currentDateAndTime = simpleDateFormat.format(new Date());
        String[] arr = currentDateAndTime.split("/");
        year = arr[0];
        month = arr[1];
        day = arr[2];
        time = arr[3];
        date = year+month+day;
        Attendance attendance = new Attendance(date, time, uid, name, course, date+"_"+uid);
        attend_ref.push().setValue(attendance);
    }
    private void checkAttendance(){
        Log.d(TAG, "checkAttendance function");
        String year,month,day,uid,date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String currentDateAndTime = simpleDateFormat.format(new Date());
        String[] arr = currentDateAndTime.split("/");
        year = arr[0];
        month = arr[1];
        day = arr[2];
        date = year+month+day;
        uid = auth.getInstance().getCurrentUser().getUid();
        Query query = attend_ref.orderByChild("date_userid").equalTo(date+"_"+uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Getting values for checking attendance");
                if(dataSnapshot.getValue()==null){
                    Log.d(TAG, "check data is null");
                    button.setEnabled(true);
                }
                else{
                    Log.d(TAG, "Check data is not null");
                    button.setEnabled(false);
                    button.setText("Your attendance was already marked for today");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Getting error in check attendance");
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
}
