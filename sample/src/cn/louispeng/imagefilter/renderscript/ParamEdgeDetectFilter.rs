#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// 参数化边缘检测效果

#include "Clamp.rsh"
#include "YUV.rsh"

typedef float3 (*PF) (int32_t, int32_t);

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;
int32_t gDoGrayConversion = 1;
int32_t gDoInversion = 1;
float gThreshold = 0.25f;
float gK00 = 1.0f;
float gK01 = 2.0f;
float gK02 = 1.0f;

// Magic factors
static float gThresholdSq;

// Static variables
static uint32_t _width;
static uint32_t _height;
static PF processFunction;

static float3 _getColor(int32_t x, int32_t y) {
	int32_t theX = x, theY = y;
	if (theX < 0) {
		theX = 0;
	} else if (theX >= _width) {
        theX = _width - 1;
    }
    
    if (theY < 0) {
		theY = 0;
	} else if (theY >= _height) {
        theY = _height - 1;
    }
    
    return rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, theX, theY)).rgb;
}

static float3 ProcessColor(int32_t x, int32_t y) {
	float3 color1 = 0, color2 = 0, color3 = 0, color4 = 0, color5 = 0, color6 = 0, color7 = 0, color8 = 0;
	
	color1 = _getColor(x - 1, y - 1);
	color2 = _getColor(x, y - 1);
	color3 = _getColor(x + 1, y - 1);
	color4 = _getColor(x - 1, y);
	color5 = _getColor(x + 1, y);
	color6 = _getColor(x - 1, y + 1);
	color7 = _getColor(x, y + 1);
	color8 = _getColor(x + 1, y + 1);
	
	float3 rgb = {0, 0, 0};
	
	float colorSum1 = color1.r * gK00 + color2.r * gK01 + color3.r * gK02 + color6.r * (-gK00) + color7.r
            * (-gK01) + color8.r * (-gK02);
    float colorSum2 = color1.r * gK00 + color3.r * (-gK00) + color4.r * gK01 + color6.r * gK02 + color5.r
            * (-gK01) + color8.r * (-gK02);
    if (gDoInversion == 1) {
        rgb.r = (((colorSum1 * colorSum1) + (colorSum2 * colorSum2)) > gThresholdSq) ? 1.0f : 0.0f;
    } else {
    	rgb.r = (((colorSum1 * colorSum1) + (colorSum2 * colorSum2)) > gThresholdSq) ? 0.0f : 1.0f;
    }
    
    colorSum1 = color1.g * gK00 + color2.g * gK01 + color3.g * gK02 + color6.g * (-gK00) + color7.g
            * (-gK01) + color8.g * (-gK02);
    colorSum2 = color1.g * gK00 + color3.g * (-gK00) + color4.g * gK01 + color6.g * gK02 + color5.g
            * (-gK01) + color8.g * (-gK02);    
    if (gDoInversion == 1) {
        rgb.g = (((colorSum1 * colorSum1) + (colorSum2 * colorSum2)) > gThresholdSq) ? 1.0f : 0.0f;
    } else {
    	rgb.g = (((colorSum1 * colorSum1) + (colorSum2 * colorSum2)) > gThresholdSq) ? 0.0f : 1.0f;
    }
    
    colorSum1 = color1.b * gK00 + color2.b * gK01 + color3.b * gK02 + color6.b * (-gK00) + color7.b
            * (-gK01) + color8.b * (-gK02);
    colorSum2 = color1.b * gK00 + color3.b * (-gK00) + color4.b * gK01 + color6.b * gK02 + color5.b
            * (-gK01) + color8.b * (-gK02);
    if (gDoInversion == 1) {
        rgb.b = (((colorSum1 * colorSum1) + (colorSum2 * colorSum2)) > gThresholdSq) ? 1.0f : 0.0f;
    } else {
    	rgb.b = (((colorSum1 * colorSum1) + (colorSum2 * colorSum2)) > gThresholdSq) ? 0.0f : 1.0f;
    }
		
	return rgb;
}

static float3 ProcessGray(int32_t x, int32_t y) {
	float color1 = 0, color2 = 0, color3 = 0, color4 = 0, color5 = 0, color6 = 0, color7 = 0, color8 = 0;
	
	color1 = GetGrayscale(_getColor(x - 1, y - 1));
	color2 = GetGrayscale(_getColor(x, y - 1));
	color3 = GetGrayscale(_getColor(x + 1, y - 1));
	color4 = GetGrayscale(_getColor(x - 1, y));
	color5 = GetGrayscale(_getColor(x + 1, y));
	color6 = GetGrayscale(_getColor(x - 1, y + 1));
	color7 = GetGrayscale(_getColor(x, y + 1));
	color8 = GetGrayscale(_getColor(x + 1, y + 1));	
	
    float colorSum1 = 
    	color1 * gK00 + color2 * gK01 + color3 * gK02 + color6 * (-gK00) + color7 * (-gK01) + color8 * (-gK02);
    float colorSum2 = 
    	color1 * gK00 + color3 * (-gK00) + color4 * gK01 + color5 * (-gK01) + color6 * gK02 + color8 * (-gK02);
    
    float color;
    if (gDoInversion == 1) {
        color = (((colorSum1 * colorSum1) + (colorSum2 * colorSum2)) > gThresholdSq) ? 1.0f : 0.0f;
    } else {
    	color = (((colorSum1 * colorSum1) + (colorSum2 * colorSum2)) > gThresholdSq) ? 0.0f : 1.0f;
    }
    
    float3 rgb = {color, color, color};
	return rgb;
}

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	float gThresholdSqFactor = gThreshold * 2.0f;
	gThresholdSq = gThresholdSqFactor * gThresholdSqFactor;	
	
	if (gDoGrayConversion == 1) {
    	processFunction = ProcessGray;
    } else {
    	processFunction = ProcessColor;
    }
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
    *v_out = rsPackColorTo8888(processFunction(x, y));
}