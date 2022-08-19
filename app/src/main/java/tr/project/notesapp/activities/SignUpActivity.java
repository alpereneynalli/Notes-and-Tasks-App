package tr.project.notesapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import tr.project.notesapp.R;
import tr.project.notesapp.utils.Utils;

public class SignUpActivity extends AppCompatActivity {

    private AppCompatButton signUp;
    private FirebaseAuth firebaseAuth;
    private EditText email;
    private EditText password;
    private EditText passwordAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUp = findViewById(R.id.btn_sign_up_signuppage);
        email = findViewById(R.id.et_signup_email);
        password = findViewById(R.id.et_signup_password);
        passwordAgain = findViewById(R.id.et_signup_password_again);
        firebaseAuth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();
                String passwordAgainText = passwordAgain.getText().toString().trim();

                if (emailText.isEmpty() || passwordText.isEmpty() || passwordAgainText.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all the fields on the screen.", Toast.LENGTH_SHORT).show();
                } else if (!passwordText.equals(passwordAgainText)) {
                    Toast.makeText(SignUpActivity.this, "Passwords are not the same.", Toast.LENGTH_SHORT).show();
                } else if (passwordText.length() < 6) {
                    Toast.makeText(SignUpActivity.this, "Password length must be greater than 6", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                sendEmailVerification();
                                Utils.signedUpDialog(SignUpActivity.this, "You have successfully signed up! Please confirm your account from your e-mail. Don't forget to check your spam box.");
                            } else {
                                Toast.makeText(SignUpActivity.this, "Failed to register.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendEmailVerification() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    firebaseAuth.signOut();
                }
            });
        }
    }


}