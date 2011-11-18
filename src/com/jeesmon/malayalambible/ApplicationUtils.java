package com.jeesmon.malayalambible;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;

public class ApplicationUtils {
	private static String mSETTRenderLatinString = null;
	public static String getSETTRenderLatinString(Context context) {
        if (mSETTRenderLatinString == null) {
                InputStream ism = context.getResources().openRawResource(R.raw.renderer);
                if (ism != null) {
                        StringBuilder sbr = new StringBuilder();
                        String line;

                        try {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(ism, "UTF-8"));
                                while ((line = reader.readLine()) != null) {
                                        if ((line.length() > 0) &&
                                                        (!line.startsWith("//"))) {
                                                sbr.append(line).append("\n");
                                        }
                                }
                        } catch (IOException e) {
                                Log.w("SETT Rendering", "Unable to load SETT Rendering Engine: " + e.getMessage());
                        } finally {
                                try {
                                        ism.close();
                                } catch (IOException e) {
                                        Log.w("SETT Rendering", "Unable to load SETT Rendering Engine: " + e.getMessage());
                                }
                        }
                        mSETTRenderLatinString = sbr.toString();
                } else {        
                        mSETTRenderLatinString = "";
                }
        }
        return mSETTRenderLatinString;
}
}
