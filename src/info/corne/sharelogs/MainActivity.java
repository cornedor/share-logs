package info.corne.sharelogs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import info.corne.sharelogs.Compress;
import info.corne.sharelogs.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menu)
	{
		switch (menu.getItemId()) {
		case R.id.action_about:
			// Show the about dialog.
			AlertDialog aboutDialog = new AlertDialog.Builder(this).create();
			aboutDialog.setTitle("About");
			aboutDialog.setMessage(getResources().getString(R.string.about_info));
			aboutDialog.setIcon(R.drawable.ic_launcher);
			aboutDialog.show();
			return true;
		default:
			return super.onOptionsItemSelected(menu);
		}
		
	}
	
	private void store(final boolean share)
	{
		findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				final String dir = Environment.getExternalStorageDirectory().toString() + "/ShareLogs";
				String[] mkdir = {"mkdir", dir};
				ShellCommand.run(mkdir);
				
				String[] orgfiles = {"/devlog/kernel_log", "/devlog/system_log", "/proc/last_kmsg"};
				ArrayList<String> zipfiles = new ArrayList<String>();
				
				for(int i = 0; i < orgfiles.length; i++)
				{
					String filename = orgfiles[i].substring(orgfiles[i].lastIndexOf("/") + 1);
					String[] command = {"su", "-c", "cp " + orgfiles[i] +" " + dir + "/" + filename};
					ShellCommand.run(command);
					File f = new File(dir + "/" + filename);
					if(f.exists())
					{
						zipfiles.add(dir + "/" + filename);
					}
				}
				
				String[] files = new String[zipfiles.size()];
				files = zipfiles.toArray(files);
				
				SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmm");
				final String format = s.format(new Date());
				System.out.println(format);
				
				new Compress(files, dir + 
						"/logs_collection-" + format + ".zip").compress();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						findViewById(R.id.progressBar1).setVisibility(View.GONE);
						if(share)
						{
							Intent share = new Intent(Intent.ACTION_SEND);
							String uri = "file://" + dir + "/logs_collection-" + format + ".zip";
							share.setType("application/octet-stream");
							share.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri));
							startActivity(Intent.createChooser(share, "Share Logs"));
						}
						else
						{
							Toast.makeText(getApplicationContext(), "The logs are now stored in " + dir + 
									"/logs_collection-" + format + ".zip", Toast.LENGTH_LONG).show();
						}
					}
				});
			}
		}).start();
	}
	
	public void onSaveClick(View view)
	{
		store(false);
	}
	
	public void onShareClick(View view)
	{
		Toast.makeText(getApplicationContext(), "Please wait for the share dialog to appear.", Toast.LENGTH_LONG).show();
		store(true);
	}
}
