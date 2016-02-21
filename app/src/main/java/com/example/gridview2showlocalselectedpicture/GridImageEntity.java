package com.example.gridview2showlocalselectedpicture;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/1/27 0027.
 */
public class GridImageEntity {
    private String imagePath;
    private Bitmap gridBitmap;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public Bitmap getGridBitmap() {
        return gridBitmap;
    }

    public void setGridBitmap(Bitmap gridBitmap) {
        this.gridBitmap = gridBitmap;
    }
}
