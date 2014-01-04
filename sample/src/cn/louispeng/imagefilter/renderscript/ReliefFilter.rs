#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;

// Magic factors

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
	
	float3 f3;
	if (x == _width - 1) {
    	f3 = f4.rgb;
	} else {
		float4 theF4 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x + 1, y));
		f3 = (f4.rgb - theF4.rgb) + 0.5f;
		f3 = FClamp01Float3(f3);
	}
    
    *v_out = rsPackColorTo8888(f3);
}