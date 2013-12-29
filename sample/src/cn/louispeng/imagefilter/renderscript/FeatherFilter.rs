#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;


// magic factor
const static float _size = 0.5f;
static uint32_t _width;
static uint32_t _height;
static uint32_t _ratio;
static uint32_t _cx;
static uint32_t _cy;
static uint32_t _max;
static uint32_t _min;
static uint32_t _diff;

void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	_ratio = _width >  _height ?  _height * 32768 / _width : _width * 32768 / _height;
 	_cx = _width >> 1;
	_cy = _height >> 1;
	_max = _cx * _cx + _cy * _cy;
	_min = _max * (1 - _size);
	_diff = _max - _min;
	
	rsDebug("width = ", _width);
	rsDebug("height = ", _height);
	rsDebug("_ratio = ", _ratio);
	rsDebug("_cx = ", _cx);
	rsDebug("_cy = ", _cy);
	rsDebug("_max = ", _max);
	rsDebug("_min = ", _min);
	rsDebug("_diff = ", _diff);
}

void featherFilter() {
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
	// Calculate distance to center and adapt aspect ratio
	int dx = _cx - x;
  	int dy = _cy - y;
  	if (_width > _height){
		dx = (dx * _ratio) >> 15;
  	} else {
     	dy = (dy * _ratio) >> 15;
  	}
  
  	int distSq = dx * dx + dy * dy;
  	float v =  ((float)distSq / (float)_diff) * 255.0f;
  	
  	float3 f3;
  	f3.r = FClamp0255Float(f4.r + v);
  	f3.g = FClamp0255Float(f4.g + v);
  	f3.b = FClamp0255Float(f4.b + v);
	
    *v_out = rsPackColorTo8888(f3);
}