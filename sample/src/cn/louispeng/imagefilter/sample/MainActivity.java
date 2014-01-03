
package cn.louispeng.imagefilter.sample;

import cn.louispeng.imagefilter.renderscript.ScriptC_BlackWhiteFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_BrickFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_BrightContrastFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_CleanGlassFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_FeatherFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_GradientMapFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_IllusionFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_InvertFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_LightFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_MirrorFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_MistFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_MosaicFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_NoiseFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_OilPaintFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_RaiseFrameFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_SaturationModifyFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_Test;
import cn.louispeng.imagefilter.renderscript.ScriptC_VignetteFilter;

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

            script.invoke_filter();

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

            script.invoke_filter();

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

            script.invoke_filter();

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

            script.invoke_filter();

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

            script.invoke_filter();

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

            script.invoke_filter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };

    private class InvertFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_InvertFilter script = new ScriptC_InvertFilter(mRS, getResources(), R.raw.invertfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_filter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };

    private class BrightContrastFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_BrightContrastFilter script = new ScriptC_BrightContrastFilter(mRS, getResources(),
                    R.raw.brightcontrastfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_filter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };

    private class IllusionFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_IllusionFilter script = new ScriptC_IllusionFilter(mRS, getResources(), R.raw.illusionfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_filter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };

    private class LightFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_LightFilter script = new ScriptC_LightFilter(mRS, getResources(), R.raw.lightfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_filter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };

    private class MosaicFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_MosaicFilter script = new ScriptC_MosaicFilter(mRS, getResources(), R.raw.mosaicfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();
            script.invoke_filter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };

    private class MirrorFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_MirrorFilter script = new ScriptC_MirrorFilter(mRS, getResources(), R.raw.mirrorfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_filter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };

    private class VignetteFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_VignetteFilter script = new ScriptC_VignetteFilter(mRS, getResources(), R.raw.vignettefilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_filter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };

    private class MistFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_MistFilter script = new ScriptC_MistFilter(mRS, getResources(), R.raw.mistfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_filter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };

    private class CleanGlassFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_CleanGlassFilter script = new ScriptC_CleanGlassFilter(mRS, getResources(), R.raw.cleanglassfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_filter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };
    
    private class OilPaintFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_OilPaintFilter script = new ScriptC_OilPaintFilter(mRS, getResources(), R.raw.oilpaintfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_filter();

            mOutAllocation.copyTo(mBitmapOut);

            Log.d("profile", script.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
        }
    };
    
    private class RaiseFrameFilter implements IImageFilter {
        @Override
        public void process() {
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);

            ScriptC_RaiseFrameFilter script = new ScriptC_RaiseFrameFilter(mRS, getResources(), R.raw.raiseframefilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            long startTime = System.currentTimeMillis();

            script.invoke_filter();

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

        // Test
        new Thread("Test thread") {
            @Override
            public void run() {
                ScriptC_Test script = new ScriptC_Test(mRS, getResources(), R.raw.test);
                script.invoke_test();
            }
        }.start();

        mFilterList.add(new RaiseFrameFilter());
        mFilterList.add(new OilPaintFilter());
        mFilterList.add(new CleanGlassFilter());
        mFilterList.add(new MistFilter());
        mFilterList.add(new VignetteFilter());
        mFilterList.add(new MirrorFilter());
        mFilterList.add(new MosaicFilter());
        mFilterList.add(new LightFilter());
        mFilterList.add(new IllusionFilter());
        mFilterList.add(new BrightContrastFilter());
        mFilterList.add(new InvertFilter());
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
