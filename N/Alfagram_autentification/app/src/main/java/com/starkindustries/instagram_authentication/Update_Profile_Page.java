package com.starkindustries.instagram_authentication;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Objects;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Update_Profile_Page#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Update_Profile_Page extends Fragment {
    public AppCompatImageView update_profile_imageview;
    public TextInputEditText update_fullname,update_email,update_username;
    public StorageReference storageReference,child_refrence;
    public AppCompatButton update_button;
    public FirebaseAuth auth;
    public FirebaseFirestore store;
    public String user_id;
    public DocumentReference reference;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Update_Profile_Page() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Update_Profile_Page.
     */
    // TODO: Rename and change types and number of parameters
    public static Update_Profile_Page newInstance(String param1, String param2) {
        Update_Profile_Page fragment = new Update_Profile_Page();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update__profile__page, container, false);
//        update_profile_imageview=view.findViewById(R.id.update_profile_imageview);
        update_fullname=view.findViewById(R.id.update_fullname);
        update_email=view.findViewById(R.id.update_email);
        update_username=view.findViewById(R.id.update_username);
        update_button=view.findViewById(R.id.update_button);
        auth=FirebaseAuth.getInstance();
        store=FirebaseFirestore.getInstance();
        update_profile_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK);
                gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery,Keys.GALLERY_KEY);
            }
        });
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(update_fullname.getText().toString().trim()))
                {
                    update_fullname.setError("Enter Proper Name");
                    return ;
                }
                if(TextUtils.isEmpty(update_username.getText().toString().trim()))
                {
                    update_username.setError("Enter Proper Username");
                    return ;
                }
                if(TextUtils.isEmpty(update_email.getText().toString().trim()))
                {
                    update_email.setError("Enter Proper Email");
                    return ;
                }
                user_id=auth.getCurrentUser().getUid();
                reference=store.collection(Keys.COLECTION_NAME).document(user_id);
                HashMap<String, Object> map = new HashMap<String,Object>();
                map.put(Keys.NAME,update_fullname.getText().toString().trim());
                map.put(Keys.USERNAME,update_username.getText().toString().trim());
                map.put(Keys.EMAIL,update_email.getText().toString());
                reference.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Data Updated Sucessfully", Toast.LENGTH_SHORT).show();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame,new Profile_Page());
                        transaction.commit();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Some thing Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==Keys.GALLERY_KEY)
        {
            Uri image_uri=data.getData();
//            update_profile_imageview.setImageURI(data.getData());
        }
        storageReference= FirebaseStorage.getInstance().getReference();
        child_refrence=storageReference.child("profile.jpg");
        child_refrence.putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "Profile Pic Uploaded Sucessfully", Toast.LENGTH_SHORT).show();
            }
        });
        super.onActivityResult(requestCode, resultCode, data);
    }
}