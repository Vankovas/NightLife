package nl.fontys.ivan.valentin.nightlife;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import extra.MultiSelectionSpinner;

public class Personalize extends AppCompatActivity implements BasicFunctionality {
    private User userInfo = new User(); //Needed in every class
    private FirebaseUser user; //Needed in every class
    private FirebaseAuth mAuth; //Needed in every class
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users"); //Needed in every class;

    private MultiSelectionSpinner musicSpinner;
    private MultiSelectionSpinner foodSpinner;
    private Spinner atmosphere;
    private Spinner entertainment;
    private boolean main = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize);

        //Getting Info From Previous Activity
        Intent intent = getIntent();
        if (intent.getBooleanExtra("pref", false)) main = true;
        else main = false;

        getUserInfo(); //////////////////////////////////////////////////////////////////////////////////////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        atmosphere = findViewById(R.id.atmosphere_spinner);
        entertainment = findViewById(R.id.entertainment_spinner);

        musicSpinner = findViewById(R.id.music_spinner);
        List<String> music = Arrays.asList(getResources().getStringArray(R.array.music));
        musicSpinner.setItems(music);

        foodSpinner = findViewById(R.id.food_spinner);
        List<String> food = Arrays.asList(getResources().getStringArray(R.array.food));
        foodSpinner.setItems(food);
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
        userInfo.setFoodChoice(foodSpinner.getSelectedStrings());
        userInfo.setAtmosphere((String) atmosphere.getSelectedItem());
        userInfo.setMusicChoice(musicSpinner.getSelectedStrings());
        userInfo.setEntertainment((String) entertainment.getSelectedItem());
        db.child(userid).setValue(userInfo);
        Log.d("GetUser", "After dbADD \n" + userInfo.getUserInfoString());
    }

    public void back(View view) {
        if (main == true) startActivity(new Intent(Personalize.this, MainActivity.class));
        else startActivity(new Intent(Personalize.this, AboutYou.class));
    }

    public void finish(View view) {
        updateUserInfo();
        startActivity(new Intent(Personalize.this, MainActivity.class));
    }
}
