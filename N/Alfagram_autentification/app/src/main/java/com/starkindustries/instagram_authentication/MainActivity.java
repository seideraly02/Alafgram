package com.starkindustries.instagram_authentication;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.starkindustries.instagram_authentication.databinding.ActivityMainBinding;
public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;
    public FirebaseAuth auth;
    public FirebaseFirestore store;
    public SharedPreferences prefrences;
    public SharedPreferences.Editor edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding= DataBindingUtil.setContentView(MainActivity.this,R.layout.activity_main);
        auth= FirebaseAuth.getInstance();
        store=FirebaseFirestore.getInstance();
        prefrences=getSharedPreferences(Keys.SHARED_PREFRENCES_NAME,MODE_PRIVATE);
        edit = prefrences.edit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(prefrences.getBoolean(Keys.FLAG,true)&&(auth.getCurrentUser()==null))
                {
                    Intent inext=new Intent(MainActivity.this,Login_Screen.class);
                    Pair pairs[] = new Pair[2];
                    pairs[0]= new Pair<View,String>(binding.appLogo,"logo");
                    pairs[1]=new Pair<View,String>(binding.appName,"name");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                    startActivity(inext,options.toBundle());
                }
                else
                {
                Intent inext = new Intent(MainActivity.this, DashBoard.class);
                startActivity(inext);
                }
            }
        },1000);
    }
}