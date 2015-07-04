package com.example.dailyselfie;

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Text;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<String> {

	private final Activity context;

	private final ArrayList<String> listOfImages;
	
	public CustomAdapter(Activity context, ArrayList<String> listOfImages) {
		super(context, R.layout.thelist, listOfImages);
		// TODO Auto-generated constructor stub
		
		this.context=context;
		this.listOfImages = listOfImages;

	}
	
	public static Bitmap rotateImage(Bitmap src, float degree) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		Bitmap bmp = Bitmap.createBitmap(src, 0 , 0, src.getWidth(), src.getHeight(), matrix, true);
		return bmp;
	}
	
	public View getView(int position,View view,ViewGroup parent) {
		
		ViewHolder holder;
		
		if(view == null)
		{		
		LayoutInflater inflater=context.getLayoutInflater();
		view =inflater.inflate(R.layout.thelist, null,true);
		
		holder = new ViewHolder();
		holder.imageView = (ImageView) view.findViewById(R.id.selfie_image);
		holder.txtTitle = (TextView) view.findViewById(R.id.selfie_name);
		view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}
		
		
		Bitmap bitmap = BitmapFactory.decodeFile(listOfImages.get(position));
//		Bitmap rotatedImg = rotateImage(bitmap, 90);
		File f = new File(listOfImages.get(position));	
		
		holder.txtTitle.setText(f.getName());
		holder.imageView.setImageBitmap(bitmap);

		return view;
		
	};
}

class ViewHolder {
	 TextView txtTitle;
	 ImageView imageView;
}
