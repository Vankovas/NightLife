package nl.fontys.ivan.valentin.nightlife;

import android.content.Intent;
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

import static android.widget.Toast.LENGTH_SHORT;

public class sign_up extends AppCompatActivity {
    private User userInfo = new User(); //Needed in every class
    private FirebaseUser user; //Needed in every class
    private FirebaseAuth mAuth; //Needed in every class
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users"); //Needed in every class;

    private EditText emailRegistration, passwordRegistration;

    @Override
    public void onBackPressed() {
        this.back();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        this.emailRegistration = (EditText) findViewById(R.id.email_register);
        this.passwordRegistration = (EditText) findViewById(R.id.password_register);

        Button backBtn = findViewById(R.id.back_button);

        View.OnClickListener handler = new View.OnClickListener() {
            public void onClick(View v) {

                switch (v.getId()) {

                    case R.id.back_button:
                        startActivity(new Intent(sign_up.this, StartupScreen.class));
                        break;
                }
            }
        };

        backBtn.setOnClickListener(handler);
    }


    public void createAccount(View view) {
        final String email, password;
        email = this.emailRegistration.getText().toString();
        password = this.passwordRegistration.getText().toString();
        if (email.length() > 0 && password.length() > 0) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("CreateUser", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                User dbUser = new User(email, password);
                                dbUser.setGender("Non-Binary");
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(dbUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("UpdateDatabase", "Successfully updated database");
                                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    userInfo = dataSnapshot.getValue(User.class);
                                                    Log.d("GetUser", userInfo.getUserInfoString());
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        } else {
                                            Log.d("UpdateDatabase", "Failed to update database");
                                        }
                                    }
                                });


                                startActivity(new Intent(sign_up.this, AboutYou.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.e("CreateUser", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(sign_up.this, "Authentication failed.",
                                        LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(sign_up.this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
        }
    }


    public void backButtonClick(View view) {
        this.back();
    }

    public void back() {
        startActivity(new Intent(sign_up.this, StartupScreen.class));
    }
}
