
package cn.louispeng.imagefilter.sample;

import cn.louispeng.imagefilter.renderscript.ScriptC_BlackWhiteFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_BrickFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_FeatherFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_GradientMapFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_NoiseFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_SaturationModifyFilter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.RSErrorHandler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private class FilterTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            out.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mFilterList.get(mFilterIndex).process();
            mFilterIndex++;
            if (mFilterIndex >= mFilterList.size()) {
                mFilterIndex = 0;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            out.invalidate();
            out.setVisibility(View.VISIBLE);
            super.onPostExecute(result);
            mFilterTask = null;
        }
    }

    private interface IImageFilter {
        public void process();
    };

    private class SaturationModifyFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_SaturationModifyFilter script = new ScriptC_SaturationModifyFilter(mRS, getResources(),
                    R.raw.saturationmodifyfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_saturationModifyFilter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };

    private class GradientMapFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_GradientMapFilter script = new ScriptC_GradientMapFilter(mRS, getResources(),
                    R.raw.gradientmapfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_gradientMapFilter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };

    private class NoiseFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_NoiseFilter script = new ScriptC_NoiseFilter(mRS, getResources(), R.raw.noisefilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_noiseFilter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };

    private class BlackWhiteFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_BlackWhiteFilter script = new ScriptC_BlackWhiteFilter(mRS, getResources(), R.raw.blackwhitefilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_blackWhiteFilter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };

    private class BrickFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_BrickFilter script = new ScriptC_BrickFilter(mRS, getResources(), R.raw.brickfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_brickFilter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };

    private class FeatherFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_FeatherFilter script = new ScriptC_FeatherFilter(mRS, getResources(), R.raw.featherfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_setup();
            script.invoke_featherFilter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };

    private final ArrayList<IImageFilter> mFilterList = new ArrayList<IImageFilter>();

    private ImageView in;

    private ImageView out;

    private Bitmap mBitmapIn;

    private Bitmap mBitmapOut;

    private RenderScript mRS;

    private Allocation mInAllocation;

    private Allocation mOutAllocation;

    private int mFilterIndex = 0;

    private FilterTask mFilterTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBitmapIn = loadBitmap(R.drawable.image2);
        mBitmapOut = Bitmap.createBitmap(mBitmapIn.getWidth(), mBitmapIn.getHeight(), mBitmapIn.getConfig());

        in = (ImageView)findViewById(R.id.displayin);
        in.setImageBitmap(mBitmapIn);
        in.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mFilterTask) {
                    mFilterTask = new FilterTask();
                    mFilterTask.execute();
                }
            }
        });

        out = (ImageView)findViewById(R.id.displayout);
        out.setImageBitmap(mBitmapOut);

        mRS = RenderScript.create(this);
        mRS.setErrorHandler(new RSErrorHandler() {
            @Override
            public void run() {
                Log.e(MainActivity.this.getApplication().getPackageName(), "RenderScripte Error = "
                        + this.mErrorMessage);
                super.run();
            }
        });

        mFilterList.add(new FeatherFilter());
        mFilterList.add(new BrickFilter());
        mFilterList.add(new BlackWhiteFilter());
        mFilterList.add(new NoiseFilter());
        mFilterList.add(new GradientMapFilter());
        mFilterList.add(new SaturationModifyFilter());
    }

    private Bitmap loadBitmap(int resource) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeResource(getResources(), resource, options);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
