package com.codeeasy.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class Profile extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
     private ImageView profile_image;
     private TextView name;
     private TextView email;
     private Button signoutBtn;

     private GoogleApiClient googleApiClient;
     private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile_image = findViewById(R.id.profile_image);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        signoutBtn = findViewById(R.id.signoutBtn);

         gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

         googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this ,this)
                 .addApi(Auth.GOOGLE_SIGN_IN_API ,gso).build();

         signoutBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                     @Override
                     public void onResult(@NonNull @NotNull Status status) {
                         if(status.isSuccess())
                             gotoMainActivity();
                         else
                             Toast.makeText(Profile.this, "Log Out Failed", Toast.LENGTH_SHORT).show();
                     }
                 });
             }
         });
    }

    private void gotoMainActivity() {
        startActivity(new Intent(Profile .this , MainActivity.class));
        finish();
    }



    @Override

    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {

    }
    private void handleSignInResult(GoogleSignInResult result){
        if (result.isSuccess()){
            GoogleSignInAccount account = result. getSignInAccount();

            name.setText(account.getDisplayName());
            email.setText(account.getEmail());

            Picasso.get().load(account.getPhotoUrl()).placeholder(R.mipmap.ic_launcher).into(profile_image);
        }else{
            gotoMainActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull @NotNull GoogleSignInResult result) {
                    handleSignInResult(result);
                }
            });
        }
    }
}