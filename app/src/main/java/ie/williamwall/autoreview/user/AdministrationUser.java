package ie.williamwall.autoreview.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ie.williamwall.autoreview.R;
import ie.williamwall.autoreview.firebase.LoginActivityFirebase;
import ie.williamwall.autoreview.maps.MapsActivity;
import ie.williamwall.autoreview.review.AdministrationReview;
import ie.williamwall.autoreview.weather.Weather;

/**
 * Created by william on 27/02/2018.
 */

public class AdministrationUser extends AppCompatActivity {
    ListView lv;
//    EditText nameTxt, emailTxt, phoneTxt, passwordTxt;
    SearchView searchName;
    CustomAdapterUser myAdapter;
    ArrayList<User> users = new ArrayList<User>();
    Spinner listSpinner;
    int id = -1;
    ArrayList<String> justNames = new ArrayList<String>();
    ArrayAdapter<String>adapter;
//    Button instanceClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administration_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadData();




        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        User userInstance = new User(R.mipmap.car, "JAMES", "HACK", "0879858985" ,"12", currentDateTimeString);
        users.add(userInstance);
        lv = (ListView) findViewById(R.id.listViewMain);
        searchName = (SearchView) findViewById(R.id.searchViewName);
        listSpinner = (Spinner) findViewById(R.id.searchSpinner);

//        instanceClass = (Button) findViewById(R.id.action_user);
//        instanceClass.setEnabled(false);

        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, justNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listSpinner.setAdapter(adapter);


//        nameTxt = (EditText) findViewById(R.id.editName);
//        emailTxt = (EditText) findViewById(R.id.editEmail);
//        phoneTxt = (EditText) findViewById(R.id.editPhone);
//        passwordTxt = (EditText) findViewById(R.id.editPassword);



        myAdapter = new CustomAdapterUser(this, R.layout.item_layout_administration_user, users);
        lv.setAdapter(myAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String nameInstance = "";
                String emailInstance = "";
                String phoneInstance = "";
                String passwordInstance = "";
                id = position;
                nameInstance = users.get(position).getName();
                emailInstance = users.get(position).getEmail();
                phoneInstance = users.get(position).getPhone();
                passwordInstance = users.get(position).getPassword();
                window(position);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                AlertDialog.Builder alert = new AlertDialog.Builder(AdministrationUser.this);
                alert.setTitle("Administer User");

                LinearLayout layout = new LinearLayout(AdministrationUser.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText name = new EditText(AdministrationUser.this);
                name.setHint("Name");
                layout.addView(name);

                final EditText email = new EditText(AdministrationUser.this);
                email.setHint("Email");
                layout.addView(email);

                final EditText phone = new EditText(AdministrationUser.this);
                phone.setHint("Phone");
                layout.addView(phone);

                final EditText password = new EditText(AdministrationUser.this);
                password.setHint("Password");
                layout.addView(password);




                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String stringEditName = name.getText().toString();
                        String stringEditEmail = email.getText().toString();
                        String stringEditPhone = phone.getText().toString();
                        String stringEditPassword = password.getText().toString();
                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                        User temp = new User(R.mipmap.car, stringEditName, stringEditEmail, stringEditPhone, stringEditPassword,currentDateTimeString);
                        users.add(temp);
                        myAdapter.notifyDataSetChanged();
                        justNames.add(stringEditName);
//                        listSpinner.add

                        saveData();
                        Toast.makeText(AdministrationUser.this, "Saved Sucessfully", Toast.LENGTH_LONG).show();

                    }
                });

                alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdministrationUser.this);

                        builder.setTitle("Confirm");
                        builder.setMessage("Are you sure?");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing but close the dialog

                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Do nothing
                                dialog.dismiss();
                            }
                        });
                    }
                });

                alert.setView(layout);


                alert.show();

            }
        });


//        lv.setTextFilterEnabled(true);

        searchName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                lv.setFilterText(newText);

                myAdapter.getFilter().filter(newText);
                return false;
            }
        });

//


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_user);
        item.setEnabled(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

//        MenuItem item = menu.findItem(R.id.action_user);


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
             if (id == R.id.action_logout) {
            Toast.makeText(this, "Logged Off", Toast.LENGTH_SHORT).show();
            Intent Intent = new Intent(AdministrationUser.this, LoginActivityFirebase.class);
            startActivity(Intent);

//            return true;
        }
        if (id == R.id.action_weather) {
            Toast.makeText(this, "Weather Report", Toast.LENGTH_SHORT).show();
            Intent Intent = new Intent(AdministrationUser.this, Weather.class);
            startActivity(Intent);

//            return true;
        }
        if (id == R.id.action_location) {
            Toast.makeText(this, "Logged Off", Toast.LENGTH_SHORT).show();
            Intent Intent = new Intent(AdministrationUser.this, MapsActivity.class);
            startActivity(Intent);

//            return true;
        }
        if (id == R.id.action_review) {
            Toast.makeText(this, "Weather Report", Toast.LENGTH_SHORT).show();
            Intent Intent = new Intent(AdministrationUser.this, AdministrationReview.class);
            startActivity(Intent);

//            return true;
        }
        if (id == R.id.action_user) {
            Toast.makeText(this, "Weather Report", Toast.LENGTH_SHORT).show();



//            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void add() {

//        String stringName = nameTxt.getText().toString();
//        String stringEmail = emailTxt.getText().toString();
//        String stringPhone = phoneTxt.getText().toString();
//        String stringPassword = passwordTxt.getText().toString();
//        User temp = new User(R.mipmap.car, stringName, stringEmail, stringPhone, stringPassword);
//        users.add(temp);
//        myAdapter.notifyDataSetChanged();
//        saveData();
//        Toast.makeText(AdministrationUser.this, "Saved Sucessfully", Toast.LENGTH_LONG).show();

    }

    private void update() {

    }

    private void delete() {

    }

    private void clear() {

    }


    private void saveData() {

        SharedPreferences sharedPreferencesUser = getSharedPreferences("shared preferences3", MODE_PRIVATE);
        SharedPreferences.Editor editorUser = sharedPreferencesUser.edit();
        Gson gsonUser = new Gson();
        String jsonUser = gsonUser.toJson(users);
        editorUser.putString("task list3", jsonUser);
        editorUser.apply();
    }

    private void loadData() {
        users = new ArrayList<User>();
        SharedPreferences sharedPreferencesUser = getSharedPreferences("shared preferences3", MODE_PRIVATE);
        Gson gsonUser = new Gson();
        String jsonUser = sharedPreferencesUser.getString("task list3", null);
        Type typeUser = new TypeToken<ArrayList<User>>() {
        }.getType();
        users = gsonUser.fromJson(jsonUser, typeUser);
        if (users == null) {
            users = new ArrayList<>();
        }
    }

    public int window(int position)
    {



        AlertDialog.Builder alert = new AlertDialog.Builder(AdministrationUser.this);
        alert.setTitle("Administer Review");

        LinearLayout layout = new LinearLayout(AdministrationUser.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText name = new EditText(AdministrationUser.this);
        name.setHint("Name");
        layout.addView(name);

        final EditText email = new EditText(AdministrationUser.this);
        email.setHint("Email");
        layout.addView(email);

        final EditText phone = new EditText(AdministrationUser.this);
        phone.setHint("Phone");
        layout.addView(phone);

        final EditText password = new EditText(AdministrationUser.this);
        password.setHint("Password");
        layout.addView(password);

        id = position;
        name.setText(users.get(position).getName());
        email.setText(users.get(position).getEmail());
        phone.setText(users.get(position).getPhone());
        password.setText(users.get(position).getPassword());



        alert.setView(layout);

        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String stringEditName = name.getText().toString();
                String stringEditEmail = email.getText().toString();
                String stringEditPhone = phone.getText().toString();
                String stringEditPassword = password.getText().toString();
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                User tempEdit = new User(R.mipmap.car, stringEditName, stringEditEmail, stringEditPhone, stringEditPassword,currentDateTimeString);
                users.set(id, tempEdit);
                id = -1;
                myAdapter.notifyDataSetChanged();
                saveData();
                Toast.makeText(AdministrationUser.this, "Saved Sucessfully", Toast.LENGTH_LONG).show();
            }
        });

        alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String stringEditName = name.getText().toString();
                String stringEditEmail = email.getText().toString();
                String stringEditPhone = phone.getText().toString();
                String stringEditPassword = password.getText().toString();
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                User tempEdit = new User(R.mipmap.car, stringEditName, stringEditEmail, stringEditPhone, stringEditPassword,currentDateTimeString);
                users.set(id, tempEdit);
                id = -1;
                users.remove(tempEdit);
                myAdapter.notifyDataSetChanged();
                saveData();
            }
        });
        alert.setNeutralButton("New", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog.Builder alert = new AlertDialog.Builder(AdministrationUser.this);
                alert.setTitle("Administer User");

                LinearLayout layout = new LinearLayout(AdministrationUser.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText name = new EditText(AdministrationUser.this);
                name.setHint("Name");
                layout.addView(name);

                final EditText email = new EditText(AdministrationUser.this);
                email.setHint("Email");
                layout.addView(email);

                final EditText phone = new EditText(AdministrationUser.this);
                phone.setHint("Phone");
                layout.addView(phone);

                final EditText password = new EditText(AdministrationUser.this);
                password.setHint("Password");
                layout.addView(password);




                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String stringEditName = name.getText().toString();
                        String stringEditEmail = email.getText().toString();
                        String stringEditPhone = phone.getText().toString();
                        String stringEditPassword = password.getText().toString();
                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                        User temp = new User(R.mipmap.car, stringEditName, stringEditEmail, stringEditPhone, stringEditPassword,currentDateTimeString);
                        users.add(temp);
                        myAdapter.notifyDataSetChanged();
                        saveData();
                        Toast.makeText(AdministrationUser.this, "Saved Sucessfully", Toast.LENGTH_LONG).show();

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.setView(layout);


                alert.show();

            }
        });

        alert.show();

        return position;

    }

}