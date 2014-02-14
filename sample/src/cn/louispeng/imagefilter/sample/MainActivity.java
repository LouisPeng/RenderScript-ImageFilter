package cn.louispeng.imagefilter.sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.Float3;
import android.renderscript.RenderScript;
import android.renderscript.ScriptC;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.louispeng.imagefilter.renderscript.ScriptC_BannerFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_BigBrotherFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_BlackWhiteFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_BlindFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_BrickFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_BrightContrastFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_CleanGlassFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_ColorQuantizeFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_ColorToneFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_ConvolutionFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_EdgeFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_FeatherFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_FillPatternFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_GammaFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_GaussianBlurFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_GradientMapFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_GrayscaleFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_HistogramEqualFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_HslModifyFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_IllusionFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_ImageBlender;
import cn.louispeng.imagefilter.renderscript.ScriptC_InvertFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_LightFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_MirrorFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_MistFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_MosaicFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_NoiseFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_OilPaintFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_PaintBorderFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_ParamEdgeDetectFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_PixelateFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_PosterizeFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_RadialDistortionFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_RaiseFrameFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_ReflectionFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_ReliefFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_SaturationModifyFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_SharpFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_SoftGlowFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_Test;
import cn.louispeng.imagefilter.renderscript.ScriptC_ThreeDGridFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_ThresholdFilter;
import cn.louispeng.imagefilter.renderscript.ScriptC_VignetteFilter;

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

    public static class BlendMode {
        public static int Normal = 0;

        public static int Additive = 1;

        public static int Subractive = 2;

        public static int Multiply = 3;

        public static int Overlay = 4;

        public static int ColorDodge = 5;

        public static int ColorBurn = 6;

        public static int Lighten = 7;

        public static int Darken = 8;

        public static int Reflect = 9;

        public static int Glow = 10;

        public static int LinearLight = 11;

        public static int Frame = 12;/* photo frame */

    }

    private abstract class IImageFilter {
        private long startTime;

        protected RenderScript mRS;

        protected Allocation mInAllocation;

        protected Allocation mOutAllocation;

        protected ScriptC mScript;

        protected void preProcess() {
            mRS = RenderScript.create(getApplicationContext());
            mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
        }

        protected void _preProcess() {
        }

        protected abstract void _process();

        protected void _postProcess() {
        }

        public void process() {
            preProcess();
            startTime = System.currentTimeMillis();
            _preProcess();
            _process();
            Log.d("profile", getClass().getSimpleName() + " use " + (System.currentTimeMillis() - startTime));
            _postProcess();
            postProcess();
        }

        protected void postProcess() {
            mOutAllocation.copyTo(mBitmapOut);
            File outputFile = new File(Environment.getExternalStorageDirectory() + "/test/"
                    + this.getClass().getSimpleName() + ".jpeg");
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (null != fos) {
                mBitmapOut.compress(CompressFormat.JPEG, 100, fos);

                try {
                    fos.close();
                    fos = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
        private final float mSaturationFactor;

        public SaturationModifyFilter() {
            mSaturationFactor = 0.5f;
        }

        public SaturationModifyFilter(float saturationFactor) {
            mSaturationFactor = saturationFactor;
        }

        @Override
        public void _process() {
            ScriptC_SaturationModifyFilter script = new ScriptC_SaturationModifyFilter(mRS, getResources(),
                    R.raw.saturationmodifyfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gSaturationFactor(mSaturationFactor);
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
        // The brightness factor.
        // Should be in the range [-1.0f, 1.0f].
        private final float mBrightnessFactor;

        // The contrast factor.
        // Should be in the range [-1.0f, 1.0f].
        private final float mContrastFactor;

        public BrightContrastFilter(float brightnessFactor, float contrastFactor) {
            mBrightnessFactor = brightnessFactor;
            mContrastFactor = contrastFactor;
        }

        @Override
        protected final void _process() {
            ScriptC_BrightContrastFilter script = new ScriptC_BrightContrastFilter(mRS, getResources(),
                    R.raw.brightcontrastfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gBrightnessFactor(mBrightnessFactor);
            script.set_gContrastFactor(mContrastFactor);
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

    private class FillPatternFilter extends IImageFilter {
        private Bitmap patternBitmap;

        private Allocation patternAllocation;

        @Override
        protected void _preProcess() {
            patternBitmap = loadBitmap(R.drawable.image1);
            patternAllocation = Allocation.createFromBitmap(mRS, patternBitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
        }

        @Override
        protected void _postProcess() {
            patternAllocation.destroy();
            patternAllocation = null;
            patternBitmap.recycle();
            patternBitmap = null;
        }

        @Override
        protected void _process() {
            ScriptC_FillPatternFilter script = new ScriptC_FillPatternFilter(mRS, getResources(),
                    R.raw.fillpatternfilter);

            script.set_gIn(mInAllocation);
            script.set_gPattern(patternAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class SharpFilter extends IImageFilter {
        private final float mStep;

        public SharpFilter(float step) {
            mStep = step;
        }

        @Override
        protected void _process() {
            ScriptC_SharpFilter script = new ScriptC_SharpFilter(mRS, getResources(), R.raw.sharpfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gStep(mStep);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class GrayscaleFilter extends IImageFilter {
        @Override
        protected void _process() {
            ScriptC_GrayscaleFilter script = new ScriptC_GrayscaleFilter(mRS, getResources(), R.raw.grayscalefilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class HistogramEqualFilter extends IImageFilter {
        private final float mContrastIntensity;

        private Allocation pixelGrayscaleArrayAllocation;

        public HistogramEqualFilter(float contrastIntensity) {
            mContrastIntensity = contrastIntensity;
        }

        @Override
        protected void _preProcess() {
            pixelGrayscaleArrayAllocation = Allocation.createSized(mRS, Element.U8(mRS), mBitmapIn.getWidth()
                    * mBitmapIn.getHeight());
        }

        @Override
        protected void _postProcess() {
            pixelGrayscaleArrayAllocation.destroy();
            pixelGrayscaleArrayAllocation = null;
        }

        @Override
        protected void _process() {
            ScriptC_HistogramEqualFilter script = new ScriptC_HistogramEqualFilter(mRS, getResources(),
                    R.raw.histogramequalfilter);

            script.bind_pixelGrayscaleArray(pixelGrayscaleArrayAllocation);
            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gContrastIntensity(mContrastIntensity);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class HslModifyFilter extends IImageFilter {
        private final float mHueFactor;

        public HslModifyFilter(float hueFactor) {
            mHueFactor = hueFactor;
        }

        @Override
        protected void _process() {
            ScriptC_HslModifyFilter script = new ScriptC_HslModifyFilter(mRS, getResources(), R.raw.hslmodifyfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);
            script.set_gHueFactor(mHueFactor);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class GaussianBlurFilter extends IImageFilter {
        private int mPadding = 3;

        // The bluriness factor, should be in the range [0, 40].
        private float mSigma = 0.75f;

        private Allocation mImageWithPaddingBufferAllocation;
        private Allocation mTempBufferAllocation;

        public GaussianBlurFilter(int padding, float sigma) {
            mPadding = padding;
            mSigma = sigma;
        }

        @Override
        protected void _preProcess() {
            int heightWithPadding = mBitmapIn.getHeight() + mPadding * 2;
            int widthWithPadding = mBitmapIn.getWidth() + mPadding * 2;
            int bufferSize = widthWithPadding * heightWithPadding * 3;
            mImageWithPaddingBufferAllocation = Allocation.createSized(mRS, Element.F32(mRS), bufferSize);
            mTempBufferAllocation = Allocation.createSized(mRS, Element.F32(mRS), bufferSize);
        }

        @Override
        protected void _postProcess() {
            mImageWithPaddingBufferAllocation.destroy();
            mImageWithPaddingBufferAllocation = null;
            mTempBufferAllocation.destroy();
            mTempBufferAllocation = null;
        }

        @Override
        protected void _process() {
            ScriptC_GaussianBlurFilter script = new ScriptC_GaussianBlurFilter(mRS, getResources(),
                    R.raw.gaussianblurfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);
            script.set_gPadding(mPadding);
            script.set_gSigma(mSigma);
            script.bind_gImageWithPaddingBuffer(mImageWithPaddingBufferAllocation);
            script.bind_gTempBuffer(mTempBufferAllocation);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class ThresholdFilter extends IImageFilter {
        private final float mThreshold;

        public ThresholdFilter(float threshold) {
            mThreshold = threshold;
        }

        @Override
        protected void _process() {
            ScriptC_ThresholdFilter script = new ScriptC_ThresholdFilter(mRS, getResources(), R.raw.thresholdfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);
            script.set_gThreshold(mThreshold);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class RadialDistortionFilter extends IImageFilter {
        @Override
        protected void _process() {
            ScriptC_RadialDistortionFilter script = new ScriptC_RadialDistortionFilter(mRS, getResources(),
                    R.raw.radialdistortionfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class BlockPrintFilter extends IImageFilter {
        private Allocation mTmpOutputAllocation;

        private ScriptC_ImageBlender mBlenderScript;

        @Override
        protected void _preProcess() {
            mTmpOutputAllocation = Allocation.createFromBitmap(mRS, mBitmapOut);
        }

        @Override
        protected void _postProcess() {
            mTmpOutputAllocation.destroy();
            mTmpOutputAllocation = null;
            mBlenderScript.destroy();
            mBlenderScript = null;
        }

        @Override
        protected void _process() {
            ScriptC_ParamEdgeDetectFilter script = new ScriptC_ParamEdgeDetectFilter(mRS, getResources(),
                    R.raw.paramedgedetectfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mTmpOutputAllocation);
            script.set_gThreshold(0.25f);
            script.set_gK00(1.0f);
            script.set_gK01(2.0f);
            script.set_gK02(1.0f);
            script.set_gDoGrayConversion(0);
            script.set_gDoInversion(0);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;

            mBlenderScript = new ScriptC_ImageBlender(mRS, getResources(), R.raw.imageblender);

            mBlenderScript.set_gIn1(mInAllocation);
            mBlenderScript.set_gIn2(mTmpOutputAllocation);
            mBlenderScript.set_gOut(mOutAllocation);
            mBlenderScript.set_gBlendMode(BlendMode.Multiply);
            mBlenderScript.set_gScript(mBlenderScript);

            mBlenderScript.invoke_filter();
        }
    };

    private class SmashColorFilter extends IImageFilter {
        private Allocation mTmpOutputAllocation;

        private ScriptC_ImageBlender mBlenderScript;

        @Override
        protected void _preProcess() {
            mTmpOutputAllocation = Allocation.createFromBitmap(mRS, mBitmapOut);
        }

        @Override
        protected void _postProcess() {
            mTmpOutputAllocation.destroy();
            mTmpOutputAllocation = null;
            mBlenderScript.destroy();
            mBlenderScript = null;
        }

        @Override
        protected void _process() {
            ScriptC_ParamEdgeDetectFilter script = new ScriptC_ParamEdgeDetectFilter(mRS, getResources(),
                    R.raw.paramedgedetectfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mTmpOutputAllocation);
            script.set_gThreshold(0.25f);
            script.set_gK00(1.0f);
            script.set_gK01(2.0f);
            script.set_gK02(1.0f);
            script.set_gDoGrayConversion(0);
            script.set_gDoInversion(0);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;

            mBlenderScript = new ScriptC_ImageBlender(mRS, getResources(), R.raw.imageblender);

            mBlenderScript.set_gIn1(mInAllocation);
            mBlenderScript.set_gIn2(mTmpOutputAllocation);
            mBlenderScript.set_gOut(mOutAllocation);
            mBlenderScript.set_gBlendMode(BlendMode.LinearLight);
            mBlenderScript.set_gMixture(2.5f);
            mBlenderScript.set_gScript(mBlenderScript);

            mBlenderScript.invoke_filter();
        }
    };

    private class SoftGlowFilter extends IImageFilter {
        // BrightContrastFilter factors
        private final float mBrightness;

        private final float mContrast;

        // For GaussianBlurFilter
        private final float mSigma;

        private final int mPadding = 3;

        private Allocation mImageWithPaddingBufferAllocation;

        private Allocation mTempBufferAllocation;

        private Allocation mTmpOutputAllocation;

        private ScriptC_BrightContrastFilter mBrightContrastFilterScript;

        private ScriptC_GaussianBlurFilter mGaussianBlurFilterScript;

        public SoftGlowFilter(float sigma, float brightness, float contrast) {
            mSigma = sigma;
            mBrightness = brightness;
            mContrast = contrast;
        }

        @Override
        protected void _preProcess() {
            int heightWithPadding = mBitmapIn.getHeight() + mPadding * 2;
            int widthWithPadding = mBitmapIn.getWidth() + mPadding * 2;
            int bufferSize = widthWithPadding * heightWithPadding * 3;
            mImageWithPaddingBufferAllocation = Allocation.createSized(mRS, Element.F32(mRS), bufferSize);
            mTempBufferAllocation = Allocation.createSized(mRS, Element.F32(mRS), bufferSize);
            mTmpOutputAllocation = Allocation.createFromBitmap(mRS, mBitmapOut);
        }

        @Override
        protected void _postProcess() {
            mImageWithPaddingBufferAllocation.destroy();
            mImageWithPaddingBufferAllocation = null;
            mTempBufferAllocation.destroy();
            mTempBufferAllocation = null;

            mTmpOutputAllocation.destroy();
            mTmpOutputAllocation = null;
            mBrightContrastFilterScript.destroy();
            mBrightContrastFilterScript = null;
            mGaussianBlurFilterScript.destroy();
            mGaussianBlurFilterScript = null;
        }

        @Override
        protected void _process() {
            mGaussianBlurFilterScript = new ScriptC_GaussianBlurFilter(mRS, getResources(), R.raw.gaussianblurfilter);
            mGaussianBlurFilterScript.set_gIn(mInAllocation);
            mGaussianBlurFilterScript.set_gOut(mOutAllocation);
            mGaussianBlurFilterScript.set_gPadding(mPadding);
            mGaussianBlurFilterScript.set_gSigma(mSigma);
            mGaussianBlurFilterScript.bind_gImageWithPaddingBuffer(mImageWithPaddingBufferAllocation);
            mGaussianBlurFilterScript.bind_gTempBuffer(mTempBufferAllocation);
            mGaussianBlurFilterScript.set_gScript(mGaussianBlurFilterScript);
            mGaussianBlurFilterScript.invoke_filter();

            mBrightContrastFilterScript = new ScriptC_BrightContrastFilter(mRS, getResources(),
                    R.raw.brightcontrastfilter);
            mBrightContrastFilterScript.set_gIn(mOutAllocation);
            mBrightContrastFilterScript.set_gOut(mTmpOutputAllocation);
            mBrightContrastFilterScript.set_gScript(mBrightContrastFilterScript);
            mBrightContrastFilterScript.set_gBrightnessFactor(mBrightness);
            mBrightContrastFilterScript.set_gContrastFactor(mContrast);
            mBrightContrastFilterScript.invoke_filter();

            ScriptC_SoftGlowFilter script = new ScriptC_SoftGlowFilter(mRS, getResources(), R.raw.softglowfilter);

            script.set_gIn(mInAllocation);
            script.set_gProcessedIn(mTmpOutputAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class BigBrotherFilter extends IImageFilter {
        @Override
        protected void _process() {
            ScriptC_BigBrotherFilter script = new ScriptC_BigBrotherFilter(mRS, getResources(), R.raw.bigbrotherfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class BannerFilter extends IImageFilter {
        private final boolean mIsHorizontal;

        public BannerFilter(boolean isHorizontal) {
            mIsHorizontal = isHorizontal;
        }

        @Override
        protected void _process() {
            ScriptC_BannerFilter script = new ScriptC_BannerFilter(mRS, getResources(), R.raw.bannerfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gIsHorizontal(mIsHorizontal ? 1 : 0);
            script.invoke_process();
            mScript = script;
        }
    };

    private class ParamEdgeDetectFilter extends IImageFilter {
        private final boolean DoGrayConversion;

        private final boolean DoInversion;

        public ParamEdgeDetectFilter() {
            DoGrayConversion = true;
            DoInversion = true;
        }

        public ParamEdgeDetectFilter(boolean doGrayConversion, boolean doInversion) {
            DoGrayConversion = doGrayConversion;
            DoInversion = doInversion;
        }

        @Override
        protected void _process() {
            ScriptC_ParamEdgeDetectFilter script = new ScriptC_ParamEdgeDetectFilter(mRS, getResources(),
                    R.raw.paramedgedetectfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gDoGrayConversion(DoGrayConversion ? 1 : 0);
            script.set_gDoInversion(DoInversion ? 1 : 0);
            script.set_gScript(script);
            script.invoke_filter();
            mScript = script;
        }
    };

    private class ComicFilter extends IImageFilter {
        // For GaussianBlurFilter
        private final int mPadding = 3;

        private Allocation mImageWithPaddingBufferAllocation;

        private Allocation mTempBufferAllocation;

        private Allocation mEdgedAllocation;

        private Allocation mSaturatedAllocation;

        private ScriptC_SaturationModifyFilter mSaturationModifyFilter;

        private ScriptC_GaussianBlurFilter mGaussianBlurFilterScript;

        private ScriptC_ImageBlender mBlenderScript;

        private ScriptC_ParamEdgeDetectFilter mParamEdgeDetectFilter;

        @Override
        protected void _preProcess() {
            int heightWithPadding = mBitmapIn.getHeight() + mPadding * 2;
            int widthWithPadding = mBitmapIn.getWidth() + mPadding * 2;
            int bufferSize = widthWithPadding * heightWithPadding * 3;
            mImageWithPaddingBufferAllocation = Allocation.createSized(mRS, Element.F32(mRS), bufferSize);
            mTempBufferAllocation = Allocation.createSized(mRS, Element.F32(mRS), bufferSize);

            mEdgedAllocation = Allocation.createFromBitmap(mRS, mBitmapOut);
            mSaturatedAllocation = Allocation.createFromBitmap(mRS, mBitmapOut);
        }

        @Override
        protected void _postProcess() {
            mImageWithPaddingBufferAllocation.destroy();
            mImageWithPaddingBufferAllocation = null;
            mTempBufferAllocation.destroy();
            mTempBufferAllocation = null;

            mEdgedAllocation.destroy();
            mEdgedAllocation = null;
            mSaturatedAllocation.destroy();
            mSaturatedAllocation = null;

            mSaturationModifyFilter.destroy();
            mSaturationModifyFilter = null;
            mGaussianBlurFilterScript.destroy();
            mGaussianBlurFilterScript = null;
            mBlenderScript.destroy();
            mBlenderScript = null;
            mParamEdgeDetectFilter.destroy();
            mParamEdgeDetectFilter = null;
        }

        @Override
        protected void _process() {
            mSaturationModifyFilter = new ScriptC_SaturationModifyFilter(mRS, getResources(),
                    R.raw.saturationmodifyfilter);
            mSaturationModifyFilter.set_gIn(mInAllocation);
            mSaturationModifyFilter.set_gOut(mSaturatedAllocation);
            mSaturationModifyFilter.set_gSaturationFactor(1.0f);
            mSaturationModifyFilter.set_gScript(mSaturationModifyFilter);
            mSaturationModifyFilter.invoke_filter();

            mGaussianBlurFilterScript = new ScriptC_GaussianBlurFilter(mRS, getResources(), R.raw.gaussianblurfilter);
            mGaussianBlurFilterScript.set_gIn(mSaturatedAllocation);
            mGaussianBlurFilterScript.set_gOut(mSaturatedAllocation);
            mGaussianBlurFilterScript.set_gPadding(mPadding);
            mGaussianBlurFilterScript.set_gSigma(1.0f);
            mGaussianBlurFilterScript.bind_gImageWithPaddingBuffer(mImageWithPaddingBufferAllocation);
            mGaussianBlurFilterScript.bind_gTempBuffer(mTempBufferAllocation);
            mGaussianBlurFilterScript.set_gScript(mGaussianBlurFilterScript);
            mGaussianBlurFilterScript.invoke_filter();

            mBlenderScript = new ScriptC_ImageBlender(mRS, getResources(), R.raw.imageblender);
            mBlenderScript.set_gIn1(mSaturatedAllocation);
            mBlenderScript.set_gIn2(mSaturatedAllocation);
            mBlenderScript.set_gOut(mOutAllocation);
            mBlenderScript.set_gBlendMode(BlendMode.Lighten);
            mBlenderScript.set_gMixture(1.0f);
            mBlenderScript.set_gScript(mBlenderScript);
            mBlenderScript.invoke_filter();

            mParamEdgeDetectFilter = new ScriptC_ParamEdgeDetectFilter(mRS, getResources(), R.raw.paramedgedetectfilter);
            mParamEdgeDetectFilter.set_gIn(mOutAllocation);
            mParamEdgeDetectFilter.set_gOut(mEdgedAllocation);
            mParamEdgeDetectFilter.set_gScript(mParamEdgeDetectFilter);
            mParamEdgeDetectFilter.invoke_filter();

            ScriptC_ImageBlender script = new ScriptC_ImageBlender(mRS, getResources(), R.raw.imageblender);
            script.set_gIn1(mOutAllocation);
            script.set_gIn2(mEdgedAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gBlendMode(BlendMode.Lighten);
            script.set_gMixture(0.8f);
            script.set_gScript(script);
            script.invoke_filter();
            mScript = script;
        }
    };

    private class GammaFilter extends IImageFilter {
        private final int mGamma;

        public GammaFilter(int gamma) {
            mGamma = gamma;
        }

        @Override
        protected void _process() {
            ScriptC_GammaFilter script = new ScriptC_GammaFilter(mRS, getResources(), R.raw.gammafilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gGamma(mGamma);
            script.set_gScript(script);
            script.invoke_filter();
            mScript = script;
        }
    };

    private class PosterizeFilter extends IImageFilter {
        private final int mLevel;

        public PosterizeFilter(int _level) {
            mLevel = _level;
        }

        @Override
        protected void _process() {
            ScriptC_PosterizeFilter script = new ScriptC_PosterizeFilter(mRS, getResources(), R.raw.posterizefilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gLevel(mLevel);
            script.set_gScript(script);
            script.invoke_filter();
            mScript = script;
        }
    };

    private class ReflectionFilter extends IImageFilter {
        private final boolean mIsHorizontal;

        public ReflectionFilter(boolean isHorizontal) {
            mIsHorizontal = isHorizontal;
        }

        @Override
        protected void _process() {
            ScriptC_ReflectionFilter script = new ScriptC_ReflectionFilter(mRS, getResources(), R.raw.reflectionfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gIsHorizontal(mIsHorizontal ? 1 : 0);
            script.invoke_process();
            mScript = script;
        }
    };

    private class PixelateFilter extends IImageFilter {
        private final int mSquareSize;

        public PixelateFilter(int squareSize) {
            mSquareSize = squareSize;
        }

        @Override
        protected void _process() {
            ScriptC_PixelateFilter script = new ScriptC_PixelateFilter(mRS, getResources(), R.raw.pixelatefilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gSquareSize(mSquareSize);
            script.invoke_process();
            mScript = script;
        }
    };

    private class NightVisionFilter extends IImageFilter {
        // BrightContrastFilter factors
        private final float mBrightness;

        private final float mContrast;

        private Allocation mTmpOutputAllocation;

        private ScriptC_BrightContrastFilter mBrightContrastFilterScript;

        public NightVisionFilter(float brightness, float contrast) {
            mBrightness = brightness;
            mContrast = contrast;
        }

        @Override
        protected void _preProcess() {
            mTmpOutputAllocation = Allocation.createFromBitmap(mRS, mBitmapOut);
        }

        @Override
        protected void _postProcess() {
            mTmpOutputAllocation.destroy();
            mTmpOutputAllocation = null;
            mBrightContrastFilterScript.destroy();
            mBrightContrastFilterScript = null;
        }

        @Override
        protected void _process() {
            mBrightContrastFilterScript = new ScriptC_BrightContrastFilter(mRS, getResources(),
                    R.raw.brightcontrastfilter);
            mBrightContrastFilterScript.set_gIn(mInAllocation);
            mBrightContrastFilterScript.set_gOut(mTmpOutputAllocation);
            mBrightContrastFilterScript.set_gScript(mBrightContrastFilterScript);
            mBrightContrastFilterScript.set_gBrightnessFactor(mBrightness);
            mBrightContrastFilterScript.set_gContrastFactor(mContrast);
            mBrightContrastFilterScript.invoke_filter();

            ScriptC_SoftGlowFilter script = new ScriptC_SoftGlowFilter(mRS, getResources(), R.raw.softglowfilter);

            script.set_gIn(mInAllocation);
            script.set_gProcessedIn(mTmpOutputAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class EmbossFilter extends IImageFilter {
        private final float mIntensity;
        private final float mOffset;
        private final int mUseBrightness;

        public EmbossFilter(final float intensity, final float offset, final boolean useBrightness) {
            mIntensity = intensity;
            mOffset = offset;
            mUseBrightness = useBrightness ? 1 : 0;
        }

        @Override
        protected void _process() {
            ScriptC_ConvolutionFilter script = new ScriptC_ConvolutionFilter(mRS, getResources(),
                    R.raw.convolutionfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gKernel1stLine(new Float3(-2.0f * mIntensity, -mIntensity, 0.0f));
            script.set_gKernel2ndLine(new Float3(-mIntensity, 1.0f, mIntensity));
            script.set_gKernel3rdLine(new Float3(0.0f, mIntensity, 2.0f * mIntensity));
            script.set_gFactor(1.0f);
            script.set_gOffset(mOffset);
            script.set_gUseBrightness(mUseBrightness);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class LowPassConvolutionFilter extends IImageFilter {
        private final int mUseBrightness;
        private final float mOffset;

        public LowPassConvolutionFilter(final float offset, final boolean useBrightness) {
            mOffset = offset;
            mUseBrightness = useBrightness ? 1 : 0;
        }

        @Override
        protected void _process() {
            ScriptC_ConvolutionFilter script = new ScriptC_ConvolutionFilter(mRS, getResources(),
                    R.raw.convolutionfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gKernel1stLine(new Float3(1.0f, 1.0f, 1.0f));
            script.set_gKernel2ndLine(new Float3(1.0f, 1.0f, 1.0f));
            script.set_gKernel3rdLine(new Float3(1.0f, 1.0f, 1.0f));
            script.set_gFactor(9.0f);
            script.set_gOffset(mOffset);
            script.set_gUseBrightness(mUseBrightness);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    private class EdgeDetectionConvolutionFilter extends IImageFilter {
        private final int mUseBrightness;
        private final float mOffset;

        public EdgeDetectionConvolutionFilter(final float offset, final boolean useBrightness) {
            mOffset = offset;
            mUseBrightness = useBrightness ? 1 : 0;
        }

        @Override
        protected void _process() {
            ScriptC_ConvolutionFilter script = new ScriptC_ConvolutionFilter(mRS, getResources(),
                    R.raw.convolutionfilter);

            script.set_gIn(mInAllocation);
            script.set_gOut(mOutAllocation);
            script.set_gKernel1stLine(new Float3(-1.0f, 0.0f, -1.0f));
            script.set_gKernel2ndLine(new Float3(0.0f, 4.0f, 0.0f));
            script.set_gKernel3rdLine(new Float3(-1.0f, 0.0f, -1.0f));
            script.set_gFactor(1.0f);
            script.set_gOffset(mOffset);
            script.set_gUseBrightness(mUseBrightness);
            script.set_gScript(script);

            script.invoke_filter();
            mScript = script;
        }
    };

    // TODO add new filter above
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

        in = (ImageView) findViewById(R.id.displayin);
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

        out = (ImageView) findViewById(R.id.displayout);
        out.setImageBitmap(mBitmapOut);

        // Test
        Thread testThread = new Thread("Test thread") {
            @Override
            public void run() {
                RenderScript mRS = RenderScript.create(getApplicationContext());
                ScriptC_Test script = new ScriptC_Test(mRS, getResources(), R.raw.test);
                script.invoke_test();
                script.destroy();
                mRS.destroy();
            }
        };
        // testThread.start();

        // TODO add filter into list here
        mFilterList.add(new EdgeDetectionConvolutionFilter(0.1f, false));
        mFilterList.add(new EdgeDetectionConvolutionFilter(0.1f, true));
        mFilterList.add(new LowPassConvolutionFilter(0.1f, false));
        mFilterList.add(new LowPassConvolutionFilter(0.1f, true));
        mFilterList.add(new EmbossFilter(1.0f, 0.1f, false));
        mFilterList.add(new EmbossFilter(1.0f, 0.1f, true));
        mFilterList.add(new SoftGlowFilter(3.0f, 0.4f, 1.1f));
        mFilterList.add(new NightVisionFilter(0.4f, 1.1f));
        mFilterList.add(new PixelateFilter(20));
        mFilterList.add(new ReflectionFilter(true));
        mFilterList.add(new ReflectionFilter(false));
        mFilterList.add(new PosterizeFilter(2));
        mFilterList.add(new GammaFilter(50));
        mFilterList.add(new ComicFilter());
        mFilterList.add(new ParamEdgeDetectFilter(true, true));
        mFilterList.add(new ParamEdgeDetectFilter(false, true));
        mFilterList.add(new ParamEdgeDetectFilter(true, false));
        mFilterList.add(new ParamEdgeDetectFilter(false, false));
        mFilterList.add(new BannerFilter(true));
        mFilterList.add(new BannerFilter(false));
        mFilterList.add(new BigBrotherFilter());
        mFilterList.add(new SmashColorFilter());
        mFilterList.add(new BlockPrintFilter());
        mFilterList.add(new RadialDistortionFilter());
        mFilterList.add(new ThresholdFilter(0.5f));
        mFilterList.add(new GaussianBlurFilter(3, 10.0f));
        mFilterList.add(new HslModifyFilter(20.0f));
        mFilterList.add(new HistogramEqualFilter(0.25f));
        mFilterList.add(new GrayscaleFilter());
        mFilterList.add(new SharpFilter(1.0f));
        mFilterList.add(new FillPatternFilter());
        mFilterList.add(new ThreeDGridFilter(16, 100.0f / 255.0f));
        mFilterList.add(new ColorToneFilter(new Float3(0.1294f, 0.6588f, 0.9961f), 0.7529f));
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
        mFilterList.add(new BrightContrastFilter(0.5f, 0.5f));
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
