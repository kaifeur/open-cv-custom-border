

import border.BorderMaker;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.Random;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class BorderMakerClient {
    /* Подгружает библиотеку OpenCV() из подключенной зависимости.
     */
    static {
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    /**
     * Точка входа в программу.
     * В качестве аргумента можно передать путь до исходного изображения.
     *
     * @param args 1 - путь до изображения
     */
    public static void main(String[] args) {
        Mat src;
        new Mat();
        new Mat();
        new Mat();
        Mat dstRef = new Mat(), dstConst = new Mat(), dstRep = new Mat(),
                dstMyRef, dstMyConst, dstMyRep,
                refDiff = new Mat(), constDiff = new Mat(), repDiff = new Mat();

        int top, bottom, left, right;

        //Имена окон
        String origReflectWindow = "copyMakeBorder - Reflect";
        String myReflectWindow = "border.MyBorder - Reflect";
        String diffReflectWindow = "Diff - Reflect";

        String origConstWindow = "copyMakeBorder - Constant";
        String myConstWindow = "border.MyBorder - Constant";
        String diffConstWindow = "Diff - Constant";

        String origRepWindow = "copyMakeBorder - Replicate";
        String myRepWindow = "border.MyBorder - Replicate";
        String diffRepWindow = "Diff - Replicate";

        //Проверка наличия параметра-пути
        String imageName = ((args.length > 0) ? args[0] : "/Users/keet/Lenna.png");
        src = imread(imageName, Imgcodecs.IMREAD_COLOR);

        if (src.empty()) {
            System.out.println("Error opening image!");
            System.out.println("Program Arguments: [image_name -- default /Users/keet/Lenna.png] \n");
            System.exit(-1);
        }

        //Размеры рамок
        top = (int) (0.05 * src.rows());
        bottom = top;
        left = (int) (0.05 * src.cols());
        right = left;

        //Задание случайного цвета для Constant-рамки
        Random random = new Random();
        Scalar color = new Scalar(random.nextInt(256),
                random.nextInt(256), random.nextInt(256));

        Mat img = new Mat();
        src.copyTo(img);
        BorderMaker borderMaker = new BorderMaker(img);

        //Создание рамок разных типов
        Core.copyMakeBorder(src, dstRef, top, bottom, left, right, Core.BORDER_REFLECT, color);
        dstMyRef = borderMaker.createImageWithBorder(top, bottom, left, right, border.BorderMaker.BorderType.REFLECT, color);

        Core.copyMakeBorder(src, dstConst, top, bottom, left, right, Core.BORDER_CONSTANT, color);
        dstMyConst = borderMaker.createImageWithBorder(top, bottom, left, right, border.BorderMaker.BorderType.CONSTANT, color);

        Core.copyMakeBorder(src, dstRep, top, bottom, left, right, Core.BORDER_REPLICATE, color);
        dstMyRep = borderMaker.createImageWithBorder(top, bottom, left, right, border.BorderMaker.BorderType.REPLICATE, color);

        //Создание окон
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

        //Цикл отрисовки изображения и ожидания кнопки ESC для выхода
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