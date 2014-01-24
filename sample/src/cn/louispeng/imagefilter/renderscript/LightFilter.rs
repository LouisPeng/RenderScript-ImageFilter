#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// 光照效果

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;

// Magic factors
static float Light = 150.0f / 255.0f;

// Static variables
static uint32_t _width;
static float _halfWidth;
static uint32_t _height;
static float _halfHeight;
static float _R;

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	_halfWidth = _width / 2;
	_halfHeight = _height / 2;
	_R = fmin(_halfWidth, _halfHeight);
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
	float3 f3;
	float length = sqrt(pow(((float)x - _halfWidth), 2) + pow(((float)y - _halfHeight), 2)); 
 	if(length < _R) {
  		float pixel = Light * (1.0f - length / _R);
  		f3 = FClamp01Float3(f4.rgb + pixel);
	} else {
		f3 = f4.rgb;
	}
    
    *v_out = rsPackColorTo8888(f3);
}