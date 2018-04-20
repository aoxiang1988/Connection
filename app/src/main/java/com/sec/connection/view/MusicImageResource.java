package com.sec.connection.view;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MusicImageResource {
	
	private static Bitmap bm = null;
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");  
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options(); 
    
	public static Bitmap getArtwork(Context context,long song_id,long album_id){
		if (album_id < 0) {  
            // This is something that is not in the database, so get the album art directly  
            // from the file.  
            if (song_id >= 0) {  
                if (bm != null) {  
                    return bm;  
                }
            }  
            return null;  
        }  
        ContentResolver res = context.getContentResolver();  
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);  
        if (uri != null) {  
            InputStream in = null;  
            try {  
                in = res.openInputStream(uri);
                sBitmapOptions.inJustDecodeBounds = true;
                int hight = sBitmapOptions.outHeight/5;
                int width = sBitmapOptions.outWidth/5;
                sBitmapOptions.inSampleSize = calculateInSampleSize(sBitmapOptions, width, hight);
                sBitmapOptions.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeStream(in, null, sBitmapOptions);

        	    return bm;
            } catch (FileNotFoundException ignored) {}
            finally {  
            	try {  
                    if (in != null) {  
                        in.close();  
                    }  
                } catch (IOException ignored) {}
            }  
        }  
		return null;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) { 
	    // Raw height and width of image  
	    final int height = options.outHeight; 
	    final int width = options.outWidth; 
	    int inSampleSize = 1; 
	 
	    if (height > reqHeight || width > reqWidth) { 
	        if (width > height) { 
	            inSampleSize = Math.round((float)height / (float)reqHeight); 
	        } else { 
	            inSampleSize = Math.round((float)width / (float)reqWidth); 
	        } 
	    } 
	    return inSampleSize; 
	} 
}
