#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Blend.rsh"

// set from the java SDK level
rs_allocation gIn1;
rs_allocation gIn2;
rs_allocation gOut;
rs_script gScript;
int32_t gBlendMode = Multiply;
float gMixture = 0.9f;

// Magic factors

// Static variables

static void setup() {
}

void filter() {
	setup();
    rsForEach(gScript, gIn1, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
	float4 theF4 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn2, x, y));
    
    float3 f3;
    if (x == 2 && y == 2) {
    	f3 = FBlend(f4.rgb, theF4.rgb, gBlendMode, gMixture, 1);
	} else {
		f3 = FBlend(f4.rgb, theF4.rgb, gBlendMode, gMixture, 0);
	}    
    
    *v_out = rsPackColorTo8888(f3);
}