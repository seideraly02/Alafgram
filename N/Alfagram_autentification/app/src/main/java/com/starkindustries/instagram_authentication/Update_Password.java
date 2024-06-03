package com.starkindustries.instagram_authentication;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.util.HashMap;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Update_Password#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Update_Password extends Fragment {
    public TextInputEditText current_pass,password,confirmPassword,email;
    public boolean passed,confirmpassed;
    public AppCompatButton send_email,sign_in;
    public AppCompatButton forgot_password,update_password;
    public FirebaseAuth auth;
    public FirebaseFirestore store;
    public FirebaseUser user;
    public String user_id;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Update_Password() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Update_Password.
     */
    // TODO: Rename and change types and number of parameters
    public static Update_Password newInstance(String param1, String param2) {
        Update_Password fragment = new Update_Password();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update__password, container, false);
        auth=FirebaseAuth.getInstance();
        store=FirebaseFirestore.getInstance();
        password=view.findViewById(R.id.password);
        current_pass=view.findViewById(R.id.current_password);
        forgot_password=view.findViewById(R.id.forgot_password);
        confirmPassword=view.findViewById(R.id.confirm_password);
        update_password=view.findViewById(R.id.update_password);
        user=auth.getCurrentUser();
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP)
                {
                    if(event.getRawX()>=(password.getRight()-password.getCompoundDrawables()[Keys.PASSWORD_KEY].getBounds().width()))
                    {
                        int selection=password.getSelectionEnd();
                        if(passed)
                        {
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility_off,0);
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passed=false;
                        }
                        else
                        {
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility_on,0);
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passed=true;
                        }
                        password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        confirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP)
                {
                    if(event.getRawX()>=(confirmPassword.getRight()-confirmPassword.getCompoundDrawables()[Keys.PASSWORD_KEY].getBounds().width()))
                    {
                        int selection = confirmPassword.getSelectionEnd();
                        if(confirmpassed)
                        {
                            confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility_off,0);
                            confirmpassed=false;
                        }
                        else
                        {
                            confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility_on,0);
                            confirmpassed=true;
                        }
                        confirmPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog reset_pass_dialog = new Dialog(getContext());
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
                                Toast.makeText(getContext(), "Email Send Sucessfully", Toast.LENGTH_SHORT).show();
                                reset_pass_dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Something went wrong,Please chech your Internet connection", Toast.LENGTH_SHORT).show();
                                Log.d("email_error",e.getMessage().toString().trim());
                            }
                        });
                    }
                });
            }
        });


        update_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id=auth.getCurrentUser().getUid();
                DocumentReference docrefrence = store.collection(Keys.COLECTION_NAME).document(user_id);
                docrefrence.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.exists())
                        {
                            String db_password=value.getString(Keys.PASSWORD);
                            if(password.getText().toString().trim().length()<8)
                            {
                                password.setError("Password Length less than 8 Charecters");
                                return;
                            }
                            else if(confirmPassword.getText().toString().trim().length()<8)
                            {
                                confirmPassword.setError("Confirm Password length less than 8 charecters");
                                return ;
                            }
                            else if(!password.getText().toString().trim().equals(confirmPassword.getText().toString().trim()))
                            {

                                password.setError("Password and Current password doesnot Matched");
                                confirmPassword.setError("Password and Current password doesnot Matched");
                                return ;
                            }

                            else if(current_pass.getText().toString().trim().equals(value.getString(Keys.PASSWORD)))
                            {
                                user.updatePassword(password.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Password Updated Sucessfully", Toast.LENGTH_SHORT).show();
                                        HashMap<String,Object> update_password = new HashMap<String,Object>();
                                        update_password.put(Keys.PASSWORD,password.getText().toString().trim());
                                        docrefrence.update(update_password).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(), "Password saved in Collection Sucessfully", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        Intent inext = new Intent(getContext(), Login_Screen.class);
                                        startActivity(inext);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Something went Wroong\nCheck your Internet Connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else
                            {

                                Toast.makeText(getContext(), "Enter Correct Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        return view;
    }
}