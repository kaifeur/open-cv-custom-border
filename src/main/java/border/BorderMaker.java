package border;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Данный класс представляет собой набор методов для создания рамок разных типов.
 */
public class BorderMaker {
    private final Logger logger = LoggerFactory.getLogger(BorderMaker.class);

    private final Mat originalImage;
    private Mat currentImage;
    private int topBorderSize;
    private int bottomBorderSize;
    private int leftBorderSize;
    private int rigthBorderSize;

    public BorderMaker(Mat img) {
        originalImage = img.clone();
        logger.info("Border maker created successfully with img {}", originalImage);
    }

    public Mat createImageWithBorder(final int topBorderSize, final int bottomBorderSize,
                                     final int leftBorderSize, final int rigthBorderSize,
                                     final BorderType borderType, final Scalar borderColor) {
        this.topBorderSize = topBorderSize;
        this.bottomBorderSize = bottomBorderSize;
        this.leftBorderSize = leftBorderSize;
        this.rigthBorderSize = rigthBorderSize;

        extendImageForBorderSize();

        switch (borderType) {
            case CONSTANT:
                makeConstantBorder(borderColor);
                break;
            case REFLECT:
                makeReflectBorder();
                break;
            case REPLICATE:
                makeReplicateBorder();
                break;
            default:
                throw new IllegalArgumentException("Wrong border type");
        }
        final Mat result = new Mat();
        currentImage.copyTo(result);
        return result;
    }

    /**
     * Создает расширенную матрицу, содержащую исходную в центре.
     */
    private void extendImageForBorderSize() {
        final Mat temp = new Mat(currentImage.rows() + topBorderSize + bottomBorderSize,
                currentImage.cols() + leftBorderSize + rigthBorderSize, currentImage.type());
        currentImage.copyTo(temp.colRange(leftBorderSize, leftBorderSize + currentImage.cols())
                .rowRange(topBorderSize, topBorderSize + currentImage.rows()));
        currentImage = temp.clone();
    }

    /**
     * Добавляет рамку определенного цвета.
     */
    private void makeConstantBorder(final Scalar borderColor) {
        currentImage = originalImage.clone();

        currentImage.rowRange(0, topBorderSize).setTo(borderColor);
        currentImage.rowRange(topBorderSize + originalImage.rows(),
                currentImage.rows()).setTo(borderColor);
        currentImage.colRange(0, leftBorderSize).setTo(borderColor);
        currentImage.colRange(leftBorderSize + originalImage.cols(),
                currentImage.cols()).setTo(borderColor);
    }

    /**
     * Добавляет зеркальную рамку.
     */
    private void makeReflectBorder() {
        currentImage = originalImage.clone();
        checkBorderMakingPossibility();

        doSideFlips();
        doCornerFlips();
    }

    private void checkBorderMakingPossibility() {
        if (!isImageSizeEnough()) {
            throw new IllegalArgumentException("Border size can't " +
                    "be more than tempImage size when border type is REFLECT");
        }
    }

    private boolean isImageSizeEnough() {
        return leftBorderSize <= originalImage.cols()
                || rigthBorderSize <= originalImage.cols()
                || topBorderSize <= originalImage.rows()
                || bottomBorderSize <= originalImage.rows();
    }

    private void doSideFlips() {
        doLeftSideFlip();
        doRightSideFlip();
        doTopSideFlip();
        doBottomSideFlip();
    }

    private void doCornerFlips() {
        doTopLeftCornerFlip();
        doTopRightCornerFlip();
        doBottomLeftCornerFlip();
        doBottomRightCornerFlip();
    }

    private void doLeftSideFlip() {
        Core.flip(originalImage.colRange(0, leftBorderSize),
                currentImage.colRange(0, leftBorderSize)
                        .rowRange(topBorderSize, topBorderSize + originalImage.rows()), 1);
    }

    private void doRightSideFlip() {
        Core.flip(originalImage.colRange(originalImage.cols() - rigthBorderSize, originalImage.cols()),
                currentImage.colRange(leftBorderSize + originalImage.rows(), currentImage.cols())
                        .rowRange(topBorderSize, topBorderSize + originalImage.rows()), 1);
    }

    private void doTopSideFlip() {
        Core.flip(originalImage.rowRange(0, topBorderSize),
                currentImage.colRange(leftBorderSize, leftBorderSize + originalImage.cols())
                        .rowRange(0, topBorderSize), 0);
    }

    private void doBottomSideFlip() {
        Core.flip(originalImage.rowRange(originalImage.rows() - bottomBorderSize, originalImage.rows()),
                currentImage.colRange(leftBorderSize, leftBorderSize + originalImage.cols())
                        .rowRange(topBorderSize + originalImage.rows(), currentImage.rows()), 0);
    }

    private void doTopLeftCornerFlip() {
        Core.flip(originalImage.colRange(0, leftBorderSize)
                        .rowRange(0, topBorderSize),
                currentImage.colRange(0, leftBorderSize).
                        rowRange(0, topBorderSize), -1);
    }

    private void doTopRightCornerFlip() {
        Core.flip(originalImage.colRange(originalImage.cols() - rigthBorderSize, originalImage.cols())
                        .rowRange(0, topBorderSize),
                currentImage.colRange(leftBorderSize + originalImage.cols(), currentImage.cols())
                        .rowRange(0, topBorderSize), -1);
    }

    private void doBottomLeftCornerFlip() {
        Core.flip(originalImage.colRange(0, leftBorderSize)
                        .rowRange(originalImage.rows() - bottomBorderSize, originalImage.rows()),
                currentImage.colRange(0, leftBorderSize)
                        .rowRange(topBorderSize + originalImage.rows(), currentImage.rows()), -1);
    }

    private void doBottomRightCornerFlip() {
        Core.flip(originalImage.colRange(originalImage.cols() - rigthBorderSize, originalImage.cols())
                        .rowRange(originalImage.rows() - bottomBorderSize, originalImage.rows()),
                currentImage.colRange(leftBorderSize + originalImage.cols(), currentImage.cols())
                        .rowRange(topBorderSize + originalImage.rows(), currentImage.rows()), -1);
    }

    /**
     * Добавляет рамку, повторяющую крайний пиксель.
     */
    private void makeReplicateBorder() {
        currentImage = originalImage.clone();

        for (int i = topBorderSize; i < topBorderSize + originalImage.rows(); i++) {
            double[] leftBorderSizeColor = originalImage.get(i - topBorderSize, 0);
            for (int j = 0; j < leftBorderSize; j++) {
                currentImage.put(i, j, leftBorderSizeColor);
            }

            double[] rightColor = originalImage.get(i - topBorderSize, originalImage.cols() - 1);
            for (int j = leftBorderSize + originalImage.cols(); j < currentImage.cols(); j++) {
                currentImage.put(i, j, rightColor);
            }
        }

        for (int i = leftBorderSize; i < leftBorderSize + originalImage.cols(); i++) {
            double[] topBorderSizeColor = originalImage.get(0, i - leftBorderSize);
            for (int j = 0; j < topBorderSize; j++) {
                currentImage.put(j, i, topBorderSizeColor);
            }

            double[] bottomBorderSizeColor = originalImage.get(originalImage.rows() - 1, i - leftBorderSize);
            for (int j = topBorderSize + originalImage.rows(); j < currentImage.rows(); j++) {
                currentImage.put(j, i, bottomBorderSizeColor);
            }
        }

        for (int i = 0; i < topBorderSize; i++) {
            double[] leftBorderSizeTopColor = originalImage.get(0, 0);
            for (int j = 0; j < leftBorderSize; j++) {
                currentImage.put(i, j, leftBorderSizeTopColor);
            }

            double[] rightTopColor = originalImage.get(0, originalImage.cols() - 1);
            for (int j = leftBorderSize + originalImage.cols(); j < currentImage.cols(); j++) {
                currentImage.put(i, j, rightTopColor);
            }
        }

        for (int i = topBorderSize + originalImage.rows(); i < currentImage.rows(); i++) {
            double[] leftBorderSizeBottomColor = originalImage.get(originalImage.rows() - 1, 0);
            for (int j = 0; j < leftBorderSize; j++) {
                currentImage.put(i, j, leftBorderSizeBottomColor);
            }

            double[] rightBottomColor = originalImage.get(originalImage.rows() - 1, originalImage.cols() - 1);
            for (int j = leftBorderSize + originalImage.cols(); j < currentImage.cols(); j++) {
                currentImage.put(i, j, rightBottomColor);
            }
        }
    }

    public enum BorderType {
        CONSTANT,
        REFLECT,
        REPLICATE
    }
}
