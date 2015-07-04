package com.example.dailyselfie;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	private Context context = this;

	private static final int CAMERA_CAPTURE = 10;
	private static final int DELETE_ID = 0;
	private static final String TAG = null;
	
	private int mShortAnimationDuration = 300;
	private Animator mCurrentAnimator;

	ArrayList<String> loi;
	CustomAdapter loiAdapter;
	ListView loiList;

	NotificationCompat.Builder notification;
	PendingIntent pIntent;
	NotificationManager manager;
	Intent resultIntent;
	TaskStackBuilder stackBuilder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		loi = new ArrayList<String>();
		loiList = getListView();
		loiAdapter = new CustomAdapter(MainActivity.this, loi);
		loiList.setAdapter(loiAdapter);
		DisplayImages();
		loiList.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this,
						"Chal Gya setOnItemLongClickListener!",
						Toast.LENGTH_LONG).show();
				LayoutInflater inflate = LayoutInflater.from(MainActivity.this);
				View custom_dialog = inflate
						.inflate(R.layout.menu_dialog, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MainActivity.this);
				alertDialogBuilder.setCancelable(true).setView(custom_dialog);
				final AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				Window window = alertDialog.getWindow();
				window.setLayout(165, 200);
				final int pos = position;
				Button del = (Button) custom_dialog.findViewById(R.id.delete);

				del.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						File myPath = new File(loi.get(pos));
						loiAdapter.remove(loi.get(pos)); // remove the item
						loiAdapter.notifyDataSetChanged(); // let the adapter
															// know to update
						myPath.delete();
						alertDialog.dismiss();

					}
				});
				Button cancel = (Button) custom_dialog
						.findViewById(R.id.cancel);

				cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						alertDialog.dismiss();

					}
				});

				return true;
			}
		});

		TheTimerTask remind = new TheTimerTask();
		Timer myTimer = new Timer();
		myTimer.schedule(remind, 2000, 120000);

	}

	class TheTimerTask extends TimerTask {
		public void run() {
			notification = new NotificationCompat.Builder(MainActivity.this);
			notification.setContentTitle("Daily Selfie!");
			notification.setContentText("You haven't taken a Selfie lately!");
			notification.setTicker("Take a Selfie!");
			notification.setSmallIcon(R.drawable.notif_selfie);
			stackBuilder = TaskStackBuilder.create(MainActivity.this);
			stackBuilder.addParentStack(MainActivity.class);
			resultIntent = new Intent(MainActivity.this, MainActivity.class);
			stackBuilder.addNextIntent(resultIntent);
			pIntent = stackBuilder.getPendingIntent(0,
					PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setContentIntent(pIntent);
			manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			manager.notify(0, notification.build());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_camera) {
			startCapture();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void startCapture() {

		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

		if (cameraIntent.resolveActivity(getPackageManager()) != null) {

			File photoFile = null;
			try {
				photoFile = CreateImageFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (photoFile != null) {
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(cameraIntent, CAMERA_CAPTURE);
			}
		}
	}

	private File CreateImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "Image_" + timeStamp + "_";

		File storageDirectory = getExternalFilesDir("");
		File image = File.createTempFile(imageFileName, ".jpg",
				storageDirectory);
		return image;
	}

	public static Bitmap rotateImage(Bitmap src, float degree) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
				src.getHeight(), matrix, true);
		return bmp;
	}

	@Override
	public void onActivityResult(final int requestCode, int resultCode,
			Intent data) {

		switch (requestCode) {
		case CAMERA_CAPTURE:
			if (resultCode == RESULT_OK) {
				DisplayImages();
			}
			break;
		}
	}

	private void DisplayImages() {
		// TODO Auto-generated method stub
		File myPath = getExternalFilesDir(null);
		loi.clear();
		try {

			for (File f : myPath.listFiles()) {
				loi.add(f.getAbsolutePath());
			}

			loiAdapter.notifyDataSetChanged();
		} catch (Exception ex) {
			Log.w("Error", ex.getMessage());
		}
	}

	@Override
	protected void onListItemClick(ListView l, final View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		// custom dialog
		LayoutInflater li = LayoutInflater.from(context);
		View custom = li.inflate(R.layout.custom_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setView(custom);
		alertDialogBuilder.setCancelable(true);

		if (mCurrentAnimator != null) {
			mCurrentAnimator.cancel();
		}

		// Load the high-resolution "zoomed-in" image.
		final ImageView expandedImageView = (ImageView) findViewById(R.id.expanded_image);
		expandedImageView.setImageResource(position);


		
		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();

		v.getGlobalVisibleRect(startBounds);
		custom.findViewById(R.id.viewImage).getGlobalVisibleRect(finalBounds,
				globalOffset);
		startBounds.offset(-globalOffset.x, -globalOffset.y);
		finalBounds.offset(-globalOffset.x, -globalOffset.y);

		float startScale;
		if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds
				.width() / startBounds.height()) {
			// Extend start bounds horizontally
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		} else {
			// Extend start bounds vertically
			startScale = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}

		
//		v.setAlpha(0f);
		expandedImageView.setVisibility(View.VISIBLE);

		expandedImageView.setPivotX(0f);
		expandedImageView.setPivotY(0f);

		// Construct and run the parallel animation of the four translation and
		// scale properties
		// (X, Y, SCALE_X, and SCALE_Y).
		AnimatorSet set = new AnimatorSet();
		set.play(
				ObjectAnimator.ofFloat(expandedImageView, View.X,
						startBounds.left, finalBounds.left))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
						startBounds.top, finalBounds.top))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
						startScale, 1f))
				.with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y,
						startScale, 1f));
		set.setDuration(mShortAnimationDuration);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mCurrentAnimator = null;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mCurrentAnimator = null;
			}
		});
		set.start();
		mCurrentAnimator = set;

		// Upon clicking the zoomed-in image, it should zoom back down to the
		// original bounds
		// and show the thumbnail instead of the expanded image.
		final float startScaleFinal = startScale;
		expandedImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mCurrentAnimator != null) {
					mCurrentAnimator.cancel();
				}

				// Animate the four positioning/sizing properties in parallel,
				// back to their
				// original values.
				AnimatorSet set = new AnimatorSet();
				set.play(
						ObjectAnimator.ofFloat(expandedImageView, View.X,
								startBounds.left))
						.with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
								startBounds.top))
						.with(ObjectAnimator.ofFloat(expandedImageView,
								View.SCALE_X, startScaleFinal))
						.with(ObjectAnimator.ofFloat(expandedImageView,
								View.SCALE_Y, startScaleFinal));
				set.setDuration(mShortAnimationDuration);
				set.setInterpolator(new DecelerateInterpolator());
				set.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						v.setAlpha(1f);
						expandedImageView.setVisibility(View.VISIBLE);
						mCurrentAnimator = null;
					}

					@Override
					public void onAnimationCancel(Animator animation) {
						v.setAlpha(1f);
						expandedImageView.setVisibility(View.VISIBLE);
						mCurrentAnimator = null;
					}
				});
				set.start();
				mCurrentAnimator = set;
			}
		});

//		// create alert dialog
//		final AlertDialog alertDialog = alertDialogBuilder.create();
//		Bitmap bitmap = BitmapFactory.decodeFile(loi.get(position));
//
//		// set the custom dialog components - text, image and button
//		ImageView image = (ImageView) custom.findViewById(R.id.viewImage);
//		// image.setImageBitmap(rotateImage(bitmap, 90));
//		image.setImageBitmap(bitmap);
//
//		alertDialog.show();
//		Window window = alertDialog.getWindow();
//		window.setLayout(300, 360);
	}

}