package com.adeiakykloforias.adeiakykloforias;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {
    String name;
    String address;
    String code = null;
    AdView adView;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ADVERTISEMENT
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setVisibility(View.GONE);

        //Buttons
        Button button_ph = findViewById(R.id.ph_Button);
        Button button_sm = findViewById(R.id.sm_Button);
        Button button_bank = findViewById(R.id.bank_Button);
        Button button_help = findViewById(R.id.help_Button);
        Button button_cer = findViewById(R.id.cer_Button);
        Button button_ex = findViewById(R.id.ex_Button);
        Button button_send = findViewById(R.id.sendButton);

        //Message TxtView
        TextView msg = findViewById(R.id.Message);

        //EditTexts
        EditText nameField = findViewById(R.id.editName);
        EditText addressField = findViewById(R.id.editAddress);

        //Check for Night Mode
        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:   //If on Night Mode
                //Set EditText text color to light purple
                nameField.setHintTextColor(Color.parseColor("#FFBB86FC"));
                addressField.setHintTextColor(Color.parseColor("#FFBB86FC"));
                //Button Text Color to white
                button_ph.setTextColor(Color.parseColor("#FFFFFFFF"));
                button_sm.setTextColor(Color.parseColor("#FFFFFFFF"));
                button_bank.setTextColor(Color.parseColor("#FFFFFFFF"));
                button_help.setTextColor(Color.parseColor("#FFFFFFFF"));
                button_cer.setTextColor(Color.parseColor("#FFFFFFFF"));
                button_ex.setTextColor(Color.parseColor("#FFFFFFFF"));
                button_send.setTextColor(Color.parseColor("#FFFFFFFF"));
                break;
            case Configuration.UI_MODE_NIGHT_NO:

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                //Set EditText text color to light purple
                nameField.setHintTextColor(Color.parseColor("#FF6200EE"));
                addressField.setHintTextColor(Color.parseColor("#FF6200EE"));
                break;
        }
        //Read name and address
        nameField.setText(readNameFromFile());
        addressField.setText(readAddressFromFile());
        name = nameField.getText().toString();
        address = addressField.getText().toString();

        //Name TextChangedListener
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                msg.setText((" " + ((code == null)? "" : code) + " " + s.toString() + " " + address + " "));
                name = s.toString();
                if(code != null && name != null && !name.trim().isEmpty() && address != null && !address.trim().isEmpty())
                    msg.setBackgroundResource(R.drawable.rounded_corner_green);
                else
                    msg.setBackgroundResource(R.drawable.rounded_corner_red);
            }
        });
        //Address TextChangedListener
        addressField.addTextChangedListener( new TextWatcher() {
             @Override
             public void afterTextChanged(Editable s) {
             }
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {
             }
             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                 msg.setText(" " + ((code == null) ? "" : code) + " " + name + " " + s.toString() + " ");
                 address = s.toString();
                 if (code != null && name != null && !name.trim().isEmpty() && address != null && !address.trim().isEmpty())
                     msg.setBackgroundResource(R.drawable.rounded_corner_green);
                 else
                     msg.setBackgroundResource(R.drawable.rounded_corner_red);
             }
        });
        //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
    }

    /// WRITE ///
    //Write Name
    private void writeNameToFile(String name) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("name.txt", MODE_PRIVATE));
            outputStreamWriter.write(name.trim());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    //Write Address
    private void writeAddressToFile(String address) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("address.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(address.trim());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /// READ ///
    //Read Name
    private String readNameFromFile() {
        String ret = null;
        try {
            InputStream inputStream = openFileInput("name.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString().trim();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    //Read Address
    private String readAddressFromFile() {
        String ret = null;
        try {
            InputStream inputStream = openFileInput("address.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString().trim();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    //Buttons Clicked
    public void pharmacy(View view){    code = "1"; editMsg();  }

    public void supermarket(View view){ code = "2"; editMsg();  }

    public void bank(View view){        code = "3"; editMsg();  }

    public void help(View view){        code = "4"; editMsg();  }

    public void ceremony(View view){    code = "5"; editMsg();  }

    public void exercise(View view){    code = "6"; editMsg();  }

    //Edits Message TextView
    public void editMsg(){
        TextView msg = findViewById(R.id.Message); msg.setText("  " + code + " " + name + " " + address + "  ");
        if(code != null && name != null && !name.trim().isEmpty() && address != null && !address.trim().isEmpty()) {
            msg.setBackgroundResource(R.drawable.rounded_corner_green);
            adView.setVisibility(View.VISIBLE);
        }else
            msg.setBackgroundResource(R.drawable.rounded_corner_red);
    }

    /// SMS ///
    @SuppressLint("UnlocalizedSms")
    public void sendSMS(View view){
        if(code != null && name != null && !name.trim().isEmpty() && address != null && !address.trim().isEmpty()) {
            TextView msg = findViewById(R.id.Message);
            //SmsManager smsManager = SmsManager.getDefault();
            try {
                Uri uri = Uri.parse("smsto:13033");
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", msg.getText().toString().trim());
                startActivity(intent);
                //smsManager.sendTextMessage("13033", null, msg.getText().toString(), null, null);
                Toast.makeText(getApplicationContext(), "Μετέρεστε στα Μηνύματα...", Toast.LENGTH_SHORT).show();
                writeNameToFile(name);
                writeAddressToFile(address);
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
            }
        }else {
            if (name == null || name.trim().isEmpty())
                Toast.makeText(getApplicationContext(), "ΕΙΣΑΓΕΤΕ ΟΝΟΜΑΤΕΠΩΝΥΜΟ", Toast.LENGTH_SHORT).show();
            else if (address == null || address.trim().isEmpty())
                Toast.makeText(getApplicationContext(), "ΕΙΣΑΓΕΤΕ ΔΙΕΥΘΥΝΣΗ", Toast.LENGTH_SHORT).show();
            else if (code == null)
                Toast.makeText(getApplicationContext(), "ΕΠΙΛΕΞΤΕ ΛΟΓΟ ΜΕΤΑΚΙΝΗΣΗΣ", Toast.LENGTH_SHORT).show();
        }
    }
}
