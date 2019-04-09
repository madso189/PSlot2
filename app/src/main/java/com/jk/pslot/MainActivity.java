package com.jk.pslot;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity {
    private Menu menu;
    private SignInButton mGoogleBtn;
   private TextView nameText;
    private FirebaseAuth mAuth;
    static final int GOOGLE_SIGN=123;
    GoogleSignInClient mGooleSignInClient;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

   // private GoogleApiClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


        mGoogleBtn = (SignInButton)findViewById(R.id.googleBtn);
        nameText=(TextView)findViewById(R.id.nameText);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGooleSignInClient = GoogleSignIn.getClient(this,gso);

        mGoogleBtn.setOnClickListener(v -> SignInGoogle());

    }



    void SignInGoogle(){
        Intent signIntent=mGooleSignInClient.getSignInIntent();
        startActivityForResult(signIntent,GOOGLE_SIGN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account=task.getResult(ApiException.class);
                if(account!=null)firebaseAuthWithGoogle(account);

            }
            catch (ApiException e){
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG","firebaseAuthWithGoogle: "+account.getId());

        AuthCredential credential=GoogleAuthProvider
                .getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this,task->{
                    if (task.isSuccessful()) {
                        Log.d("TAG","signIn success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    }else{
                        Log.w("TAG","signin failure",task.getException());
                        Toast.makeText(this,"Signin failed",Toast.LENGTH_SHORT);
                        updateUI(null);
                    }

                });

    }
    private void updateUI(FirebaseUser user) {
        if(user!=null){
           /* String name=user.getDisplayName();
            String email=user.getEmail();
            Toast.makeText(this,"success",Toast.LENGTH_SHORT);

            mGoogleBtn.setVisibility(View.INVISIBLE);
            nameText.setVisibility(View.VISIBLE);
            nameText.setText(name);*/
            startActivity(new Intent(MainActivity.this,home.class));


        }
        else{
            Toast.makeText(this,"Signin failed",Toast.LENGTH_SHORT);
            mGoogleBtn.setVisibility(View.VISIBLE);
            nameText.setVisibility(View.INVISIBLE);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu=menu;
        try {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
      try {
          FirebaseUser user = mAuth.getCurrentUser();

          String name = user.getDisplayName();
          if(user!=null){
          if (Build.VERSION.SDK_INT > 11) {
              invalidateOptionsMenu();
              menu.findItem(R.id.userName).setVisible(true);
              menu.findItem(R.id.userName).setTitle(name);
              menu.findItem(R.id.signOut).setVisible(true);

            }
          }


      }catch (Exception e){}
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {



            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}
