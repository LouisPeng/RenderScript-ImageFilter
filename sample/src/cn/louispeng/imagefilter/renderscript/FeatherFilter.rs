#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;


// magic factor
const static float _size = 0.5f;
static int32_t _width;
static int32_t _height;
static int32_t _ratio;
static int32_t _centerX;
static int32_t _centerY;
static int32_t _max;
static int32_t _min;
static int32_t _diff;

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	_ratio = (_width >  _height) ?  ((_height * 0x8000) / _width) : ((_width * 0x8000) / _height);
 	_centerX = _width >> 1;
	_centerY = _height >> 1;
	_max = _centerX * _centerX + _centerY * _centerY;
	_min = _max * (1 - _size);
	_diff = _max - _min;
}

void featherFilter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
	// Calculate distance to center and adapt aspect ratio
	int32_t distanceX = _centerX - x;
  	int32_t distanceY = _centerY - y;
  	if (_width > _height){
		distanceX = (distanceX * _ratio) >> 15;
  	} else {
     	distanceY = (distanceY * _ratio) >> 15;
  	}
  
  	uint32_t distSq = distanceX * distanceX + distanceY * distanceY;
  	float v =  (float)distSq / _diff;
  	  	
  	float3 f3 = f4.rgb + v;
  	f3 = FClamp01Float3(f3);
	
    *v_out = rsPackColorTo8888(f3);
}