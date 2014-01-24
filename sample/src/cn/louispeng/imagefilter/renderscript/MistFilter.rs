#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// 雾化效果

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
    
    int32_t random = rsRand(1, 123456);
  	int32_t theX = x + random % 19;
  	int32_t theY = y + random % 19;
  	if(theX >= _width) {
		theX = _width - 1;   				  
  	}
  	if(theY >= _height) {
		theY = _height - 1;   				  
  	}
  	
  	float4 theF4 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, theX, theY));
    
    *v_out = rsPackColorTo8888(theF4);
}