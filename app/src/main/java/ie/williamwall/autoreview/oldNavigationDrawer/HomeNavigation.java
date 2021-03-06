package ie.williamwall.autoreview.oldNavigationDrawer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ie.williamwall.autoreview.R;
import ie.williamwall.autoreview.newNavigationDrawer.ShareFacebook;
import ie.williamwall.autoreview.firebaseAdministrator.LoginActivityFirebase;
import ie.williamwall.autoreview.firebaseReview.CustomImage;
import ie.williamwall.autoreview.firebaseReview.MyAdapter;
import ie.williamwall.autoreview.firebaseReview.Person;
import ie.williamwall.autoreview.maps.MapsActivity;

import static ie.williamwall.autoreview.firebaseReview.CustomImage.DATABASE_PATH;
// Designed and Developed @ William Wall
// Email @ william@williamwall.ie
// GitHub @ https://github.com/william-wall/Auto-Review-App-Android-GUI
public class HomeNavigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar=null;
            private FirebaseAuth auth;
            Activity activity;
            TextView userNameDisplayNav;
            ListView listView;
            List<Person> list;
            ProgressDialog progressDialog;
            MyAdapter myAdapter;
            private DatabaseReference databaseReference;
            private Person selectedUser;
            private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent move= new Intent(HomeNavigation.this,CustomImage.class);
                startActivity(move);
            }
        });

        listView = (ListView) findViewById(R.id.list1);


        auth = FirebaseAuth.getInstance();

        final FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
        setDataToView(userId);


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(HomeNavigation.this, LoginActivityFirebase.class));
                    finish();
                }
            }
        };


        list = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching please wait");
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                list.clear();

                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    Person person = snap.getValue(Person.class);
                    list.add(person);

                }
                myAdapter = new MyAdapter(HomeNavigation.this, R.layout.data_items, list);
                listView.setAdapter(myAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Person user = (Person) adapterView.getItemAtPosition(i);
                selectedUser = user;
                String userInstanceReview = user.getUserName();

                String titleReview = user.getName();
                String commentReview = user.getEmail();

                Intent moving = new Intent(HomeNavigation.this,ViewReview.class);
                moving.putExtra("message_title", titleReview);
                moving.putExtra("message_comment", commentReview);
                startActivity(moving);
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
                String gotIT=   getDataToView(userId);

                final Person user = (Person) adapterView.getItemAtPosition(i);
                selectedUser = user;

                String userInstanceReview = user.getUserName();

                String UID = user.getName();
                String title = user.getName();
                String review = user.getEmail();

                if(!userInstanceReview.matches(gotIT))
                {
                    Toast.makeText(getApplicationContext(), "You cannot edit a review you didnt create!!!", Toast.LENGTH_LONG).show();

                }
                else {

                    Intent move = new Intent(HomeNavigation.this, Updating.class);
                    move.putExtra("message_key", title);
                    move.putExtra("message_key2", review);
                    move.putExtra("MyClass", user);
                    startActivity(move);
                }
            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        View headerView = navigationView.getHeaderView(0);
        userNameDisplayNav = (TextView) headerView.findViewById(R.id.usersNameNav);
        final FirebaseUser userNav = FirebaseAuth.getInstance().getCurrentUser();
        String gotNameNav = getDataToView(userNav);
        userNameDisplayNav.setText(gotNameNav);

    }


    public void browseImages(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 0);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            imageUri = data.getData();

            try{

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);

            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public String getActualImage(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    @SuppressLint("SetTextI18n")
    private void setDataToView(FirebaseUser user) {

    }
    @SuppressLint("SetTextI18n")
    private String getDataToView(FirebaseUser user) {
        String getDataString = user.getEmail();
        return getDataString;
    }























    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_navigation, menu);

//        final FirebaseUser userId2 = FirebaseAuth.getInstance().getCurrentUser();
//        setDataToView(userId2);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent move= new Intent(HomeNavigation.this,CustomImage.class);
            startActivity(move);
//            Toast.makeText(HomeNavigation.this, "Deleted Review", Toast.LENGTH_SHORT).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //here is the main place where we need to work on.
        int id=item.getItemId();
        switch (id){

            case R.id.nav_home:
                Intent h= new Intent(HomeNavigation.this,HomeNavigation.class);
                startActivity(h);
                break;
            case R.id.nav_import:
                Intent i= new Intent(HomeNavigation.this,WeatherNavigation.class);
                startActivity(i);
                break;
            case R.id.nav_gallery:
                Intent g= new Intent(HomeNavigation.this,MapsActivity.class);
                startActivity(g);
                break;
//            case R.id.nav_slideshow:
//                Intent s= new Intent(HomeNavigation.this,ShareNavigation.class);
//                startActivity(s);
            case R.id.nav_settings:
                Intent se= new Intent(HomeNavigation.this,ShareFacebook.class);
                startActivity(se);
                break;
            case R.id.nav_tools:
                Intent t= new Intent(HomeNavigation.this,AccountNavigation.class);
                startActivity(t);
                break;
                case R.id.nav_logout:
                    auth.signOut();
// this listener will be called when there is change in firebase user session
                    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user == null) {
                                // user auth state is changed - user is null
                                // launch login activity
                                startActivity(new Intent(HomeNavigation.this, LoginActivityFirebase.class));
                                finish();
                            }
                        }
                    };
                    break;

            // this is done, now let us go and intialise the home page.
            // after this lets start copying the above.
            // FOLLOW MEEEEE>>>
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(HomeNavigation.this, LoginActivityFirebase.class));
                finish();
            } else {
                setDataToView(user);

            }
        }


    };
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
