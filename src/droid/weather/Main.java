package droid.weather;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import Geocoding.Geocoder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class Main extends Activity implements Button.OnClickListener {
	
	private LocationManager locMan;
	private LocationListener locationListener;
	private Location currentLocation;
	private RadioButton getLocation;
	private RadioButton specifyLocation;
	private Button getReport;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       
        //Set radiobutton and command button behaviour
        this.getLocation = (RadioButton) findViewById(R.id.getLocation);
        this.specifyLocation = (RadioButton) findViewById(R.id.specifyLocation);
        this.getReport = (Button) findViewById(R.id.getReport);
        getReport.setOnClickListener(this);
        
    }


	@Override
	public void onClick(View v) {
		if (v == getReport)
		{
			if(getLocation.isChecked() == true)
			{
				handleReverseGeocodeClick();
			}
			if(specifyLocation.isChecked() == true)
			{
				String cityParamString = ((EditText) findViewById(R.id.locationInput))
				.getText().toString();
			}
		}
	
	// Get an instance of the android system LocationManager 
	// so we can access the phone's GPS receiver
		this.locMan = 
			(LocationManager) getSystemService(Context.LOCATION_SERVICE);
	
	// Subscribe to the location manager's updates on the current location
	this.locMan.requestLocationUpdates("gps", (long)300000, (float) 10.0, new LocationListener()
		{
			public void onLocationChanged(Location arg0) 
			{
				handleLocationChanged(arg0);
			}
		
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
				
			}
		
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub
				
			}
		
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub
			}
		});
    }
    
    private void handleLocationChanged(Location loc)
    {
    	// Save the latest location
    	this.currentLocation = loc;
    }
    
    private void handleReverseGeocodeClick()
    {
    	if (this.currentLocation != null)
    	{
    		// Kickoff an asynchronous task to fire the reverse geocoding
    		// request off to google
    		ReverseGeocodeLookupTask task = new ReverseGeocodeLookupTask();
    		task.applicationContext = this;
    		task.execute();
    	}
    	else
    	{
    		// If we don't know our location yet, we can't do reverse
    		// geocoding - display a please wait message
    		showToast("Please wait until the GPS is initialised");
    	}
    }
    
	public void showToast(CharSequence message)
    {
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(getApplicationContext(), message, duration);
		toast.show();
    }
	
	public class ReverseGeocodeLookupTask extends AsyncTask <Void, Void, String>
    {
    	private ProgressDialog dialog;
    	protected Context applicationContext;
    	
    	@Override
    	protected void onPreExecute()
    	{
    		this.dialog = ProgressDialog.show(applicationContext, "Please wait...", 
                    "Converting your location to an address", true);
    	}
    	
		@Override
		protected String doInBackground(Void... params) 
		{
			String locationName = "";
			
			if (currentLocation != null)
			{
				locationName = Geocoder.reverseGeocode(currentLocation);
				
			}
			
			return locationName;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			this.dialog.cancel();
			showToast("Location Detected: " + result);
		}
    }
}


	