import border.BorderMaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import util.ImageChecker;

import java.util.concurrent.ThreadLocalRandom;

import static org.opencv.imgcodecs.Imgcodecs.IMREAD_COLOR;
import static org.opencv.imgcodecs.Imgcodecs.imread;

public class BorderMakerTest {
    public static final String IMAGE_RESOURCE_NAME = "Lenna.png";
    private Mat image;

    static {
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    @BeforeEach
    public void loadImage() {
        image = imread(this.getClass().getResource(IMAGE_RESOURCE_NAME).getPath(), IMREAD_COLOR);
        Assertions.assertFalse(image.empty());
    }

    @Test
    public void makeReflectBorderAndCompareWithOpenCV() {
        BorderMaker borderMaker = new BorderMaker(image);
        int borderSize = 25;
        Mat myImageWithBorder = borderMaker.createImageWithBorder(borderSize, borderSize, borderSize,
                borderSize, BorderMaker.BorderType.REFLECT, null);

        Mat opencvImageWithBorder = new Mat();
        Core.copyMakeBorder(image, opencvImageWithBorder, borderSize, borderSize, borderSize,
                borderSize, Core.BORDER_REFLECT);

        Mat subtractResult = new Mat();
        Core.subtract(myImageWithBorder, opencvImageWithBorder, subtractResult);

        Assertions.assertTrue(ImageChecker.isImageFullBlack(subtractResult));
    }

    @Test
    public void makeReplicateBorderAndCompareWithOpenCV() {
        BorderMaker borderMaker = new BorderMaker(image);
        int borderSize = 25;
        Mat myImageWithBorder = borderMaker.createImageWithBorder(borderSize, borderSize, borderSize,
                borderSize, BorderMaker.BorderType.REPLICATE, null);

        Mat opencvImageWithBorder = new Mat();
        Core.copyMakeBorder(image, opencvImageWithBorder, borderSize, borderSize, borderSize,
                borderSize, Core.BORDER_REPLICATE);

        Mat subtractResult = new Mat();
        Core.subtract(myImageWithBorder, opencvImageWithBorder, subtractResult);

        Assertions.assertTrue(ImageChecker.isImageFullBlack(subtractResult));
    }

    @Test
    public void makeConstantBorderAndCompareWithOpenCV() {
        BorderMaker borderMaker = new BorderMaker(image);
        int borderSize = 25;
        Scalar color = new Scalar(ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256));

        Mat myImageWithBorder = borderMaker.createImageWithBorder(borderSize, borderSize, borderSize,
                borderSize, BorderMaker.BorderType.CONSTANT, color);

        Mat opencvImageWithBorder = new Mat();
        Core.copyMakeBorder(image, opencvImageWithBorder, borderSize, borderSize, borderSize,
                borderSize, Core.BORDER_CONSTANT, color);

        Mat subtractResult = new Mat();
        Core.subtract(myImageWithBorder, opencvImageWithBorder, subtractResult);

        Assertions.assertTrue(ImageChecker.isImageFullBlack(subtractResult));
    }
}
