#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "Clamp.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gPattern;
rs_allocation gOut;
rs_script gScript;

float gMixture = 0.2f;

// Magic factors
static float mix1;
static float mix2;

// Static variables
static uint32_t _patternWidth;
static uint32_t _patternHeight;

static void setup() {
	_patternWidth = rsAllocationGetDimX(gPattern);
	_patternHeight = rsAllocationGetDimY(gPattern);
	mix1 = gMixture;
	mix2 = 1.0f - gMixture;
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
    uint32_t xx = x % _patternWidth;
    uint32_t yy = y % _patternHeight;
	float4 patternF4 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gPattern, xx, yy));

    float3 theF3 = FClamp01Float3(f4.rgb + patternF4.rgb);
    
    float3 f3 = f4.rgb * mix2 + theF3 * mix1;

    *v_out = rsPackColorTo8888(f3);
}