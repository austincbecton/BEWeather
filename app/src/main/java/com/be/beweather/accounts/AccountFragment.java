package com.be.beweather.accounts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.be.beweather.R;
import com.be.beweather.model.WebViewModel;
import com.be.beweather.weatherdata.GeneralLoadingScreenFragment;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountFragment extends Fragment {

    WebViewModel model;
    FirebaseUser user;
    StormAccount thisAccount;
    TextView title_my_account_name;
    TextView title_my_account_level;
    private ArrayList<StormAccount> allAccountsList;
    private ViewGroup accountFragmentViews;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService backgroundWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final String TAG = "AccountFragment";
    private View inflatedView;
    Button change_name_button;
    Boolean changeNameIsShowing;
    EditText changeNameDialog;


    public Button logoutButton;
    /*
       private static final String ARG_PARAM1 = "param1";
       private static final String ARG_PARAM2 = "param2";

       private String mParam1;
       private String mParam2;

        */
    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        /*
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

         */
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

         */

        model = WebViewModel.getWebViewModel(getContext(), getActivity());
        user = FirebaseAuth.getInstance().getCurrentUser();
        backgroundWriteExecutor.execute(()->{
            allAccountsList = model.getAllAccounts_foruseOnBackgroundThread();
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set up loading screen
        getLayoutInflater().inflate(R.layout.general_loading_screen,
                view.findViewById(R.id.account_fragment_root));
        inflatedView = view;

        //This will set up rotation/animations for loading screen.
        setLoadingScreen(view);


        //Set up views
        title_my_account_name = view.findViewById(R.id.title_my_account_name);
        title_my_account_level = view.findViewById(R.id.title_my_account_level);
        changeNameIsShowing = false;


        /**
         * MainActivity should have already checked model for current user.
         * First method:     set up loading screen
         * Second method:    check if user signed in now ***IN FIREBASE***
         *                  if yes, update model
         *                  if no, start login process + remove loading screen, then update textviews
         * Third method:    remove loading screen + update textviews
         */


        //Get Firebase account
        user = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println("Current user is " + model.getCurrentAccountFromModel());


        //Check current sign in
        try {
            if (checkCurrentLogIn()) {
                //stopLoadingScreen(view);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking current log in");
            //In case of error, we'll just log out.
            //stopLoadingScreen(view);
            logOutFirebase();
        }


        //Observe gets updates from model.currentAccount, then updates the account info displayed
        final Observer<String> currentUserObserver = new Observer<String>() {

            @Override
            public void onChanged(@Nullable final String currentAccountId) {

                System.out.println("onChanged called in AccountFragment");
                //Update the current account being used.
                for (StormAccount nextAccount : allAccountsList) {
                    System.out.println("Accounts test: allAccountsList item: "+ nextAccount.getFirebaseId());

                    if (nextAccount.getFirebaseId().equals(currentAccountId)) {
                        StormAccount retrievedAccount = new StormAccount();
                        retrievedAccount.setNickname(nextAccount.getNickname());
                        retrievedAccount.setFirebaseId(nextAccount.getFirebaseId());
                        retrievedAccount.setMembership(nextAccount.getMembership());
                        thisAccount = retrievedAccount;
                    }
                }


                try {
                    title_my_account_name.setText(thisAccount.getNickname());
                    title_my_account_level.setText(thisAccount.getMembership());

                }catch (Exception e) {
                    Log.e(TAG, "Error setting textviews due to null account");
                }


            }

        };


        model.currentAccount.observe(getViewLifecycleOwner(), currentUserObserver);




       // View.inflate(getContext(), R.layout.fragment_account , view.findViewById(R.id.general_loading_screen_root));

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
                        model.setCurrentAccount(null);
                        logOutFirebase();
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


        change_name_button = view.findViewById(R.id.change_name_button);
        change_name_button.setOnClickListener(View -> {
            alterFunctionality_change_name_button(view);

        });

        try {thisAccount.getFirebaseId();}
        catch(Exception e) {
            for (StormAccount nextAccount : allAccountsList) {
                System.out.println("Searching accts: " + nextAccount.getFirebaseId());

                if (nextAccount.getFirebaseId().equals(model.getCurrentAccountFromModel())) {
                        StormAccount newAccount = setUpNewAccount();
                        newAccount.setFirebaseId(user.getUid());
                        newAccount.setNickname("noName");
                        newAccount.setMembership("basic");
                        thisAccount = newAccount;
                    }

            }
        }


        //At end of onViewCreated, we'll double check that somebody's signed in.
        //If not, we'll start sign in process again.
        try {thisAccount.getFirebaseId();}
        catch(Exception e) {
            checkCurrentLogIn();
        }







    }

    //Allows user to change their nickname
    //Switches to an edittext, then when finished, it changes back to textview
    private void alterFunctionality_change_name_button(View view) {
        if (changeNameIsShowing) {
            if (!changeNameDialog.getText().toString().isEmpty()) {

                try {thisAccount.setNickname(changeNameDialog.getText().toString());}
                catch(Exception e) {
                    Log.e(TAG, "Error setting nickname. The account might be null.");
                }
                model.updateAccount(thisAccount);
            }

            changeNameDialog.setVisibility(View.INVISIBLE);
            title_my_account_name.setVisibility(View.VISIBLE);
            title_my_account_name.setText(thisAccount.getNickname());
            changeNameIsShowing = false;


        }
        else {

            changeNameDialog = new EditText(getContext());
            changeNameDialog.setLayoutParams(title_my_account_name.getLayoutParams());
            title_my_account_name.setVisibility(android.view.View.INVISIBLE);
            ConstraintLayout my_account_card_internal_layout = view.findViewById(R.id.my_account_card_internal_layout);
            my_account_card_internal_layout.addView(changeNameDialog);
            changeNameIsShowing = true;
        }
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


            //check if this account exists already
            boolean foundAccount = false;
            try {
                for (StormAccount nextAccount : allAccountsList) {
                    if (nextAccount.getFirebaseId().equals(FirebaseAuth.getInstance().getUid())) {
                        model.setCurrentAccount(nextAccount.getFirebaseId());
                        foundAccount = true;

                        StormAccount newAccount = setUpNewAccount();
                        newAccount.setFirebaseId(nextAccount.getFirebaseId());
                        newAccount.setNickname(nextAccount.getNickname());
                        newAccount.setMembership(nextAccount.getMembership());
                        thisAccount = newAccount;

                        break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error, likely relted to user.getUid");
            }


            //if not, make a new one using firebaseAuth's ["user"] info
            if (!foundAccount) {
                //Use set up new account method so we can then save it in database and model
                StormAccount newAccount = setUpNewAccount();
                newAccount.setFirebaseId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                newAccount.setNickname("noName");
                newAccount.setMembership("basic");
                thisAccount = newAccount;
                model.saveAccountInStormDatabase(newAccount);
                model.setCurrentAccount(newAccount.getFirebaseId());
                backgroundWriteExecutor.execute(()->{
                    allAccountsList = model.getAllAccounts_foruseOnBackgroundThread();
                });
            }

            System.out.println("End of onSignInResult. User is: " + model.getCurrentAccountFromModel());

        } else {
            System.out.println("DID NOT SET MODEL");

        }

        try {model.setCurrentAccount(FirebaseAuth.getInstance().getUid());}
        catch(Exception e) {
            Log.e(TAG, "error getting user.getUid");
            try {
                model.setCurrentAccount(FirebaseAuth.getInstance().getUid());
            }
            catch(Exception e1) {
                Log.e(TAG, "error getting user.getUid in second attempt");

            }
        }
        stopLoadingScreen(inflatedView);




    }



    public void logOutFirebase() {

        AuthUI.getInstance()
                .signOut(getContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "loggin in user: logOutFirebase");
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


    public void setLoadingScreen(View view) {

        ImageView loadingIcon = view.findViewById(R.id.loadingIcon);
        Animation rotateAnim = AnimationUtils.loadAnimation(getContext(), R.anim.loading_anim);
        loadingIcon.startAnimation(rotateAnim);
        boolean loggedIn = false;
        if (model.getCurrentAccountFromModel() != null) {
            if (!model.getCurrentAccountFromModel().isEmpty()) {
                loggedIn = true;
            }
        }

        Boolean finalLoggedIn = loggedIn;
        loadingIcon.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!finalLoggedIn) {
                    loadingIcon.startAnimation(rotateAnim);
                    setLoadingScreen(view);
                }
            }
        }, 5100);
    }


    private boolean checkCurrentLogIn() {


        if (model.getCurrentAccountFromModel() != null) {
            Log.i(TAG, "Current user acc to model: " + model.getCurrentAccountFromModel());
            Log.i(TAG, "Current user acc to firebase user variable: "+ FirebaseAuth.getInstance().getUid());

            if (model.getCurrentAccountFromModel().equals(FirebaseAuth.getInstance().getUid())) {
                //TODO: Update local account information with firebase
                stopLoadingScreen(inflatedView);
                backgroundWriteExecutor.execute(() -> {
                    allAccountsList = model.getAllAccounts_foruseOnBackgroundThread();
                    for (StormAccount account : allAccountsList) {
                        if (account.getFirebaseId().equals(user.getUid())) {
                            StormAccount retrievedAccount = new StormAccount();
                            retrievedAccount.setNickname(account.getNickname());
                            retrievedAccount.setFirebaseId(account.getFirebaseId());
                            retrievedAccount.setMembership(account.getMembership());
                            thisAccount = retrievedAccount;

                        }
                    }
                });

                return true;
            } else {
                Log.i(TAG, "loggin in user: checkCurrentLogIn method, else statement 1");
                logIn();
                return false;
            }

        } else {
            Log.i(TAG, "loggin in user: checkCurrentLogIn method, else statement 2");
            logIn();
            return false;
        }


    }




    private void stopLoadingScreen(View thisFragmentsView) {

        thisFragmentsView.findViewById(R.id.general_loading_screen_root).setVisibility(View.INVISIBLE);

    }

    private StormAccount setUpNewAccount() {
        StormAccount account = new StormAccount();
        account.setFirebaseId(FirebaseAuth.getInstance().getUid());
        try {account.setNickname(FirebaseAuth.getInstance().getUid());} catch (Exception e) {System.out.println("Error getting user.getDisplayName in acct frag");}
        if (account.getNickname() == null) {
            account.setNickname("no name set");
        }
        account.setMembership("basic");
        return account;
    }


}

