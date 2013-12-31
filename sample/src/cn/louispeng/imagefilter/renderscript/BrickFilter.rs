#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;

// magic factor
const static float ThreshHold = 0.5f;

void brickFilter() {
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	float mean = (f4.r + f4.g + f4.b) / 3;
	mean = (mean >= ThreshHold ? 1.0f : 0.0f);
    
    float3 f3 = {mean, mean, mean};
    
    *v_out = rsPackColorTo8888(f3);
}