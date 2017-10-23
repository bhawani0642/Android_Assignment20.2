package com.acadgild.updatecontact;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
        private final static int PERMISSION_ALL = 1;

        private Button insertContactBtn;
        private Button updateContactBtn;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            insertContactBtn = (Button) findViewById(R.id.insert_contact);
            updateContactBtn = (Button) findViewById(R.id.update_contact);

            // The request code used in ActivityCompat.requestPermissions()
            // and returned in the Activity's onRequestPermissionsResult()
            String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};

            if(!hasPermissions(this, PERMISSIONS)){
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }else{
                initialize();
            }
        }
        public void initialize(){
            insertContactBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    insertContact();
                }
            });
            updateContactBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateContact();
                }
            });
        }

        private void updateContact() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Get the layout inflater
            LayoutInflater inflater = getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View view = inflater.inflate(R.layout.contact_main, null);

            final EditText name = view.findViewById(R.id.input_contact_name);
            final EditText phone = view.findViewById(R.id.input_contact_phone);

            builder.setView(view)
                    .setTitle(R.string.update_title)
                    // Add action buttons
                    .setPositiveButton(R.string.save_contact, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String contactName = name.getText().toString();
                            String contactPhone = phone.getText().toString();

                            int saveStatus = 0;
                            try {
                                saveStatus = ContactsHelper.updateContact(getContentResolver(), contactName, contactPhone);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            } catch (OperationApplicationException e) {
                                e.printStackTrace();
                            }

                            if (saveStatus == 1) {
                                Toast.makeText(getApplicationContext(), "Updated successfully.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to update.", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel_contact, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //MainActivity.this.getDialog().cancel();
                        }
                    });
            AlertDialog dialog = builder.create();

            dialog.show();
        }

        private void insertContact() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Get the layout inflater
            LayoutInflater inflater = getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View view = inflater.inflate(R.layout.contact_main, null);

            final EditText name = view.findViewById(R.id.input_contact_name);
            final EditText phone = view.findViewById(R.id.input_contact_phone);

            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.save_contact, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String contactName = name.getText().toString();
                            String contactPhone = phone.getText().toString();

                            boolean saveStatus = ContactsHelper.insertContact(getContentResolver(), contactName, contactPhone);

                            if (saveStatus) {
                                Toast.makeText(getApplicationContext(), "Saved successfully.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to save.", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel_contact, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //MainActivity.this.getDialog().cancel();
                        }
                    });
            AlertDialog dialog = builder.create();

            dialog.show();
        }



        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case PERMISSION_ALL : {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted,  Do the
                        // contacts-related task you need to do.
                        // Since reading contacts takes more time, let's run it on a separate thread.
                        initialize();
                    } else {

                        // permission denied,  Disable the
                        // functionality that depends on this permission.
                        Toast.makeText(this, "You've denied the required permission.", Toast.LENGTH_LONG);
                    }
                    return;
                }

                // other 'case' lines to check for other
                // permissions this app might request
            }
        }

        public static boolean hasPermissions(Context context, String... permissions) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
                for (String permission : permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
