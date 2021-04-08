package com.farmapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MyDialogFragment dialogFragment;
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

    final String TAG="tag";

    List<Parent> expandableListTitle = new ArrayList<Parent>();
    List<Child> Childs = new ArrayList<Child>();
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;

    ArrayList<SensorData> SensorDataItemsSensor1 = new ArrayList<SensorData>();
    ArrayList<String> Moisture_PercentageListnameSensor1 = new ArrayList<String>();
    ArrayList<Timestamp> timeStampDateListnameSensor1 = new ArrayList<Timestamp>();
    public static Timestamp firstValueForSensor1,timeStampOneHourAgoForSensor1;
    public static Double Moisture_PercentageForSensor1=0.0, Moisture_PercentageOneHourAgoForSensor1=0.0, ChangeInMoistureForSensor1=0.0;

    ArrayList<SensorData> SensorDataItemsSensor2 = new ArrayList<SensorData>();
    ArrayList<String> Moisture_PercentageListnameSensor2 = new ArrayList<String>();
    ArrayList<Timestamp> timeStampDateListnameSensor2 = new ArrayList<Timestamp>();
    public static Timestamp firstValueForSensor2,timeStampOneHourAgoForSensor2;
    public static Double Moisture_PercentageForSensor2=0.0, Moisture_PercentageOneHourAgoForSensor2=0.0, ChangeInMoistureForSensor2=0.0;

    ArrayList<SensorData> SensorDataItemsSensor3 = new ArrayList<SensorData>();
    ArrayList<String> Moisture_PercentageListnameSensor3 = new ArrayList<String>();
    ArrayList<Timestamp> timeStampDateListnameSensor3 = new ArrayList<Timestamp>();
    public static Timestamp firstValueForSensor3,timeStampOneHourAgoForSensor3;
    public static Double Moisture_PercentageForSensor3=0.0, Moisture_PercentageOneHourAgoForSensor3=0.0, ChangeInMoistureForSensor3=0.0;

    public static ArrayList<CropData> CropDataItems = new ArrayList<CropData>();
    ArrayList<String> CropDataListname = new ArrayList<String>();

    ArrayList<PermanentDbData> Permanent_dbDataItems = new ArrayList<PermanentDbData>();

    public static ArrayList<IrrigationTypeData> IrrigationTypeDataItems = new ArrayList<IrrigationTypeData>();
    ArrayList<String> IrrigationTypeDataListname = new ArrayList<String>();

    public static ArrayList<WaterResourceData> WaterResourceDataItems = new ArrayList<WaterResourceData>();
    ArrayList<String> WaterResourceDataListname = new ArrayList<String>();


    public static int area, cropId, current_crop, irrigationTypeId, Moisture_min, Moisture_max;
    public static String type_of_land, id, waterResourceId, Crop_name, irrigation_name, WaterResource_name;
    public static double diameter_of_pipe, motar_power, time_for_electriciy_available;

    Button crop_details_button;

    SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);

        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                pullToRefresh.setRefreshing(false);
            }
        });

        crop_details_button = (Button) findViewById(R.id.crop_details_button);
        crop_details_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment = new MyDialogFragment();

                Bundle bundle = new Bundle();
                bundle.putBoolean("notAlertDialog", true);

                dialogFragment.setArguments(bundle);

                ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                dialogFragment.show(ft, "dialog");
            }
        });
    }

    private void refreshData() {

        getData();

        for (CropData CropData : MainActivity.CropDataItems) {
            if (Integer.valueOf(CropData.getId()) == MainActivity.cropId ) {
                Crop_name = CropData.getCrop_name();
                Moisture_min = CropData.getMoisture_min();
                Moisture_max = CropData.getMoisture_max();
            }
        }

        expandableListAdapter = new CustomExpandableListAdapter(getBaseContext(), expandableListTitle, Childs);
        expandableListView.setAdapter(expandableListAdapter);

        //To expand one child at a time
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return false;
            }
        });

        expandableListView.setOnGroupClickListener (new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference CropDataRef = database.getReference("CropData");
        DatabaseReference Sensor1_dbRef = database.getReference("Sensor_db").child("Sensor1_data");
        DatabaseReference Sensor2_dbRef = database.getReference("Sensor_db").child("Sensor2_data");
        DatabaseReference Sensor3_dbRef = database.getReference("Sensor_db").child("Sensor3_data");
        DatabaseReference Permanent_dbRef = database.getReference("Permanent_db");
        DatabaseReference IrrigationType_dbRef = database.getReference("IrrigationType_db");
        DatabaseReference WaterResource_dbRef = database.getReference("WaterResource_db");

        Sensor1_dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Moisture_PercentageListnameSensor1.clear();
                SensorDataItemsSensor1.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SensorDbData SensorDb = postSnapshot.getValue(SensorDbData.class);

                    String[] data = String.valueOf(SensorDb.getupload_time()).split(",");
                    DateFormat DateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date CurrentDate = new Date();

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
                    String output1 = null;
                    Timestamp timeStampDate = null;
                    try {
                        Date date1 = df.parse(DateFormatter.format(CurrentDate)+" "+data[0].trim()+".000+05:30");
                        DateFormat outputFormatter1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        output1 = outputFormatter1.format(date1);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        Date parsedDate = dateFormat.parse(output1);
                        timeStampDate = new java.sql.Timestamp(parsedDate.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    SensorData Sensor = new SensorData();
                    Sensor.setDate_time(timeStampDate);
                    Sensor.setMoisture_Percentage(Double.parseDouble(data[1].trim()));

                    Moisture_PercentageListnameSensor1.add(String.valueOf(data[1].trim()));
                    timeStampDateListnameSensor1.add(timeStampDate);
                    SensorDataItemsSensor1.add(Sensor);
                }

                Collections.sort(timeStampDateListnameSensor1);
                firstValueForSensor1 = timeStampDateListnameSensor1.get(timeStampDateListnameSensor1.size()-1);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(firstValueForSensor1);
                calendar.add(calendar.HOUR_OF_DAY, -1);
                Date date = calendar.getTime();
                timeStampOneHourAgoForSensor1=new Timestamp(date.getTime());

                Moisture_PercentageOneHourAgoForSensor1 = 0.0;
                Moisture_PercentageForSensor1 = 0.0;
                for (SensorData SensorData : SensorDataItemsSensor1) {
                    Timestamp currentDate = SensorData.getDate_time();

                    if (currentDate.compareTo(timeStampOneHourAgoForSensor1) == 0 ) {
                        Moisture_PercentageOneHourAgoForSensor1 = (SensorData.getMoisture_Percentage());
                    }
                    if (currentDate.compareTo(firstValueForSensor1) == 0 ) {
                        Moisture_PercentageForSensor1 = (SensorData.getMoisture_Percentage());
                    }
                }
                ChangeInMoistureForSensor1 = (Moisture_PercentageForSensor1-Moisture_PercentageOneHourAgoForSensor1);

                refreshData();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Sensor2_dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Moisture_PercentageListnameSensor2.clear();
                SensorDataItemsSensor2.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SensorDbData SensorDb = postSnapshot.getValue(SensorDbData.class);

                    String[] data = String.valueOf(SensorDb.getupload_time()).split(",");
                    DateFormat DateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date CurrentDate = new Date();

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
                    String output1 = null;
                    Timestamp timeStampDate = null;
                    try {
                        Date date1 = df.parse(DateFormatter.format(CurrentDate)+" "+data[0].trim()+".000+05:30");
                        DateFormat outputFormatter1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        output1 = outputFormatter1.format(date1);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        Date parsedDate = dateFormat.parse(output1);
                        timeStampDate = new java.sql.Timestamp(parsedDate.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    SensorData Sensor = new SensorData();
                    Sensor.setDate_time(timeStampDate);
                    Sensor.setMoisture_Percentage(Double.parseDouble(data[1].trim()));

                    Moisture_PercentageListnameSensor2.add(String.valueOf(data[1].trim()));
                    timeStampDateListnameSensor2.add(timeStampDate);
                    SensorDataItemsSensor2.add(Sensor);
                }

                Collections.sort(timeStampDateListnameSensor2);
                firstValueForSensor2 = timeStampDateListnameSensor2.get(timeStampDateListnameSensor2.size()-1);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(firstValueForSensor2);
                calendar.add(calendar.HOUR_OF_DAY, -1);
                Date date = calendar.getTime();
                timeStampOneHourAgoForSensor2=new Timestamp(date.getTime());

                Moisture_PercentageOneHourAgoForSensor2 = 0.0;
                Moisture_PercentageForSensor2 = 0.0;
                for (SensorData SensorData : SensorDataItemsSensor2) {
                    Timestamp currentDate = SensorData.getDate_time();
                    if (currentDate.compareTo(timeStampOneHourAgoForSensor2) == 0 ) {
                        Moisture_PercentageOneHourAgoForSensor2 = (SensorData.getMoisture_Percentage());
                    }
                    if (currentDate.compareTo(firstValueForSensor2) == 0 ) {
                        Moisture_PercentageForSensor2 = (SensorData.getMoisture_Percentage());
                    }
                }
                ChangeInMoistureForSensor2 = (Moisture_PercentageForSensor2-Moisture_PercentageOneHourAgoForSensor2);
                refreshData();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Sensor3_dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Moisture_PercentageListnameSensor3.clear();
                SensorDataItemsSensor3.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SensorDbData SensorDb = postSnapshot.getValue(SensorDbData.class);

                    String[] data = String.valueOf(SensorDb.getupload_time()).split(",");
                    DateFormat DateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date CurrentDate = new Date();

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
                    String output1 = null;
                    Timestamp timeStampDate = null;
                    try {
                        Date date1 = df.parse(DateFormatter.format(CurrentDate)+" "+data[0].trim()+".000+05:30");
                        DateFormat outputFormatter1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        output1 = outputFormatter1.format(date1);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        Date parsedDate = dateFormat.parse(output1);
                        timeStampDate = new java.sql.Timestamp(parsedDate.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    SensorData Sensor = new SensorData();
                    Sensor.setDate_time(timeStampDate);
                    Sensor.setMoisture_Percentage(Double.parseDouble(data[1].trim()));

                    Moisture_PercentageListnameSensor3.add(String.valueOf(data[1].trim()));
                    timeStampDateListnameSensor3.add(timeStampDate);
                    SensorDataItemsSensor3.add(Sensor);
                }

                Collections.sort(timeStampDateListnameSensor3);
                firstValueForSensor3 = timeStampDateListnameSensor3.get(timeStampDateListnameSensor3.size()-1);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(firstValueForSensor3);
                calendar.add(calendar.HOUR_OF_DAY, -1);
                Date date = calendar.getTime();
                timeStampOneHourAgoForSensor3=new Timestamp(date.getTime());

                Moisture_PercentageOneHourAgoForSensor3 = 0.0;
                Moisture_PercentageForSensor3 = 0.0;
                for (SensorData SensorData : SensorDataItemsSensor3) {
                    Timestamp currentDate = SensorData.getDate_time();
                    if (currentDate.compareTo(timeStampOneHourAgoForSensor3) == 0 ) {
                        Moisture_PercentageOneHourAgoForSensor3 = (SensorData.getMoisture_Percentage());
                    }
                    if (currentDate.compareTo(firstValueForSensor3) == 0 ) {
                        Moisture_PercentageForSensor3 = (SensorData.getMoisture_Percentage());
                    }
                }
                ChangeInMoistureForSensor3 = (Moisture_PercentageForSensor3-Moisture_PercentageOneHourAgoForSensor3);
                refreshData();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Permanent_dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Permanent_dbDataItems.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PermanentDbData PermanentDb = postSnapshot.getValue(PermanentDbData.class);
                    Permanent_dbDataItems.add(PermanentDb);
                }
                for (PermanentDbData PermanentDb : Permanent_dbDataItems) {
                    int is_current_crop = PermanentDb.getcurrent_crop();
                    if (is_current_crop == 1 ) {
                        area = PermanentDb.getArea();
                        cropId = PermanentDb.getCropId();
                        current_crop = PermanentDb.getcurrent_crop();
                        irrigationTypeId = PermanentDb.getIrrigationTypeId();
                        type_of_land = PermanentDb.getType_of_land();
                        id = PermanentDb.getId();
                        waterResourceId = PermanentDb.getWaterResourceId();
                        diameter_of_pipe = PermanentDb.getDiameter_of_pipe();
                        motar_power = PermanentDb.getMotar_power();
                        time_for_electriciy_available = PermanentDb.getTime_for_electriciy_available();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        CropDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CropDataListname.clear();
                CropDataItems.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CropData CropDb = postSnapshot.getValue(CropData.class);
                    CropDataListname.add(CropDb.getCrop_name());
                    CropDataItems.add(CropDb);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        IrrigationType_dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                IrrigationTypeDataListname.clear();
                IrrigationTypeDataItems.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    IrrigationTypeData IrrigationTypeDb = postSnapshot.getValue(IrrigationTypeData.class);
                    IrrigationTypeDataListname.add(IrrigationTypeDb.getName());
                    IrrigationTypeDataItems.add(IrrigationTypeDb);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        WaterResource_dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                WaterResourceDataListname.clear();
                WaterResourceDataItems.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    WaterResourceData WaterResourceDb = postSnapshot.getValue(WaterResourceData.class);
                    WaterResourceDataListname.add(WaterResourceDb.getName());
                    WaterResourceDataItems.add(WaterResourceDb);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        refreshData();
    }

    public List<Child> getData() {
        expandableListTitle.clear();
        Parent parents1 = new Parent();
        parents1.setDateTime(String.valueOf(Moisture_PercentageForSensor1));
        parents1.setpName("Sensor 1");
        parents1.setCaseISN("Last updated on\n"+firstValueForSensor1);
        expandableListTitle.add(parents1);

        Parent parents2 = new Parent();
        parents2.setDateTime(String.valueOf(Moisture_PercentageForSensor2));
        parents2.setpName("Sensor 2");
        parents2.setCaseISN("Last updated on\n"+firstValueForSensor2);
        expandableListTitle.add(parents2);

        Parent parents3 = new Parent();
        parents3.setDateTime(String.valueOf(Moisture_PercentageForSensor3));
        parents3.setpName("Sensor 3");
        parents3.setCaseISN("Last updated on\n"+firstValueForSensor3);
        expandableListTitle.add(parents3);

        Childs.clear();
        Child childs1 = new Child();
        childs1.setId(String.format("%.4f", ChangeInMoistureForSensor1));
        Childs.add(childs1);

        Child childs2 = new Child();
        childs2.setId(String.format("%.4f", ChangeInMoistureForSensor2));
        Childs.add(childs2);

        Child childs3 = new Child();
        childs3.setId(String.format("%.4f", ChangeInMoistureForSensor3));
        Childs.add(childs3);
        return Childs;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getBaseContext(), StartActivity.class);
        startActivity(i);
    }
}
