#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// 清扫玻璃效果

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;

// Magic factors
static float _Size = 0.5f;

// Static variables
static uint32_t _width;
static uint32_t _height;
static float _ratio;
static uint32_t _centerX;
static uint32_t _centerY;
static uint32_t _max;
static uint32_t _min;

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	_ratio = (_width >  _height) ?  ((float)_height / _width) : ((float)_width / _height);
 	_centerX = _width >> 1;
	_centerY = _height >> 1;
	_max = _centerX * _centerX + _centerY * _centerY;
	_min = _max * (1 - _Size) * (1 - _Size);
}

void filter() {
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
		distanceY = distanceY * _ratio * 2;
  	} else {
     	distanceX = distanceX * _ratio * 2;
  	}
  
  	uint32_t distSq = distanceX * distanceX + distanceY * distanceY;
  	
  	float3 f3;
  	float4 theF4;
  	if (distSq > _min) {
	    int32_t k = rsRand(1, 123456);
        
        uint32_t theX = x + k % 19;
        uint32_t theY = y + k % 19;
        if (theX >= _width) {
            theX = _width - 1;
        }
        if (theY >= _height) {
            theY = _height - 1;
        }
        
        theF4 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, theX, theY));
	} else {
		theF4 = f4;
	}
	
	f3 = theF4.rgb;
    
    *v_out = rsPackColorTo8888(f3);
}