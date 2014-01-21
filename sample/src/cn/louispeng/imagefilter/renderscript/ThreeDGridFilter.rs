#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;
int32_t gSize;
float gDepth;

// Magic factors

// Static variables

static void setup() {
	gSize = ((gSize >= 1) ? gSize : 1);
    gDepth = gDepth;
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
	float d = 0;
    if (((y - 1) % gSize == 0) && (x % gSize > 0) && ((x + 1) % gSize > 0)) {
        d = -gDepth; // top
    } else if (((y + 2) % gSize == 0) && (x % gSize > 0) && ((x + 1) % gSize > 0)) {
        d = gDepth; // bottom
    } else if (((x - 1) % gSize == 0) && (y % gSize > 0) && ((y + 1) % gSize) > 0) {
        d = gDepth; // left
    } else if (((x + 2) % gSize == 0) && (y % gSize > 0) && ((y + 1) % gSize) > 0) {
        d = -gDepth; // right
    }

    float3 f3 = FClamp01Float3(f4.rgb + d);
    
    *v_out = rsPackColorTo8888(f3);
}