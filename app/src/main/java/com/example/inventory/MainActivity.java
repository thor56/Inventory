package com.example.inventory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
//import android.widget.Toolbar;

import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList; import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
//    public static final String MY_NAME = "sacfirebase.firebaseio.com.myName";
//    public static final String MY_ID = "sacfirebase.firebaseio.com.myId";
    //view objects

    EditText editTextName;
    EditText editTextDesc;
    EditText editTextNuOfUnits;
    Button buttonAddArtist;
    ListView listViewArtists;
    String mea_val;
    //a list to store all the artist from firebase database
    List<NameList> namelists;
    //our database reference object
    DatabaseReference databaseArtists;
//    private StorageReference mStorageRef;
//    private Button buttonChoose;
//    private Button buttonUpload;
//    private ImageView imageView;
private Button btnChoose, btnUpload;
    private ImageView imageView;

    private Uri filePath;
    private  String uploadPath = "nothing yet";



    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;



    //    private ImageView imageView;
//a Uri object to store file path
//    private Uri filePath;



    private static final int PICK_IMAGE_REQUEST = 234;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);



        databaseArtists = FirebaseDatabase.getInstance().getReference("namelist");
        //getting views
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextDesc = (EditText) findViewById(R.id.editTextDesc);
        editTextNuOfUnits =  findViewById(R.id.numOfUnits);
        listViewArtists = (ListView) findViewById(R.id.listViewArtists);
        buttonAddArtist = (Button) findViewById(R.id.button_add_data);
        // Spinner element
        final Spinner spinner = (Spinner) findViewById(R.id.measureUnit);
//        Button button=(Button)findViewById(R.id.button);
        // Spinner click listener
        spinner.setOnItemSelectedListener(MainActivity.this);
        // Spinner Drop down elements
        List<String> measure = new ArrayList<String>();
        measure.add("Kg");
        measure.add("lbs");
        measure.add("Ton");
        measure.add("Litre");
        measure.add("Dozen");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, measure);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent= new Intent(MainActivity.this,SecondActivity.class);
//                intent.putExtra("data",String.valueOf(spinner.getSelectedItem()));
//                startActivity(intent);
//            }
//        });


        //list to store artists
        namelists = new ArrayList<>();
        //adding an onclicklistener to button
        buttonAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling the method addArtist()
                //the method is defined below
                //this method is actually performing the write operation
               mea_val =  String.valueOf(spinner.getSelectedItem());

                addnamelist(mea_val);

            }
        });
        listViewArtists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                NameList nmlist = namelists.get(i);
                showUpdateDeleteDialog(nmlist.getDescription(), nmlist.getName());
                return true;
            }
        });

        //image section
        btnChoose = (Button) findViewById(R.id.btnChoose);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        imageView = (ImageView) findViewById(R.id.imgView);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

//        buttonChoose = (Button) findViewById(R.id.buttonChoose);
//        buttonUpload = (Button) findViewById(R.id.imageCam);
//        imageView = (ImageView) findViewById(R.id.imageView);
//
//        buttonChoose.setOnClickListener( this);
//        buttonUpload.setOnClickListener( this);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });


    }

    public void setSupportActionBar(Toolbar myToolbar) {
    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                             uploadPath = taskSnapshot.getMetadata().getPath();
                            Toast.makeText(MainActivity.this, "Uploaded" + uploadPath, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }


    private void addnamelist(String mea_val) {
        String name = editTextName.getText().toString().trim();
        String desc = editTextDesc.getText().toString().trim();
        String nuOfUnits = editTextNuOfUnits.getText().toString().trim();
        String measure_value = mea_val;
//        String up_path = uploadPath;
        //checking if the value is provided
        if (!TextUtils.isEmpty(name)) {
            //getting a unique id using push().getKey() method
            //it will create a unique id and we will use it as the Primary Key for our Artist
            String id = databaseArtists.push().getKey();
            //creating an Artist Object
            NameList artist = new NameList(desc, name, measure_value,uploadPath, nuOfUnits);
            //Saving the Artist
            databaseArtists.child(id).setValue(artist);
            //setting edittext to blank again
            editTextName.setText("");
            editTextDesc.setText("");
            editTextNuOfUnits.setText("");
            //displaying a success toast
            Toast.makeText(this, "Name added " + measure_value, Toast.LENGTH_LONG).show();
        }
        else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        //attaching value event listener
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                namelists.clear();
                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                //getting artist NameList
                    NameList nmlist = postSnapshot.getValue(NameList.class);
                //adding artist to the list
                namelists.add(nmlist);
            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }

            //creating adapter
            ShowNameList myAdapter = new ShowNameList(MainActivity.this, namelists);
 listViewArtists.setAdapter(myAdapter);
        }
        public void onCancelled(DatabaseError databaseError) { }
    });
}
    private void showUpdateDeleteDialog(final String myId, String myName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateArtist);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteArtist);
        dialogBuilder.setTitle(myName);
        final AlertDialog b = dialogBuilder.create();
        b.show();
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    updateArtist(myId, name,mea_val);
                    b.dismiss();
                }
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteArtist(myId);
                b.dismiss();
            }
        });
    }
    private boolean updateArtist(String description, String name,String mea_val) {
        //getting the specified artist reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("namelist").child(name);
        //updating artist
        NameList nmlist = new NameList(description, name, mea_val, uploadPath,"");
        dR.setValue(nmlist);
        Toast.makeText(getApplicationContext(), "Name Updated", Toast.LENGTH_LONG).show();

        return true;
    }
    private boolean deleteArtist(String id) {
        //getting the specified artist reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("namelist").child(id);
        //removing artist
        dR.removeValue();
        //removing all tracks
        Toast.makeText(getApplicationContext(), "Item Deleted", Toast.LENGTH_LONG).show(); return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onClick(View view) {
        //if the clicked button is choose
        if (view == btnChoose) {
            chooseImage();
        }
        //if the clicked button is upload
        else if (view == btnUpload) {
//            uploadImage();
        }
    }
}