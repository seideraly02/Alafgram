package com.starkindustries.instagram_authentication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.starkindustries.instagram_authentication.databinding.ActivityRegisterBinding;
import java.security.Key;
import java.util.HashMap;
import java.util.Objects;
public class Register_Activity extends AppCompatActivity {
    public ActivityRegisterBinding binding;
    public FirebaseAuth auth;
    public FirebaseFirestore store;
    public String user_id;
    public boolean passed;
    public boolean confirmpassed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        binding= DataBindingUtil.setContentView(Register_Activity.this,R.layout.activity_register);
        auth=FirebaseAuth.getInstance();
        store=FirebaseFirestore.getInstance();
        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_screen = new Intent(Register_Activity.this,Login_Screen.class);
                startActivity(login_screen);
            }
        });
        binding.password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP)
                {
                    if(event.getRawX()>=(binding.password.getRight()-binding.password.getCompoundDrawables()[Keys.PASSWORD_KEY].getBounds().width()))
                    {
                        int selection=binding.password.getSelectionEnd();
                        if(passed)
                        {
                            binding.password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility_on,0);
                            binding.password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passed=false;
                        }
                        else
                        {
                            binding.password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility_off,0);
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
        binding.confirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP)
                {
                    if(event.getRawX()>=(binding.confirmPassword.getRight()-binding.confirmPassword.getCompoundDrawables()[Keys.PASSWORD_KEY].getBounds().width()))
                    {
                        int selection = binding.confirmPassword.getSelectionEnd();
                        if(confirmpassed)
                        {
                            binding.confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            binding.confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility_on,0);
                            confirmpassed=false;
                        }
                        else
                        {
                            binding.confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            binding.confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility_off,0);
                            confirmpassed=true;
                        }
                        binding.confirmPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(binding.fullname.getText().toString().trim()))
                {
                    binding.fullname.setError("Enter Complete name");
                    return ;
                }
                if(TextUtils.isEmpty(binding.username.getText().toString().trim()))
                {
                    binding.username.setError("Enter Complete Username");
                    return ;
                }
                if(TextUtils.isEmpty(binding.email.getText().toString().trim()))
                {
                    binding.email.setError("Enter Complete Email");
                    return ;
                }
                if(TextUtils.isEmpty(binding.password.getText().toString().trim()))
                {
                    binding.password.setError("Enter Proper Password");
                    return;
                }
                if(TextUtils.isEmpty(binding.confirmPassword.getText().toString().trim()))
                {
                    binding.confirmPassword.setError("Enter Proper Password");
                    return;
                }
                if(!binding.password.getText().toString().trim().equals(binding.confirmPassword.getText().toString().trim()))
                {
                    binding.password.setError("Password and Confirm Password did'nt Match");
                    binding.confirmPassword.setError("Password and Confirm Password did'nt Match");
                    return ;
                }
                auth.createUserWithEmailAndPassword(binding.email.getText().toString().trim(),binding.password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(Register_Activity.this, "User Created Sucessfully", Toast.LENGTH_SHORT).show();
                            user_id=auth.getCurrentUser().getUid();
                            DocumentReference docref = store.collection("User Credentials").document(user_id);
                            HashMap<String, Object> map = new HashMap<String,Object>();
                            map.put(Keys.NAME,binding.fullname.getText().toString().trim());
                            map.put(Keys.USERNAME,binding.username.getText().toString().trim());
                            map.put(Keys.EMAIL,binding.email.getText().toString().trim());
                            map.put(Keys.PASSWORD,binding.password.getText().toString().trim());
                            docref.set(map);
                            Intent dashboard = new Intent(Register_Activity.this, DashBoard.class);
                            startActivity(dashboard);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register_Activity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}