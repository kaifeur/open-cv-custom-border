import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

/**
 * Данный класс представляет собой набор методов для создания рамок разных типов.
 */
public class MyBorder {
    private MyBorder() {
    }

    /**
     * Создает рамку типа borderType.
     *
     * @param src        исходная матрица
     * @param dst        матрица назначения
     * @param top        размер рамки сверху
     * @param bottom     снизу
     * @param left       слева
     * @param rigth      справа
     * @param borderType тип рамки (see {@link BorderType}
     * @param color      цвет рамки
     */
    public static void makeBorder(final Mat src, final Mat dst, final int top,
                                  final int bottom, final int left, final int rigth,
                                  final BorderType borderType, final Scalar color) {
        switch (borderType) {
            case CONSTANT:
                makeConstantBorder(src, dst, top, bottom, left, rigth, color);
                break;
            case REFLECT:
                makeReflectBorder(src, dst, top, bottom, left, rigth);
                break;
            case REPLICATE:
                makeReplicateBorder(src, dst, top, bottom, left, rigth);
                break;
            default:
                throw new IllegalArgumentException("Wrong border type");
        }
    }

    /**
     * Создает расширенную матрицу, содержащую исходную в центре.
     *
     * @return новую матрицу
     */
    private static Mat createExtendedMat(final Mat src, final int top,
                                         final int bottom, final int left, final int rigth) {
        final Mat temp = new Mat(src.rows() + top + bottom, src.cols() + left + rigth, src.type());
        src.copyTo(temp.colRange(left, left + src.cols()).rowRange(top, top + src.rows()));
        return temp;
    }

    /**
     * Добавляет рамку определенного цвета.
     */
    private static void makeConstantBorder(final Mat src, final Mat dst,
                                           final int top, final int bottom, final int left,
                                           final int rigth, final Scalar color) {
        final Mat temp = createExtendedMat(src, top, bottom, left, rigth);
        temp.rowRange(0, top).setTo(color);
        temp.rowRange(top + src.rows(), temp.rows()).setTo(color);
        temp.colRange(0, left).setTo(color);
        temp.colRange(left + src.cols(), temp.cols()).setTo(color);
        temp.copyTo(dst);
    }

    /**
     * Добавляет зеркальную рамку.
     */
    private static void makeReflectBorder(final Mat src, final Mat dst, final int top,
                                          final int bottom, final int left, final int rigth) {
        if (left > src.cols() || rigth > src.cols() || top > src.rows() || bottom > src.rows()) {
            throw new IllegalArgumentException("Border size can't " +
                    "be more than image size when border type is REFLECT");
        }
        final Mat temp = createExtendedMat(src, top, bottom, left, rigth);

        //        for (int i = top; i < top + src.rows(); i++) {
//            for (int j = 0; j < left; j++) {
//                temp.put(i, j, src.get(i - top, Math.abs(j - left) - 1));
//            }
//        }

        //left
        Core.flip(src.colRange(0, left), temp.colRange(0, left).rowRange(top, top + src.rows()), 1);

//        for (int i = top; i < top + src.rows(); i++) {
//            for (int j = left + src.cols(); j < temp.cols(); j++) {
//                temp.put(i, j, src.get(i - top, src.cols() - Math.abs(src.cols() - (j - left)) - 1));
//            }
//        }

        //right
        Core.flip(src.colRange(src.cols() - rigth, src.cols()),
                temp.colRange(left + src.rows(), temp.cols()).rowRange(top, top + src.rows()), 1);

//        for (int i = 0; i < top; i++) {
//            for (int j = left; j < left + src.rows(); j++) {
//                temp.put(i, j, src.get(Math.abs(i - top) - 1, j - left));
//            }
//        }

        //top
        Core.flip(src.rowRange(0, top), temp.colRange(left, left + src.cols()).rowRange(0, top), 0);

//        for (int i = top + src.rows(); i < temp.rows(); i++) {
//            for (int j = left; j < left + src.rows(); j++) {
//                temp.put(i, j, src.get(src.rows() - Math.abs(src.rows() - (i - top)) - 1, j - left));
//            }
//        }

        //bottom
        Core.flip(src.rowRange(src.rows() - bottom, src.rows()),
                temp.colRange(left, left + src.cols()).rowRange(top + src.rows(), temp.rows()), 0);

        //top-left corner
        Core.flip(src.colRange(0, left).rowRange(0, top), temp.colRange(0, left).rowRange(0, top), -1);

        //top-right corner
        Core.flip(src.colRange(src.cols() - rigth, src.cols()).rowRange(0, top),
                temp.colRange(left + src.cols(), temp.cols()).rowRange(0, top), -1);

        //bottom-left corner
        Core.flip(src.colRange(0, left).rowRange(src.rows() - bottom, src.rows()),
                temp.colRange(0, left).rowRange(top + src.rows(), temp.rows()), -1);

        //bottom-right corner
        Core.flip(src.colRange(src.cols() - rigth, src.cols()).rowRange(src.rows() - bottom, src.rows()),
                temp.colRange(left + src.cols(), temp.cols()).rowRange(top + src.rows(), temp.rows()), -1);

        temp.copyTo(dst);
    }

    /**
     * Добавляет рамку, повторяющую крайний пиксель.
     */
    private static void makeReplicateBorder(final Mat src, final Mat dst, final int top,
                                            final int bottom, final int left, final int rigth) {
        final Mat temp = createExtendedMat(src, top, bottom, left, rigth);

        for (int i = top; i < top + src.rows(); i++) {
            double[] leftColor = src.get(i - top, 0);
            for (int j = 0; j < left; j++) {
                temp.put(i, j, leftColor);
            }

            double[] rightColor = src.get(i - top, src.cols() - 1);
            for (int j = left + src.cols(); j < temp.cols(); j++) {
                temp.put(i, j, rightColor);
            }
        }

        for (int i = left; i < left + src.cols(); i++) {
            double[] topColor = src.get(0, i - left);
            for (int j = 0; j < top; j++) {
                temp.put(j, i, topColor);
            }

            double[] bottomColor = src.get(src.rows() - 1, i - left);
            for (int j = top + src.rows(); j < temp.rows(); j++) {
                temp.put(j, i, bottomColor);
            }
        }

        for (int i = 0; i < top; i++) {
            double[] leftTopColor = src.get(0, 0);
            for (int j = 0; j < left; j++) {
                temp.put(i, j, leftTopColor);
            }

            double[] rightTopColor = src.get(0, src.cols() - 1);
            for (int j = left + src.cols(); j < temp.cols(); j++) {
                temp.put(i, j, rightTopColor);
            }
        }

        for (int i = top + src.rows(); i < temp.rows(); i++) {
            double[] leftBottomColor = src.get(src.rows() - 1, 0);
            for (int j = 0; j < left; j++) {
                temp.put(i, j, leftBottomColor);
            }

            double[] rightBottomColor = src.get(src.rows() - 1, src.cols() - 1);
            for (int j = left + src.cols(); j < temp.cols(); j++) {
                temp.put(i, j, rightBottomColor);
            }
        }

        temp.copyTo(dst);
    }

    /**
     * Возможные типы рамок.
     */
    public enum BorderType {
        CONSTANT,
        REFLECT,
        REPLICATE
    }
}
