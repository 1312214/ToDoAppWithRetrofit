package com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.AppConfig;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.BuildConfig;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.R;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.network.APIServiceProvider;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.restbean.Author;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.restbean.TokenID;
import com.duyhoang.restfulwebserviceintergrationRetrofitRefactoring.ui.fragment.RegisterDialogFrag;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements RegisterDialogFrag.RegisterListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener{



    Button btnLogin;
    EditText etUsername;
    EditText etPassword;
    TextView txtRegister;
    Switch aSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        if(AppConfig.getServerEndPointPreference())
            aSwitch.setChecked(true);
        else
            aSwitch.setChecked(false);
        aSwitch.setTextOff("Localhost");
        aSwitch.setTextOn("Remote");

        if(AppConfig.getRegisteredSuccessfulAuthor() != null){
            etUsername.setText(AppConfig.getRegisteredSuccessfulAuthor().getAuthorName());
        }

        //BE CAREFULL WITH BUILDCONIGG.DEBUG WHEN BUILDING FOR RELEASE VERSON.
//        if(BuildConfig.DEBUG)
//            etPassword.setText("ha");

    }

    private void initUI() {
        etUsername = findViewById(R.id.edit_user_name);
        etPassword = findViewById(R.id.edit_password);
        btnLogin = findViewById(R.id.button_sign_in);
        txtRegister = findViewById(R.id.text_register);
        aSwitch = findViewById(R.id.switch_mode);

        btnLogin.setOnClickListener(this);
        txtRegister.setOnClickListener(this);
        aSwitch.setOnCheckedChangeListener(this);
    }


    @Override
    public void onRegisterSuccess(Author author) {
        AppConfig.saveRegisteredSuccessfulAuthor(new GsonBuilder().create().toJson(author));
        AppConfig.saveUsername(author.getAuthorName());
    }

    @Override
    public void onRegisterFailure(String message) {
        Toast.makeText(this, "Error: - " + message , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_sign_in: login();
                break;
            case R.id.text_register: showRegistationDialog();
                break;
        }

    }


    private void showRegistationDialog() {
        RegisterDialogFrag registerDialogFrag = new RegisterDialogFrag();
        registerDialogFrag.setRegisterListener(this);
        registerDialogFrag.show(getSupportFragmentManager(), "RegisterDialog");
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(isChecked){
            AppConfig.setServerEndPointPreference(true);
            AppConfig.seletedEndPoint = AppConfig.API_ENDPOINTS.REMOTE;
        }
        else{
            AppConfig.setServerEndPointPreference(false);
            AppConfig.seletedEndPoint = AppConfig.API_ENDPOINTS.LOCAL;
        }
    }




    private void goToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }


    private void login(){
        showBusyDialog("Logging");
        if(isValidAccount()){
            String password = etPassword.getText().toString();
            Author user = AppConfig.getRegisteredSuccessfulAuthor();
            user.setAuthorPassword(password);

            Call<TokenID> tokenIDCall = APIServiceProvider.getAPIServiceProvider().loginAuthor(user);
            tokenIDCall.enqueue(new Callback<TokenID>() {
                @Override
                public void onResponse(Call<TokenID> call, Response<TokenID> response) {
                    AppConfig.saveSessionTokenID(response.body().getToken());
                    dismissBusyDialog();
                    goToHomeActivity();
                }

                @Override
                public void onFailure(Call<TokenID> call, Throwable t) {
                    toastMessage("Network issue: " + t.getMessage(), Toast.LENGTH_SHORT );
                }
            });
        }
        else {
            toastMessage("Your information account is invalid", Toast.LENGTH_LONG);
        }


    }

    private boolean isValidAccount() {
        String username = etUsername.getText().toString();
        String pw = etPassword.getText().toString();
        if(username == null || username.length() == 0 || pw == null || username.length() == 0)
            return false;
        else
            return true;
    }


}


