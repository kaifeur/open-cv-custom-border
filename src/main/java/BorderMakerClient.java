import border.BorderMaker;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class BorderMakerClient {
    public static final String LENNA_IMAGE_PATH = "/Users/keet/Lenna.png";
    public static final int ESCAPE_CODE = 27;
    private static final Logger logger = LoggerFactory.getLogger(BorderMakerClient.class);

    /*
     * Loads OpenCV library (lib needs).
     */
    static {
        nu.pattern.OpenCV.loadShared();
    }

    /**
     * @param args 1 - path to the image
     */
    public static void main(String[] args) {
        Mat sourceImage;
        Mat opencvReflectResult = new Mat(),
                opencvConstResult = new Mat(),
                opencvReplicateResult = new Mat(),
                reflectDiff = new Mat(),
                constantDiff = new Mat(),
                replicateDiff = new Mat();

        int top, bottom, left, right;

        String origReflectWindow = "copyMakeBorder - Reflect";
        String myReflectWindow = "border.MyBorder - Reflect";
        String diffReflectWindow = "Diff - Reflect";

        String origConstWindow = "copyMakeBorder - Constant";
        String myConstWindow = "border.MyBorder - Constant";
        String diffConstWindow = "Diff - Constant";

        String origRepWindow = "copyMakeBorder - Replicate";
        String myRepWindow = "border.MyBorder - Replicate";
        String diffRepWindow = "Diff - Replicate";

        logger.info("Loading image {}", LENNA_IMAGE_PATH);
        String imageName = args.length > 0 ? args[0] : LENNA_IMAGE_PATH;
        sourceImage = imread(imageName, Imgcodecs.IMREAD_COLOR);

        if (sourceImage.empty()) {
            logger.error("Error opening image");
            logger.info("Program Arguments: [image_name -- default {}]", LENNA_IMAGE_PATH);
            System.exit(42);
        }

        logger.info("Image was loaded successfully");

        top = (int) (0.05 * sourceImage.rows());
        bottom = top;
        left = (int) (0.05 * sourceImage.cols());
        right = left;

        Scalar color = new Scalar(ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256));

        Mat sourceImageCopy = new Mat();
        sourceImage.copyTo(sourceImageCopy);
        BorderMaker borderMaker = new BorderMaker(sourceImageCopy);

        Core.copyMakeBorder(sourceImage, opencvReflectResult,
                top, bottom, left, right, Core.BORDER_REFLECT, color);
        Mat bMakerReflectResult = borderMaker.createImageWithBorder(top, bottom, left, right,
                BorderMaker.BorderType.REFLECT, color);

        Core.copyMakeBorder(sourceImage, opencvConstResult,
                top, bottom, left, right, Core.BORDER_CONSTANT, color);
        Mat bMakerConstantResult = borderMaker.createImageWithBorder(top, bottom, left, right,
                BorderMaker.BorderType.CONSTANT, color);

        Core.copyMakeBorder(sourceImage, opencvReplicateResult,
                top, bottom, left, right, Core.BORDER_REPLICATE, color);
        Mat bMakerReplicateResult = borderMaker.createImageWithBorder(top, bottom, left, right,
                BorderMaker.BorderType.REPLICATE, color);

        HighGui.namedWindow(origReflectWindow, HighGui.WINDOW_AUTOSIZE);
        HighGui.namedWindow(myReflectWindow, HighGui.WINDOW_AUTOSIZE);
        HighGui.namedWindow(diffReflectWindow, HighGui.WINDOW_AUTOSIZE);

        HighGui.namedWindow(origConstWindow, HighGui.WINDOW_AUTOSIZE);
        HighGui.namedWindow(myConstWindow, HighGui.WINDOW_AUTOSIZE);
        HighGui.namedWindow(diffConstWindow, HighGui.WINDOW_AUTOSIZE);

        HighGui.namedWindow(origRepWindow, HighGui.WINDOW_AUTOSIZE);
        HighGui.namedWindow(myRepWindow, HighGui.WINDOW_AUTOSIZE);
        HighGui.namedWindow(diffRepWindow, HighGui.WINDOW_AUTOSIZE);

        Core.subtract(opencvReflectResult, bMakerReflectResult, reflectDiff);
        Core.subtract(opencvConstResult, bMakerConstantResult, constantDiff);
        Core.subtract(opencvReplicateResult, bMakerReplicateResult, replicateDiff);

        while (true) {
            HighGui.imshow(origReflectWindow, opencvReflectResult);
            HighGui.imshow(myReflectWindow, bMakerReflectResult);
            HighGui.imshow(diffReflectWindow, reflectDiff);

            HighGui.imshow(origConstWindow, opencvConstResult);
            HighGui.imshow(myConstWindow, bMakerConstantResult);
            HighGui.imshow(diffConstWindow, constantDiff);

            HighGui.imshow(origRepWindow, opencvReplicateResult);
            HighGui.imshow(myRepWindow, bMakerReplicateResult);
            HighGui.imshow(diffRepWindow, replicateDiff);

            char pressedKey = (char) HighGui.waitKey(500);
            pressedKey = Character.toLowerCase(pressedKey);
            if (pressedKey == ESCAPE_CODE) {
                logger.info("Escape key was pressed, now exit");
                break;
            }
        }

        System.exit(0);
    }
}
