package com.example.gridview2showlocalselectedpicture;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private final int isSelectedPicture = 1;        //打开图片标记
    private String newSelectedImagePath;                //选择图片路径
    private GridImageEntity addImageEntity; //加号图片
    private ArrayList<GridImageEntity> imageEntities;
    private GridImageAdapter gridImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridView1);
        imageEntities = new ArrayList<>();
//        //添加添加按钮的图片
        addImageEntity = new GridImageEntity();

        addImageEntity.setImagePath("add");
        addImageEntity.setGridBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        imageEntities.add(addImageEntity);

        gridImageAdapter = new GridImageAdapter(this, R.layout.griditem_addpic, imageEntities);
        gridView.setAdapter(gridImageAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (imageEntities.size() == 10) { //第一张为默认图片
                    Toast.makeText(MainActivity.this, "图片数9张已满", Toast.LENGTH_SHORT).show();
                } else if (position == imageEntities.size() - 1) { //点击最后一个图片
                    Toast.makeText(MainActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
                    //选择图片
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, isSelectedPicture);
                    //通过onResume()刷新数据
                } else {
                    dialog(position);
                }
            }
        });
    }

    //获取图片路径 响应startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //打开图片
        if (resultCode == RESULT_OK && requestCode == isSelectedPicture) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                Cursor cursor = getContentResolver().query(
                        uri,
                        new String[]{MediaStore.Images.Media.DATA},
                        null,
                        null,
                        null);
                //返回 没找到选择图片
                if (null == cursor) {
                    return;
                }
                //光标移动至开头 获取图片路径
                cursor.moveToFirst();
                newSelectedImagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        }
    }

    //刷新图片
    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(newSelectedImagePath)) {

            imageEntities.remove(imageEntities.size() - 1);// 把加号图片去掉
            GridImageEntity gridImageEntity = new GridImageEntity();
            gridImageEntity.setImagePath("" + newSelectedImagePath);
            gridImageEntity.setGridBitmap(BitmapFactory.decodeFile(newSelectedImagePath));
            imageEntities.add(gridImageEntity);

            imageEntities.add(addImageEntity);//把加号图片加到最后一个

            gridImageAdapter.notifyDataSetChanged();
            newSelectedImagePath = null;
        }
    }

    /*
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                imageEntities.remove(position);
                gridImageAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    class GridImageAdapter extends ArrayAdapter<GridImageEntity> {
        private int resourceId;

        public GridImageAdapter(Context context, int textViewResourceId, ArrayList<GridImageEntity> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridImageEntity gridImageEntity = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            ImageView fruitImage = (ImageView) view.findViewById(R.id.imageView);
            fruitImage.setImageBitmap(gridImageEntity.getGridBitmap());
            return view;
        }
    }


}
