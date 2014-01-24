#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;

float gStep = 1.0f;

// Magic factors

// Static variables
static uint32_t _width;
static uint32_t _height;
static float Laplacian[9] = { -1, -1, -1, -1, 0, -1, -1, -1, -1 };

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	Laplacian[4] = 8.0f + gStep;
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
	float3 f3 = {0, 0, 0};
	uint32_t xx = x + 1;
	uint32_t yy = y + 1;
	if (xx >= 1 && xx < _width - 1 && yy >= 1 && yy < _height - 1) {
	    int32_t Index = 0;
	    for (int32_t col = -1; col <= 1; col++) {
	        for (int32_t row = -1; row <= 1; row++) {
	        	float4 theF4 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, xx + row, yy + col));
				f3 += theF4.rgb * Laplacian[Index];
	            Index++;
	        }
	    } 
	    f3 = FClamp01Float3(f3);
    } else {
    	f3.r = 1;
    	f3.g = 1;
    	f3.b = 1;
    }
    
    *v_out = rsPackColorTo8888(f3);
}