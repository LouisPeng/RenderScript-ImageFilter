#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"
#include "HSL.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;
float gHueFactor;

// Magic factors

// Static variables

static void setup() {
	gHueFactor = fmax(0.0f, fmin(359.0f, gHueFactor));
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
    float3 hsl = RgbToHsl(f4.rgb);
    hsl.x = gHueFactor;
    float3 f3 = HslToRgb(hsl);
                
    *v_out = rsPackColorTo8888(f3);
}