package util;

import org.opencv.core.Mat;

public class ImageChecker {
    private ImageChecker() {
    }

    public static boolean isImageFullBlack(final Mat image) {
        byte[] imageInByte = new byte[(int) (image.total() * image.channels())];
        image.get(0, 0, imageInByte);
        return isArrayAllZero(imageInByte);
    }

    private static boolean isArrayAllZero(byte[] imageInByte) {
        for (byte l : imageInByte) {
            if (l != 0) {
                return false;
            }
        }
        return true;
    }
}
