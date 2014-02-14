#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "LUT.rsh"
#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;
int32_t gLevel;

// magic factor

// Static variables 
static float LUT[255] = {0.0f};
static uint32_t _width;
static uint32_t _height;

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
}

static float initLUTtableItem(const float _level, const int _LUTIndex) {
	float d = 255.0f / (_level - 1.0f);
    int32_t n = (int32_t) (_LUTIndex / d + 0.5f); // round
    return FClampFloat(d * n, 0.0f, 255.0f);  // round
}

static void initLUTtable() {
	int32_t _level = ((gLevel >= 2) ? gLevel : 2);
	for (int32_t _LUTIndex = 0; _LUTIndex <= 255; _LUTIndex++) {
		LUT[_LUTIndex] = initLUTtableItem(_level, _LUTIndex);
	}
}

void filter() {
	setup();
	initLUTtable();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
	float3 f3;
	if ( x < (_width - 1) && y < (_height - 1)) {
		f3 = LUT_Process(f4, LUT); 
	} else {
		f3 = f4.rgb;
	}
	   
    *v_out = rsPackColorTo8888(f3);
}