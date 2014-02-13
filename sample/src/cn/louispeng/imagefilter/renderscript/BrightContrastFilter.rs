#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// 高亮对比度特效

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;
// The brightness factor.
// Should be in the range [-1.0f, 1.0f].
float gBrightnessFactor  = 0.25f;

// The contrast factor.
// Should be in the range [-1.0f, 1.0f].
float gContrastFactor  = 0.5f;

// magic factor
static float ContrastFactor1;

void filter() {
	ContrastFactor1  = (1.0f + gContrastFactor) * (1.0f + gContrastFactor);
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
    float3 f3 = f4.rgb;
    
	// Modify brightness (addition)
	if (gBrightnessFactor != 0.0f) {
	   	// Add brightness
	   	f3 = f3 + gBrightnessFactor;
	   	f3 = FClamp01Float3(f3);
	}
	
	// Modifiy contrast (multiplication)
 	if (ContrastFactor1 != 1.0f){
	    // Transform to range [-0.5f, 0.5f]
	    f3 = f3 - 0.5f;
	
	    // Multiply contrast factor
	    f3 = f3 * ContrastFactor1;
	
	    // Transform back to range [0.0f, 1.0f]
	    f3 = f3 + 0.5f;
	    f3 = FClamp01Float3(f3);
	}
    
    *v_out = rsPackColorTo8888(f3);
}