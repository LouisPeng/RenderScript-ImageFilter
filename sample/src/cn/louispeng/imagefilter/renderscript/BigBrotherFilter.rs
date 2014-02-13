#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;

// Magic factors

// Static variables
static uint32_t _width;
static uint32_t _height;
static const int32_t DOT_AREA = 10;
static const int32_t arrDither[DOT_AREA * DOT_AREA] = {        
    167,200,230,216,181,94,72,193,242,232,
    36,52,222,167,200,181,126,210,94,72,
    232,153,111,36,52,167,200,230,216,181,
    94,72,193,242,232,36,52,222,167,200,
    181,126,210,94,72,232,153,111,36,52,
    167,200,230,216,181,94,72,193,242,232,
    36,52,222,167,200,181,126,210,94,72,
    232,153,111,36,52,167,200,230,216,181,
    94,72,193,242,232,36,52,222,167,200,
    181,126,210,94,72,232,153,111,36,52
};

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
    float3 f3;
    uint32_t offset_x = x % DOT_AREA;
    uint32_t offset_y = y % DOT_AREA;
    uint32_t index = offset_y * DOT_AREA + offset_x; 

    int32_t l_grayIntensity = (1.0f - f4.b) * 255;
    if (l_grayIntensity > arrDither[index]) {
        f3.r = 0;
        f3.g = 0;
        f3.b = 0;
    } else {
        f3.r = 1;
        f3.g = 1;
        f3.b = 1;
    }
            
    *v_out = rsPackColorTo8888(f3);
}