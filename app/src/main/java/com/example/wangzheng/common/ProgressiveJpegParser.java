package com.example.wangzheng.common;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.wangzheng.App;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Progressively scans jpeg data and instructs caller when enough data is available to decode
 * a partial image.
 * <p/>
 * <p> This class treats any sequence of bytes starting with 0xFFD8 as a valid jpeg image
 * <p/>
 * <p> Users should call parseMoreData method each time new chunk of data is received. The buffer
 * passed as a parameter should include entire image data received so far.
 */
public class ProgressiveJpegParser {
    public static void simple() {
        ProgressiveJpegParser jpegParser = new ProgressiveJpegParser();
        jpegParser.setOnImageDataListener(
                new ProgressiveJpegParser.OnImageDataListener() {
                    @Override
                    public void onImageDataReady(byte[] datas) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length);
                    }
                }
        );
        AssetManager assetManager = App.instance().getAssets();
        InputStream sourceInput = null;
        try {
            sourceInput = assetManager.open("jpeg_test.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        jpegParser.doParseMoreData(sourceInput, outputStream);
    }

    /**
     * Initial state of the parser. Next byte read by the parser should be 0xFF.
     */
    private static final int READ_FIRST_JPEG_BYTE = 0;

    /**
     * Parser saw only one byte so far (0xFF). Next byte should be second byte of SOI marker
     */
    private static final int READ_SECOND_JPEG_BYTE = 1;

    /**
     * Next byte is either entropy coded data or first byte of a marker. First byte of marker
     * cannot appear in entropy coded data, unless it is followed by 0x00 escape byte.
     */
    private static final int READ_MARKER_FIRST_BYTE_OR_ENTROPY_DATA = 2;

    /**
     * Last read byte is 0xFF, possible start of marker (possible, because next byte might be
     * "escape byte" or 0xFF again)
     */
    private static final int READ_MARKER_SECOND_BYTE = 3;

    /**
     * Last two bytes constitute a marker that indicates start of a segment, the following two bytes
     * denote 16bit size of the segment
     */
    private static final int READ_SIZE_FIRST_BYTE = 4;

    /**
     * Last three bytes are marker and first byte of segment size, after reading next byte, bytes
     * constituting remaining part of segment will be skipped
     */
    private static final int READ_SIZE_SECOND_BYTE = 5;

    /**
     * Parsed data is not a JPEG file
     */
    private static final int NOT_A_JPEG = 6;

    private static final int DIRECTLY_END = 7;

    /**
     * The buffer size in bytes to use.
     */
    private static final int BUFFER_SIZE = 32 * 1024;

    private int mBufferSize = BUFFER_SIZE;

    private int mParserState;
    private int mLastByteRead;


    public interface OnImageDataListener {
        void onImageDataReady(byte[] datas);
    }

    private OnImageDataListener mOnImageDataListener;

    public ProgressiveJpegParser() {
        mLastByteRead = 0;
        mParserState = READ_FIRST_JPEG_BYTE;
    }

    public void setOnImageDataListener(OnImageDataListener listener) {
        mOnImageDataListener = listener;
    }

    private ByteArrayOutputStream mBaos;


    private void writeToBaos(ByteArrayOutputStream outputStream, int nextByte) {
        outputStream.write(nextByte);
    }

    private void writeToBaos(InputStream inputStream, ByteArrayOutputStream outputStream, int length)
            throws IOException {

        byte[] buffer;
        int readNum = 0;
        while (length > mBufferSize) {
            buffer = new byte[mBufferSize];
            int perReadNum = 0;
            while (perReadNum < mBufferSize) {
                perReadNum += inputStream.read(buffer, 0, mBufferSize - perReadNum);
                readNum += perReadNum;
            }
        }
        buffer = new byte[length - readNum];
        while (readNum < length) {
            readNum += inputStream.read(buffer, 0, length - readNum);
        }


        outputStream.write(buffer);

    }

    private boolean writeToBaos(InputStream inputStream, ByteArrayOutputStream outputStream)
            throws IOException {
        final byte[] bytes = new byte[mBufferSize];
        int count;
        while ((count = inputStream.read(bytes, 0, mBufferSize)) != -1) {
            outputStream.write(bytes, 0, count);

        }
        return true;
    }


    /**
     * Parses more data from inputStream.
     *
     * @param inputStream instance of buffered pooled byte buffer input stream
     */
    public boolean doParseMoreData(final InputStream inputStream, ByteArrayOutputStream outputStream) {
        mBaos = outputStream;
        try {
            int nextByte;
            while ((nextByte = inputStream.read()) != -1) {
                writeToBaos(outputStream, nextByte);

                switch (mParserState) {
                    case READ_FIRST_JPEG_BYTE:
                        if (nextByte == JfifUtil.MARKER_FIRST_BYTE) {
                            mParserState = READ_SECOND_JPEG_BYTE;
                        } else {
                            mParserState = NOT_A_JPEG;
                        }
                        break;

                    case READ_SECOND_JPEG_BYTE:
                        if (nextByte == JfifUtil.MARKER_SOI) {
                            mParserState = READ_MARKER_FIRST_BYTE_OR_ENTROPY_DATA;
                        } else {
                            mParserState = NOT_A_JPEG;
                        }
                        break;

                    case READ_MARKER_FIRST_BYTE_OR_ENTROPY_DATA:
                        if (nextByte == JfifUtil.MARKER_FIRST_BYTE) {
                            mParserState = READ_MARKER_SECOND_BYTE;
                        }
                        break;

                    case READ_MARKER_SECOND_BYTE:
                        if (nextByte == JfifUtil.MARKER_FIRST_BYTE) {
                            mParserState = READ_MARKER_SECOND_BYTE;
                        } else if (nextByte == JfifUtil.MARKER_ESCAPE_BYTE) {
                            mParserState = READ_MARKER_FIRST_BYTE_OR_ENTROPY_DATA;
                        } else {
                            if (nextByte == JfifUtil.MARKER_SOS || nextByte == JfifUtil.MARKER_EOI) {
                                newScanOrImageEndFound();
                            }

                            if (doesMarkerStartSegment(nextByte)) {
                                mParserState = READ_SIZE_FIRST_BYTE;
                            } else {
                                mParserState = READ_MARKER_FIRST_BYTE_OR_ENTROPY_DATA;
                            }
                        }
                        break;

                    case READ_SIZE_FIRST_BYTE:
                        mParserState = READ_SIZE_SECOND_BYTE;
                        break;

                    case READ_SIZE_SECOND_BYTE:
                        final int size = (mLastByteRead << 8) + nextByte;
                        // We need to jump after the end of the segment - skip size-2 next bytes.
                        // We might want to skip more data than is available to read, in which case we will
                        // consume entire data in inputStream and exit this function before entering another
                        // iteration of the loop.
                        final int bytesToSkip = size - 2;
                        // StreamUtil.skip(inputStream, bytesToSkip);

                        // Todo by lsy: Save the skip data in Buffer
                        writeToBaos(inputStream, outputStream, bytesToSkip);
                        mParserState = READ_MARKER_FIRST_BYTE_OR_ENTROPY_DATA;
                        break;

                    case NOT_A_JPEG:
                        writeToBaos(inputStream, outputStream);
                        break;
                    case DIRECTLY_END:
                        writeToBaos(inputStream, outputStream);
                        break;
                    default:
                        break;
                }

                mLastByteRead = nextByte;
            }
        } catch (IOException ioe) {
            // does not happen, input stream returned by pooled byte buffer does not throw IOExceptions
        }
        return true;
    }

    /**
     * Not every marker is followed by associated segment
     */
    private static boolean doesMarkerStartSegment(int markerSecondByte) {
        if (markerSecondByte == JfifUtil.MARKER_TEM) {
            return false;
        }

        if (markerSecondByte >= JfifUtil.MARKER_RST0 && markerSecondByte <= JfifUtil.MARKER_RST7) {
            return false;
        }

        return markerSecondByte != JfifUtil.MARKER_EOI && markerSecondByte != JfifUtil.MARKER_SOI;
    }

    private void newScanOrImageEndFound() throws IOException {
        if (mOnImageDataListener != null) {
            byte[] bytes = mBaos.toByteArray();
            byte[] tailBytes = new byte[]{(byte) JfifUtil.MARKER_FIRST_BYTE, (byte) JfifUtil.MARKER_EOI};
            byte[] finalBytes = new byte[bytes.length];

            System.arraycopy(bytes, 0, finalBytes, 0, bytes.length - 2);
            System.arraycopy(tailBytes, 0, finalBytes, bytes.length - 2, tailBytes.length);

            mOnImageDataListener.onImageDataReady(finalBytes);
        }
    }


    /**
     * Util for obtaining information from JPEG file.
     */
    public class JfifUtil {
        /**
         * Definitions of jpeg markers as well as overall description of jpeg file format can be found
         * here: <a href="http://www.w3.org/Graphics/JPEG/itu-t81.pdf">Recommendation T.81</a>
         */
        public static final int MARKER_FIRST_BYTE  = 0xFF;
        public static final int MARKER_ESCAPE_BYTE = 0x00;
        public static final int MARKER_SOI         = 0xD8;
        public static final int MARKER_TEM         = 0x01;
        public static final int MARKER_EOI         = 0xD9;
        public static final int MARKER_SOS         = 0xDA;
        public static final int MARKER_APP1        = 0xE1;
        public static final int MARKER_SOFn        = 0xC0;
        public static final int MARKER_RST0        = 0xD0;
        public static final int MARKER_RST7        = 0xD7;
        public static final int APP1_EXIF_MAGIC    = 0x45786966;
    }
}