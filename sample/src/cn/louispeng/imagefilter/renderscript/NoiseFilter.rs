#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;

// magic factor
static const float Intensity = 0.2f;
static const int32_t Num = (int32_t)(Intensity * 32768.0f);

void noiseFilter() {
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float3 f3 = {0.0f, 0.0f, 0.0f};
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
    if (Num != 0) {
        int r = rsRand(-255, 0xff) * Num;
        int g = rsRand(-255, 0xff) * Num;
        int b = rsRand(-255, 0xff) * Num;
        int rr = f4.r + (r >> 15);
        int gg = f4.g + (g >> 15);
        int bb = f4.b + (b >> 15);
        f3.r = FClamp0255Float(rr);
        f3.g = FClamp0255Float(gg);
        f3.b = FClamp0255Float(bb);
    }
    
    *v_out = rsPackColorTo8888(f3);
}