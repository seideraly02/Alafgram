package com.starkindustries.instagram_authentication;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile_Page#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile_Page extends Fragment {
    public AppCompatTextView profile_name,profile_user_name,profile_email;
    public AppCompatImageView profile_imageview;
    public AppCompatButton update_profile_button;
    public FirebaseAuth auth;
    public FirebaseFirestore store;
    public String user_id;
//    public StorageReference storageReference,child_redrence;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Profile_Page() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile_Page.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile_Page newInstance(String param1, String param2) {
        Profile_Page fragment = new Profile_Page();
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
        View view =  inflater.inflate(R.layout.fragment_profile__page, container, false);
        profile_name=view.findViewById(R.id.profile_name);
        profile_user_name=view.findViewById(R.id.profile_user_name);
        profile_email=view.findViewById(R.id.profile_email);
        update_profile_button=view.findViewById(R.id.update_profile_button);
        profile_imageview=view.findViewById(R.id.profile_imageview);
        auth=FirebaseAuth.getInstance();
        store=FirebaseFirestore.getInstance();
        user_id=auth.getCurrentUser().getUid();
        DocumentReference refrence = store.collection(Keys.COLECTION_NAME).document(user_id);
        refrence.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists())
                {
                    profile_name.setText(value.getString(Keys.NAME));
                    profile_email.setText(value.getString(Keys.EMAIL));
                    profile_user_name.setText(value.getString(Keys.USERNAME));
                }
            }
        });
//        storageReference= FirebaseStorage.getInstance().getReference();
//        child_redrence=storageReference.child("profile.jpg");
//        child_redrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Picasso.get().load(uri).into(profile_imageview);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getContext(), "Something went Wrong", Toast.LENGTH_SHORT).show();
//            }
//        });
        update_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame,new Update_Profile_Page());
                transaction.commit();
            }
        });
        return view;
    }
}