package com.dht.editimage.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.dht.editimage.R;
import com.dht.editimage.editimage.EditImageActivity;
import com.dht.editimage.editimage.adapter.StickerAdapter;
import com.dht.editimage.editimage.adapter.StickerTypeAdapter;
import com.dht.editimage.editimage.model.StickerBean;
import com.dht.editimage.editimage.task.StickerTask;
import com.dht.editimage.editimage.util.BitmapUtils;
import com.dht.editimage.editimage.view.uitls.stickerview.StickerItem;
import com.dht.editimage.editimage.view.uitls.stickerview.StickerView;
import com.dht.editimage.picchooser.SelectPictureActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 贴图分类fragment
 *
 * @author panyi
 */
public class StirckerFragment extends Fragment {
    public static final int INDEX = 1;

    public static final String TAG = StirckerFragment.class.getName();
    public static final String STICKER_FOLDER = "stickers";

    public static final int REQUEST_PERMISSON_SORAGE = 1;//相册返回
    public static final int SELECT_GALLERY_IMAGE_CODE = 7;//相册返回标识


    private int imageWidth, imageHeight;//获取屏幕宽高
    private Bitmap mainBitmap;//选中的图片

    private View mainView;
    private EditImageActivity activity;
    private ViewFlipper flipper;
    private View backToMenu;// 返回主菜单
    private RecyclerView typeList;// 贴图分类列表
    private RecyclerView stickerList;// 贴图素材列表
    private View backToType;// 返回类型选择
    private StickerView mStickerView;// 贴图显示控件
    private StickerAdapter mStickerAdapter;// 贴图列表适配器
    private Button edit_xuanze;//选择图片
    private String path;//路径
    private SeekBar seekbar;//拖动条
    private LoadImageTask task;
    private boolean imagetrue = true;//判断图片是否被选中

    //  private LoadStickersTask mLoadStickersTask;
    private List<StickerBean> stickerBeanList = new ArrayList<StickerBean>();

    private SaveStickersTask mSaveTask;

    public static StirckerFragment newInstance(EditImageActivity activity) {
        StirckerFragment fragment = new StirckerFragment();
        fragment.activity = activity;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_sticker_type,
                null);


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels;
        imageHeight = metrics.heightPixels;

        this.mStickerView = activity.mStickerView;
        this.mStickerView.setStirckerFragment(this);
        flipper = (ViewFlipper) mainView.findViewById(R.id.flipper);
        flipper.setInAnimation(activity, R.anim.in_bottom_to_top);
        flipper.setOutAnimation(activity, R.anim.out_bottom_to_top);
        backToMenu = mainView.findViewById(R.id.back_to_main);
        edit_xuanze = (Button) mainView.findViewById(R.id.edit_xuanze);//选择图片

        edit_xuanze.setOnClickListener(onclike);
        typeList = (RecyclerView) mainView
                .findViewById(R.id.stickers_type_list);
        typeList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        typeList.setLayoutManager(mLayoutManager);
        typeList.setAdapter(new StickerTypeAdapter(this));//贴图适配


        backToType = mainView.findViewById(R.id.back_to_type);// back按钮
        seekbar = (SeekBar) mainView.findViewById(R.id.seekbar);
        seekbar.setMax(100);
        seekbar.setProgress(100);
        seekbar.setOnSeekBarChangeListener(onSeekbar);
        stickerList = (RecyclerView) mainView.findViewById(R.id.stickers_list);
        // stickerList.setHasFixedSize(true);
        LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(
                activity);
        stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        stickerList.setLayoutManager(stickerListLayoutManager);
        mStickerAdapter = new StickerAdapter(this);
        stickerList.setAdapter(mStickerAdapter);
        return mainView;
    }

    //进度条拖动事件，模糊度
    SeekBar.OnSeekBarChangeListener onSeekbar = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (imagetrue) {
                mStickerView.TranBitmap(progress);
            } else {
                Toast.makeText(getActivity(), "请选择一张图片", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    //相册点击
    OnClickListener onclike = new OnClickListener() {
        @Override
        public void onClick(View v) {
            selectFromAblum();
        }
    };

    /**
     * 从相册选择编辑图片
     */
    private void selectFromAblum() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            openAblumWithPermissionsCheck();
        } else {
            openAblum();
        }//end if
    }

    //相册选取
    private void openAblumWithPermissionsCheck() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSON_SORAGE);
            return;
        }
        openAblum();
    }

    //启动相册
    private void openAblum() {
        getActivity().startActivityForResult(new Intent(
                        getActivity(), SelectPictureActivity.class),
                SELECT_GALLERY_IMAGE_CODE);
    }

    //调用相册返回方法
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == activity.RESULT_OK) {
            // System.out.println("RESULT_OK");
            switch (requestCode) {
                case SELECT_GALLERY_IMAGE_CODE://
                    handleSelectFromAblum(data);
                    //   edit_xuanze.setClickable(false);//在合成图片没有应用之前再次选择图片会出现图片回收失败，所以屏蔽butt按钮点击
                    break;
            }// end switch
        }
    }

    //获取路径
    private void handleSelectFromAblum(Intent data) {
        path = data.getStringExtra("imgPath");
        // System.out.println("path---->"+path);
        startLoadTask();
    }

    private void startLoadTask() {
        task = new LoadImageTask();
        task.execute(path);
    }


    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return BitmapUtils.getSampledBitmap(params[0], imageWidth / 4, imageHeight / 4);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (mainBitmap != null) {
                mainBitmap.recycle();
                mainBitmap = null;
                System.gc();
            }
            mainBitmap = result;
            //mStickerView.addBitImage(mainBitmap);
            mStickerView.addBitImage(getTransparentBitmap(mainBitmap, 100));
            seekbar.setVisibility(View.VISIBLE);
        }

    }// end inner class

    //判断
    public void gone() {
        if (mStickerView.getDrawable()) {
            seekbar.setVisibility(View.VISIBLE);
        } else {
            seekbar.setVisibility(View.GONE);
        }
    }

    //图片透明度处理number 为透明度，大小0-100，0表示完全透明
    public static Bitmap getTransparentBitmap(Bitmap sourceImg, int number) {
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值
        number = number * 255 / 100;
        for (int i = 0; i < argb.length; i++) {
            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
        }
        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg.getHeight(), Bitmap.Config.ARGB_8888);
        return sourceImg;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单
        backToType.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {// 返回上一级列表
                flipper.showPrevious();
            }
        });
    }

    /**
     * 跳转至贴图详情列表
     *
     * @param path
     */
    public void swipToStickerDetails(String path) {
        mStickerAdapter.addStickerImages(path);
        flipper.showNext();
    }

    /**
     * 从Assert文件夹中读取位图数据
     *
     * @param fileName
     * @return
     */
    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 选择贴图加入到页面中
     *
     * @param path
     */
    public void selectedStickerItem(String path) {
        mStickerView.addBitImage(getImageFromAssetsFile(path));
    }

    public StickerView getmStickerView() {
        return mStickerView;
    }


    public void setmStickerView(StickerView mStickerView) {
        this.mStickerView = mStickerView;
    }

    /**
     * 返回主菜单页面
     *
     * @author panyi
     */
    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }// end inner class

    public void backToMain() {
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(0);
        mStickerView.setVisibility(View.GONE);
        activity.bannerFlipper.showPrevious();
        seekbar.setVisibility(View.GONE);//隐藏拖动条
    }

    /**
     * 保存贴图任务
     *
     * @author panyi
     */
    private final class SaveStickersTask extends StickerTask {
        public SaveStickersTask(EditImageActivity activity) {
            super(activity);
        }

        @Override
        public void handleImage(Canvas canvas, Matrix m) {
            LinkedHashMap<Integer, StickerItem> addItems = mStickerView.getBank();
            for (Integer id : addItems.keySet()) {
                StickerItem item = addItems.get(id);
                item.matrix.postConcat(m);// 乘以底部图片变化矩阵
                canvas.drawBitmap(item.bitmap, item.matrix, null);
            }// end for
        }

        @Override
        public void onPostResult(Bitmap result) {
            mStickerView.clear();
            activity.changeMainBitmap(result);
        }
    }// end inner class

    /**
     * 保存贴图层 合成一张图片
     */
    public void saveStickers() {
        // System.out.println("保存 合成图片");
        if (mSaveTask != null) {
            mSaveTask.cancel(true);
        }
        mSaveTask = new SaveStickersTask((EditImageActivity) getActivity());
        mSaveTask.execute(activity.mainBitmap);

        //应用后恢复butt点击
        //     edit_xuanze.setClickable(true);
    }

    //没有图片时候隐藏拖动栏
    public void hidentSeekBar() {
        seekbar.setVisibility(View.GONE);
    }

    //图片没有被选中
    public void toFragfalse() {
        imagetrue = false;
    }

    //图片被选中
    public void toFragtrue() {
        imagetrue = true;
    }

}// end class
