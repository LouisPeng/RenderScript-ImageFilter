#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

#include "LUTFilter.rsh"
#include "Clamp.rsh"

// set from the java SDK level
float gGamma;
rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;

// magic factor

// static 
static float LUT[255] = {0.0f};

static float InitLUTtableItem(const float _fInvGamma, const int nLUTIndex) {
	// FIXME
	float fMax = pow(255.0, _fInvGamma) / 255.0f;
	float d = pow((float)nLUTIndex, _fInvGamma);
	return FClamp01Float(d / fMax);
}

static void InitLUTtable() {
	gGamma = (gGamma >= 1.0f) ? gGamma : 1.0f;
	float _fInvGamma = 1.0f / (gGamma / 100.0f);
	for (int nLUTIndex = 0; nLUTIndex < 255; nLUTIndex++) {
		LUT[nLUTIndex] = InitLUTtableItem(_fInvGamma, nLUTIndex);
	}
}

void filter() {
	InitLUTtable();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	   
	float3 f3 = LUT_Process(f4, LUT); 
	   
    *v_out = rsPackColorTo8888(f3);
}