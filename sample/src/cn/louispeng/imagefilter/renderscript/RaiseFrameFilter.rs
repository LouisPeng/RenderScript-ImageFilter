#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;

// Magic factors
int gSize = 20;
static float Alpha = 0.5f;

// Static variables
static uint32_t _width;
static uint32_t _height;

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
	float3 theMagicF3;
    if ((x < gSize) && (y < _height - x) && (y >= x)) {
        float3 tmp = {1.0f, 1.0f, 0.2549f}; // left
        theMagicF3 = tmp;
    } else if ((y < gSize) && (x < _width - y) && (x >= y)) {
        float3 tmp = {1.0f, 1.0f, 0.4706f}; // top
        theMagicF3 = tmp;
    } else if ((x > _width - gSize) && (y >= _width - x) && (y < _height + x - _width)) {
    	float3 tmp = {0, 0, 0.2549f}; // right
    	theMagicF3 = tmp;
    } else if (y > _height - gSize) {
    	float3 tmp = {0, 0, 0.4706f}; // bottom
    	theMagicF3 = tmp;
    } else {
    	*v_out = rsPackColorTo8888(f4);
        return;
    }

    float negoAlpha = 1 - Alpha;
    float3 f3;
    f3.r = theMagicF3.r * Alpha + f4.r * negoAlpha;
    f3.g = theMagicF3.g * Alpha + f4.g * negoAlpha;
    f3.b = theMagicF3.b * Alpha + f4.b * negoAlpha;
    
    *v_out = rsPackColorTo8888(f3);
}