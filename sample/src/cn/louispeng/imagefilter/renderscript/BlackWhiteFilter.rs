#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// 黑白效果

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;

rs_script gScript;

// magic factor
const static float3 gBlackWhiteMult = {0.3f, 0.11f, 0.59f};

void filter() {
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
	float corfinal = (f4.r * gBlackWhiteMult.r) + (f4.g * gBlackWhiteMult.g) + (f4.b * gBlackWhiteMult.b);
	
    float3 f3 = {corfinal, corfinal, corfinal};
    
    *v_out = rsPackColorTo8888(f3);
}