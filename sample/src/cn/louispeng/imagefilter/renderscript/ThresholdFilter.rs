#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// 阈值特效

#include "Clamp.rsh"
#include "YUV.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;
float gThreshold;

// Magic factors

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
	
    float3 f3;
    
    if (GetGrayscale(f4.rgb) > gThreshold) {
    	f3.r = 1.0f;
    	f3.g = 1.0f;
    	f3.b = 1.0f;
    } else {
    	f3.r = 0.0f;
    	f3.g = 0.0f;
    	f3.b = 0.0f;
    }
    
    *v_out = rsPackColorTo8888(f3);
}