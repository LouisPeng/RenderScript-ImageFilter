#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// 直方图特效

#include "Clamp.rsh"
#include "YUV.rsh"

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
uint8_t *pixelGrayscaleArray;
rs_script gScript;

// Magic factors
static float ContrastIntensity = 1.0f;

// Static variables
static uint32_t _width;
static uint32_t _height;
static float _GrayscaleCumulativeProbabilityArray[MAX_GRAYSCALE_COUNT];
static uint8_t _maxGrayscale;
static uint8_t _minGrayscale;
static uint8_t _disGrayscale;

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	
	uint32_t _GrayscaleCountArray[MAX_GRAYSCALE_COUNT] = {0};
    
    // Clear gray scale cumulative probability array
    for (int32_t i = 1; i < MAX_GRAYSCALE_COUNT; i++) {
        _GrayscaleCumulativeProbabilityArray[i] = 0;
    }
    
    // Calculate gray scale's count
    int32_t pos = 0;
    _maxGrayscale = 0;
    _minGrayscale = 0;
    for (int32_t y = 0; y < _height; y++) {
        for (int32_t x = 0; x < _width; x++) {
            float4 theF4 = rsUnpackColor8888(*(const uchar4*)rsGetElementAt(gIn, x, y));
            
            // get pixel's gray scale
            uint8_t grayscale = GetGrayscale0255(theF4.rgb);
            if (_maxGrayscale < grayscale) {
            	_maxGrayscale = grayscale;
            } else if (_minGrayscale > grayscale) {
            	_minGrayscale = grayscale;
            }
            
            // store pixel's gray scale
            pixelGrayscaleArray[pos] = grayscale;
            // store gray scale's count
            _GrayscaleCountArray[grayscale]++;
            
            pos++;
        }
    }
    
    _disGrayscale = _maxGrayscale - _minGrayscale;
    
    // Calulate gray scale cumulative count
    for (int32_t i = 1; i < MAX_GRAYSCALE_COUNT; i++) {
        _GrayscaleCountArray[i] += _GrayscaleCountArray[i - 1];
    }
    
    uint32_t pixelCount = _height * _width;
    for (int32_t i = 0; i < MAX_GRAYSCALE_COUNT; i++) {
        _GrayscaleCumulativeProbabilityArray[i] = ((float)(_GrayscaleCountArray[i] * (MAX_GRAYSCALE_COUNT - 1))) / pixelCount;
        _GrayscaleCumulativeProbabilityArray[i] = (ContrastIntensity * _GrayscaleCumulativeProbabilityArray[i]) + ((1.0f - ContrastIntensity) * i);
    }
}

void filter() {
	setup();
    rsForEach(gScript, gIn, gOut, 0, 0);	// for each element of the input allocation,
    										// call root() method on gScript
}

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
	float4 f4 = rsUnpackColor8888(*v_in);	// extract RGBA values, see rs_core.rsh
	
    float3 f3;
    uint8_t grayscale = pixelGrayscaleArray[y * _width + x];    
    if (grayscale != 0) {
        float cumulativeProbability = _GrayscaleCumulativeProbabilityArray[grayscale];
        float factor = ((cumulativeProbability * _disGrayscale + _minGrayscale) / (MAX_GRAYSCALE_COUNT - 1)) / (MAX_GRAYSCALE_COUNT - 1);
        f3 = f4.rgb * factor;
        f3 = FClamp01Float3(f3);
    } else {
    	f3 = f4.rgb;
    }
    
    *v_out = rsPackColorTo8888(f3);
}
