#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// 马赛克效果

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;

// Magic factors
static uint32_t MosiacSize = 40;

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
    float4 theF4;
    if (y % MosiacSize == 0 && x % MosiacSize == 0) {
		theF4 = f4;
	} else {
		uint32_t theX = MosiacSize * (x / MosiacSize);
		uint32_t theY = MosiacSize * (y / MosiacSize);
		theF4 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, theX, theY));
	}
    
    f3 = theF4.rgb;
    
    *v_out = rsPackColorTo8888(f3);
}