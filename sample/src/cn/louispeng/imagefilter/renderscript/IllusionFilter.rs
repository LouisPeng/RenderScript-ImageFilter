#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;
float gAmount;

// magic factor

static int32_t _width;
static int32_t _height;
static float _amount;

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	float LIB_PI = 3.14159265358979323846f;
	_amount = LIB_PI / ((gAmount >= 1.0f) ? gAmount : 1.0f) ;
}

void illusionFilter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
    float _scale = sqrt((float)(_width * _width + _height * _height)) / 2 ;
	int _offset = (int)(_scale / 2.0f);
    float cx = (x - _width / 2.0f) / _scale ;
	float cy = (y - _height / 2.0f) / _scale ;
	float angle = floor ( atan2(cy, cx) / 2.0f / _amount) * 2.0f * _amount + _amount;
	float radius =  sqrt(cx * cx + cy * cy);
	int xx = (int)(x - _offset *  cos(angle));
	int yy = (int)(y - _offset *  sin(angle));
	xx = FClamp(xx, 0, _width - 1);
	yy = FClamp(yy, 0, _height - 1);

	const uchar4* theF4Point = rsGetElementAt(gIn, xx, yy);
	float4 theF4 = rsUnpackColor8888(*theF4Point);

    float3 f3;
    f3.r = f4.r + radius * (theF4.r - f4.r);
    f3.g = f4.g + radius * (theF4.g - f4.g);
    f3.b = f4.b + radius * (theF4.b - f4.b);
    *v_out = rsPackColorTo8888(f3);
}