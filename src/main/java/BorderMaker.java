import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.Random;

public class BorderMaker {
    static {
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        Mat src;
        Mat dstRef = new Mat(), dstConst = new Mat(), dstRep = new Mat(),
                dstMyRef = new Mat(), dstMyConst = new Mat(), dstMyRep = new Mat(),
                refDiff = new Mat(), constDiff = new Mat(), repDiff = new Mat();

        int top, bottom, left, right;

        String origReflectWindow = "copyMakeBorder - Reflect";
        String myReflectWindow = "MyBorder - Reflect";
        String diffReflectWindow = "Diff - Reflect";

        String origConstWindow = "copyMakeBorder - Constant";
        String myConstWindow = "MyBorder - Constant";
        String diffConstWindow = "Diff - Constant";

        String origRepWindow = "copyMakeBorder - Replicate";
        String myRepWindow = "MyBorder - Replicate";
        String diffRepWindow = "Diff - Replicate";

        String imageName = ((args.length > 0) ? args[0] : "/Users/keet/Lenna.png");
        src = Imgcodecs.imread(imageName, Imgcodecs.IMREAD_COLOR);

        if (src.empty()) {
            System.out.println("Error opening image!");
            System.out.println("Program Arguments: [image_name -- default /Users/keet/Lenna.png] \n");
            System.exit(-1);
        }

        top = (int) (0.05 * src.rows());
        bottom = top;
        left = (int) (0.05 * src.cols());
        right = left;

        Random random = new Random();
        Scalar color = new Scalar(random.nextInt(256),
                random.nextInt(256), random.nextInt(256));

        Core.copyMakeBorder(src, dstRef, top, bottom, left, right, Core.BORDER_REFLECT, color);
        MyBorder.makeBorder(src, dstMyRef, top, bottom, left, right, MyBorder.BorderType.REFLECT, color);

        Core.copyMakeBorder(src, dstConst, top, bottom, left, right, Core.BORDER_CONSTANT, color);
        MyBorder.makeBorder(src, dstMyConst, top, bottom, left, right, MyBorder.BorderType.CONSTANT, color);

        Core.copyMakeBorder(src, dstRep, top, bottom, left, right, Core.BORDER_REPLICATE, color);
        MyBorder.makeBorder(src, dstMyRep, top, bottom, left, right, MyBorder.BorderType.REPLICATE, color);

        HighGui.namedWindow(origReflectWindow, HighGui.WINDOW_AUTOSIZE);
        HighGui.namedWindow(myReflectWindow, HighGui.WINDOW_AUTOSIZE);
        HighGui.namedWindow(diffReflectWindow, HighGui.WINDOW_AUTOSIZE);

        HighGui.namedWindow(origConstWindow, HighGui.WINDOW_AUTOSIZE);
        HighGui.namedWindow(myConstWindow, HighGui.WINDOW_AUTOSIZE);
        HighGui.namedWindow(diffConstWindow, HighGui.WINDOW_AUTOSIZE);

        HighGui.namedWindow(origRepWindow, HighGui.WINDOW_AUTOSIZE);
        HighGui.namedWindow(myRepWindow, HighGui.WINDOW_AUTOSIZE);
        HighGui.namedWindow(diffRepWindow, HighGui.WINDOW_AUTOSIZE);

        Core.subtract(dstRef, dstMyRef, refDiff);
        Core.subtract(dstConst, dstMyConst, constDiff);
        Core.subtract(dstRep, dstMyRep, repDiff);

        while (true) {
            HighGui.imshow(origReflectWindow, dstRef);
            HighGui.imshow(myReflectWindow, dstMyRef);
            HighGui.imshow(diffReflectWindow, refDiff);

            HighGui.imshow(origConstWindow, dstConst);
            HighGui.imshow(myConstWindow, dstMyConst);
            HighGui.imshow(diffConstWindow, constDiff);

            HighGui.imshow(origRepWindow, dstRep);
            HighGui.imshow(myRepWindow, dstMyRep);
            HighGui.imshow(diffRepWindow, repDiff);

            char c = (char) HighGui.waitKey(500);
            c = Character.toLowerCase(c);
            if (c == 27) {
                break;
            }
        }

        System.exit(0);
    }
}
