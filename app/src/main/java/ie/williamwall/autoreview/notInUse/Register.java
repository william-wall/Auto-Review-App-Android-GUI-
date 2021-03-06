package ie.williamwall.autoreview.notInUse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import ie.williamwall.autoreview.R;
import ie.williamwall.autoreview.user.User;

// Designed and Developed @ William Wall
// Email @ william@williamwall.ie
// GitHub @ https://github.com/william-wall/Auto-Review-App-Android-GUI

public class Register extends AppCompatActivity {

    private EditText enterNameW, enterEmailW, enterPhoneW, enterPasswordW, enterConfirmPasswordW;
    public String name, email, phone, password, confirmPassword;
    private Button registerButton;
    ArrayList<User> userInstance = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        enterNameW = (EditText) findViewById(R.id.enterName);
        enterEmailW = (EditText) findViewById(R.id.enterEmail);
        enterPhoneW = (EditText) findViewById(R.id.enterPhone);
        enterPasswordW = (EditText) findViewById(R.id.enterPassword);
        enterConfirmPasswordW = (EditText) findViewById(R.id.enterConfirmPassword);
        registerButton = (Button) findViewById(R.id.registerButton);
        Log.d("myTag", "This is my message");
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    public void register() {
        initilize();
        if (!validate()) {
            Toast.makeText(this, "Signup has Failed", Toast.LENGTH_SHORT).show();
        } else {
            onSignupSuccess();
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

            User user = new User(R.mipmap.ic_launcher_round, name, email, phone, password,currentDateTimeString);
            userInstance.add(user);
            Log.d("list", user.toString());
            Intent move = new Intent(Register.this, AdministrationUserOld.class);
            move.putExtra("message_key", name);
            move.putExtra("message_key2", email);
            move.putExtra("message_key3", phone);
            move.putExtra("message_key4", password);
            startActivity(move);
        }
    }

    public void onSignupSuccess() {
        name = enterNameW.getText().toString();
        email = enterEmailW.getText().toString();
        phone = enterPhoneW.getText().toString();
        password = enterPasswordW.getText().toString();
        confirmPassword = enterConfirmPasswordW.getText().toString();
    }

    public void initilize() {
        name = enterNameW.getText().toString().trim();
        email = enterEmailW.getText().toString().trim();
        phone = enterPhoneW.getText().toString().trim();
        password = enterPasswordW.getText().toString().trim();
        confirmPassword = enterConfirmPasswordW.getText().toString().trim();
    }

    public boolean validate() {
        boolean valid = true;
        if (name.isEmpty() || name.length() > 32) {
            enterNameW.setError("Please Enter Valid Name");
            valid = false;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            enterEmailW.setError("Please Enter Valid Email");
        }
        if (phone.isEmpty()) {
            enterPhoneW.setError("Please Enter Valid Phone");
        }
        if (password.isEmpty()) {
            enterPasswordW.setError("Please Enter Valid Password");
        }
        if (confirmPassword.isEmpty()) {
            enterConfirmPasswordW.setError("Please Enter Valid Password");
        }
        if (!password.equals(confirmPassword))
        {
            enterPasswordW.getText().clear();
            enterConfirmPasswordW.getText().clear();
            enterPasswordW.setError("Please Enter Valid Password");
            enterConfirmPasswordW.setError("Please Enter Valid Password");
            return valid = false;
        }
        return valid;
    }
}
