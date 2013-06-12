package com.explorer;

import java.io.File; 
import java.io.FileNotFoundException; 
import java.io.IOException; 
import java.util.ArrayList; 
 
import android.content.ContentProvider; 
import android.content.ContentProviderOperation; 
import android.content.ContentProviderResult; 
import android.content.ContentValues; 
import android.content.OperationApplicationException; 
import android.content.res.AssetFileDescriptor; 
import android.database.Cursor; 
import android.net.Uri; 
import android.os.Environment; 
import android.os.ParcelFileDescriptor; 
import android.util.Log; 
 
public class TestContextProvider extends ContentProvider { 
 
    @Override 
    public int delete(Uri uri, String selection, String[] selectionArgs) { 
        // TODO Auto-generated method stub  
        Log.e("H3c", "delete"); 
        return 0; 
    } 
 
    @Override 
    public String getType(Uri uri) { 
        // TODO Auto-generated method stub  
        Log.e("H3c", "gettype"); 
        return null; 
    } 
 
    @Override 
    public Uri insert(Uri uri, ContentValues values) { 
        // TODO Auto-generated method stub  
        Log.e("H3c", "insert"); 
        return null; 
    } 
 
    @Override 
    public boolean onCreate() { 
        // TODO Auto-generated method stub  
        Log.e("H3c", "create"); 
        return false; 
    } 
 
    @Override 
    public Cursor query(Uri uri, String[] projection, String selection, 
            String[] selectionArgs, String sortOrder) { 
        // TODO Auto-generated method stub  
        Log.e("H3c", "query"); 
        return null; 
    } 
 
    @Override 
    public int update(Uri uri, ContentValues values, String selection, 
            String[] selectionArgs) { 
        // TODO Auto-generated method stub  
        Log.e("H3c", "update"); 
        return 0; 
    } 
 
    @Override 
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) 
            throws FileNotFoundException { 
        // TODO Auto-generated method stub  
        Log.e("H3c", "openAssetFile"); 
        return super.openAssetFile(uri, mode); 
    } 
 
    //此方法非常重要，一定要重写，否则默认报FileNotFound异常  
    @Override 
    public ParcelFileDescriptor openFile(Uri uri, String mode) 
            throws FileNotFoundException { 
        // TODO Auto-generated method stub  
        File root = Environment.getExternalStorageDirectory(); 
        root.mkdirs(); 
        File path = new File(root, uri.getEncodedPath()); 
 
        Log.e("H3c", "opeFile:"+path); 
        int imode = 0; 
        if (mode.contains("w")) { 
            imode |= ParcelFileDescriptor.MODE_WRITE_ONLY; 
            if (!path.exists()) { 
                try { 
                    path.createNewFile(); 
                } catch (IOException e) { 
                    e.printStackTrace(); 
                } 
            } 
        } 
        if (mode.contains("r")) 
            imode |= ParcelFileDescriptor.MODE_READ_ONLY; 
        if (mode.contains("+")) 
            imode |= ParcelFileDescriptor.MODE_APPEND; 
 
        return ParcelFileDescriptor.open(path, imode); 
    } 
 
} 
