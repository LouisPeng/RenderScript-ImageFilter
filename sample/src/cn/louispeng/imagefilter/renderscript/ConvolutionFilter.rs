#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// 卷积运算（光照效果）

#include "Clamp.rsh"
#include "YUV.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;

// Magic factors

// Static variables
static uint32_t _width;
static uint32_t _height;
static float factor = 1.0f;
static float3 kernel[3] = {{0, 0, 0},{0, 1.0f, 0},{0, 0, 0.4f}};
static float offset = 1.0f / 255.0f;

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

static float GetPixelBrightness(const uchar4 *v_in, int32_t x, int32_t y) {
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
    return GetY(theF4.rgb);
}

static float3 GetPixelColor(const uchar4 *v_in, int32_t x, int32_t y) {
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

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	int32_t x_int = (int32_t)x;
	int32_t y_int = (int32_t)y;
	
	
    float3 f3 = {0, 0, 0};
    float3 color = {0, 0, 0};
    
    float3 value = kernel[0];
    if (value.x != 0) {
        color = GetPixelColor(v_in, x_int - 1, y_int - 1);
        color = color * value.x;
        f3 += color;
    }
    if (value.y != 0) {
        color = GetPixelColor(v_in, x_int, y_int - 1);
        color = color * value.x;
        f3 += color;
    }
    if (value.z != 0) {
        color = GetPixelColor(v_in, x_int + 1, y_int - 1);
        color = color * value.x;
        f3 += color;
    }
    
    value = kernel[1];
    if (value.x != 0) {
        color = GetPixelColor(v_in, x_int - 1, y_int);
        color = color * value.x;
        f3 += color;
    }
    if (value.y != 0) {
        color = GetPixelColor(v_in, x_int, y_int);
        color = color * value.x;
        f3 += color;
    }
    if (value.z != 0) {
        color = GetPixelColor(v_in, x_int + 1, y_int);
        color = color * value.x;
        f3 += color;
    }
    
    value = kernel[2];
    if (value.x != 0) {
        color = GetPixelColor(v_in, x_int - 1, y_int + 1);
        color = color * value.x;
        f3 += color;
    }
    if (value.y != 0) {
        color = GetPixelColor(v_in, x_int, y_int + 1);
        color = color * value.x;
        f3 += color;
    }
    if (value.z != 0) {
        color = GetPixelColor(v_in, x_int + 1, y_int + 1);
        color = color * value.x;
        f3 += color;
    }
    
    f3 = FClamp01Float3((f3 / factor) + offset);
                
    *v_out = rsPackColorTo8888(f3);
}