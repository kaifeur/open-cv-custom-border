import border.BorderMaker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ImageChecker;

import java.util.concurrent.ThreadLocalRandom;

import static org.opencv.imgcodecs.Imgcodecs.IMREAD_COLOR;
import static org.opencv.imgcodecs.Imgcodecs.imread;

public class BorderMakerTest {
    public static final String IMAGE_RESOURCE_NAME = "Lenna.png";
    public static final String IMAGES_ARE_NOT_THE_SAME = "Images are not the same";

    private final Logger logger = LoggerFactory.getLogger(BorderMakerTest.class);

    private Mat image;

    //loads OpenCV library (lib needs)
    static {
        nu.pattern.OpenCV.loadShared();
    }

    private Mat opencvImageWithBorder;
    private Mat myImageWithBorder;

    @BeforeEach
    public void loadImage() {
        logger.info("Loading image from resources");
        image = imread(this.getClass().getResource(IMAGE_RESOURCE_NAME).getPath(), IMREAD_COLOR);
        if (image.empty()) {
            logger.error("Can't load the image from resources");
            throw new IllegalStateException("Can't load the image");
        }
    }

    @Test
    public void makeReflectBorderAndCompareWithOpenCV() {
        BorderMaker borderMaker = new BorderMaker(image);
        int borderSize = 25;

        logger.info("Creating image with a reflect border with BorderMaker");
        myImageWithBorder = borderMaker.createImageWithBorder(borderSize, borderSize, borderSize,
                borderSize, BorderMaker.BorderType.REFLECT, null);

        logger.info("Creating image with a reflect border with OpenCV API");
        opencvImageWithBorder = new Mat();
        Core.copyMakeBorder(image, opencvImageWithBorder, borderSize, borderSize, borderSize,
                borderSize, Core.BORDER_REFLECT);

        logger.info("Subtracting images");
        Mat subtractResult = new Mat();
        Core.subtract(myImageWithBorder, opencvImageWithBorder, subtractResult);

        Assertions.assertTrue(ImageChecker.isImageFullBlack(subtractResult), IMAGES_ARE_NOT_THE_SAME);
    }

    @Test
    public void makeReplicateBorderAndCompareWithOpenCV() {
        BorderMaker borderMaker = new BorderMaker(image);
        int borderSize = 25;

        logger.info("Creating image with a replicate border with BorderMaker");
        myImageWithBorder = borderMaker.createImageWithBorder(borderSize, borderSize, borderSize,
                borderSize, BorderMaker.BorderType.REPLICATE, null);

        logger.info("Creating image with a replicate border with OpenCV API");
        opencvImageWithBorder = new Mat();
        Core.copyMakeBorder(image, opencvImageWithBorder, borderSize, borderSize, borderSize,
                borderSize, Core.BORDER_REPLICATE);

        logger.info("Subtracting images");
        Mat subtractResult = new Mat();
        Core.subtract(myImageWithBorder, opencvImageWithBorder, subtractResult);

        Assertions.assertTrue(ImageChecker.isImageFullBlack(subtractResult), IMAGES_ARE_NOT_THE_SAME);
    }

    @Test
    public void makeConstantBorderAndCompareWithOpenCV() {
        BorderMaker borderMaker = new BorderMaker(image);
        int borderSize = 25;
        Scalar color = new Scalar(ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256));

        logger.info("Creating image with a constant border with BorderMaker");
        myImageWithBorder = borderMaker.createImageWithBorder(borderSize, borderSize, borderSize,
                borderSize, BorderMaker.BorderType.CONSTANT, color);

        logger.info("Creating image with a constant border with OpenCV API");
        opencvImageWithBorder = new Mat();
        Core.copyMakeBorder(image, opencvImageWithBorder, borderSize, borderSize, borderSize,
                borderSize, Core.BORDER_CONSTANT, color);

        logger.info("Subtracting images");
        Mat subtractResult = new Mat();
        Core.subtract(myImageWithBorder, opencvImageWithBorder, subtractResult);

        Assertions.assertTrue(ImageChecker.isImageFullBlack(subtractResult), IMAGES_ARE_NOT_THE_SAME);
    }
}
