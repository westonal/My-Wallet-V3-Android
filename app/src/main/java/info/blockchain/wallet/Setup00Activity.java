package info.blockchain.wallet;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.dm.zbar.android.scanner.ZBarConstants;

import info.blockchain.wallet.pairing.PairingFactory;
import info.blockchain.wallet.util.AppUtil;

/**
 * Created by riaanvos on 23/03/15.
 */
public class Setup00Activity extends ActionBarActivity {

	public static final int PAIRING_QR = 2005;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pair);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blockchain_blue)));
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Fragment fragment = new PairWalletFragment();
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				FragmentManager fragmentManager = getFragmentManager();

				if (fragmentManager.getBackStackEntryCount() > 0 )
					fragmentManager.popBackStack();
				else
				{
					//Start splash screen again
					Intent intent = new Intent(Setup00Activity.this, Setup0Activity.class);
					startActivity(intent);
					finish();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void setActionBarTitle(String title){
		getSupportActionBar().setTitle(title);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(resultCode == Activity.RESULT_OK && requestCode == PAIRING_QR)	{
			if(data != null && data.getStringExtra(ZBarConstants.SCAN_RESULT) != null)	{
				AppUtil.getInstance(this).wipeApp();
				String strResult = data.getStringExtra(ZBarConstants.SCAN_RESULT);
				Log.i("Pairing result", strResult);
				pairingThreadQR(strResult);
			}
		}
		else if(resultCode == Activity.RESULT_CANCELED && requestCode == PAIRING_QR)	{
			AppUtil.getInstance(this).wipeApp();
		}
		else {
			;
		}

	}

	private void pairingThreadQR(final String data) {

		final Handler handler = new Handler();

		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();

				if(PairingFactory.getInstance(Setup00Activity.this).handleQRCode(data))	{
					//Toast.makeText(getActivity(), "Pairing OK", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(Setup00Activity.this, Setup2Activity.class);
					intent.putExtra("pairing", true);
					startActivity(intent);
					Setup00Activity.this.finish();
				}
				else	{
					//Toast.makeText(getActivity(), "Pairing KO", Toast.LENGTH_SHORT).show();
					AppUtil.getInstance(Setup00Activity.this).wipeApp();
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						;
					}
				});

				Looper.loop();
			}
		}).start();
	}
}
