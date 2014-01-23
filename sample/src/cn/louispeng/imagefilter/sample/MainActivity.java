
package cn.louispeng.imagefilter.sample;

import cn.louispeng.imagefilter.renderscript.ScriptC_BlackWhiteFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_BlindFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_BrickFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_BrightContrastFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_CleanGlassFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_ColorQuantizeFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_ColorToneFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_EdgeFilter;
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
import cn.louispeng.imagefilter.renderscript.ScriptC_PaintBorderFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_RaiseFrameFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_ReliefFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_SaturationModifyFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_Test;
import cn.louispeng.imagefilter.renderscript.ScriptC_ThreeDGridFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_VignetteFilter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Float3;
import android.renderscript.RenderScript;
import android.renderscript.ScriptC;
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

    private abstract class IImageFilter {
        private long startTime;

        protected RenderScript mRS;

        protected Allocation mInAllocation;

        protected Allocation mOutAllocation;

        protected ScriptC mScript;

        protected void _preProcess() {
            mRS = RenderScript.create(getApplicationContext());
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
        }

        protected abstract void _process();

        public void process() {
            _preProcess();
            startTime = System.currentTimeMillis();
            _process();
            Log.d("profile", mScript.getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
            _postProcess();
        }

        protected void _postProcess() {
            mOutAllocation.copyTo(mBitmapOut);
            mScript.destroy();
            mScript = null;
            mInAllocation.destroy();
            mInAllocation = null;
            mOutAllocation.destroy();
            mOutAllocation = null;
            mRS.destroy();
            mRS = null;
            System.gc();
        }
    };

    private class SaturationModifyFilter extends IImageFilter {
        @Override
        public void _process() {
            ScriptC_SaturationModifyFilter script = new ScriptC_SaturationModifyFilter(mRS, getResources(),
                    R.raw.saturationmodifyfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class GradientMapFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_GradientMapFilter script = new ScriptC_GradientMapFilter(mRS, getResources(),
                    R.raw.gradientmapfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class NoiseFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_NoiseFilter script = new ScriptC_NoiseFilter(mRS, getResources(), R.raw.noisefilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class BlackWhiteFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_BlackWhiteFilter script = new ScriptC_BlackWhiteFilter(mRS, getResources(), R.raw.blackwhitefilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class BrickFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_BrickFilter script = new ScriptC_BrickFilter(mRS, getResources(), R.raw.brickfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class FeatherFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_FeatherFilter script = new ScriptC_FeatherFilter(mRS, getResources(), R.raw.featherfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class InvertFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_InvertFilter script = new ScriptC_InvertFilter(mRS, getResources(), R.raw.invertfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class BrightContrastFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_BrightContrastFilter script = new ScriptC_BrightContrastFilter(mRS, getResources(),
                    R.raw.brightcontrastfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class IllusionFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_IllusionFilter script = new ScriptC_IllusionFilter(mRS, getResources(), R.raw.illusionfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class LightFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_LightFilter script = new ScriptC_LightFilter(mRS, getResources(), R.raw.lightfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class MosaicFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_MosaicFilter script = new ScriptC_MosaicFilter(mRS, getResources(), R.raw.mosaicfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class MirrorFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_MirrorFilter script = new ScriptC_MirrorFilter(mRS, getResources(), R.raw.mirrorfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class VignetteFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_VignetteFilter script = new ScriptC_VignetteFilter(mRS, getResources(), R.raw.vignettefilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class MistFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_MistFilter script = new ScriptC_MistFilter(mRS, getResources(), R.raw.mistfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class CleanGlassFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_CleanGlassFilter script = new ScriptC_CleanGlassFilter(mRS, getResources(), R.raw.cleanglassfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class OilPaintFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_OilPaintFilter script = new ScriptC_OilPaintFilter(mRS, getResources(), R.raw.oilpaintfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class RaiseFrameFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_RaiseFrameFilter script = new ScriptC_RaiseFrameFilter(mRS, getResources(), R.raw.raiseframefilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class EdgeFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_EdgeFilter script = new ScriptC_EdgeFilter(mRS, getResources(), R.raw.edgefilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class ReliefFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_ReliefFilter script = new ScriptC_ReliefFilter(mRS, getResources(), R.raw.relieffilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class PaintBorderFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_PaintBorderFilter script = new ScriptC_PaintBorderFilter(mRS, getResources(),
                    R.raw.paintborderfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);
            script.set_gPaintBorderColor(new Float3(1.0f, 0, 0));

            script.invoke_filter();
            mScript = script;
        }
    };

    private class BlindFilter extends IImageFilter {
        private final Float3 mBlindColor;

        private final int mIsHorizontal;

        public BlindFilter(Float3 blindColor, boolean isHorizontal) {
            mBlindColor = blindColor;
            mIsHorizontal = isHorizontal ? 1 : 0;
        }

        @Override
        protected final void _process() {
            ScriptC_BlindFilter script = new ScriptC_BlindFilter(mRS, getResources(), R.raw.blindfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);
            script.set_gBlindColor(mBlindColor);
            script.set_gIsHorizontal(mIsHorizontal);
            script.set_gWidth(96);
            script.set_gOpacity(1.0f);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class ColorQuantizeFilter extends IImageFilter {
        @Override
        protected final void _process() {
            ScriptC_ColorQuantizeFilter script = new ScriptC_ColorQuantizeFilter(mRS, getResources(),
                    R.raw.colorquantizefilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class ColorToneFilter extends IImageFilter {
        private final Float3 mRGB;

        private final float mSaturation;

        public ColorToneFilter(Float3 rgb, float saturation) {
            mRGB = rgb;
            mSaturation = saturation;
        }

        @Override
        protected final void _process() {
            ScriptC_ColorToneFilter script = new ScriptC_ColorToneFilter(mRS, getResources(), R.raw.colortonefilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);
            script.set_gTone(mRGB);
            script.set_gSaturation(mSaturation);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class ThreeDGridFilter extends IImageFilter {
        private final int mSize;

        private final float mDepth;

        public ThreeDGridFilter(int size, float depth) {
            mSize = size;
            mDepth = depth;
        }

        @Override
        protected final void _process() {
            ScriptC_ThreeDGridFilter script = new ScriptC_ThreeDGridFilter(mRS, getResources(), R.raw.threedgridfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);
            script.set_gSize(mSize);
            script.set_gDepth(mDepth);

            script.invoke_filter();
            mScript = script;
        }
    };

    private final ArrayList<IImageFilter> mFilterList = new ArrayList<IImageFilter>();

    private ImageView in;

    private ImageView out;

    private Bitmap mBitmapIn;

    private Bitmap mBitmapOut;

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

        // Test
        new Thread("Test thread") {
            @Override
            public void run() {
                RenderScript mRS = RenderScript.create(getApplicationContext());
                ScriptC_Test script = new ScriptC_Test(mRS, getResources(), R.raw.test);
                script.invoke_test();
                script.destroy();
                mRS.destroy();
            }
        }.start();

        mFilterList.add(new ThreeDGridFilter(16, 100.0f / 255.0f));
        mFilterList.add(new ColorToneFilter(new Float3(0.12941176470588f, 0.65882352941176f, 0.99607843137255f),
                0.75294117647059f));
        mFilterList.add(new ColorQuantizeFilter());
        mFilterList.add(new BlindFilter(new Float3(0, 0, 0), true));
        mFilterList.add(new BlindFilter(new Float3(1.0f, 1.0f, 1.0f), false));
        mFilterList.add(new ReliefFilter());
        mFilterList.add(new EdgeFilter());
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
