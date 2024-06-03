package com.starkindustries.instagram_authentication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.starkindustries.instagram_authentication.databinding.ActivityDashBoardBinding;
public class DashBoard extends AppCompatActivity {
    public ActivityDashBoardBinding binding;
    public SharedPreferences preferences;
    public SharedPreferences.Editor edit;
    public FirebaseAuth auth;
    public FirebaseFirestore store;
    public FirebaseUser user;
    public ActionBarDrawerToggle toggler;
    public TextInputEditText email_address_edittext;
    public AppCompatButton verify_email_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        binding= DataBindingUtil.setContentView(DashBoard.this,R.layout.activity_dash_board);
        auth=FirebaseAuth.getInstance();
        store=FirebaseFirestore.getInstance();
        user=auth.getCurrentUser();
        preferences=getSharedPreferences(Keys.SHARED_PREFRENCES_NAME,MODE_PRIVATE);
        edit=preferences.edit();
        setSupportActionBar(binding.toolbar);
        try {
            getSupportActionBar().setTitle("Alfagram");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        toggler = new ActionBarDrawerToggle(DashBoard.this,binding.drawerLayout,binding.toolbar,R.string.open,R.string.close);
        binding.drawerLayout.addDrawerListener(toggler);
        toggler.syncState();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frame,new Profile_Page());
        transaction.commit();
//        binding.logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                preferences=getSharedPreferences(Keys.SHARED_PREFRENCES_NAME,MODE_PRIVATE);
//                edit=preferences.edit();
//                FirebaseAuth.getInstance().signOut();
//                if(auth.getCurrentUser()==null)
//                {
//                    edit.putBoolean(Keys.FLAG,false);
//                    Intent inext = new Intent(DashBoard.this, Login_Screen.class);
//                    startActivity(inext);
//                    finish();
//                }
//            }
//        });
        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int item_id = item.getItemId();
                if(item_id==R.id.logout)
                {
                    edit.putBoolean(Keys.FLAG,false);
                    FirebaseAuth.getInstance().signOut();
                    Intent login_screen = new Intent(DashBoard.this, Login_Screen.class);
                    startActivity(login_screen);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }
                else if(item_id==R.id.update_password)
                {
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.frame,new Update_Password());
                    transaction.commit();
                    binding.drawerLayout.closeDrawer(GravityCompat.START);

                }
                else if(item_id==R.id.verify_email_address)
                {
                    if(user.isEmailVerified())
                    {
                        Toast.makeText(DashBoard.this, "Your Email-Id is already Verified", Toast.LENGTH_SHORT).show();
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                    }
                    else
                    {
                        Dialog verify_email_dialog = new Dialog(DashBoard.this);
                        verify_email_dialog.setContentView(R.layout.verify_email_layout);
                        email_address_edittext=verify_email_dialog.findViewById(R.id.email_address_edittext);
                        verify_email_button=verify_email_dialog.findViewById(R.id.verify_email_button);
                        verify_email_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(TextUtils.isEmpty(email_address_edittext.getText().toString().trim()))
                                {
                                    email_address_edittext.setError("Enter Proper Email Address");
                                    return ;
                                }
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(DashBoard.this, "Verification Email Sent Sucessfully", Toast.LENGTH_SHORT).show();
                                        verify_email_dialog.dismiss();
                                        FragmentManager manager = getSupportFragmentManager();
                                        FragmentTransaction transaction = manager.beginTransaction();
                                        transaction.replace(R.id.frame,new Profile_Page());
                                        transaction.commit();
                                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        verify_email_dialog.dismiss();
                                        FragmentManager manager = getSupportFragmentManager();
                                        FragmentTransaction transaction = manager.beginTransaction();
                                        transaction.replace(R.id.frame,new Profile_Page());
                                        transaction.commit();
                                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                                        Toast.makeText(DashBoard.this, "Something Went wrong , Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        verify_email_dialog.show();
                    }
                }
                else if(item_id==R.id.profile)
                {
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.frame,new Profile_Page());
                    transaction.commit();
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }
                else if(item_id==R.id.update_profile)
                {
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.frame,new Update_Profile_Page());
                    transaction.commit();
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(DashBoard.this).inflate(R.menu.menu_layout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id=item.getItemId();
        if(item_id==R.id.profile)

            Toast.makeText(this, "Opening Profile Page", Toast.LENGTH_SHORT).show();
        else if(item_id==R.id.update_profile)
            Toast.makeText(this, "Opening Profile Page", Toast.LENGTH_SHORT).show();
        else if(item_id==R.id.update_password)
            Toast.makeText(this, "Updating Password", Toast.LENGTH_SHORT).show();
        else if(item_id==R.id.verify_email_address)
        {
            if(user.isEmailVerified())
            {
                Toast.makeText(DashBoard.this, "Your Email-Id is already Verified", Toast.LENGTH_SHORT).show();
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
            else
            {
                Dialog verify_email_dialog = new Dialog(DashBoard.this);
                verify_email_dialog.setContentView(R.layout.verify_email_layout);
                email_address_edittext=verify_email_dialog.findViewById(R.id.email_address_edittext);
                verify_email_button=verify_email_dialog.findViewById(R.id.verify_email_button);
                verify_email_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(email_address_edittext.getText().toString().trim()))
                        {
                            email_address_edittext.setError("Enter Proper Email Address");
                            return ;
                        }
                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(DashBoard.this, "Verification Email Sent Sucessfully", Toast.LENGTH_SHORT).show();
                                verify_email_dialog.dismiss();
                                FragmentManager manager = getSupportFragmentManager();
                                FragmentTransaction transaction = manager.beginTransaction();
                                transaction.replace(R.id.frame,new Profile_Page());
                                transaction.commit();
                                binding.drawerLayout.closeDrawer(GravityCompat.START);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                verify_email_dialog.dismiss();
                                binding.drawerLayout.closeDrawer(GravityCompat.START);
                                FragmentManager manager = getSupportFragmentManager();
                                FragmentTransaction transaction = manager.beginTransaction();
                                transaction.replace(R.id.frame,new Profile_Page());
                                transaction.commit();
                                Toast.makeText(DashBoard.this, "Something Went wrong , Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                verify_email_dialog.show();
            }
        }
        else if(item_id==R.id.logout)
        {
            Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
//            FirebaseAuth.getInstance().signOut();
//            Intent login_screen = new Intent(DashBoard.this, Login_Screen.class);
//            startActivity(login_screen);
//            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(item_id==android.R.id.home)
        {
            AlertDialog.Builder exit_dialoog = new AlertDialog.Builder(DashBoard.this);
            exit_dialoog.setTitle("Exit");
            exit_dialoog.setMessage("Are you sure,you want to exit");
            exit_dialoog.setIcon(R.drawable.logout);
            exit_dialoog.setCancelable(false);
            exit_dialoog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DashBoard.super.onBackPressed();
                }
            });
            exit_dialoog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder exit_dialoog = new AlertDialog.Builder(DashBoard.this);
        exit_dialoog.setTitle("Exit");
        exit_dialoog.setMessage("Are you sure,you want to exit");
        exit_dialoog.setIcon(R.drawable.logout);
        exit_dialoog.setCancelable(false);
        exit_dialoog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DashBoard.super.onBackPressed();
            }
        });
        exit_dialoog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        exit_dialoog.show();
    }
}