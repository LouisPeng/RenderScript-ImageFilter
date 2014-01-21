#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;

float3 gBlindColor;
int32_t gIsHorizontal;	// 1 for horizontal; 0 for vertical
float gOpacity;		// 0.01f to 1.0f
int32_t gWidth;

// Magic factors

// Static variables
static uint32_t _width;
static uint32_t _height;

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	gOpacity = FClampFloat(gOpacity, 0.01f, 1.0f);
	gWidth = (gWidth >= 2) ? gWidth : 2;
	gIsHorizontal = (gIsHorizontal != 0 && gIsHorizontal != 1) ? 1 : gIsHorizontal;
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
    float3 f3;
    if (x == _width - 1 || y == _height - 1) {
    	f3 = f4.rgb;
    } else {
    	int32_t nMod = 0;
        if (gIsHorizontal == 1) {
        	// horizontal direction
            nMod = y % _width;
        } else { 
        	// vertical direction
            nMod = x % _width;
        }

        float fDelta = gOpacity / (_width - 1.0f);
        float a = FClamp01Float(nMod * fDelta);
        if (gBlindColor.r == 0 && gBlindColor.g == 0 && gBlindColor.b == 1.0f) {
            f3 = gBlindColor;
        } else {
	        if (a == 0) {
	            f3 = f4.rgb;
	        } else {
		        float t = 1.0f - a;
		        f3.r = gBlindColor.r * a + f4.r * t;
		        f3.g = gBlindColor.g * a + f4.g * t;
		        f3.b = gBlindColor.b * a + f4.b * t;
		        f3 = FClamp01Float3(f3);
	    	}
        }
    }
    
    *v_out = rsPackColorTo8888(f3);
}