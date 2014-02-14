#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// 卷积运算
typedef float3 (*PF) (int32_t, int32_t);

#include "Clamp.rsh"
#include "YUV.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;
float3 gKernel1stLine = {0, 0, 0};
float3 gKernel2ndLine = {0, 1.0f, 0};
float3 gKernel3rdLine = {0, 0, 0};
float gFactor = 1.0f;
float gOffset = 0.1f;
int32_t gUseBrightness = 0;

// Magic factors

// Static variables
static uint32_t _width;
static uint32_t _height;
static PF processFunction;

static float3 GetPixelBrightness(int32_t x, int32_t y) {
	if (x < 0) {
        x = 0;
    } else if (x >= _width) {
        x = _width - 1;
    }
    if (y < 0) {
        y = 0;
    } else if (y >= _height) {
        y = _height - 1;
    }
    
    float4 theF4 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x, y));
    float brightness = GetY(theF4.rgb);
    float3 f3 = {brightness, brightness, brightness};
    return f3;
}

static float3 GetPixelColor(int32_t x, int32_t y) {
	if (x < 0) {
        x = 0;
    } else if (x >= _width) {
        x = _width - 1;
    }
    if (y < 0) {
        y = 0;
    } else if (y >= _height) {
        y = _height - 1;
    }
    float4 theF4 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x, y));
    return theF4.rgb;
}

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	if (gUseBrightness == 1) {
		processFunction = GetPixelBrightness;
	} else {
		processFunction = GetPixelColor;
	}
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	float3 f3;
	if (x > 0 && x < (_width - 1) && y > 0 && y < (_height - 1)) {
		int32_t x_int = (int32_t)x;
		int32_t y_int = (int32_t)y;
	    
	    f3 = processFunction(x_int - 1, y_int - 1) * gKernel1stLine.x;
	    f3 += processFunction(x_int, y_int - 1) * gKernel1stLine.y;
	    f3 += processFunction(x_int + 1, y_int - 1) * gKernel1stLine.z;
	    f3 += processFunction(x_int - 1, y_int) * gKernel2ndLine.x;
	    f3 += processFunction(x_int, y_int) * gKernel2ndLine.y;
	    f3 += processFunction(x_int + 1, y_int) * gKernel2ndLine.z;
	    f3 += processFunction(x_int - 1, y_int + 1) * gKernel3rdLine.x;
	    f3 += processFunction(x_int, y_int + 1) * gKernel3rdLine.y;
	    f3 += processFunction(x_int + 1, y_int + 1) * gKernel3rdLine.z;
	    
	    f3 = FClamp01Float3(f3 / gFactor + gOffset);
    } else {
    	f3 = f4.rgb;
    }
                
    *v_out = rsPackColorTo8888(f3);
}