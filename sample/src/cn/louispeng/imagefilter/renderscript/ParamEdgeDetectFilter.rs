#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"
#include "YUV.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;
int gDoGrayConversion = 1;
int gDoInversion = 1;

// Magic factors
static float Threshold = 0.25f;
static float thresholdSqFactor;
static float thresholdSq;
static float K00 = 1.0f;
static float K01 = 2.0f;
static float K02 = 1.0f;

// Static variables
static uint32_t _width;
static uint32_t _height;

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	float thresholdSqFactor = Threshold * 2.0f;
	float thresholdSq = thresholdSqFactor * thresholdSqFactor;
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

static float3 ProcessColor(float4 f4, uint32_t x, uint32_t y) {
	float3 rgb;
	return rgb;
}

static float3 ProcessGray(float4 f4, uint32_t x, uint32_t y) {
	float color1 = 0, color2 = 0, color3 = 0, color4 = 0, color5 = 0, color6 = 0, color7 = 0, color8 = 0;
	
	if (x > 0 & y > 0) {
		color1 = GetY(rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x - 1, y - 1)).rgb);
	}
	
	if (y > 0) {
		color2 = GetY(rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x, y - 1)).rgb);
	}
	
	if (x < (_width - 1) && y > 0) {
    	color3 = GetY(rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x + 1, y - 1)).rgb);
	}
	
	if (x > 0) {
    	color4 = GetY(rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x - 1, y)).rgb);
	}
	
	if (x < (_width - 1)) {
    	color5 = GetY(rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x + 1, y)).rgb);
	}
	
	if (x > 0 && y < (_height - 1)) {
    	color6 = GetY(rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x - 1, y + 1)).rgb);
	}
	
	if (y < (_height - 1)) {
    	color7 = GetY(rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x, y + 1)).rgb);
	}
	
	if (x < (_width - 1) && y < (_height - 1)) {
    	color8 = GetY(rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x + 1, y + 1)).rgb);
	}
	
    float colorSum1 = 
    	color1 * K00 + color2 * K01 + color3 * K02 + color6 * (-K00) + color7 * (-K01) + color8 * (-K02);
    float colorSum2 = 
    	color1 * K00 + color3 * (-K00) + color4 * K01 + color5 * (-K01) + color6 * K02 + color8 * (-K02);
    
    float color = (((colorSum1 * colorSum1) + (colorSum2 * colorSum2)) > thresholdSq) ? 0.0f : 1.0f;
    if (gDoInversion == 1) {
        color = 1.0f - color;
    }
    
    rsDebug("color = ", color);
    float3 rgb = {color, color, color};
	return rgb;
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
    float3 f3;
    if (gDoGrayConversion == 1) {
    	f3 = ProcessGray(f4, x, y);
    } else {
    	f3 = ProcessColor(f4, x, y);
    }
    
    *v_out = rsPackColorTo8888(f3);
}