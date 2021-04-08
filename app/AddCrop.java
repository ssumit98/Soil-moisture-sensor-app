package com.farmapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddCrop extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference CropDataRef, Permanent_dbRef, Sensor_dbRef, IrrigationType_dbRef, WaterResource_dbRef;

    Spinner crop_name_spinner;
    Spinner water_resource_spinner;
    Spinner type_of_irrigation_spinner;

    Button save_Button;

    EditText type_of_land, size_of_pipe, motar_power, electricity_availability;

    ArrayList<CropData> CropDataItems = new ArrayList<CropData>();
    ArrayList<String> CropDataListname = new ArrayList<String>();

    ArrayList<PermanentDbData> Permanent_dbDataItems = new ArrayList<PermanentDbData>();

    ArrayList<WaterResourceData> WaterResourceDataItems = new ArrayList<WaterResourceData>();
    ArrayList<String> WaterResourceDataListname = new ArrayList<String>();

    ArrayList<IrrigationTypeData> IrrigationTypeDataItems = new ArrayList<IrrigationTypeData>();
    ArrayList<String> IrrigationTypeDataListname = new ArrayList<String>();

    public static String CropId;
    public static int WaterResourceId,IrrigationTypeId;
    String type_of_land_value;
    int crop_name_spinner_value, type_of_irrigation_spinner_value,
            water_resource_spinner_value;
    double electricity_availability_value, motar_power_value, size_of_pipe_value;

    final String TAG="tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crop);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        save_Button = (Button) findViewById(R.id.save);

        type_of_land = (EditText) findViewById(R.id.type_of_land);
        size_of_pipe = (EditText) findViewById(R.id.size_of_pipe);
        motar_power = (EditText) findViewById(R.id.motar_power);
        electricity_availability = (EditText) findViewById(R.id.electricity_availability);

        crop_name_spinner = (Spinner) findViewById(R.id.crop_name_spinner);
        water_resource_spinner = (Spinner) findViewById(R.id.water_resource_spinner);
        type_of_irrigation_spinner = (Spinner) findViewById(R.id.type_of_irrigation_spinner);

        crop_name_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CropId = CropDataItems.get(i).getId();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        water_resource_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                WaterResourceId = WaterResourceDataItems.get(i).getId();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        type_of_irrigation_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                IrrigationTypeId = IrrigationTypeDataItems.get(i).getId();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        save_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!type_of_land.getText().toString().equals("") && !size_of_pipe.getText().toString().equals("") && !motar_power.getText().toString().equals("") && !electricity_availability.getText().toString().equals("")){
                    crop_name_spinner_value = Integer.parseInt(CropId);
                        size_of_pipe_value = Double.parseDouble(size_of_pipe.getText().toString());
                    type_of_irrigation_spinner_value = IrrigationTypeId;
                        motar_power_value = Double.parseDouble(motar_power.getText().toString());
                        electricity_availability_value = Double.parseDouble(electricity_availability.getText().toString());
                    type_of_land_value = type_of_land.getText().toString();
                    water_resource_spinner_value = WaterResourceId;

                    createUser(crop_name_spinner_value,size_of_pipe_value,type_of_irrigation_spinner_value,motar_power_value,electricity_availability_value,
                                type_of_land_value,water_resource_spinner_value);
                }
                else{
                    Toast.makeText(AddCrop.this, "Fill all the fields", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String userId;
    private void createUser(int crop_name_spinner, double size_of_pipe, int type_of_irrigation_spinner, double motar_power,
                            double electricity_availability,String type_of_land,int water_resource_spinner) {
        userId = Permanent_dbRef.push().getKey();// convert to int
        int Area = 1;
        int current_crop = 1;

        for (PermanentDbData PermanentDb : Permanent_dbDataItems) {
            int is_current_crop = PermanentDb.getcurrent_crop();
                Permanent_dbRef.child(PermanentDb.getId())
                        .child("current_crop")
                        .setValue(0);
        }

//        PermanentDbData(int Area, int CropId, long Diameter_of_pipe, int Id, int IrrigationTypeId, long Motar_power, long Time_for_electriciy_available, String Type_of_land, String WaterResourceId)
        PermanentDbData PermanentDb = new PermanentDbData(Area,crop_name_spinner,current_crop,size_of_pipe,userId,type_of_irrigation_spinner,
                motar_power,electricity_availability,type_of_land,String.valueOf(water_resource_spinner));

        Permanent_dbRef.child(userId).setValue(PermanentDb);

        addUserChangeListener();
    }

    private void addUserChangeListener() {
        // User data change listener
        Permanent_dbRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PermanentDbData user = dataSnapshot.getValue(PermanentDbData.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                // clear edit text
                type_of_land.setText("");
                size_of_pipe.setText("");
                motar_power.setText("");
                electricity_availability.setText("");
                Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        CropDataRef = database.getReference("CropData");
        Permanent_dbRef = database.getReference("Permanent_db");
        Sensor_dbRef = database.getReference("Sensor_db").child("Sensor1_data");
        IrrigationType_dbRef = database.getReference("IrrigationType_db");
        WaterResource_dbRef = database.getReference("WaterResource_db");

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
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(AddCrop.this, android.R.layout.simple_spinner_item, CropDataListname);
                addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                crop_name_spinner.setAdapter(addressAdapter);
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
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(AddCrop.this, android.R.layout.simple_spinner_item, IrrigationTypeDataListname);
                addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                type_of_irrigation_spinner.setAdapter(addressAdapter);
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
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(AddCrop.this, android.R.layout.simple_spinner_item, WaterResourceDataListname);
                addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                water_resource_spinner.setAdapter(addressAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getBaseContext(), StartActivity.class);
        startActivity(i);
    }
}
