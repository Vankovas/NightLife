package nl.fontys.ivan.valentin.nightlife;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class Login extends AppCompatActivity implements BasicFunctionality {
    private User userInfo = new User(); //Needed in every class
    private FirebaseUser user; //Needed in every class
    private FirebaseAuth mAuth; //Needed in every class
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users"); //Needed in every class;

    private EditText email, password;
    Button login;

    @Override
    public void onBackPressed() {
        this.back();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        this.email = findViewById(R.id.email_input);
        this.password = findViewById(R.id.password_input);
    }

    public void logInUser(View view) {
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        if (email.length() > 0 && password.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success
                                Log.d("Login", "signInWithEmail:success");
                                user = mAuth.getCurrentUser();
                                getUserInfo();
                                startActivity(new Intent(Login.this, MainActivity.class));
                            } else {
                                // If sign in fails
                                Log.w("Login", "signInWithEmail:failure", task.getException());
                                Toast.makeText(Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(Login.this, "Enter fields.",
                    Toast.LENGTH_SHORT).show();
        }
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
        Log.d("GetUser", "After removal \n" + userInfo.getUserInfoString());
        userInfo.setAtmosphere("THANK YOU JESUS");
        userInfo.setEntertainment("Disco");
        Log.d("GetUser", "After edit \n" + userInfo.getUserInfoString());
        db.child(userid).setValue(userInfo);
        Log.d("GetUser", "After dbADD \n" + userInfo.getUserInfoString());
    }

    public void backButtonClick(View view) {
        this.back();
    }

    public void back() {
        startActivity(new Intent(Login.this, StartupScreen.class));
    }
}
