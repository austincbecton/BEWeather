package com.be.beweather.accounts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.be.beweather.R;
import com.be.beweather.model.WebViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AccountFragment extends Fragment {

    WebViewModel model;
    FirebaseUser user;
    StormAccount thisAccount;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    TextView title_my_account_name;
    TextView title_my_account_level;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService backgroundWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Button logoutButton;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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

        model = WebViewModel.getWebViewModel(getContext(), getActivity());
        user = FirebaseAuth.getInstance().getCurrentUser();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //Set up views
        title_my_account_name = view.findViewById(R.id.title_my_account_name);
        title_my_account_level = view.findViewById(R.id.title_my_account_level);


        //Get Firebase account
        user = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println("Current user is " + model.getCurrentAccountFromModel());

        if (model.getCurrentAccountFromModel() != null) {
            if (model.getCurrentAccountFromModel().equals("") ||
                    model.getCurrentAccountFromModel().isEmpty() ||
                    model.getCurrentAccountFromModel().equals("123")) {

                logIn();

            } else {
                if (user != null) {
                    System.out.println("WERE IN" + user.getUid());
                    System.out.println(model.getCurrentAccountFromModel());
                } else {
                    logIn();
                }

            }

        } else {

            logOutFirebase();
        }



        //Observe updates to current user
        final Observer<String> currentUserObserver = new Observer<String>() {

            @Override
            public void onChanged(@Nullable final String currentAccountId) {

                backgroundWriteExecutor.execute(() -> {

                    try {

                        thisAccount = model.getAccountFromDatabase(user.getUid());
                        System.out.println("This account was set in on changed method to: "+
                                thisAccount.getFirebaseId()+thisAccount.getNickname());
                        title_my_account_name.setText(thisAccount.getNickname());
                        title_my_account_level.setText(thisAccount.getMembership());


                    }catch (Exception e) {
                        System.out.println("Error in the onChanged method in AccountFragment");
                    }

                });


            }

        };


        model.currentAccount.observe(getViewLifecycleOwner(), currentUserObserver);





        NavigationView accountOptions = view.findViewById(R.id.account_navigation_menu);
        accountOptions.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull  MenuItem item) {
                if (item.getTitle().toString().equals("delete account")) {
                    try {
                        System.out.println("Account to delete: " + user.getUid());
                        user.delete();
                        StormAccount accountToBeDeleted = new StormAccount();
                        accountToBeDeleted.setFirebaseId(model.getCurrentAccountFromModel());
                        System.out.println("Account to delete: " + accountToBeDeleted.getFirebaseId());
                        model.deleteAccount(accountToBeDeleted);
                    } catch (Exception e) {
                        System.out.println("Error deleting this account");
                    }

                }
                else if (item.getTitle().toString().equals("logout")) {
                    logOutFirebase();
                }
                return false;
            }
        });



    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == Activity.RESULT_OK) {



            StormAccount account = new StormAccount();
            account.setFirebaseId(FirebaseAuth.getInstance().getUid());
            try {account.setNickname(user.getDisplayName());} catch (Exception e) {System.out.println("Error getting user.getDisplayName in acct frag");}
            if (account.getNickname() == null) {
                account.setNickname("no name set");
            }
            account.setMembership("basic");
            model.saveAccountInStormDatabase(account);
            title_my_account_name.setText(account.getNickname());
            title_my_account_level.setText(account.getMembership());

            model.setCurrentAccount(account.getFirebaseId());
            System.out.println(account.getNickname());
            System.out.println(model.getCurrentAccountFromModel());


        } else {
            System.out.println("DID NOT SET MODEL");

        }
    }



    public void logOutFirebase() {

        AuthUI.getInstance()
                .signOut(getContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        logIn();
                    }
                });


    }

    public void logIn() {
        FirebaseAuth.getInstance().signOut();
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);


    }




}

/*






 */