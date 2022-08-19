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
import com.google.firebase.auth.FirebaseAuth;

import tr.project.notesapp.R;
import tr.project.notesapp.utils.Utils;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText email;
    private AppCompatButton btnReset;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.et_reset_password_email);
        btnReset = findViewById(R.id.btn_reset_password);
        firebaseAuth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailText = email.getText().toString().trim();
                if (emailText.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Enter your email please.", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(emailText).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Utils.signedUpDialog(ForgotPasswordActivity.this, "We have sent you an email. You can recover your password using your mail.");
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Wrong e-mail or the account doesn't exist. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}