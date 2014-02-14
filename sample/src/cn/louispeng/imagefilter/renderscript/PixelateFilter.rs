#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
int gSquareSize = 20;

// Magic factors

// Static variables
static uint32_t _width;
static uint32_t _height;

static void fillRect(uint32_t a_x, uint32_t a_y, float3 rgb) {
    for (uint32_t x = a_x; x < a_x + gSquareSize; x++) {
        for (uint32_t y = a_y; y < a_y + gSquareSize; y++) {
            if (x < _width && y < _height) {
                uchar4 *v_out = (uchar4*)rsGetElementAt(gOut, x, y);
                *v_out = rsPackColorTo8888(rgb);
            }
        }
    }
}


/**
 * Method gets the predominant colour pixels to extrapolate the pixelation
 * from
 * 
 * @param a_x
 * @param a_y
 * @param gSquareSize
 * @return
 */
static float3 getPredominantRGB(uint32_t a_x, uint32_t a_y) {
    float red = -1.0f;
    float green = -1.0f;
    float blue = -1.0f;

    for (uint32_t x = a_x; x < a_x + gSquareSize; x++) {
        for (uint32_t y = a_y; y < a_y + gSquareSize; y++) {
        	float4 theF4 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x, y));
            if (x < _width && y < _height) {
                if (red == -1.0f) {
                    red = theF4.r;
                } else {
                    red = (red + theF4.r) / 2;
                }
                if (green == -1.0f) {
                    green = theF4.g;
                } else {
                    green = (green + theF4.g) / 2;
                }
                if (blue == -1.0f) {
                    blue = theF4.b;
                } else {
                    blue = (blue + theF4.b) / 2;
                }
            }
        }
    }
    
    float3 f3 = {red, green, blue};
    return f3;
}
    
static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
}

void process() {
	setup();
	float3 rgb;
    for (uint32_t x = 0; x < _width; x += gSquareSize) {
        for (uint32_t y = 0; y < _height; y += gSquareSize) {
            fillRect(x, y, getPredominantRGB(x, y));
        }
    }
}
