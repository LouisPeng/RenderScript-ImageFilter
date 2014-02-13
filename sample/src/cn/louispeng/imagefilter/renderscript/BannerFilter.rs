#pragma version(1)
#pragma rs java_package_name(cn.louispeng.imagefilter.renderscript)

// set from the java SDK level
rs_allocation gIn;
rs_allocation gOut;
int32_t gIsHorizontal = 1;	//0垂直方向， 1水平方向

// Magic factors

// Static variables
static uint32_t _width;
static uint32_t _height;
static const int32_t _BannerNum = 10;
static uint2 _Points[_BannerNum];

static void initOutAllcation() {
	float3 rgb = {0.7969f, 0.7969f, 0.7969f};
    for (uint32_t theX = 0; theX < _width; theX++) {
        for (uint32_t theY = 0; theY < _height; theY++) {
            uchar4 *v_out = (uchar4*)rsGetElementAt(gOut, theX, theY);
            *v_out = rsPackColorTo8888(rgb);
        }
    }
}

static void horizontalProcess() {
	uint32_t dh = _height / _BannerNum;
    uint32_t dw = _width;
        
    for (uint32_t i = 0; i < _BannerNum; i++) {
        _Points[i].x = 0;
        _Points[i].y = i * dh;
    }
    
    for (uint32_t x = 0; x < dh; x++) {
        for (uint32_t y = 0; y < _BannerNum; y++) {
            for (uint32_t k = 0; k < dw; k++) {
                uint32_t theX = _Points[y].x + k;
                uint32_t theY = _Points[y].y + (uint32_t) (x / 1.1f);
                uchar4 *v_in = (uchar4*)rsGetElementAt(gIn, theX, theY);
                uchar4 *v_out = (uchar4*)rsGetElementAt(gOut, theX, theY);
                *v_out = *v_in;
            }
        }
    }
    
    // 对图像其余部分做填充
    for (uint32_t theX = 0; theX < _width; theX++) {
        for (uint32_t theY = _Points[_BannerNum - 1].y + dh; theY < _height; theY++) {
            uchar4 *v_in = (uchar4*)rsGetElementAt(gIn, theX, theY);
            uchar4 *v_out = (uchar4*)rsGetElementAt(gOut, theX, theY);
            *v_out = *v_in;
        }
    }
} 

static void verticalProcess() {
	uint32_t dw = _width / _BannerNum;
    uint32_t dh = _height;
    for (uint32_t i = 0; i < _BannerNum; i++) {
    	_Points[i].x = i * dw;
        _Points[i].y = 0;
    }
    
    for (uint32_t x = 0; x < dw; x++) {
        for (uint32_t y = 0; y < _BannerNum; y++) {
            for (uint32_t k = 0; k < dh; k++) {
                uint32_t theX = _Points[y].x + (uint32_t) (x / 1.1f);
                uint32_t theY = _Points[y].y + k;
                uchar4 *v_in = (uchar4*)rsGetElementAt(gIn, theX, theY);
                uchar4 *v_out = (uchar4*)rsGetElementAt(gOut, theX, theY);
                *v_out = *v_in;
            }
        }
    }
    // 对图像其余部分做填充
    for (uint32_t theY = 0; theY < _height; theY++) {
        for (uint32_t theX = _Points[_BannerNum - 1].x + dw; theX < _width; theX++) {
            uchar4 *v_in = (uchar4*)rsGetElementAt(gIn, theX, theY);
            uchar4 *v_out = (uchar4*)rsGetElementAt(gOut, theX, theY);
            *v_out = *v_in;
        }
    }
}

static void setup() {
	_width = rsAllocationGetDimX(gIn);
	_height = rsAllocationGetDimY(gIn);
	initOutAllcation();
}

void process() {
	setup();
	if (gIsHorizontal == 1) {
		horizontalProcess();
	} else {
		verticalProcess();
	}
}
