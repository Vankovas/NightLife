package nl.fontys.ivan.valentin.nightlife;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AboutYou extends AppCompatActivity implements BasicFunctionality {
    private User userInfo = new User(); //Needed in every class
    private FirebaseUser user; //Needed in every class
    private FirebaseAuth mAuth; //Needed in every class
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users"); //Needed in every class;

    private Date finalDate;
    private Spinner transport;
    private Spinner gender;
    private TextView date;
    private DatePickerDialog datePickerDialog;
    private boolean main = false;
    private String dateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_you);
        if (finalDate == null) {
            finalDate = new Date();
        }
        Intent intent = getIntent();
        if (intent.getBooleanExtra("aboutYou", false)) main = true;
        else main = false;
        getUserInfo(); //////////////////////////////////////////////////////////////////////////////////////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        transport = findViewById(R.id.transport_menu);
        Button finishBtn = findViewById(R.id.finish_button);

        // initiate the date picker and a button
        date = (TextView) findViewById(R.id.date);
        // perform click event on edit text
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                final Calendar restrictor = Calendar.getInstance();
                final int mYear = c.get(Calendar.YEAR); // current year
                final int mMonth = c.get(Calendar.MONTH); // current month
                final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                restrictor.set(restrictor.get(Calendar.YEAR) - 18, restrictor.get(Calendar.MONTH), restrictor.get(Calendar.DAY_OF_MONTH));

                // date picker dialog
                datePickerDialog = new DatePickerDialog(AboutYou.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override

                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                date.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);
                                dateStr = String.format(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(restrictor.getTimeInMillis());
                datePickerDialog.show();
            }
        });
        gender = findViewById(R.id.gender_menu);
    }

    public void getUserInfo() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(User.class) == null) {

                } else {
                    userInfo = dataSnapshot.getValue(User.class);
                    Log.d("GetUser", userInfo.getUserInfoString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateUserInfo() {
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("GetUser", "Before removal \n" + userInfo.getUserInfoString());
        db.child(userid).removeValue();
        userInfo.setDateOfBirth(dateStr);
        userInfo.setGender((String) gender.getSelectedItem());
        userInfo.setTransport((String) transport.getSelectedItem());
        db.child(userid).setValue(userInfo);
        Log.d("GetUser", "After dbADD \n" + userInfo.getUserInfoString());
    }

    public void finish(View view) {
        updateUserInfo();
        if (main) startActivity(new Intent(AboutYou.this, MainActivity.class));
        else startActivity(new Intent(AboutYou.this, Personalize.class));
    }
}
