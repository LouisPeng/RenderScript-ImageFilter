#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;

// Magic factors
static const float Levels = 5.0f;
static const float Mult = 0.003921569f;

// Static variables

static void setup() {
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
	float R = (((int) (f4.r * 255.0f * 0.003921569f * Levels)) / Levels) * 255.0f;
	float G = (((int) (f4.g * 255.0f * 0.003921569f * Levels)) / Levels) * 255.0f;
	float B = (((int) (f4.b * 255.0f * 0.003921569f * Levels)) / Levels) * 255.0f;
        
    float3 f3 = {R / 255.0f, G / 255.0f, B / 255.0f};
    *v_out = rsPackColorTo8888(f3);
}