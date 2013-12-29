#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;


// magic factor
const static float _size = 0.5f;
static int _width;
static int _height;
static int _ratio;
static int _cx;
static int _cy;
static int _max;
static int _min;
static int _diff;

void featherFilter() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	rsDebug("width = %d", _width);
	rsDebug("height = %d", _height);
	_ratio = _width >  _height ?  _height * 32768 / _width : _width * 32768 / _height;
 	_cx = _width >> 1;
	_cy = _height >> 1;
	_max = _cx * _cx + _cy * _cy;
	_min = _max * (1 - _size);
	_diff = _max - _min;
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
  	float v =  ((float)distSq / _diff) * 255;
  	
  	float3 f3;
  	f3.r = f4.r + v;
  	f3.g = f4.g + v;
  	f3.b = f4.b + v;
  	f3.r = FClamp0255Float(f4.r + v);
  	f3.g = FClamp0255Float(f4.g + v);
  	f3.b = FClamp0255Float(f4.b + v);
	
    *v_out = rsPackColorTo8888(f3);
}