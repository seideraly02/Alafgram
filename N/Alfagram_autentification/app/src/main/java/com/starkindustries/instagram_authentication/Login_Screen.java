package com.starkindustries.instagram_authentication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.starkindustries.instagram_authentication.databinding.ActivityLoginScreenBinding;
public class Login_Screen extends AppCompatActivity {
    public ActivityLoginScreenBinding binding;
    public static final int RIGHT=2;
    public static boolean passed;
    public FirebaseAuth auth;
    public FirebaseFirestore store;
    public SharedPreferences prefrences;
    public SharedPreferences.Editor edit;
    public TextInputEditText email;
    public AppCompatButton sign_in,send_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        binding= DataBindingUtil.setContentView(Login_Screen.this,R.layout.activity_login_screen);
        auth= FirebaseAuth.getInstance();
        store=FirebaseFirestore.getInstance();
        prefrences=getSharedPreferences(Keys.SHARED_PREFRENCES_NAME,MODE_PRIVATE);
        edit=prefrences.edit();
        binding.password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP)
                {
                    if(event.getRawX()>=(binding.password.getRight()-binding.password.getCompoundDrawables()[RIGHT].getBounds().width()))
                    {
                        int selection = binding.password.getSelectionEnd();
                        if(passed)
                        {
                            binding.password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility_off,0);
                            binding.password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passed=false;
                        }
                        else
                        {
                        binding.password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility_on,0);
                        binding.password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passed=true;
                        }
                        binding.password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        binding.login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(v.getWindowToken(),0);
                if(TextUtils.isEmpty(binding.username.getText().toString().trim())||TextUtils.isEmpty(binding.password.getText().toString().trim())) {
                    binding.username.setError("Enter Correct Username/Password");
                    return ;
                }
                if(binding.password.getText().toString().trim().length()<8) {
                    binding.password.setError("Password Length is Less than 8 charecter's");
                    return ;
                }
                auth.signInWithEmailAndPassword(binding.username.getText().toString().trim(),binding.password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(Login_Screen.this, "Logged-in Sucessfully", Toast.LENGTH_SHORT).show();
                            edit.putBoolean(Keys.FLAG,true);
                            Intent dashboard = new Intent(Login_Screen.this, DashBoard.class);
                            startActivity(dashboard);
                            finish();
                        }
                        else
                            Toast.makeText(Login_Screen.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inext = new Intent(Login_Screen.this,Register_Activity.class);
                startActivity(inext);

            }
        });
        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog reset_pass_dialog = new Dialog(Login_Screen.this);
                reset_pass_dialog.setContentView(R.layout.forgot_password_layout);
                reset_pass_dialog.setCancelable(false);
                reset_pass_dialog.show();
                email=reset_pass_dialog.findViewById(R.id.reset_email);
                sign_in=reset_pass_dialog.findViewById(R.id.signin);
                send_email=reset_pass_dialog.findViewById(R.id.send_email);
                send_email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(email.getText().toString().trim()))
                        {
                            email.setError("Enter valid Email");
                            return ;
                        }
                        auth.sendPasswordResetEmail(email.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Login_Screen.this, "Email Send Sucessfully", Toast.LENGTH_SHORT).show();
                                reset_pass_dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login_Screen.this, "Something went wrong,Please chech your Internet connection", Toast.LENGTH_SHORT).show();
                                Log.d("email_error",e.getMessage().toString().trim());
                            }
                        });
                    }
                });
            }
        });
    }
}